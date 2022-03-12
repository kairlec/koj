#include "child.h"
#include "defined.h"

#include <cstdio>

#include <sys/resource.h>
#include <unistd.h>
#include <climits>

#ifdef _KOJ_DEBUG
#include <iostream>
#endif // _KOJ_DEBUG


child_config::~child_config() {
	for (int i = 1; i < MAX_ARRAY_LENGTH; i++) {
		if (args[i] == nullptr) {
			break;
		}
		delete[] args[i];
	}
	for (int i = inherit_env_count; i < MAX_ARRAY_LENGTH; i++) {
		if (env[i] == nullptr) {
			break;
		}
		delete[] env[i];
	}
}

void setrlimit_check(int&& __resource, const unsigned long int& value) {
	rlimit max_statck{ value,value };
	int return_code = setrlimit(__resource, &max_statck);
	if (return_code != 0) {
		throw SET_RLIMIT_EXCEPTION(__resource, return_code, strerror(errno));
	}
}

void child_process(const struct child_config& config) {
	if (config.max_stack > 0 && config.max_stack != LLONG_MAX) {
		setrlimit_check(RLIMIT_STACK, config.max_stack);
	}
	if (!config.memory_limit_check_only && config.max_memory > 0 && config.max_memory != LLONG_MAX) {
		// 需要保留2倍内容调用
		setrlimit_check(RLIMIT_AS, config.max_memory * 2);
	}
	if (config.max_cpu_time > 0 && config.max_cpu_time != LONG_MAX) {
		// 这里要设置的是秒数,所以这里向上取整1s
		setrlimit_check(RLIMIT_CPU, (config.max_cpu_time + 1000) / 1000);
	}
	if (config.max_process_number > 0 && config.max_process_number != LONG_MAX) {
		setrlimit_check(RLIMIT_NPROC, config.max_process_number);
	}
	if (config.max_output_size > 0 && config.max_output_size != LLONG_MAX) {
		setrlimit_check(RLIMIT_FSIZE, config.max_output_size);
	}

	if (!config.keep_stdin) {
#ifdef _KOJ_DEBUG
		std::cout << "redirect stdin" << std::endl;
#endif // _KOJ_DEBUG
		cfile_holder in_file(_KOJ_STDIN_PATH, "r");
		in_file.redirect_to(stdin);
	}

	if (!config.keep_stderr) {
#ifdef _KOJ_DEBUG
		std::cout << "redirect stderr" << std::endl;
#endif // _KOJ_DEBUG
		cfile_holder err_file(_KOJ_STDERR_PATH, "w");
		err_file.redirect_to(stderr);
	}

	if (!config.keep_stdout) {
#ifdef _KOJ_DEBUG
		std::cout << "redirect stdout" << std::endl;
#endif // _KOJ_DEBUG
		cfile_holder out_file(_KOJ_STDOUT_PATH, "w");
		out_file.redirect_to(stdout);
	}

	execve(config.args[0], config.args, config.env);
	throw EXECVE_EXCEPTION();
}

#ifdef _KOJ_DEBUG
std::ostream& operator<<(std::ostream& _stream, const child_config& config)noexcept {
	_stream << "keep_stdin= " << config.keep_stdin << std::endl;
	_stream << "keep_stdout= " << config.keep_stdout << std::endl;
	_stream << "keep_stderr= " << config.keep_stderr << std::endl;
	_stream << "max_cpu_time= " << config.max_cpu_time << std::endl;
	_stream << "max_real_time= " << config.max_real_time << std::endl;
	_stream << "max_memory= " << config.max_memory << std::endl;
	_stream << "max_stack= " << config.max_stack << std::endl;
	_stream << "max_process_number= " << config.max_process_number << std::endl;
	_stream << "max_output_size= " << config.max_output_size << std::endl;
	_stream << "memory_limit_check_only= " << config.memory_limit_check_only << std::endl;
	std::cout << "config.args:" << std::endl;
	for (int i = 0; i < MAX_ARRAY_LENGTH; i++) {
		if (config.args[i] == nullptr) {
			break;
		}
		std::cout << config.args[i] << std::endl;
	}
	std::cout << "config.env:" << std::endl;
	for (int i = 0; i < MAX_ARRAY_LENGTH; i++) {
		if (config.env[i] == nullptr) {
			break;
		}
		std::cout << config.env[i] << std::endl;
	}
	return _stream;
}
#endif // _KOJ_DEBUG
