#include "child.h"
#include "defined.h"

#include <cstdio>

#include <sys/resource.h>
#include <unistd.h>

void setrlimit_check(int&& __resource, const unsigned long int& value) {
	rlimit max_statck{ value,value };
	int return_code = setrlimit(__resource, &max_statck);
	if (return_code != 0) {
		throw SET_RLIMIT_EXCEPTION(__resource, return_code, strerror(errno));
	}
}

void child_process(const struct child_config& config) {
	if (config.max_stack > 0) {
		setrlimit_check(RLIMIT_STACK, config.max_stack);
	}
	if (!config.memory_limit_check_only && config.max_memory > 0) {
		// 需要保留2倍内容调用
		setrlimit_check(RLIMIT_AS, config.max_memory * 2);
	}
	if (config.max_cpu_time > 0) {
		// 这里要设置的是秒数,所以这里向上取整1s
		setrlimit_check(RLIMIT_CPU, (config.max_cpu_time + 1000) / 1000);
	}
	if (config.max_process_number > 0) {
		setrlimit_check(RLIMIT_NPROC, config.max_process_number);
	}
	if (config.max_output_size > 0) {
		setrlimit_check(RLIMIT_FSIZE, config.max_output_size);
	}
	cfile_holder out_file(_KOJ_STDOUT_PATH, "w");
	out_file.redirect_to(stdout);

	execve(config.exe_path, config.args, config.env);
	throw EXECVE_EXCEPTION();
}