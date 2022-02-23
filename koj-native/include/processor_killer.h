#pragma once
#ifndef _KOJ_PROCESSOR_KILLER_H
#define _KOJ_PROCESSOR_KILLER_H

#include <sys/types.h>

struct timeout_args {
	const pid_t& pid;
	const int& timeout;
};

const int kill_pid(const pid_t& pid);

void timeout_killer(const timeout_args& timeout);

void* p_timeout_killer(void* timeout_killer_args);

#endif //_KOJ_PROCESSOR
