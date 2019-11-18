#ifndef SERVERA_H
#define SERVERA_H

#include <iostream>
#include <set>
#include <iterator>
#include <string>
#include <list>

using namespace std;

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

class MinPath{
public:
	double propagation;
	double transmission;
	vector<int> vertex;
	vector<int> minlength;
};

#endif


