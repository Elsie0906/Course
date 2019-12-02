#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <sys/wait.h>
#include <math.h>
#include <ctype.h>
#include <iostream>
#include <set>
#include <iterator>
#include <string>
#include <iomanip>
#include <map>
#include <vector>

#define HOST "127.0.0.1" 	// local IP address
#define SERVERBPORT "22705"	// port for serverB
#define AWSPORTUDP "23705"	// port for AWS UDP
#define BUFFERSIZE 256

using namespace std;

vector<double> calc(float speed, int size, int length[10]){
	vector<double> tp(size, 0);

	for(int i=0; i<size; i++){
		double val = (double)length[i]/speed;
		tp[i] = val;
	}

	return tp;
}

void printCalculation(int vertex[10], vector<double> tp, double tt){
	char dash[50] = "-------------------------------------------------";

	printf("%s\n",dash);
	printf("Destionation\tTt\tTp\tDelay\t\n");
	printf("%s\n",dash);

	int size = tp.size();
	double val = 0.0;

	for(int i=0; i<size; i++){
		val = tt + tp[i];
		printf("%-12d\t%.3f\t%.3f\t%.2f\t\n", vertex[i], tt, tp[i], val);
	}
}

int main()
{
	// set up a datagram socket (server) -- From Beej p.20-23, 36

	int sockfd;
	struct addrinfo hints, *servinfo, *p;
	struct sockaddr_storage their_addr;
	int rv;
	int numbytes;
	socklen_t addr_len;	

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC; // set to AF_INET to force IPv4
	hints.ai_socktype = SOCK_DGRAM;
	hints.ai_flags = AI_PASSIVE; // assign the address of my local host to the socket structures 

	if ((rv = getaddrinfo(HOST, SERVERBPORT, &hints, &servinfo)) != 0){ // Or you can put a specific address in as the first parameter to getaddrinfo()
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
		return 1;
	}

	// loop through all the results and bind to the first
	for(p = servinfo; p != NULL; p = p->ai_next){
		if ((sockfd = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) == -1){
			perror("serverB: socket");
			continue;
		}
		if (bind(sockfd, p->ai_addr, p->ai_addrlen) == -1){
			close(sockfd);
			perror("serverB: bind");
			continue;
		}
		break;
	}

	if (p == NULL) {
        fprintf(stderr, "serverB: failed to bind socket\n");
        return 2;
    }

	freeaddrinfo(servinfo);	// free the linked list	
	printf("The Server B is up and running using UDP on port %s. \n", SERVERBPORT);

	while(1){
		addr_len = sizeof their_addr;

		float msg[2] = {0.00f};
		if((numbytes = recvfrom(sockfd, msg, sizeof msg , 0,(struct sockaddr *)&their_addr, &addr_len)) == -1){
			perror("recvfrom");
			exit(1);
		}
		// printf("prop: %.2f, trans: %.2f\n", msg[0], msg[1]);

		unsigned long int fileSize = 0;
		recvfrom(sockfd, &fileSize, sizeof fileSize , 0,(struct sockaddr *)&their_addr, &addr_len);
		printf("filesize: %lu\n", fileSize);

		int size = 0;
		recvfrom(sockfd, &size, sizeof size , 0,(struct sockaddr *)&their_addr, &addr_len);
		// printf("num of vertex: %d\n", size);

		int vertex[10] = {0};
		int minlength[10] = {0};

		recvfrom(sockfd, vertex, sizeof vertex , 0,(struct sockaddr *)&their_addr, &addr_len);
		recvfrom(sockfd, minlength, sizeof minlength , 0,(struct sockaddr *)&their_addr, &addr_len);

		printf("The Server B has received data for calculation:\n");
		printf("* Propagation speed: %.2f km/s;\n", msg[0]);
		printf("* Transmission speed %.2f Bytes/s;\n", msg[1]);

		for(int i=0; i<size; i++){
			printf("* Path length for destination %d:%d;\n", vertex[i], minlength[i]);
		}

		vector<double> tp = calc(msg[0], size, minlength);

		printf("The Server B has finished the calculation of the delays:\n");

		double tt = fileSize/(msg[1]*8);
		printCalculation(vertex, tp, tt);

		//send back to aws
		double result[10] = {0.00f};

		for(int i=0; i<size; i++){
			result[i] = tp[i];
		}

		sendto(sockfd, result, sizeof result , 0,(struct sockaddr *) &their_addr, addr_len);
		printf("The Server B has finished sending the output to AWS.\n");
	}

	close(sockfd);
	return 0;
}
