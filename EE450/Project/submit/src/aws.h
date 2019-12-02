#ifndef AWS_H
#define AWS_H

#include <iostream>
#include <set>
#include <iterator>
#include <string>
#include <list>
#include <vector>

using namespace std;

class MinPath{
public:
	double propagation;
	double transmission;
	vector<int> vertex;
	vector<int> minlength;
	vector<double> tp;
};

#endif


