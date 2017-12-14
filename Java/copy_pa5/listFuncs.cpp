// Name: Yin-Hsia Yen
// USC NetID: yinhsiay
// CSCI 455 PA5
// Fall 2017


#include <iostream>

#include <cassert>

#include "listFuncs.h"

using namespace std;

Node::Node(const string &theKey, int theValue) {
  key = theKey;
  value = theValue;
  next = NULL;
}

Node::Node(const string &theKey, int theValue, Node *n) {
  key = theKey;
  value = theValue;
  next = n;
}




//*************************************************************************

// create an empty list
void initList(ListType &list){
	list = NULL;
}

// traverse a list, for debugging
void printList(const ListType list){

	ListType p = list;

	while(p != NULL){
		cout << "(" << p->key << " , " << p->value << ")";
		p = p->next;
	}

	cout << endl;

}

// two ways to insert an element
// 1. Add the element to the tail of the list
// 2. Add the element to the head of the list
// both work cause we need to traverse the list to check if word exists
// @param key    name
// @param val    score
// @param list   current list
bool insertList(ListType &list, const string &key, int val){

	if( list == NULL){						// list is empty, which means no words use this bucket
		list = new Node(key, val, list);
		return true;
	}

	ListType p = list;

	while( p != NULL){						// some words already use this bucket(collsion!!), need to check if word exist

		if( p->key == key){					// word exists, leave node->value change to method lookUpList()
			return false;
		}

		p = p->next;
	}

	list = new Node(key, val, list);		// word does not exist, add this element in the front of the list
	return true;
}

// remove an item in the list
// @param list    current list
// @param key     name
bool removeList(ListType &list, const string &key){

	ListType p = list;

	if( p == NULL){							// this is an empty list
		return false;
	}


	if( p->key == key){						// first node in the list
		list = p->next;
		delete p;                           // reclaim memory
		return true;
	}

	while( p != NULL){

		if( p->next != NULL && p->next->key == key){
			ListType nxP = p->next;
			p->next = nxP->next;
			delete nxP;
			return true;
		}

		p = p->next;
	}

	return false;							// word does not exit in the list

}

// lookup or change an element
// @param list    current list
// @param key     name
int *lookUpList(ListType &list, const string &key){

	ListType p = list;

	while( p != NULL){

		if( p->key == key){
			return &(p->value);
		}

		p = p->next;
	}

	return NULL;
}

// how many elements in a list(bucket)
// @param list    current list
int numOfList(const ListType &list){

	int total = 0;

	ListType p = list;

	while( p != NULL){
		total++;
		p = p->next;
	}

	return total;
}
