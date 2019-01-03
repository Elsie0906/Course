#include <stdio.h>

#include <stdlib.h>
#include <sys/time.h>

#include "cs402.h"

#include "my402list.h"


int My402ListLength(My402List *list){
  return list -> num_members;
}

int My402ListEmpty(My402List *list){
  return list -> num_members <= 0;
}

int My402ListAppend(My402List *list, void *obj){
  My402ListElem *cur = (My402ListElem *)malloc(sizeof(My402ListElem));
  if(!cur){
    return 0;
  }
  cur -> obj = obj;
  My402ListElem *last = My402ListLast(list);
  if(last){
    last -> next = cur;
    cur -> prev = last;
  }
  else{
    (list -> anchor).next = cur;
    cur -> prev = &(list -> anchor);
  }
  (list -> anchor).prev = cur;
  cur -> next = &(list -> anchor);
  list -> num_members ++;
  return 1;
}

int My402ListPrepend(My402List *list, void *obj){
  My402ListElem *cur = (My402ListElem *)malloc(sizeof(My402ListElem));
  if(!cur){
    return 0;
  }
  cur -> obj = obj;
  My402ListElem *first = My402ListFirst(list);
  if(first){
    first -> prev = cur;
    cur -> next = first;
  }
  else{
    (list -> anchor).prev = cur;
    cur -> next = &(list -> anchor);
  }
  (list -> anchor).next = cur;
  cur -> prev = &(list -> anchor);
  list -> num_members ++;
  return 1;
}

void My402ListUnlink(My402List *list, My402ListElem *elem){
  if (elem == &(list -> anchor)) {
    return ;
  }
  My402ListElem *prev = elem -> prev;
  My402ListElem *next = elem -> next;
  prev -> next = next;
  next -> prev = prev;
  free(elem);
  list -> num_members --;
}

void My402ListUnlinkAll(My402List *list){
  if (My402ListEmpty(list)) {
    return ;
  }
  My402ListElem *cur = (list -> anchor).next;
  My402ListElem *next = cur -> next;
  while (cur != &(list -> anchor)) {
    My402ListUnlink(list, cur);
    cur = next;
    next = cur -> next;
  }
}

int My402ListInsertBefore(My402List *list, void *obj, My402ListElem *elem){
    if(!elem){
      return My402ListPrepend(list, obj);
    }
    else{
      My402ListElem *cur = (My402ListElem*)malloc(sizeof(My402ListElem));
      if(!cur){
        return 0;
      }
      else{
        cur -> obj = obj;
        My402ListElem *prev = elem -> prev;
        prev -> next = cur;
        elem -> prev = cur;
        cur -> prev = prev;
        cur -> next = elem;
        list -> num_members ++;
        return 1;
      }
    }
}

int My402ListInsertAfter(My402List *list, void *obj, My402ListElem *elem){
  if(!elem){
      return My402ListAppend(list, obj);
    }
    else{
      My402ListElem *cur = (My402ListElem*)malloc(sizeof(My402ListElem));
      if(!cur){
        return 0;
      }
      else{
        cur -> obj = obj;
        My402ListElem *next = elem -> next;
        elem -> next = cur;
        next -> prev = cur;
        cur -> prev = elem;
        cur -> next = next;
        list -> num_members ++;
        return 1;
      }
    }
}

My402ListElem *My402ListFirst(My402List *list){
  if(My402ListEmpty(list)){
      return NULL;
  }
  else{
    return (list -> anchor).next;
  }
}

My402ListElem *My402ListLast(My402List *list){
  if(My402ListEmpty(list)){
      return NULL;
  }
  else{
    return (list -> anchor).prev;
  }

}


My402ListElem *My402ListNext(My402List *list, My402ListElem *elem){
  if(elem == My402ListLast(list)){
    return NULL;
  }
  else{
    return elem -> next;
  }
}

My402ListElem *My402ListPrev(My402List *list, My402ListElem *elem){
  if(elem == My402ListFirst(list)){
    return NULL;
  }
  else{
    return elem -> prev;
  }
}


My402ListElem *My402ListFind(My402List *list, void *obj){
  My402ListElem *iterator = (list -> anchor).next;
  while (iterator) {
    if(iterator -> obj == obj){
      return iterator;
    }
    else{
      iterator = My402ListNext(list, iterator);
    }
  }
  return NULL;
}

int My402ListInit(My402List *list){
  list -> num_members = 0;
  (list -> anchor).obj = NULL;
  (list -> anchor).next = NULL;
  (list -> anchor).prev = NULL;
  return 1;
}
