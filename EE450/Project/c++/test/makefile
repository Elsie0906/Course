# Makefile Tutorial: 
# https://www.cs.swarthmore.edu/~newhall/unixhelp/howto_makefiles.html

# the compiler: gcc for C program, define as g++ for C++
CC = g++

# compiler flags:
#  -g    adds debugging information to the executable file
#  -Wall turns on most, but not all, compiler warnings
CFLAGS  = -g -Wall

all: Test.o
	$(CC) $(CFLAGS) -o Test Test.o

Test.o: Test.cpp Test.h
	$(CC) $(CFLAGS) -c Test.cpp


clean:
	$(RM) Test *.o *~
