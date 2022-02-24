#pragma once
#ifndef _KOJ_DEFINED_H
#define _KOJ_DEFINED_H

#define _KOJ_LOG_PATH "/tmp/koj/log"
#define _KOJ_STDIN_PATH "/tmp/koj/stdin"
#define _KOJ_STDOUT_PATH "/tmp/koj/stdout"
#define _KOJ_STATUS_PATH "/tmp/koj/status"

#include <exception>
#include <string>
#include <stdexcept>

#include <unistd.h>

#define PRE_INFO_PLACEHOLDER const char* __source_file, int __line_number
#define PRE_INFO_BODY __source_file, __line_number

#define KOJ_EXCEPTION(x...) koj_exception(__FILE__,__LINE__,##x)
#define SET_RLIMIT_EXCEPTION(x...) set_rlimit_exception(__FILE__,__LINE__,##x)
#define DUP2_EXCEPTION(x...) dup2_exception(__FILE__,__LINE__,##x)
#define EXECVE_EXCEPTION() execve_exception(__FILE__,__LINE__)

class koj_exception : public std::runtime_error {
public:
	koj_exception(PRE_INFO_PLACEHOLDER, std::string&& _msg = "");
	koj_exception(PRE_INFO_PLACEHOLDER, std::string& _msg);
	~koj_exception()noexcept;
	const char* source_file;
	const int& line_number;
	const std::string& msg;
	const char* what() const noexcept override;
};

class set_rlimit_exception :public koj_exception {
public:
	set_rlimit_exception(PRE_INFO_PLACEHOLDER, int& resources, int& return_code, std::string&& msg = "");
	~set_rlimit_exception()noexcept;
	const char* what() const noexcept override;
private:
	int& __resources;
	int& __return_code;
	std::string& __msg;
};

class dup2_exception :public koj_exception {
public:
	dup2_exception(PRE_INFO_PLACEHOLDER, std::string&& msg = "");
	~dup2_exception();
	const char* what() const noexcept override;
private:
	std::string& __msg;
};

class execve_exception :public koj_exception {
public:
	execve_exception(PRE_INFO_PLACEHOLDER);
	~execve_exception() noexcept;
	const char* what() const noexcept override;
};

class cfile_holder {
public:
	cfile_holder(const char* const&& _path, const char* const&& mode = "w");
	~cfile_holder() noexcept;
	const int no() const noexcept;
	void redirect_to(const cfile_holder& other) const;
	void redirect_to(FILE* other) const;
	void close() noexcept;
	inline cfile_holder& operator<<(const char*& __s) noexcept;
	inline cfile_holder& operator<<(const std::string& __s) noexcept;
	const char* const path;
	FILE*&& file;
};


#endif // _KOJ_DEFINED_H