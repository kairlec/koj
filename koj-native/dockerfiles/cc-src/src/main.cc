#include "defined.h"
#include "processor_killer.h"
#include "child.h"
#include "logger.h"

#include <iostream>
#include <fstream>

#include <csignal>
#include <cerrno>
#include <cstdlib>


#include <pthread.h>
#include <sys/wait.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <sys/stat.h>

#define FORK_ERROR -1  // fork进程异常
#define PTHREAD_ERROR -2  // 线程创建出错
#define WAIT_ERROR -3  // 等待进程结束时出错

#define ERROR_EXIT(error_code) \
	{\
		error_exit(log_stream,_result,error_code,__FILE__, __LINE__); \
		return; \
	}

struct run_result {
	long cpu_time = 0;
	long real_time = 0;
	long long memory = 0;
	long signal = 0;
	long exit_code = 0;
	long error = 0;
	long result = 0;
	long long output_size = 0;
};

void error_exit(std::ofstream& log_stream, run_result& _result, const int error_code, PRE_INFO_PLACEHOLDER) {
	log_write(LOG_LEVEL_FATAL, __source_file, __line_number, log_stream, { std::to_string(error_code) });
	_result.error = error_code;
	log_close(log_stream);
}

enum {
	SUCCESS = 0, // 没毛病
	CPU_TIME_LIMIT_EXCEEDED = 1, // CPU时间超时
	REAL_TIME_LIMIT_EXCEEDED = 2, // 等待时间超时
	MEMORY_LIMIT_EXCEEDED = 3, // 内存消耗超时
	RUNTIME_ERROR = 4, // 运行错误
	SYSTEM_ERROR = 5, // 系统错误
	OUTPUT_LIMIT_EXCEEDED = 6, // 系统错误
};

void run(child_config& _config, run_result& _result) {
	std::ofstream log_stream = log_open(_KOJ_LOG_PATH);

	timeval start, end;
	gettimeofday(&start, nullptr);

	pid_t child_pid = fork();

	if (child_pid < 0) {
		ERROR_EXIT(FORK_ERROR);
	}
	else if (child_pid == 0) {
		child_process(_config);
	}
	else if (child_pid > 0) {
		// 监听运行时间
		pthread_t tid = 0;
		if (_config.max_real_time > 0) {
			timeout_args killer_args{ child_pid ,_config.max_real_time };
			if (pthread_create(&tid, NULL, p_timeout_killer, (void*)(&killer_args)) != 0) {
				kill_pid(child_pid);
				ERROR_EXIT(PTHREAD_ERROR);
			}
		}

		int status;
		rusage resource_usage;

		// 等待子进程结束
		// 成功返回状态更改的子进程id
		// 错误返回-1
		if (wait4(child_pid, &status, WSTOPPED, &resource_usage) == -1) {
			kill_pid(child_pid);
			ERROR_EXIT(WAIT_ERROR);
		}

		gettimeofday(&end, NULL);
		_result.real_time = (long)(end.tv_sec * 1000 + end.tv_usec / 1000 - start.tv_sec * 1000 - start.tv_usec / 1000);

		if (_config.max_real_time > 0) {
			if (pthread_cancel(tid) != 0) {
			};
		}


		// 这就草了,正常运行没问题,在docker-gcc:9.4镜像中一直是非0
		// 真是日了狗了,死活找不到问题
		if (WIFSIGNALED(status) != 0) {
			_result.signal = WTERMSIG(status);
		}
		// Fuck Fuck Fuck Fuck Fuck Fuck Fuck Fuck Fuck Fuck Fuck Fuck Fuck Fuck Fuck Fuck Fuck Fuck Fuck Fuck Fuck

		if (_result.signal == SIGUSR1) {
			_result.result = SYSTEM_ERROR;
		}
		else {
			_result.exit_code = WEXITSTATUS(status);
			_result.cpu_time = (long)(resource_usage.ru_utime.tv_sec * 1000 +
				resource_usage.ru_utime.tv_usec / 1000);
			_result.memory = resource_usage.ru_maxrss * 1024;

			if (_result.exit_code != 0) {
				_result.result = RUNTIME_ERROR;
			}

			if (_result.signal == SIGSEGV) {
				if (_config.max_memory > 0 && _result.memory > _config.max_memory) {
					_result.result = MEMORY_LIMIT_EXCEEDED;
				}
				else {
					_result.result = RUNTIME_ERROR;
				}
			}
			else {
				if (_result.signal != 0) {
					_result.result = RUNTIME_ERROR;
				}
				if (_config.max_memory > 0 && _result.memory > _config.max_memory) {
					_result.result = MEMORY_LIMIT_EXCEEDED;
				}
				if (_config.max_real_time > 0 && _result.real_time > _config.max_real_time) {
					_result.result = REAL_TIME_LIMIT_EXCEEDED;
				}
				if (_config.max_cpu_time > 0 && _result.cpu_time > _config.max_cpu_time) {
					_result.result = CPU_TIME_LIMIT_EXCEEDED;
				}
				struct stat st;
				if (stat(_KOJ_STDOUT_PATH, &st) == 0) {
					_result.output_size = st.st_size;
					if (_config.max_output_size > 0 && _result.output_size >= _config.max_output_size) {
						_result.result = OUTPUT_LIMIT_EXCEEDED;
					}
				}
			}
		}

		log_close(log_stream);
	}
}

/*
struct child_config {
	int max_cpu_time;
	int max_real_time;
	long max_memory;
	long max_stack;
	int max_process_number;
	long max_output_size;
	bool memory_limit_check_only;
	char* exe_path;
	char** args;
	char** env;
};
*/
int tokenize(char* str, char* block[], std::string&& del = ";") {
	if (str == nullptr) {
		block[0] = nullptr;
		return 0;
	}
#ifdef _KOJ_DEBUG
	std::cout << "raw:" << str << std::endl;
#endif // _KOJ_DEBUG
	std::string s(str);
	std::size_t start = 0;
	std::size_t end = s.find(del);
	std::size_t cnt = 0;
	while (end != std::string::npos) {
		auto substr = s.substr(start, end - start);
		auto data = substr.c_str();
		block[cnt] = new char[substr.size() + 1];
		memcpy(block[cnt], data, substr.size() + 1);
		cnt++;
		start = end + del.size();
		end = s.find(del, start);
	}

	auto substr = s.substr(start, end - start);
	auto data = substr.data();
	block[cnt] = new char[substr.size() + 1];
	memcpy(block[cnt], data, substr.size() + 1);
#ifdef _KOJ_DEBUG
	std::cout << block[cnt] << std::endl;
	if (cnt > 0) {
		std::cout << "0->" << std::hex << reinterpret_cast<void*>(block[cnt - 1]) << std::endl;
		std::cout << "1->" << std::hex << reinterpret_cast<void*>(block[cnt]) << std::endl;
		std::cout << std::dec;
	}
#endif // _KOJ_DEBUG
	block[cnt + 1] = nullptr;
	return cnt;
}

char* get_env(const char* env_name, bool nullable = false) {
	char* env = std::getenv(env_name);
	if (env == nullptr && !nullable) {
		std::cerr << "ERROR: Env Variable \"" << env_name << "\" is not set." << std::endl;
		exit(-1);
	}

	return env;
}

char* trim_start(char*& base, const char*&& pos) {
	for (int i = 0;; i++) {
		char c = base[i];
		char t = pos[i];
		if (!t) {
			return &base[i];
		}
		if (c != t) {
			return nullptr;
		}
	}
	return nullptr;
}


void run(char** envp) {
	child_config config{
		true,//keep_stdin,
		true,//keep_stdout,
		true,//keep_stderr,
		-1,//max_cpu_time,
		-1,//max_real_time,
		-1,//max_memory,
		-1,//max_stack,
		-1,//max_process_number,
		-1,//max_output_size,
		true,//memory_limit_check_only,
		0,//inherit_env_count
	};
	char* env_path = nullptr;
	char* addon = nullptr;
	char* env_s = nullptr;
	for (char** env = envp; *env != nullptr; env++) {
		char* env_str = *env;
		char* this_env = *env;

#ifdef _KOJ_DEBUG
		std::cout << "got env: " << env_str << std::endl;
#endif // _KOJ_DEBUG


		this_env = trim_start(env_str, "KEEP_STDERR=");
		if (this_env != nullptr) {
			config.keep_stderr = atoi(this_env) != 0;
			continue;
		}
		this_env = trim_start(env_str, "KEEP_STDIN=");
		if (this_env != nullptr) {
			config.keep_stdin = atoi(this_env) != 0;
			continue;
		}
		this_env = trim_start(env_str, "KEEP_STDOUT=");
		if (this_env != nullptr) {
			config.keep_stdout = atoi(this_env) != 0;
			continue;
		}
		this_env = trim_start(env_str, "MEMORY_LIMIT_CHECK_ONLY=");
		if (this_env != nullptr) {
			config.memory_limit_check_only = atoi(this_env) != 0;
			continue;
		}
		this_env = trim_start(env_str, "MAX_CPU_TIME=");
		if (this_env != nullptr) {
			config.max_cpu_time = atol(this_env);
			continue;
		}
		this_env = trim_start(env_str, "MAX_REAL_TIME=");
		if (this_env != nullptr) {
			config.max_real_time = atol(this_env);
			continue;
		}
		this_env = trim_start(env_str, "MAX_MEMORY=");
		if (this_env != nullptr) {
			config.max_memory = atoll(this_env);
			continue;
		}
		this_env = trim_start(env_str, "MAX_STACK=");
		if (this_env != nullptr) {
			config.max_stack = atoll(this_env);
			continue;
		}
		this_env = trim_start(env_str, "MAX_PROCESS_NUMBER=");
		if (this_env != nullptr) {
			config.max_process_number = atol(this_env);
			continue;
		}
		this_env = trim_start(env_str, "MAX_OUTPUT_SIZE=");
		if (this_env != nullptr) {
			config.max_output_size = atoll(this_env);
			continue;
		}
		this_env = trim_start(env_str, "PATH=");
		if (this_env != nullptr) {
			env_path = this_env;
			continue;
		}
		this_env = trim_start(env_str, "EXE_PATH=");
		if (this_env != nullptr) {
			config.args[0] = this_env;
			continue;
		}
		this_env = trim_start(env_str, "ARGS=");
		if (this_env != nullptr) {
#ifdef _KOJ_DEBUG
			std::cout << "args:" << std::endl;
#endif
			tokenize(this_env, &config.args[1]);
#ifdef _KOJ_DEBUG
			std::cout << "end args" << std::endl;
#endif
			continue;
		}
		this_env = trim_start(env_str, "ADDON_PATH=");
		if (this_env != nullptr) {
			addon = this_env;
			continue;
		}
		this_env = trim_start(env_str, "ENV=");
		if (this_env != nullptr) {
			env_s = this_env;
			continue;
		}

		config.env[config.inherit_env_count++] = env_str;
	}
	if (config.args[0] == nullptr) {
		std::cerr << "ERROR: Env Variable \"EXE_PATH\" is not set." << std::endl;
		exit(-1);
	}
#ifdef _KOJ_DEBUG
	std::cout << "env:" << std::endl;
#endif
	int addon_length = addon == nullptr ? 0 : strlen(addon);
#ifdef _KOJ_DEBUG
	std::cout << "env path=" << env_path << std::endl;
#endif
	if (env_path == nullptr) {
		std::cerr << "ERROR: Env Variable \"PATH\" is not set." << std::endl;
		exit(-1);
	}
	int env_path_length = strlen(env_path);
	if (addon_length == 0) {
		// 附加的path为空,第一参数为"PATH="固定长5 + 原环境参数
		int real_length = env_path_length + 1 + 5;
		config.env[config.inherit_env_count] = new char[real_length];
		char* path_pt = config.env[config.inherit_env_count];
		memcpy(path_pt, "PATH=", 5);
		// 偏移5格,拷贝环境长度+1,和最后的\0
		memcpy(&path_pt[5], env_path, env_path_length + 1);
	}
	else {
		int length = addon_length + env_path_length;
		int real_length = length + 2 + 5;
		config.env[config.inherit_env_count] = new char[real_length];
		char* path_pt = config.env[config.inherit_env_count];
		memcpy(path_pt, "PATH=", 5);
		// 偏移5格,先拷贝附加参数
		memcpy(&path_pt[5], addon, addon_length);
		// 紧随其后设置冒号
		path_pt[addon_length + 5] = ':';
		// 剩余接上
		memcpy(&path_pt[addon_length + 6], env_path, env_path_length + 1);
	}
#ifdef _KOJ_DEBUG
	std::cout << "path=" << config.env[config.inherit_env_count] << std::endl;
#endif
	tokenize(env_s, &config.env[config.inherit_env_count + 1]);
	config.inherit_env_count++;
#ifdef _KOJ_DEBUG
	std::cout << "end env" << std::endl;
	std::cout << "----------config--------------------" << std::endl;
	std::cout << config;
	std::cout << "----------config--------------------" << std::endl;
#endif

	run_result result;
	std::ofstream log_stream = log_open(_KOJ_LOG_PATH);
	try {
		run(config, result);
	}
	catch (koj_exception& koje) {
		log_write(LOG_LEVEL_FATAL, koje.source_file, koje.line_number, log_stream, { koje.what() });
		log_close(log_stream);

		std::cerr << "error to run with koj exception(" << koje.source_file << "," << koje.line_number << "):" << koje.what() << std::endl;

		std::ofstream status_stream;
		status_stream.open(_KOJ_STATUS_PATH, std::ios::out | std::ios::trunc);
		status_stream << "CPU_TIME=" << result.cpu_time << std::endl;
		status_stream << "REAL_TIME=" << result.real_time << std::endl;
		status_stream << "MEMORY=" << result.memory << std::endl;
		status_stream << "SIGNAL=" << result.signal << std::endl;
		status_stream << "EXIT_CODE=" << result.exit_code << std::endl;
		status_stream << "ERROR=" << result.error << std::endl;
		status_stream << "RESULT=" << result.result << std::endl;
		status_stream << "OUTPUT_SIZE=" << result.output_size << std::endl;
		status_stream << "WRONG=" << koje.what() << std::endl;
		status_stream.close();

		raise(SIGUSR1);
		exit(EXIT_FAILURE);
	}
	/*
struct run_result {
	int cpu_time = 0;
	int real_time = 0;
	long memory = 0;
	int signal = 0;
	int exit_code = 0;
	int error = 0;
	int result = 0;
};
	*/

	std::ofstream status_stream;
	status_stream.open(_KOJ_STATUS_PATH, std::ios::out | std::ios::trunc);
	status_stream << "CPU_TIME=" << result.cpu_time << std::endl;
	status_stream << "REAL_TIME=" << result.real_time << std::endl;
	status_stream << "MEMORY=" << result.memory << std::endl;
	status_stream << "SIGNAL=" << result.signal << std::endl;
	status_stream << "EXIT_CODE=" << result.exit_code << std::endl;
	status_stream << "ERROR=" << result.error << std::endl;
	status_stream << "OUTPUT_SIZE=" << result.output_size << std::endl;
	status_stream << "RESULT=" << result.result << std::endl;
	status_stream.close();
}
int main(int argc, char** argv, char** envp) {
	try {
		run(envp);
	}
	catch (std::exception& exp) {
		std::cerr << "WARNING: RUN ERROR" << std::endl;
		std::cerr << exp.what() << std::endl;
	}

	return 0;
}