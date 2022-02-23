cmake_minimum_required(VERSION 3.16.3)
project(koj CXX)

set(CMAKE_CXX_FLAGS "-g -Wall -Werror -O3 -std=c++11 -pie -fPIC")

# make judger lib
file(GLOB SOURCE "src/*.cc")

include_directories("./include")

add_executable(koj ${SOURCE})

target_link_libraries(koj pthread)
