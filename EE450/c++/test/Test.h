#ifndef TEST_H
#define TEST_H

#include <iostream>
#include <set>
#include <iterator>
#include <string>
#include <list>

using namespace std;

// typedef struct Edge{
// 	int node1;
// 	int node2;
// 	int distance;
// }edge_t;

// typedef struct City{
// 	char mapID;
// 	double propagation;
// 	double transmission;
// 	int numOfEdges;
// 	int numOfVertices;
// 	set<int> vertices;
// 	list<Edge> edges;
// }city_t;

class Edge{

public:
	int node1;
	int node2;
	int distance;
};

class City{

public:
	char mapID;
	double propagation;
	double transmission;
	int numOfEdges;
	int numOfVertices;
	set<int> vertices;
	list<Edge> edges;
};

#endif


