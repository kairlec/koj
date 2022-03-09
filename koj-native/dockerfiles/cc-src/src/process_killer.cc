#include "processor_killer.h"

#include <csignal>

#include <unistd.h>
#include <pthread.h>


const int kill_pid(const pid_t& pid) {
	return kill(pid, SIGKILL);
}

void timeout_killer(const timeout_args& timeout) {
	if (pthread_detach(pthread_self()) != 0) {
		kill_pid(timeout.pid);
	}
	if (sleep((timeout.timeout + 1000) / 1000) != 0) {
		kill_pid(timeout.pid);
	}
	kill_pid(timeout.pid);
}

void* p_timeout_killer(void* timeout_ptr) {
	timeout_killer(*(timeout_args*)timeout_ptr);
	return nullptr;
}
