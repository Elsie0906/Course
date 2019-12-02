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
#include <string>

#define HOST "127.0.0.1"
#define AWSPORTTCP 24705	// port for AWS TCP
#define BUFFERSIZE 256

using namespace std;

void printOutData(double tt, int size, int vertex[10], int minlength[10], double tp[10]){

	char dash[50] = "-------------------------------------------------";

	printf("%s\n",dash);
	printf("Destionation\tMin Length\tTt\tTp\tDelay\t\n");
	printf("%s\n",dash);

	double val = 0.0;

	for(int i=0; i<size; i++){
		val = tt + tp[i];
		printf("%-12d\t%-12d%.2f\t%.2f\t%.2f\t\n", vertex[i], minlength[i], tt, tp[i], val);
	}

}

int main(int argc, char *argv[])
{
	// check for cmd line argument

	if(argc != 4){
		printf("Usage: ./client <Map ID> <Source Vertex Index> <File Size>\n");
		return 3;
	}

	// set up a stream socket (client) - Beej's p.34
	// int sockfd;
	// struct addrinfo hints, *servinfo, *p;
	// int rv;

	// memset(&hints, 0, sizeof hints);
	// hints.ai_family = AF_UNSPEC;
	// hints.ai_socktype = SOCK_STREAM;

	// if ((rv = getaddrinfo(HOST, AWSPORTTCP, &hints, &servinfo)) != 0){
	// 	fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
	// 	return 1;
	// }

	// // loop through all the results and connect to the first
	// for(p = servinfo; p != NULL; p = p->ai_next){
	// 	if ((sockfd = socket(p->ai_family, p->ai_socktype,p->ai_protocol)) == -1){
	// 		perror("client: socket");
	// 		continue;
	// 	}

	// 	if (connect(sockfd, p->ai_addr, p->ai_addrlen) == -1){
	// 		close(sockfd);
	// 		perror("client: connect");
	// 		continue;
	// 	}
	// 	break;
	// }

	// if (p == NULL){
	// 	fprintf(stderr, "client: failed to connect\n");
	// 	return 2;
	// }

	// freeaddrinfo(servinfo);

	// create a TCP socket, using dynamic port # - Beej's
	int sockfd = socket(PF_INET, SOCK_STREAM, 0);

	// server-side info
	struct sockaddr_in addrAWS;
	addrAWS.sin_family = AF_INET;
	addrAWS.sin_port = htons(AWSPORTTCP);
	addrAWS.sin_addr.s_addr = inet_addr(HOST);
	memset(addrAWS.sin_zero, '\0', sizeof(addrAWS.sin_zero));

	// connect
	if (connect(sockfd, (struct sockaddr*)&addrAWS, sizeof(addrAWS)) == -1){
		close(sockfd);
		perror("client: connect");
	}

	printf("The client is up and running. \n");

	/* check port number */

	// struct sockaddr_in my_addr;
	// socklen_t addrlen = sizeof(my_addr);

	// int getsock_check=getsockname(sockfd,(struct sockaddr*)&my_addr, &addrlen);
	// //Error checking
	// if (getsock_check== -1) { 
	// 	perror("getsockname"); 
	// 	exit(1);
	// }
	// else{
	// 	printf("port number %d\n", ntohs(my_addr.sin_port));
	// }

	// serialize cmd-line arguments and send to the aws server

	char mapID = *argv[1];
	int startNode = atoi(argv[2]);
	long long int fileSize = atoll(argv[3]);

	send(sockfd, &mapID, sizeof mapID, 0);
	send(sockfd, &startNode, sizeof startNode, 0);
	send(sockfd, &fileSize, sizeof fileSize, 0);

	printf("The client has sent query to AWS using TCP: start vertex %d;map %c; file size %llu\n", startNode, mapID, fileSize);

	// Receive the response from the aws server
	double tt = 0.0;
	recv(sockfd, &tt, sizeof tt, 0);

	int size = 0;
	recv(sockfd, &size, sizeof size, 0);

	int vertex[10] = {0};
	int minlength[10] = {0};
	double tp[10] = {0.0};

	recv(sockfd, vertex, sizeof vertex, 0);
	recv(sockfd, minlength, sizeof minlength, 0);
	recv(sockfd, tp, sizeof tp, 0);

	printf("The client has received results from AWS:\n");

	printOutData(tt, size, vertex, minlength, tp);

	// close socket
	close(sockfd);
	return 0;
}
