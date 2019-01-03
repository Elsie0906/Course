warmup1: warmup1.o my402list.o
	gcc -std=gnu99 -o warmup1 -g warmup1.o my402list.o

listtest: listtest.o my402list.o
	gcc -o listtest -g listtest.o my402list.o

warmup1.o: warmup1.c warmup1.h my402list.h
	gcc -std=gnu99 -g -c -Wall warmup1.c

listtest.o: listtest.c my402list.h
	gcc -g -c -Wall listtest.c

my402list.o: my402list.c my402list.h
	gcc -g -c -Wall my402list.c

clean:
	rm -f *.o listtest warmup1
