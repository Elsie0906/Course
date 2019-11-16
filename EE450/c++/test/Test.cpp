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
#include "Test.h"

using namespace std;

void parseData(char* buf, set<int> *vertices, list<Edge> *edges){
	char* delim = " ";

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

		line_size = getline(&line_buf, &line_buf_size, fp);
		city->propagation = stod(line_buf);
		// printf("propagation speend: %f\n", city->propagation);

		line_size = getline(&line_buf, &line_buf_size, fp);
		city->transmission = stod(line_buf);
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

int main()
{
	list<City> *clist = new list<City>();
	readFile(clist);

	list<City>::iterator ptr;
	for(ptr = clist->begin(); ptr != clist->end();ptr++){
		printf("city: %c\n", ptr->mapID);
		list<Edge> l = ptr->edges;
		list<Edge>::iterator it;
		for(it = l.begin(); it != l.end(); it++){
			printf("edge: %d and %d, dist = %d\n", it->node1, it->node2, it->distance);
		}
	}

	return 0;
}
