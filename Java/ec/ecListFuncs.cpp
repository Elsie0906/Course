/*  Name: Yin-Hsia Yen
 *  USC NetID: yinhsiay
 *  CS 455 Fall 2017
 *
 *  See ecListFuncs.h for specification of each function.
 */

#include <iostream>

#include <cassert>

#include "ecListFuncs.h"

using namespace std;

/*
* returns the number of runs in the list.
* A run is an ajacecent sequence two or more of the same values all in a row
* @param list
*     original list built by user
*/

int numRuns(ListType list) {

	int value = 0, count = 0, total = 0;

	if( list == NULL || (list != NULL && list->next == NULL))
		return total;

	value = list->data;								// first node, set first data as value
	list = list->next;

	while(list != NULL){

		if( list->data == value){
			count++;
		}else{										// if there is a switch
			if( count != 0){
				total++;							// repeated number happened before, add one to total, reset count
				count = 0;
			}			
			value = list->data;						// update value
		}

		if( list->next == NULL && count != 0)		// last node
			total++;

		list = list->next;
	}

  	return total;
}

/*
* returns a copy of the list, but with the elements in reverse order
* @param list
*     original list built by user
*/

ListType reverseCopy(ListType list) {

	ListType newlist = NULL, p = list;

	while( p != NULL){
		newlist = new Node(p->data, newlist);
		p = p->next;
	}


  	return newlist;

}

/*
* complexity: O(n)
* removes the middle element from the list.
* @param list
*     original list built by user
*/

void removeMiddleElmt(ListType &list) {

	if( list == NULL || (list != NULL && list->next == NULL)){				// empty list & only one node in the list
		list = NULL;
		return;
	}

	ListType prev = list;
	ListType slow = list->next;

	if( list != NULL && list->next != NULL && list->next->next == NULL){	// two nodes in the list
		list = list->next;
		prev->next = NULL;
		delete prev;
		return;
	}

	ListType fast = list->next->next;

	while(fast != NULL && fast->next != NULL && fast->next->next != NULL){	// find middle element by using two pointers: slow & fast
		prev = slow;
		slow = slow->next;
		fast = fast->next->next;
	}


	prev->next = prev->next->next;
	delete slow;

}

/*
* Splits list into two sub-lists for a given integer splitVal
*
* @param list
*     original list built by user
* @param a
*     contain all the elements up to, but not including, the first occurrence of splitVal in the original list
* @param b
*     contain all the elements after splitVal in the original list
*/

void splitOn(ListType &list, int splitVal, ListType &a, ListType &b) {
	
	ListType cur = list, prev = list;

	if( cur == NULL){												// if original list is an empty list
		a = b = NULL;
		return;
	}		

	if( cur != NULL && cur->data == splitVal){						// split value happened in the first node
		b = cur->next;
		a = list = NULL;
		delete cur;
		return;
	}						

	a = list;

	while( cur != NULL){											// split value from second node in the list
		if( cur->data == splitVal){
			b = cur->next;
			prev->next = NULL;
			delete cur;
			break;
		}
		prev = cur;
		cur = cur->next;
	}

	list = NULL;
}
