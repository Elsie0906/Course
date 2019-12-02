# Makefile Tutorial: 
# https://www.cs.swarthmore.edu/~newhall/unixhelp/howto_makefiles.html

# the compiler: gcc for C program, define as g++ for C++
CC = g++

# compiler flags:
#  -g    adds debugging information to the executable file
#  -Wall turns on most, but not all, compiler warnings
CFLAGS  = -g -Wall

all: aws.o client.o serverA.o serverB.o
	$(CC) $(CFLAGS) -o aws aws.o
	$(CC) $(CFLAGS) -o client client.o
	$(CC) $(CFLAGS) -o serverA serverA.o
	$(CC) $(CFLAGS) -o serverB serverB.o

aws.o: aws.cpp aws.h
	$(CC) $(CFLAGS) -c aws.cpp

serverA.o: serverA.cpp serverA.h
	$(CC) $(CFLAGS) -c serverA.cpp

serverB.o: serverB.cpp
	$(CC) $(CFLAGS) -c serverB.cpp

client.o: client.cpp
	$(CC) $(CFLAGS) -c client.cpp

.PHONY: serverA serverB aws

aws:
	./aws

serverA:
	./serverA

serverB:
	./serverB


clean:
	$(RM) serverA serverB aws client *.o *~
