// Name: Yin-Hsia Yen
// USC NetID: yinhsiay
// CSCI 455 PA5
// Fall 2017


//*************************************************************************
// Node class definition 
// and declarations for functions on ListType

// Note: we don't need Node in Table.h
// because it's used by the Table class; not by any Table client code.


#ifndef LIST_FUNCS_H
#define LIST_FUNCS_H
  
using namespace std;

struct Node {
  string key;
  int value;

  Node *next;

  Node(const string &theKey, int theValue);

  Node(const string &theKey, int theValue, Node *n);
};


typedef Node * ListType;

//*************************************************************************
//add function headers (aka, function prototypes) for your functions
//that operate on a list here (i.e., each includes a parameter of type
//ListType or ListType&).  No function definitions go in this file.

// create an empty list
void initList(ListType &list);

// traverse a list, for debugging
void printList(const ListType list);

// insert an element in the list
// according to concord.cpp, when a word repeats, value = repetition
// e.g. Song "Little Indian Boys", Indian repeats 4 times, therefore, node->key = "Indian", node->value = 4
bool insertList(ListType &list, const string &key, int val);

// remove an element in the list
bool removeList(ListType &list, const string &key);

// lookup or change an element(cooresponding to Table.h) 
int *lookUpList(ListType &list, const string &key);

// how many elements in a list(bucket)
// same as traverse a list, complexity O(n)
// since it should be a normal distribution (hashCode), it would not take long to find elements in a list
int numOfList(const ListType &list);

// keep the following line at the end of the file
#endif
