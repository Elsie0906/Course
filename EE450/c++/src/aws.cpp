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

#define HOST "127.0.0.1"	// local IP address
#define AWSPORTUDP "23705"	// port for AWS UDP
#define AWSPORTTCP "24705"	// port for AWS TCP
#define SERVERAPORT "21705"	// port for serverA
#define SERVERBPORT "22705"	// port for serverB
#define BUFFERSIZE 256

void *get_in_addr(struct sockaddr *sa)
{
    if (sa->sa_family == AF_INET) {
        return &(((struct sockaddr_in*)sa)->sin_addr);
    }
    return &(((struct sockaddr_in6*)sa)->sin6_addr);
}

int talkWithServerA(char *buf){
	// set up UDP - from Beej
	int AWSSocket_UDP;
	struct addrinfo hints, *servinfo, *p;
	int rv;
	int numbytes;

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_DGRAM;

	if ((rv = getaddrinfo(HOST, SERVERAPORT, &hints, &servinfo)) != 0){
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

    char function_name[20] = "Message for serverA";
    if((numbytes = sendto(AWSSocket_UDP, function_name, sizeof function_name, 0, p->ai_addr,p->ai_addrlen)) == -1){
    	perror("talkerA: sendto");
    	exit(1);
    }
    printf("The AWS has sent map ID and starting vertex to server A using UDP over port <port number>\n");

    char result[BUFFERSIZE];
    recvfrom(AWSSocket_UDP, result, sizeof result, 0 , NULL, NULL);
    printf("The AWS has received shortest path from server A:\n");
    printf("%s\n", result);

	freeaddrinfo(servinfo);
    close(AWSSocket_UDP);
    return 0;
}

int talkWithServerB(char *buf){
	// set up UDP - from Beej
	int AWSSocket_UDP;
	struct addrinfo hints, *servinfo, *p;
	int rv;
	int numbytes;

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_DGRAM;

	if ((rv = getaddrinfo(HOST, SERVERBPORT, &hints, &servinfo)) != 0){
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
		return 1;
	}

	// loop through all the results and bind to the first
	for(p = servinfo; p != NULL; p = p->ai_next){
		if ((AWSSocket_UDP = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) == -1){
			perror("talkerB: socket");
			continue;
		}
		break;
	}	

	if (p == NULL) {
        fprintf(stderr, "talkerB: failed to bind socket\n");
        return 2;
    }	

    char function_name[20] = "Message for serverB";
    if((numbytes = sendto(AWSSocket_UDP, function_name, sizeof function_name, 0, p->ai_addr,p->ai_addrlen)) == -1){
    	perror("talkerB: sendto");
    	exit(1);
    }
    printf("The AWS has sent path length, propagation speed and transmission speed to server B using UDP over port <port number>.\n");

    char result[BUFFERSIZE];
    recvfrom(AWSSocket_UDP, result, sizeof result, 0 , NULL, NULL);
    printf("The AWS has received delays from server B:\n");
    printf("%s\n", result);

    freeaddrinfo(servinfo);
    close(AWSSocket_UDP);	
    return 0;
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

		char buf[BUFFERSIZE];
		recv(new_fd, buf, sizeof buf, 0);
		printf("The AWS has received map ID <map ID>, start vertex <vertex ID> and file size <file size> from the client using TCP over port <port number>\n");
		printf("content: %s\n", buf);

		talkWithServerA(buf);
		talkWithServerB(buf);


		int result = 0;
		send(new_fd, (const char *)&result, sizeof(result), 0);
		printf("The AWS has sent calculated delay to client using TCP over port <port number>.\n");

		close(new_fd);
	}

	close(sockfd);
	return 0;
}
