#include "defined.h"

#include <exception>
#include <string>

koj_exception::koj_exception(PRE_INFO_PLACEHOLDER, std::string&& _msg) :std::runtime_error(_msg), source_file(__source_file), line_number(__line_number), msg(_msg) {}
koj_exception::koj_exception(PRE_INFO_PLACEHOLDER, std::string& _msg) :std::runtime_error(_msg), source_file(__source_file), line_number(__line_number), msg(_msg) {}
koj_exception::~koj_exception()noexcept {}
const char* koj_exception::what()const noexcept {
	return msg.c_str();
}

set_rlimit_exception::set_rlimit_exception(PRE_INFO_PLACEHOLDER, int& resources, int& return_code, std::string&& msg) : koj_exception(PRE_INFO_BODY, msg),
__resources(resources), __return_code(return_code), __msg(msg) {}

set_rlimit_exception::~set_rlimit_exception() noexcept {}

const char* set_rlimit_exception::what()const noexcept {
	return ("set_rlimit error(" + __msg + ") when setting __resources=" + std::to_string(__resources) +
		" and return code=" + std::to_string(__return_code)).c_str();
}

dup2_exception::dup2_exception(PRE_INFO_PLACEHOLDER, std::string&& msg) :koj_exception(PRE_INFO_BODY, msg), __msg(msg) {}
dup2_exception::~dup2_exception() {}

const char* dup2_exception::what()const noexcept {
	return ("dup2 error(" + __msg + ")").c_str();
}

execve_exception::execve_exception(PRE_INFO_PLACEHOLDER) :koj_exception(PRE_INFO_BODY) {}
execve_exception::~execve_exception() noexcept {}

const char* execve_exception::what()const noexcept {
	return "";
}

cfile_holder::cfile_holder(const char* const&& _path, const char* const&& mode) : path(_path), file(fopen(_path, mode)) {
	if (file == nullptr) {
		throw DUP2_EXCEPTION(std::string("open ") + mode + " " + _path);
	}
}
cfile_holder::~cfile_holder() noexcept {
	if (file != nullptr) {
		fclose(file);
		file = nullptr;
	}
}
const int cfile_holder::no() const noexcept {
	return fileno(file);
}
void cfile_holder::redirect_to(const cfile_holder& other) const {
	const int result = dup2(no(), other.no());
	if (result == -1) {
		throw DUP2_EXCEPTION(std::string(path) + ",rt=" + std::to_string(result));
	}
}
void cfile_holder::redirect_to(FILE* other) const {
	const int result = dup2(no(), fileno(other));
	if (result == -1) {
		throw DUP2_EXCEPTION(std::string(path) + ",rt=" + std::to_string(result));
	}
}
void cfile_holder::close()noexcept {
	if (file != nullptr) {
		fclose(file);
		file = nullptr;
	}
}
inline cfile_holder& cfile_holder::operator<<(const char*& __s) noexcept {
	if (file != nullptr) {
		fprintf(file, "%s", __s);
	}
	return *this;
}
inline cfile_holder& cfile_holder::operator<<(const std::string& __s)noexcept {
	if (file != nullptr) {
		fprintf(file, "%s", __s.c_str());
	}
	return *this;
}

