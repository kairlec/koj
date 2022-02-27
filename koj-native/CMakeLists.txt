cmake_minimum_required(VERSION 3.13.4)
project(koj CXX)

set(CMAKE_CXX_FLAGS "-g -Wall -Werror -O3 -std=c++11 -pie -fPIC")

# make judger lib
file(GLOB SOURCE "src/*.cc")

include_directories("./include")

add_executable(koj ${SOURCE})

target_link_libraries(koj pthread)
