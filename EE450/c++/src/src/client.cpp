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
#define AWSPORTTCP "24705"	// port for AWS TCP
#define BUFFERSIZE 256

using namespace std;

int main(int argc, char *argv[])
{
	// check for cmd line argument

	if(argc != 4){
		printf("Usage: ./client <Map ID> <Source Vertex Index> <File Size>\n");
		return 3;
	}

	// set up a stream socket (client) - Beej's p.34
	int sockfd;
	struct addrinfo hints, *servinfo, *p;
	int rv;

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_STREAM;

	if ((rv = getaddrinfo(HOST, AWSPORTTCP, &hints, &servinfo)) != 0){
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
		return 1;
	}

	// loop through all the results and connect to the first
	for(p = servinfo; p != NULL; p = p->ai_next){
		if ((sockfd = socket(p->ai_family, p->ai_socktype,p->ai_protocol)) == -1){
			perror("client: socket");
			continue;
		}

		if (connect(sockfd, p->ai_addr, p->ai_addrlen) == -1){
			close(sockfd);
			perror("client: connect");
			continue;
		}
		break;
	}

	if (p == NULL){
		fprintf(stderr, "client: failed to connect\n");
		return 2;
	}

	freeaddrinfo(servinfo);
	printf("The client is up and running. \n");

	// serialize cmd-line arguments

	char* msg = new char[BUFFERSIZE];

	strcat(msg, argv[1]);
	strcat(msg, " ");
	strcat(msg, argv[2]);
	strcat(msg, " ");
	strcat(msg, argv[3]);

	// printf("msg: %s\n", msg);

	send(sockfd, msg, sizeof msg, 0);

	printf("The client has sent query to AWS using TCP: start vertex %s;map %s; file size %s\n", argv[2], argv[1], argv[3]);

	int result = 0;
	recv(sockfd, (char *)&result, sizeof result, 0);

	printf("The client has received results from AWS:\n");

	close(sockfd);
	return 0;
}
