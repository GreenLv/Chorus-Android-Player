cmake_minimum_required(VERSION 3.22.1)
project(chorus_teki_jni C)

add_library(
        chorus-teki-jni
        SHARED
        chorus_teki_jni.c
        chorus_teki_backend_stub.c)

target_include_directories(chorus-teki-jni PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/include)
target_compile_options(chorus-teki-jni PRIVATE -Wall -Wextra -Werror)
