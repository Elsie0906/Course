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

#define HOST "127.0.0.1" 	// local IP address
#define SERVERBPORT "22705"	// port for serverB
#define AWSPORTUDP "23705"	// port for AWS UDP


int main()
{
	// set up a datagram socket (server) -- From Beej p.20-23, 36

	int sockfd;
	struct addrinfo hints, *servinfo, *p;
	int rv;

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
	printf("The Server B is up and running using UDP on port %s. \n", SERVERBPORT);

	close(sockfd);
	return 0;
}
