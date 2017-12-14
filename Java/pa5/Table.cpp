// Name: Yin-Hsia Yen
// USC NetID: yinhsiay
// CSCI 455 PA5
// Fall 2017

// Table.cpp  Table class implementation


/*
 * Modified 11/22/11 by CMB
 *   changed name of constructor formal parameter to match .h file
 */

#include "Table.h"

#include <iostream>
#include <string>
#include <cassert>
#include <algorithm>    // std::max


// listFuncs.h has the definition of Node and its methods.  -- when
// you complete it it will also have the function prototypes for your
// list functions.  With this #include, you can use Node type (and
// Node*, and ListType), and call those list functions from inside
// your Table methods, below.

#include "listFuncs.h"


//*************************************************************************

// a bunch of lists in the table (an array of ListType)
// default array length = HASH_SIZE
Table::Table() {

	hashSize = HASH_SIZE;
	table = new ListType[hashSize];

	for(int i=0; i<hashSize; i++){				// data[0], this expression is type ListType (= Node*)
		initList(table[i]);						// well-formed linked list
	}

}

// a bunch of lists in the table (an array of ListType)
// user-defined, array length = hSize
Table::Table(unsigned int hSize) {

	hashSize = hSize;
	table = new ListType[hashSize];

	for(int i=0; i<hashSize; i++){
		initList(table[i]);						// well-formed linked list
	}
}

/*
* lookup for a name in the table
* @param key    reference name
* @return
*     a pointer to corresponding score
*/
int * Table::lookup(const string &key) {
	int idx = hashCode(key);

	return lookUpList(table[idx], key);
}

/*
* remove a name in the table
* @param key    reference name
* @return 
*     true      remove the item
*     false     cannot remove; item is not in the table
*/
bool Table::remove(const string &key) {
  	int idx = hashCode(key);

  	return removeList(table[idx], key);
}

/*
* insert a name to the table
* @param key    reference name
* @param value  corresponding score
* @return
*     true      insert the item
*     false     the item is already in the table
*/
bool Table::insert(const string &key, int value) {
  	int idx = hashCode(key);

  	return insertList(table[idx], key, value);
}

/*
* how many items in the table
* @return
*     total number of items
*/
int Table::numEntries() const {               // the function doesn't change the object/memeber in the class

	int totalEntry = 0;

	for(int i=0; i<hashSize; i++){
		totalEntry += numOfList(table[i]);
	}

  	return totalEntry;
}


/* 
* print out all items in the talbe 
*/
void Table::printAll() const {

	for(int i=0; i<hashSize; i++){
		printList(table[i]);
	}

}

/* 
* print out the status of the hashtable
* @param out, the standard output
*/ 
void Table::hashStats(ostream &out) const {
  	
  	out << "number of buckets: " << hashSize << endl;
  	out << "number of entries: " << numEntries() << endl;
  	out << "number of non-empty buckets: " << nonEmptyList() << endl;
  	out << "longest chain: " << longestChain() << endl;
}


/*
* how many non-empty list in the table
* @return 
*     number of non-empty list  
*/

int Table::nonEmptyList() const{

	int num = 0;

	for(int i=0; i<hashSize; i++){
		if( table[i] != NULL)
			num++;
	}

	return num;
}

/*
* how many elements in the longest list(chain)
* @return 
*     number of longest chain items
*/
int Table::longestChain() const{

	int maxNum = 0;

	for(int i=0; i<hashSize; i++){
		maxNum = max(maxNum,numOfList(table[i]));
	}

	return maxNum;
}
