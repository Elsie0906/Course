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

void mapConstrustion(list<City> *clist){
	char dash[50] = "-------------------------------------------------";

	printf("The Server A has constructed a list of <number> maps:\n");
	printf("%s\n",dash);
	printf("Map ID\tNum Vertices\tNum Edges\t\n");

	list<City>::iterator ptr;
	for(ptr = clist->begin(); ptr != clist->end();ptr++){
		printf("%-6c\t%-12d\t%-12d\n",ptr->mapID,ptr->numOfVertices, ptr->numOfEdges);
	}

	printf("%s\n",dash);
}

int mapping(char city, list<City> *clist, map<int, int> &map1, map<int, int> &map2){

	// printf("mapping: \n");

	list<City>::iterator ptr;

	for(ptr = clist->begin(); ptr != clist->end();ptr++){

		if(ptr->mapID != city)
			continue;
		else{
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

void buildGraph(list<City> *clist, char city, int start, map<int, int> map1, int numOfVertices){
	
	int matrix[numOfVertices][numOfVertices];
	// initialize
	for(int i=0; i<numOfVertices; i++){
		for(int j=0; j<numOfVertices; j++){
			matrix[i][j] = 0;
		}
	}	

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

	for(int i=0; i<numOfVertices; i++){
		for(int j=0; j<numOfVertices; j++){
			printf("%d ",matrix[i][j]);
		}
		printf("\n");
	}
}

void pathFinding(char city, int startNode, list<City> *clist){

	map<int, int> map1; // pair(ori_idx, new_idx)
	map<int, int> map2; // pair(new_idx, ori_idx)

	int numOfVertices = mapping(city, clist, map1, map2);

	map<int, int>::iterator it;
	it = map1.find(startNode);
	if(it == map1.end()){
		printf("key-value pair not exist in map1\n");
		exit(1);
	}

	int idx = map1.find(startNode)->second;

	buildGraph(clist, city, idx, map1, numOfVertices);

}

int main()
{
	list<City> *clist = new list<City>();
	readFile(clist);

	char graph = 'A';
	int startNode = 5;

	mapConstrustion(clist);
	pathFinding(graph, startNode, clist);

	return 0;
}
