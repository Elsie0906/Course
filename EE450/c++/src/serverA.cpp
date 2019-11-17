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

#define HOST "127.0.0.1" 	// local IP address
#define SERVERAPORT "21705"	// port for serverA
#define AWSPORTUDP "23705"	// port for AWS UDP
#define BUFFERSIZE 256


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
	hints.ai_flags = AI_PASSIVE; // assign the address of my local host to the socket structures.

	if ((rv = getaddrinfo(HOST, SERVERAPORT, &hints, &servinfo)) != 0){
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
		return 1;
	}

	// loop through all the results and bind to the first
	for(p = servinfo; p != NULL; p = p->ai_next){
		if ((sockfd = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) == -1){
			perror("serverA: socket");
			continue;
		}
		if (bind(sockfd, p->ai_addr, p->ai_addrlen) == -1){
			close(sockfd);
			perror("serverA: bind");
			continue;
		}
		break;
	}

	if (p == NULL) {
        fprintf(stderr, "serverA: failed to bind socket\n");
        return 2;
    }

	freeaddrinfo(servinfo);	// free the linked list
	printf( "The Server A is up and running using UDP on port %s.\n", SERVERAPORT);

	while(1){
		addr_len = sizeof their_addr;

		char buf[BUFFERSIZE];
		if((numbytes = recvfrom(sockfd, buf, sizeof buf , 0,(struct sockaddr *)&their_addr, &addr_len)) == -1){
			perror("recvfrom");
			exit(1);
		}

		printf("The Server A has received input for finding shortest paths: starting vertex <index> of map<ID>.\n");
		printf("content: %s\n", buf);

		//send back to aws
		char result[19] = "Reply from serverA";
		sendto(sockfd, result, sizeof result , 0,(struct sockaddr *) &their_addr, addr_len);
		printf("The Server A has sent shortest paths to AWS.\n");
	}

	close(sockfd);
	return 0;
}
