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


int main()
{
	// set up a stream socket -- Beej
	int sockfd, new_fd;  // listen on sock_fd, new connection on new_fd
	struct addrinfo hints, *servinfo, *p;
	struct sockaddr_storage their_addr;
	int rv;
	int yes=1;
	socklen_t sin_size;

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

	// while(1){
	// 	sin_size = sizeof their_addr;
	// 	new_fd = accept(sockfd, (struct sockaddr *)&their_addr, &sin_size);
	// 	if (new_fd == -1){
	// 		perror("accept");
	// 		continue;
	// 	}

	// 	// get the port of client
	// 	inet_ntop(their_addr.ss_family,get_in_addr((struct sockaddr *)&their_addr),s, sizeof s);

	// 	char function_name[3];
	// 	recv(new_fd, function_name, sizeof function_name, 0);

	// 	int result = 0;
	// 	send(new_fd, (const char *)&result, sizeof(result), 0);

	// 	close(new_fd);
	// }

	close(sockfd);
	return 0;
}
