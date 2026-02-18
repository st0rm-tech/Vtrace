cmake_minimum_required(VERSION 3.22.1)

project("vtrace")

add_library(
        vtrace
        SHARED
        native-lib.cpp)

find_library(
        log-lib
        log)

target_link_libraries(
        vtrace
        ${log-lib})
