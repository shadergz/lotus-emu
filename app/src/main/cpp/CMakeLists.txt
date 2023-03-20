cmake_minimum_required(VERSION 3.18.1)

project(lotus)
add_library(emucore MODULE)

set(CMAKE_CXX_STANDARD_REQUIRED 17)
set(CMAKE_CXX_EXTENSIONS ON)

unset(CMAKE_CXX_FLAGS)

target_sources(emucore PRIVATE
        int_jni_vm.cpp
        vm_engine.cpp
        spin_lock.cpp)

find_library(log-lib log)
target_link_libraries(emucore ${log-lib})
target_include_directories(emucore PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})


target_compile_options(emucore PRIVATE -mfloat-abi=hard -mfpu=neon
        -funwind-tables -fstack-protector-strong)

target_compile_options(emucore PRIVATE $<$<CONFIG:Release>:
        -fdata-sections; -ffunction-sections; -fvisibility=hidden;
        -O2; -Wl; -ftree-vectorize; -dead_strip; -s>)

target_compile_options(emucore PRIVATE $<$<CONFIG:Debug>:-g; -O0>)