#pragma once
#ifndef _KOJ_CHILD_H
#define _KOJ_CHILD_H

#include <string.h>
#include "logger.h"

struct child_config {
	int max_cpu_time;
	int max_real_time;
	long max_memory;
	long max_stack;
	int max_process_number;
	long max_output_size;
	bool memory_limit_check_only;
	char* exe_path;
	char* args[64];
	char* env[64];
};

void child_process(const struct child_config& config);

#endif //_KOJ_CHILD_H
