cmake_minimum_required(VERSION 3.18)
set(PROJECT_NAME "nettleweb")
set(PROJECT_VERSION "0.1.0")
project(${PROJECT_NAME} VERSION ${PROJECT_VERSION})

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c17 -O3")
set(CMAKE_C_STANDARD 17)
set(CMAKE_C_STANDARD_REQUIRED true)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++20 -O3")
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED true)

include_directories("../../../ProgramFiles/jdk17/include" "../../../ProgramFiles/jdk17/include/linux")

file(GLOB_RECURSE SOURCE_FILE "src/*.cpp" "src/*.h" "src/*.c")
add_library(${PROJECT_NAME} SHARED ${SOURCE_FILE})
target_link_libraries(${PROJECT_NAME} PUBLIC)
