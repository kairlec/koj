#pragma once
#ifndef _KOJ_CHILD_H
#define _KOJ_CHILD_H

#define MAX_ARRAY_LENGTH 64

#include <string.h>
#include "logger.h"

struct child_config {
	bool keep_stdin;
	bool keep_stdout;
	long max_cpu_time;
	long max_real_time;
	long long max_memory;
	long long max_stack;
	long max_process_number;
	long long max_output_size;
	bool memory_limit_check_only;
	char* args[MAX_ARRAY_LENGTH];
	char* env[MAX_ARRAY_LENGTH];
	~child_config();
};

void child_process(const struct child_config& config);

#endif //_KOJ_CHILD_H
