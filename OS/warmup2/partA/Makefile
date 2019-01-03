warmup2: warmup2.o my402list.o
	gcc -std=gnu99 -o warmup2 -g warmup2.o my402list.o -lpthread -lm

warmup2.o: warmup2.c warmup2.h my402list.h
	gcc -std=gnu99 -g -c -Wall warmup2.c -D_POSIX_PTHREAD_SEMANTICS

my402list.o: my402list.c my402list.h
	gcc -g -c -Wall my402list.c

clean:
	rm -f *.o warmup2
