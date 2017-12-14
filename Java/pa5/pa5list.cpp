// Name: Yin-Hsia Yen
// USC NetID: yinhsiay
// CS 455 PA5

// pa5list.cpp
// a program to test the linked list code necessary for a hash table chain

// You are not required to submit this program for pa5.

// We gave you this starter file for it so you don't have to figure
// out the #include stuff.  The code that's being tested will be in
// listFuncs.cpp, which uses the header file listFuncs.h

// The pa5 Makefile includes a rule that compiles these two modules
// into one executable.

#include <iostream>
#include <string>
#include <cassert>

using namespace std;

#include "listFuncs.h"


int main() {

	ListType ll;
	cout << "list:" << endl;
	initList(ll);
	printList(ll);

	cout << "add: " << endl;
	insertList(ll, "Joe", 100);
	printList(ll);

	cout << "add: " << endl;
	insertList(ll, "Jason", 60);
	printList(ll);

	cout << "add: " << endl;
	insertList(ll, "Jane", 98);
	printList(ll);

	cout << "add: " << endl;
	bool reVal = insertList(ll, "Joe", 90);
	if( reVal == false)
		cout << "conflict happened!" << endl;
	printList(ll);

	cout << numOfList(ll) << " elements in the list"<< endl;

	cout << "lookUp: " << endl;
	int *score = lookUpList(ll, "Jane");
	cout << "Jane got score: " << *score << endl;
	printList(ll);

	cout << "change the score" << endl;
	*score = 70;
	printList(ll);

    cout << "lookUp: " << endl;
	score = lookUpList(ll, "James");
	if( score != NULL)
		cout << "James got score" << *score << endl;
	else
		cout << "cannot find James" << endl;	

	cout << "remove: " << endl;
	removeList(ll, "James");
	printList(ll);

	cout << "remove: " << endl;
	removeList(ll, "Joe");
	printList(ll);

	cout << numOfList(ll) << " elements in the list"<< endl;

	cout << "remove: " << endl;
	removeList(ll, "Jane");
	printList(ll);

	cout << "remove: " << endl;
	removeList(ll, "Jason");
	printList(ll);

	cout << numOfList(ll) << " elements in the list"<< endl;

    return 0;
}
