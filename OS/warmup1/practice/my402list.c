/******************************************************************************/
/* Important CSCI 402 usage information:                                      */
/*                                                                            */
/* This fils is part of CSCI 402 programming assignments at USC.              */
/*         53616c7465645f5f2e8d450c0c5851acd538befe33744efca0f1c4f9fb5f       */
/*         3c8feabc561a99e53d4d21951738da923cd1c7bbd11b30a1afb11172f80b       */
/*         984b1acfbbf8fae6ea57e0583d2610a618379293cb1de8e1e9d07e6287e8       */
/*         de7e82f3d48866aa2009b599e92c852f7dbf7a6e573f1c7228ca34b9f368       */
/*         faaef0c0fcf294cb                                                   */
/* Please understand that you are NOT permitted to distribute or publically   */
/*         display a copy of this file (or ANY PART of it) for any reason.    */
/* If anyone (including your prospective employer) asks you to post the code, */
/*         you must inform them that you do NOT have permissions to do so.    */
/* You are also NOT permitted to remove or alter this comment block.          */
/* If this comment block is removed or altered in a submitted file, 20 points */
/*         will be deducted.                                                  */
/******************************************************************************/

/*
 * Author:      Elsie Yin-Hsia Yen (yinhsiay@usc.edu)
 *
 * @(#)$Id: my402list.c,v 1.1 2018/08/27 14:00:00 Elsie Exp $
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/time.h>
#include <stdbool.h>

#include "cs402.h"

#include "my402list.h"

int  My402ListLength(My402List* pList){

    int count = pList->num_members;

    return count;
}
int  My402ListEmpty(My402List* pList){

    int num = pList->num_members;

    if( num == 0)   return TRUE;
    
    return FALSE;
}

int  My402ListAppend(My402List* pList, void* obj){

    if(pList == NULL){
        fprintf(stderr, "[My402ListAppend]pList is NULL");
        return FALSE;
    }

    My402ListElem* cur = malloc(sizeof(My402ListElem));

    if(cur == NULL){
        printf("Error allocating memory for My402ListElem \n");
        exit(1);
    }

    memset(cur, 0, sizeof(My402ListElem));

    My402ListElem* last = My402ListLast(pList);
    My402ListElem* anchor = &(pList->anchor);

    cur->obj = obj;
    cur->next = anchor;
    cur->prev = last;

    if(anchor == NULL){
        fprintf(stderr, "[My402ListAppend]anchor is NULL");
        return FALSE;
    }  

    anchor->prev = cur;

    if(last == NULL){
        anchor->next = cur;
    }
    else{
        last->next = cur;    
    } 

    pList->num_members++;

    //fprintf(stdout, "cur: 0x%x , num: %d \n" , cur, pList->num_members);
    //fprintf(stdout, "anchor: 0x%x, last: 0x%x \n" , anchor, last);

    return TRUE;
}
int  My402ListPrepend(My402List* pList, void* obj){

    if(pList == NULL){
        fprintf(stderr, "[My402ListPrepend]pList is NULL");
        return FALSE;
    }

    My402ListElem* cur = (My402ListElem*)malloc(sizeof(My402ListElem));
    memset(cur, 0, sizeof(My402ListElem));

    My402ListElem* first = My402ListFirst(pList);
    My402ListElem* anchor = &(pList->anchor);

    cur->obj = obj;
    cur->next = first;
    cur->prev = anchor;

    if(anchor == NULL){
        fprintf(stderr, "[My402ListPrepend]anchor is NULL");
        return FALSE;
    }

    anchor->next = cur;

    if(first == NULL){
        anchor->prev = cur;
    }
    else{
        first->prev = cur;
    }  

    pList->num_members++;         

    return TRUE;
}
void My402ListUnlink(My402List* pList, My402ListElem* elem){

    if(pList == NULL){
        fprintf(stderr, "[My402ListUnlink]pList is NULL");
        return;
    } 

    My402ListElem* anchor = &(pList->anchor);

    My402ListElem* prev = My402ListPrev(pList, elem);
    My402ListElem* next = My402ListNext(pList, elem);

    if(prev == NULL){
        //elem is the first node
        anchor->next = elem->next;
    }
    else{
        prev->next = elem->next;
    }

    if(next == NULL){
        anchor->prev = elem->prev;
    }
    else{
        next->prev = elem->prev;
    }

    free(elem);

    pList->num_members--; 

}
void My402ListUnlinkAll(My402List* pList){

    My402ListElem *elem=NULL;

    for (elem=My402ListFirst(pList); elem != NULL; elem=My402ListNext(pList, elem)) {
        My402ListUnlink(pList, elem);
    }

}
int  My402ListInsertAfter(My402List* pList, void* obj, My402ListElem* elem){

    if(pList == NULL){
        fprintf(stderr, "[My402ListInsertAfter]pList is NULL");
        return FALSE;
    }

    if(elem == NULL) return My402ListAppend(pList, obj);

    My402ListElem* cur = malloc(sizeof(My402ListElem));
    memset(cur, 0, sizeof(My402ListElem));

    My402ListElem* anchor = &(pList->anchor);
    My402ListElem* next = My402ListNext(pList, elem);   

    cur->obj = obj;
    if(next == NULL){
        cur->next = anchor;
    }
    else{
        cur->next = next; 
    }
    
    cur->prev = elem;

    elem->next = cur;

    if(next == NULL){
        anchor->prev = cur;
    }
    else{
        next->prev = cur;
    }

    pList->num_members++; 

    return TRUE;
}
int  My402ListInsertBefore(My402List* pList, void* obj, My402ListElem* elem){

    if(pList == NULL){
        fprintf(stderr, "[My402ListInsertBefore]pList is NULL");
        return FALSE;
    }

    if(elem == NULL) return My402ListPrepend(pList, obj);

    My402ListElem* cur = malloc(sizeof(My402ListElem));
    memset(cur, 0, sizeof(My402ListElem));

    My402ListElem* anchor = &(pList->anchor);
    My402ListElem* prev= My402ListPrev(pList, elem); 

    cur->obj = obj;
    cur->next = elem;

    if(prev == NULL){
        cur->prev = anchor;
    }
    else{
        cur->prev = prev;
    }

    elem->prev = cur;

    if(prev == NULL){
        anchor->next = cur;
    }
    else{
        prev->next = cur;
    }
    

    pList->num_members++; 
    return TRUE; 
}

My402ListElem *My402ListFirst(My402List* pList){

    My402ListElem* anchor = &(pList->anchor);

    My402ListElem* first = anchor->next;

    if(first == anchor) return NULL;

    return first;
}
My402ListElem *My402ListLast(My402List* pList){

    My402ListElem* anchor = &(pList->anchor);

    My402ListElem* last = anchor->prev;

    if(last == anchor)  return NULL;
    
    return last;
}

My402ListElem *My402ListNext(My402List* pList, My402ListElem* elem){
/* Returns elem->next or NULL if elem is the last item on the list. Please do not check if elem is on the list */
    
    if(elem == NULL){
        fprintf(stderr, "[My402ListNext]elem is NULL");
        return NULL;
    }

    My402ListElem* anchor = &(pList->anchor);

    My402ListElem* check = elem->next;

    if(check == anchor)   return NULL;
    
    return check;

}
My402ListElem *My402ListPrev(My402List* pList, My402ListElem* elem){

    if(elem == NULL){
        fprintf(stderr, "[My402ListPrev]elem is NULL");
        return NULL;
    }

    My402ListElem* anchor = &(pList->anchor);

    My402ListElem* check = elem->prev;

    if(check == anchor)   return NULL;    

    return check;
}

My402ListElem *My402ListFind(My402List* pList, void* obj){
    
    My402ListElem *elem=NULL;

    bool find = FALSE;

    for (elem=My402ListFirst(pList); elem != NULL; elem=My402ListNext(pList, elem)) {
        if(elem->obj == obj){
            find = TRUE;
            break;
        }    
    }

    if(find == FALSE) return NULL;

    return elem;
}

int My402ListInit(My402List* pList){

    if(pList == NULL){

        fprintf(stderr, "[My402ListInit]pList is NULL");
        return FALSE;
    }

    pList->num_members = 0;

    My402ListElem* elem = &(pList->anchor);

    if(elem == NULL){

        fprintf(stderr, "[My402ListInit]pList->anchor is NULL");
        return FALSE;
    }

    elem->obj = NULL;
    elem->next = elem;
    elem->prev = elem;

    return TRUE;
}
