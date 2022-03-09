#include "logger.h"

#include <iomanip>


std::ofstream log_open(const std::string& filename) {
	std::ofstream log_stream;
	log_stream.open(filename, std::ios::out | std::ios::trunc);
	return log_stream;
}

void log_close(std::ofstream& log_stream) {
	log_stream.close();
}

const std::string LOG_LEVEL_NOTE[]{ "FATAL", "WARNING", "INFO", "DEBUG" };

void write_time(std::ofstream& log_stream) {
	auto t = std::time(nullptr);
	auto tm = *std::localtime(&t);
	//log_stream.imbue(std::locale("zh_CN.UTF-8"));
	log_stream << std::put_time(&tm, "%c %Z");
}

void log_write(const int& level, PRE_INFO_PLACEHOLDER, std::ofstream& log_stream, const std::initializer_list<std::string>&& args) {
	log_stream << LOG_LEVEL_NOTE[level] << " [";
	write_time(log_stream);
	log_stream << "] [" << __source_file << ":" << __line_number << "] ";
	for (auto begin = args.begin(); begin != args.end(); begin++) {
		log_stream << *begin;
	}
	log_stream << std::endl;
}
