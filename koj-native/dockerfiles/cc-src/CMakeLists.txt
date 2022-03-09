cmake_minimum_required(VERSION 3.13.4)
project(koj CXX)

set(CMAKE_CXX_FLAGS "-g -Wall -Werror -O3 -std=c++11 -pie -fPIC")

IF(DEFINED _KOJ_DEBUG)
    ADD_DEFINITIONS(-D_KOJ_DEBUG=1)
ENDIF()

file(GLOB SOURCE "src/*.cc")

include_directories("./include")

add_executable(koj ${SOURCE})

target_link_libraries(koj pthread)
