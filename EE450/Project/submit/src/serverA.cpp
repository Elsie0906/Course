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
#include <ctype.h>
#include <iostream>
#include <set>
#include <iterator>
#include <string>
#include <iomanip>
#include <map>
#include <vector>
#include <limits.h>
#include "serverA.h"

#define HOST "127.0.0.1" 	// local IP address
#define SERVERAPORT "21705"	// port for serverA
#define AWSPORTUDP "23705"	// port for AWS UDP
#define BUFFERSIZE 256

void parseData(char* buf, set<int> *vertices, list<Edge> *edges){
	const char delim[2] = " ";

	char* token = strtok(buf, delim);
	int i=0;

	Edge *edge = new Edge();

	while(token != NULL){
		switch(i){
			case 0:{
				vertices->insert(atoi(token));
				edge->node1 = atoi(token);
				// printf("node1: %d", edge->node1);
				break;				
			}
			case 1:{
				vertices->insert(atoi(token));
				edge->node2 = atoi(token);
				// printf("node2: %d", edge->node2);
				break;				
			}
			case 2:{
				edge->distance = atoi(token);
				// printf("distance: %d\n", edge->distance);
				break;
			}
			default:{
				printf("too many fields\n");
				exit(1);
				break;
			}
		}
		
		token = strtok(NULL, delim);
		i++;
	}

	edges->push_back(*edge);

	// printf("numOfVertices: %lu\n", vertices.size());
}

void readFile(list<City> *clist){
	FILE *fp;
	char *line_buf = NULL;
  	size_t line_buf_size = 0;
  	ssize_t line_size;

	fp = fopen("map.txt", "r");
	if(fp == NULL){
		fprintf(stderr,"map.txt does not exist\n");
		exit(1);
	}

	

	line_size = getline(&line_buf, &line_buf_size, fp);

	while(line_size >= 0){
		City *city = new City();
		int numOfEdges = 0;
		set<int> *vertices = new set<int>();

		city->mapID = line_buf[0];
		// printf("city: %c, line: %d\n", city->mapID, __LINE__);
		// printf("city: %c\n", city->mapID);
		char* end;

		line_size = getline(&line_buf, &line_buf_size, fp);
		city->propagation = strtod(line_buf, &end);
		// printf("propagation speend: %f\n", city->propagation);

		line_size = getline(&line_buf, &line_buf_size, fp);
		city->transmission = strtod(line_buf, &end);
		// printf("transmission speend: %f\n", city->transmission);

		list<Edge> *edges = new list<Edge>();

		do{
			line_size = getline(&line_buf, &line_buf_size, fp);
			if(line_size >= 0 && isalpha(line_buf[0]) == 0){
				parseData(line_buf, vertices, edges);
				numOfEdges++;
			}
		}while(line_size >= 0 && isalpha(line_buf[0]) == 0);

		city->vertices = *vertices;
		city->edges = *edges;
		city->numOfEdges = numOfEdges;
		city->numOfVertices = vertices->size();

		// printf("numOfEdges: %lu\n", city->edges.size());	
		// printf("numOfVertices: %lu\n", city->vertices.size());

		clist->push_back(*city);	
	}

	// printf("numOfCity: %lu\n", clist->size());

	free(line_buf);
	line_buf = NULL;
	fclose(fp);

}

void mapConstrustion(list<City> *clist){
	char dash[50] = "-------------------------------------------------";

	printf("The Server A has constructed a list of %zu maps:\n", clist->size());
	printf("%s\n",dash);
	printf("Map ID\tNum Vertices\tNum Edges\t\n");
	printf("%s\n",dash);

	list<City>::iterator ptr;
	for(ptr = clist->begin(); ptr != clist->end();ptr++){
		printf("%-6c\t%-12d\t%-12d\n",ptr->mapID,ptr->numOfVertices, ptr->numOfEdges);
	}

	printf("%s\n",dash);
}

int mapping(char city, list<City> *clist, map<int, int> &map1, map<int, int> &map2, MinPath *result){

	list<City>::iterator ptr;

	for(ptr = clist->begin(); ptr != clist->end();ptr++){

		if(ptr->mapID != city)
			continue;
		else{
			result->propagation = ptr->propagation;
			result->transmission = ptr->transmission;
			set<int> vertices = ptr->vertices;
			set<int>::iterator it;
			int idx = 0;

			for(it = vertices.begin();it != vertices.end(); it++){
				// printf("ori idx: %d, new idx: %d\n", *it, idx);
				map1[*it] = idx;
				map2[idx] = *it;
				idx++;
			}
			return vertices.size();
		}
	}

	return -1;
	printf("cannot find proper mapping\n");
}

int minDistance(vector<int> dist, vector<bool> visited){
	int min = INT_MAX;
	int min_index;
	int size = dist.size();

	for(int v=0; v < size; v++){
		if(visited[v] == false && dist[v] <=min){
			min = dist[v];
			min_index = v;
		}
	}

	return min_index;
}

vector<int> findShortestPath(vector<vector<int> > matrix, int start){
	// using Dijkstra's algorithm

	int size = matrix.size();

	vector<int> dist(size, INT_MAX);
	vector<bool> visited(size, false);

	dist[start] = 0;

	for(int count =0; count < size -1; count++){
		int u = minDistance(dist, visited);

		visited[u] = true;

		for(int v=0; v< size; v++){
			if(!visited[v] && matrix[u][v] && dist[u] != INT_MAX && dist[u] + matrix[u][v] <dist[v])
				dist[v] = dist[u] + matrix[u][v];
		}
	}

	return dist;
}

vector<vector<int> > buildGraph(list<City> *clist, char city, map<int, int> map1, int numOfVertices){
	
	// initialize
	vector<vector<int> > matrix(numOfVertices, vector<int> (numOfVertices,0));
		
	list<City>::iterator ptr;

	for(ptr = clist->begin(); ptr != clist->end();ptr++){
		if(ptr->mapID != city)
			continue;
		else{
			list<Edge> edges = ptr->edges;
			list<Edge>::iterator it;

			for(it = edges.begin(); it != edges.end(); it++){
				int idx1 = map1.find(it->node1)->second;
				int idx2 = map1.find(it->node2)->second;
				matrix[idx1][idx2] = it->distance; 
				matrix[idx2][idx1] = it->distance;
				// printf("node1: %d, node2:%d, dist:%d\n", idx1, idx2, it->distance);
			}
			break;
		}	
	}

	return matrix;	
}

void printSolution(int startNode, vector<int> dist, map<int, int> map2, MinPath *result){
	char dash[50] = "-------------------------------------------------";

	int size = dist.size();

	vector<int> vertex(size-1, 0); // remove source node
	vector<int> minlength(size-1, 0); // remove source node

	printf("The Server A has identified the following shortest paths:\n");
	printf("%s\n",dash);
	printf("Destionation\tMin Length\t\n");
	printf("%s\n",dash);

	int tmp = 0;

	for(int i=0; i<size; i++){
		if(startNode == map2.find(i)->second) continue; // remove source node
		vertex[tmp] = map2.find(i)->second;
		minlength[tmp] = dist[i];
		printf("%-12d\t%-12d\n", map2.find(i)->second, dist[i]);
		tmp++;
	}

	result->vertex = vertex;
	result->minlength = minlength;
}

void pathFinding(char city, int startNode, list<City> *clist, MinPath *result){

	map<int, int> map1; // pair(ori_idx, new_idx)
	map<int, int> map2; // pair(new_idx, ori_idx)

	int numOfVertices = mapping(city, clist, map1, map2, result);

	map<int, int>::iterator it;
	it = map1.find(startNode);
	if(it == map1.end()){
		printf("key-value pair not exist in map1\n");
		exit(1);
	}

	int idx = map1.find(startNode)->second;

	vector<vector<int> > matrix = buildGraph(clist, city, map1, numOfVertices);

	vector<int> dist = findShortestPath(matrix, idx);

	printSolution(startNode, dist, map2, result);

}

void processData(char *buf, char &mapID, int &srcNode){
	const char delim[2] = " ";
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
			default:
				printf("serverA: too many fields\n");
				break;
		}

		token = strtok(NULL, delim);
		i++;
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

	// Read from the file "map.txt" and construct a list of graphs
	list<City> *clist = new list<City>();
	readFile(clist);
	mapConstrustion(clist);

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

	while(1){

		addr_len = sizeof their_addr;

		char mapID;
		int srcNode;

		if((numbytes = recvfrom(sockfd, &mapID, sizeof mapID , 0,(struct sockaddr *)&their_addr, &addr_len)) == -1){
			perror("serverA: failed to recv mapID");
			exit(1);
		}

		if((numbytes = recvfrom(sockfd, &srcNode, sizeof srcNode , 0,(struct sockaddr *)&their_addr, &addr_len)) == -1){
			perror("serverA: failed to recv source vertex index");
			exit(1);
		}
				
		printf("The Server A has received input for finding shortest paths: starting vertex %d of map %c.\n", srcNode, mapID);

		MinPath *result = new MinPath();
		double retVal[2] = {0.0};

		// Use Dijkstra algorithm tp find the path of min length from a given start vertex to all other nodes
		pathFinding(mapID, srcNode, clist, result);

		retVal[0] = result->propagation;
		retVal[1] = result->transmission;

		//send back to aws

		int numbytes;

    	if((numbytes = sendto(sockfd, retVal, sizeof retVal, 0, (struct sockaddr*)&their_addr, addr_len)) == -1){
    		perror("serverA: failed to send data");
    		exit(1);
    	}		

		int size = result->vertex.size();
    	if((numbytes = sendto(sockfd, &size, sizeof size , 0,(struct sockaddr *) &their_addr, addr_len)) == -1){
    		perror("serverA: failed to send data");
    		exit(1);
    	}

		int vertex[10] = {0};
		int minlength[10] = {0};

		for(int i=0; i<size; i++){
			vertex[i] = result->vertex[i];
			minlength[i] = result->minlength[i];
			// printf("node: %d, length: %d\n", result->vertex[i], result->minlength[i]);
		}

    	if((numbytes = sendto(sockfd, vertex, sizeof vertex , 0,(struct sockaddr *) &their_addr, addr_len)) == -1){
    		perror("serverA: failed to send vertex array");
    		exit(1);
    	}	
    	if((numbytes = sendto(sockfd, minlength, sizeof minlength , 0,(struct sockaddr *) &their_addr, addr_len)) == -1){
    		perror("serverA: failed to send minlength array");
    		exit(1);
    	}    		
		printf("The Server A has sent shortest paths to AWS.\n");
	}

	close(sockfd);
	return 0;
}
