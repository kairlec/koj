#pragma once
#ifndef _KOJ_LOGGER_H
#define _KOJ_LOGGER_H

#define LOG_LEVEL_FATAL 0
#define LOG_LEVEL_WARNING 1
#define LOG_LEVEL_INFO 2
#define LOG_LEVEL_DEBUG 3

#include "defined.h"

#include<fstream>
#include<string>

std::ofstream log_open(const std::string& filename);

void log_close(std::ofstream& log_stream);

void log_write(const int& level, PRE_INFO_PLACEHOLDER, std::ofstream& log_stream,const std::initializer_list<std::string>&& args);

#endif //_KOJ_LOGGER_H
