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
#include <iostream>
#include "aws.h"

#define HOST "127.0.0.1"	// local IP address
#define AWSPORTUDP "23705"	// port for AWS UDP
#define AWSPORTTCP "24705"	// port for AWS TCP
#define SERVERAPORT 21705	// port for serverA
#define SERVERBPORT 22705	// port for serverB
#define BUFFERSIZE 256

using namespace std;

void *get_in_addr(struct sockaddr *sa)
{
    if (sa->sa_family == AF_INET) {
        return &(((struct sockaddr_in*)sa)->sin_addr);
    }
    return &(((struct sockaddr_in6*)sa)->sin6_addr);
}

void printDataFromA(int size, int vertex[10], int minlength[10], MinPath *result){

	char dash[50] = "-------------------------------------------------";

	vector<int> vertices(size, 0);
	vector<int> minLength(size, 0);	

	printf("The AWS has received shortest path from server A:\n");
	printf("%s\n",dash);
	printf("Destionation\tMin Length\t\n");
	printf("%s\n",dash);
	for(int i=0; i<size; i++){
		vertices[i] = vertex[i];
		minLength[i] = minlength[i];
		printf("%-12d\t%-12d\n", vertex[i], minlength[i]);
	}	

	result->vertex = vertices;
	result->minlength = minLength;
}

void printDataFromB(int vertex[10], double tp[10], int size, double tt, MinPath *buf){
	char dash[50] = "-------------------------------------------------";

	vector<double> Tp(size, 0.00);

	printf("%s\n",dash);
	printf("Destionation\tTt\tTp\tDelay\t\n");
	printf("%s\n",dash);

	double val = 0.0;

	for(int i=0; i<size; i++){
		val = tt + tp[i];
		Tp[i] = tp[i];
		printf("%-12d\t%.3f\t%.3f\t%.2f\t\n", vertex[i], tt, tp[i], val);
	}

	buf->tp = Tp;
}

int createUDPSocket(){
	// set up UDP - from Beej

	int AWSSocket_UDP;
	struct addrinfo hints, *servinfo, *p;
	int rv;

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_DGRAM;

	if ((rv = getaddrinfo(HOST, AWSPORTUDP, &hints, &servinfo)) != 0){
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
		return 1;
	}	

	// loop through all the results and bind to the first
	for(p = servinfo; p != NULL; p = p->ai_next){
		if ((AWSSocket_UDP = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) == -1){
			perror("talkerA: socket");
			continue;
		}
		break;
	}

	if (p == NULL) {
        fprintf(stderr, "talkerA: failed to bind socket\n");
        return 2;
    }

    return AWSSocket_UDP;
}

int talkWithServerA(int AWSSocket_UDP,char mapID, int startNode, MinPath *result){	

	struct sockaddr_in addrServerA;
	socklen_t addrServerA_length = sizeof(addrServerA);
	int numbytes;

	memset(&addrServerA, 0, sizeof(addrServerA));
	addrServerA.sin_family = AF_INET;
	addrServerA.sin_port = htons(SERVERAPORT);
	addrServerA.sin_addr.s_addr = inet_addr(HOST);


    // serialize data

    char* msg = new char[BUFFERSIZE];
    // memset(msg, 0, sizeof msg);

    char *tmp = new char[2];
    tmp[0] = mapID;

    strcat(msg, tmp);
    strcat(msg, " ");

    string str = to_string(startNode);
    strcat(msg, str.c_str());

    if((numbytes = sendto(AWSSocket_UDP, msg, sizeof msg, 0, (struct sockaddr*)&addrServerA, addrServerA_length)) == -1){
    	perror("talkerA: sendto");
    	exit(1);
    }
    printf("The AWS has sent map ID and starting vertex to server A using UDP over port %s\n", AWSPORTUDP);

    float retVal[2] = {0.00f};
    recvfrom(AWSSocket_UDP, retVal, sizeof retVal, 0 , (struct sockaddr*)&addrServerA, &addrServerA_length);
    // printf("prop: %.2f, trans: %.2f\n", retVal[0], retVal[1]);

    result->propagation = retVal[0];
    result->transmission = retVal[1];

    int size = 0;
    recvfrom(AWSSocket_UDP, &size, sizeof size, 0 , (struct sockaddr*)&addrServerA, &addrServerA_length);
    // printf("num of vertex: %d\n", size);

    int vertex[10] = {0};
	int minlength[10] = {0};

	recvfrom(AWSSocket_UDP, vertex, sizeof vertex, 0 , (struct sockaddr*)&addrServerA, &addrServerA_length);
	recvfrom(AWSSocket_UDP, minlength, sizeof minlength, 0 , (struct sockaddr*)&addrServerA, &addrServerA_length);	

	printDataFromA(size, vertex, minlength, result);

	// freeaddrinfo(servinfo);

    return 0;
}

int talkWithServerB(int AWSSocket_UDP,MinPath *buf, unsigned long int fileSize){	

	struct sockaddr_in addrServerB;
	socklen_t addrServerB_length = sizeof(addrServerB);
	int numbytes;

	memset(&addrServerB, 0, sizeof(addrServerB));
	addrServerB.sin_family = AF_INET;
	addrServerB.sin_port = htons(SERVERBPORT);
	addrServerB.sin_addr.s_addr = inet_addr(HOST);

    float msg[2] = {0.00f};
	msg[0] = buf->propagation;
	msg[1] = buf->transmission;

    if((numbytes = sendto(AWSSocket_UDP, msg, sizeof msg, 0, (struct sockaddr*)&addrServerB, addrServerB_length)) == -1){
    	perror("talkerB: sendto");
    	exit(1);
    }

    sendto(AWSSocket_UDP, &fileSize, sizeof fileSize, 0, (struct sockaddr*)&addrServerB, addrServerB_length);

    int size = buf->vertex.size();
    sendto(AWSSocket_UDP, &size, sizeof size, 0, (struct sockaddr*)&addrServerB, addrServerB_length);

    int vertex[10] = {0};
    int minlength[10] = {0};

	for(int i=0; i<size; i++){
		vertex[i] = buf->vertex[i];
		minlength[i] = buf->minlength[i];
		// printf("node: %d, length: %d\n", buf->vertex[i], buf->minlength[i]);
	}
	sendto(AWSSocket_UDP, vertex, sizeof vertex, 0, (struct sockaddr*)&addrServerB, addrServerB_length);
	sendto(AWSSocket_UDP, minlength, sizeof minlength, 0, (struct sockaddr*)&addrServerB, addrServerB_length);
    printf("The AWS has sent path length, propagation speed and transmission speed to server B using UDP over port %s.\n", AWSPORTUDP);

    double result[10] = {0.00f};
    recvfrom(AWSSocket_UDP, result, sizeof result, 0 , (struct sockaddr*)&addrServerB, &addrServerB_length);
    printf("The AWS has received delays from server B:\n");
    
    double tt = fileSize/(msg[1]*8);
    printDataFromB(vertex, result, size, tt, buf);

    // freeaddrinfo(servinfo);
	
    return 0;
}

void parseInfo(char *buf, char &mapID, int &srcNode, unsigned long int &fileSize){
	char* delim = " ";
	char* token = strtok(buf, delim);
	int i=0;

	while(token != NULL){
		switch(i){
			case 0:{
				mapID = *token;
				// printf("token: %c\n", mapID);
				break;
			}
			case 1:{
				srcNode = atoi(token);
				// printf("token: %d\n", srcNode);
				break;
			}
			case 2:{
				fileSize = atol(token);
				// printf("token: %s\n", token);
				break;
			}
			default:
				printf("aws server: too many fields\n");
				break;
		}

		token = strtok(NULL, delim);
		i++;
	} 
}

int main()
{
	// set up a stream socket -- Beej
	int sockfd, new_fd;  // listen on sock_fd, new connection on new_fd
	struct addrinfo hints, *servinfo, *p;
	struct sockaddr_storage their_addr;
	int rv;
	int yes=1;
	socklen_t sin_size;
	char s[INET6_ADDRSTRLEN];

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = AI_PASSIVE;

	if ((rv = getaddrinfo(HOST, AWSPORTTCP, &hints, &servinfo)) != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
        return 1;
	}

	// loop through all the results and bind to the first
	for(p = servinfo; p != NULL; p = p->ai_next){
		if ((sockfd = socket(p->ai_family, p->ai_socktype,p->ai_protocol)) == -1){
			perror("aws server: socket");
			continue;
		}
		if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes,sizeof(int)) == -1){
			perror("setsockopt");
			exit(1);
		}
		if (bind(sockfd, p->ai_addr, p->ai_addrlen) == -1){
			close(sockfd);
			perror("aws server: bind");
			continue;
		}
		break;
	}

	if (p == NULL){
		fprintf(stderr, "aws server: failed to bind\n");
		return 2;
	}

	freeaddrinfo(servinfo); // all done with this structure

	if (listen(sockfd, 5) == -1){
		perror("listen");
		exit(1);
	}

	printf("The AWS is up and running. \n");

	int udp = createUDPSocket();

	while(1){
		sin_size = sizeof their_addr;
		new_fd = accept(sockfd, (struct sockaddr *)&their_addr, &sin_size);
		if (new_fd == -1){
			perror("accept");
			continue;
		}

		// get the port of client
		inet_ntop(their_addr.ss_family,get_in_addr((struct sockaddr *)&their_addr),s, sizeof s);
		// printf("server: got connection from %s\n", s);

		// deserialize data

		char mapID;
		int srcNode = -1;
		unsigned long int fileSize = 0;

		recv(new_fd, &mapID, sizeof mapID, 0);
		recv(new_fd, &srcNode, sizeof srcNode, 0);
		recv(new_fd, &fileSize, sizeof fileSize, 0);

		printf("The AWS has received map ID %c, start vertex %d and file size %lu from the client using TCP over port %s\n", mapID, srcNode, fileSize, AWSPORTTCP);
		
		MinPath *result = new MinPath();
		talkWithServerA(udp, mapID, srcNode, result);
		talkWithServerB(udp, result, fileSize);

		double tt = 0.00f;
		tt = fileSize/(result->transmission*8);
		send(new_fd, &tt, sizeof(tt), 0);

		int size = result->vertex.size();
		send(new_fd, &size, sizeof(size), 0);

		int vertex[10] = {0};
		int minlength[10] = {0};
		double tp[10] = {0.00f};

		for(int i=0; i<size; i++){
			vertex[i] = result->vertex[i];
			minlength[i] = result->minlength[i];
			tp[i] = result->tp[i];
		}

		send(new_fd, vertex, sizeof(vertex), 0);
		send(new_fd, minlength, sizeof(minlength), 0);
		send(new_fd, tp, sizeof(tp), 0);

		printf("The AWS has sent calculated delay to client using TCP over port %s.\n", AWSPORTTCP);

		close(new_fd);
	}

	close(udp);
	close(sockfd);
	return 0;
}
