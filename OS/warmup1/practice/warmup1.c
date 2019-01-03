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
 * @(#)$Id: warmup1.c,v 1.1 2018/08/27 14:00:00 Elsie Exp $
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/time.h>
#include <errno.h>
#include <sys/stat.h>
#include <unistd.h>
#include <stdbool.h>
#include <float.h>
#include <time.h>

#include "cs402.h"

#include "my402list.h"
#include "warmup1.h"

static char gszProgName[MAXPATHLENGTH];
static char endChar = '\0';
static int internal_size = 1026;
static char* internal;
static int format = 80;

int gnDebug=0;

/* ----------------------- Utility Functions ----------------------- */

static
void Usage()
{
    fprintf(stderr, "malformed command, usage: %s %s\n", gszProgName, "sort [fileName]");
    exit(-1);
}

static void SetProgramName(char *s)
{
    char *c_ptr=strrchr(s, DIR_SEP);

    if (c_ptr == NULL) {
        strcpy(gszProgName, s);
    } else {
        strcpy(gszProgName, ++c_ptr);
    }
}

static char* readData(FILE *fp){

    //char* internal = (char*)malloc(internal_size * sizeof(char));

    memset(internal, '\0', internal_size * sizeof(char));

    if(fgets(internal, internal_size, fp) == NULL)    return NULL;

    return internal;
}

static int checkEnd(char* buf){

    char* ptr = buf;
    ptr += internal_size - 2;

    if(strcmp(ptr, &endChar) != 0 && buf[internal_size-2] != '\n'){
        //fprintf(stdout, "[Debugging] checkEnd: FALSE %s \n", buf);
        return FALSE;
    }
    else{
        //fprintf(stdout, "[Debugging] checkEnd: TRUE %s \n", buf);
        return TRUE;
    }

}

static char* readLine(FILE *fp, int line_no){

    char* buf = (char*)malloc(internal_size * sizeof(char));

    memset(buf, '\0', sizeof(char) * internal_size);

    char* internal = readData(fp);

    if(internal == NULL)    return NULL;

    strcpy(buf, internal);

    if(checkEnd(internal) == FALSE){
        fprintf(stderr, "[readLine] a line is longer than 1,024 characters in line: %d\n", (line_no + 1));
        exit(3);
    }

    return buf;

}

static void removeChar(char* ptr, char garbage){
    char *src, *dest;
    for(src = dest = ptr; *src != '\0'; src++){
        *dest = *src;
        if(*dest != garbage) dest++;
    }
    *dest = '\0'; 
}

static void Parse(char* buf, My402List *pList, int line_no){

    char* delim = "\t";

    char* token = strtok(buf, delim);

    int i=0;

    MyDataElem* elem = (MyDataElem*) malloc(sizeof(MyDataElem));

    memset(elem, 0 , sizeof(MyDataElem));

    while(token != NULL){
        //printf("i: %d, %s \n", i++, token);
        switch(i){
            case 0:{
                char ch = *token;

                if(ch != '-' && ch != '+'){
                    fprintf(stderr, "wrong transcation type in line: %d \n", line_no);
                    exit(4);
                }

                char* ptr = &(elem->symbol);
                strcpy(ptr, token);
                //printf("symbol: %c\n", elem->symbol);
                break;
            }
            case 1:{
                elem->timestamp = atoi(token);

                time_t curtime;
                
                time(&curtime);

                if(elem->timestamp > (int) curtime){
                    fprintf(stderr, "timestamp is greater than the current time in line: %d \n", line_no);
                    exit(4);
                }
                //printf("timestamp: %d, len: %lu, token: %s\n", elem->timestamp, strlen(token),token);
                break;                 
            }
            case 2:{
                //double val = atof(token) * 100;
                
                int len = strlen(token);
                char* e = strchr(token, '.');

                if(e == NULL){
                    fprintf(stderr, "wrong transcation amount in line: %d \n", line_no);
                    exit(4);
                }

                int check = (int)(e - token); 

                //printf("len: %d, check: %d\n", len, check);
                if(len - check > 3){
                    fprintf(stderr, "wrong transcation amount in line: %d \n", line_no);
                    exit(4);
                }

                char* ptr = token;
                removeChar(ptr, '.');
                double val = atoi(ptr);
                elem->amount = (int)val;
                
                //printf("amount: %d, token: %s\n", elem->amount, token);
                break;                
            }
            case 3:{
                char* ptr = (char*)malloc(25 * sizeof(char));

                memset(ptr, '\0', 25 * sizeof(char));
                
                const char ch = ' ';    // remove leading white space

                while(*token == ch){
                    token++;
                }

                strncpy(ptr, token, 24);
                
                elem->description = ptr;
                
                //printf("description: %s\n", elem->description);
                break;                
            }
            default:{
                fprintf(stderr, "too many fields in line: %d \n", line_no);
                exit(4);
                break;
            }
        }

        token = strtok(NULL,delim);
        i++;
    }

    (void)My402ListAppend(pList, (void*)elem);

    free(buf);
}

static
void PrintTestList(My402List *pList, int num_items)
{
    My402ListElem *elem=NULL;

    if (My402ListLength(pList) != num_items) {
        fprintf(stderr, "List length is not %1d in PrintTestList().\n", num_items);
        exit(1);
    }

    for (elem=My402ListFirst(pList); elem != NULL; elem=My402ListNext(pList, elem)) {
        MyDataElem* data =(MyDataElem*)(elem->obj);

        time_t curtime = (time_t) (data->timestamp);

        //fprintf(stdout, "timestamp: %s, amount: %d\n", ctime(&curtime), data->amount);
        fprintf(stdout, "timestamp: %ld, amount: %d\n", curtime, data->amount);
    }

    fprintf(stdout, "\n");
}

static
void BubbleForward(My402List *pList, My402ListElem **pp_elem1, My402ListElem **pp_elem2)
    /* (*pp_elem1) must be closer to First() than (*pp_elem2) */
{
    My402ListElem *elem1=(*pp_elem1), *elem2=(*pp_elem2);
    void *obj1=elem1->obj, *obj2=elem2->obj;
    My402ListElem *elem1prev=My402ListPrev(pList, elem1);
/*  My402ListElem *elem1next=My402ListNext(pList, elem1); */
/*  My402ListElem *elem2prev=My402ListPrev(pList, elem2); */
    My402ListElem *elem2next=My402ListNext(pList, elem2);

    My402ListUnlink(pList, elem1);
    My402ListUnlink(pList, elem2);
    if (elem1prev == NULL) {
        (void)My402ListPrepend(pList, obj2);
        *pp_elem1 = My402ListFirst(pList);
    } else {
        (void)My402ListInsertAfter(pList, obj2, elem1prev);
        *pp_elem1 = My402ListNext(pList, elem1prev);
    }
    if (elem2next == NULL) {
        (void)My402ListAppend(pList, obj1);
        *pp_elem2 = My402ListLast(pList);
    } else {
        (void)My402ListInsertBefore(pList, obj1, elem2next);
        *pp_elem2 = My402ListPrev(pList, elem2next);
    }
}

static
void BubbleSortForwardList(My402List *pList, int num_items)
{
    My402ListElem *elem=NULL;
    int i=0;

    if (My402ListLength(pList) != num_items) {
        fprintf(stderr, "List length is not %1d in BubbleSortForwardList().\n", num_items);
        exit(1);
    }
    for (i=0; i < num_items; i++) {
        int j=0, something_swapped=FALSE;
        My402ListElem *next_elem=NULL;

        for (elem=My402ListFirst(pList), j=0; j < num_items-i-1; elem=next_elem, j++) {
            MyDataElem* data =(MyDataElem*)(elem->obj);

            int cur_val= data->timestamp, next_val=0;

            next_elem=My402ListNext(pList, elem);

            MyDataElem* data2 =(MyDataElem*)(next_elem->obj);
            next_val = data2->timestamp;

            if (cur_val > next_val) {
                BubbleForward(pList, &elem, &next_elem);
                something_swapped = TRUE;
            }

            if(cur_val == next_val){
                fprintf(stderr, "[Malformed Input] identical timestamp\n");
                exit(5);
            }
        }
        if (!something_swapped) break;
    }
}

static void printOutSeperateLine(FILE* fp){

    for(int i=0; i<format; i++){
        fprintf(fp, "%c", seperate[i]);
    }

    fprintf(fp, "\n");

}

static void printfOutTitle(FILE* fp){

    for(int i=0; i<format; i++){
        fprintf(fp, "%c", title[i]);
    }

    fprintf(fp, "\n");          
}

static void timeProcess(int timestamp, char* buff, int size){

    memset(buff, ' ', sizeof(char)* size);

    *(buff + size - 1) = '\0';

    time_t curtime = (time_t) timestamp;

    char* time = ctime(&curtime);

    int len = 25;

    int j = len -2;


    for(int i=size-1; i>0; i--){

        if(j < 0) break;

        while(j<=19 && j>=11) j--;

        buff[i - 1] = *(time + j);

        j--;
    }

}

static void amountProcess(int amount, char* content, bool* overflow){

    if(amount >= 1000000000){ // overflow, amount >= 10,000,000
        *overflow = TRUE;
        
        for(int i=59; i>47; i--){

            if(i == 57) content[i--] = '.';

            if(i == 53 || i == 49) content[i--] = ',';

            content[i] = '?';
        }
        return;
    }

    int rem = 0, dividend = amount;

    int i = 0;

    for(i=59; i>47; i--){
        rem = (int)(dividend%10);
        dividend = (int)dividend/10;

        //fprintf(fp, "rem: %d, dividend: %d\n", rem, dividend);

        if(i == 57) content[i--] = '.';

        if(i == 53 || i == 49) content[i--] = ',';

        content[i] = '0' + rem;

        if(dividend <= 0 && i < 57) break;
    }   
}

static void symbolProcess(char symbol, char* content){

    if(symbol == '-'){
        content[47] = '(';
        content[60] = ')';
    }    
}

static void descriptionProcess(char* ptr, char* content){
    //char* ptr = data->description;

    for(int i=0; i<24; i++){
        char ch = *(ptr + i);
        if(ch == '\0' || ch == ' ' /*|| ch == '\t'*/ || ch == '\n'){
            content[i + 20] = ' ';
        }
        else{
            content[i + 20] = *(ptr+ i);
        } 
    }

    if(content[45] != '|'){
        fprintf(stdout, "[descriptionProcess] memory trap\n");
        exit(2);
    }    
}

static void dateProcess(int timestamp, char* content){

    char* buff = (char*) malloc(sizeof(char) * 16);

    if(buff == NULL){
        printf("[dateProcess]cannot allocate memory");
        exit(1);
    }

    //printf("[malloc] buff: %p \n", buff);    

    timeProcess(timestamp, buff, 16);  

    for(int i=0; i<15; i++){
        content[i + 2] = *(buff + i);
    }

    free(buff);    
}

static void balanceProcess(char symbol, int amount, int* balance, char* content){

    //printf("symbol: %c, amount: %d, balance: %d \n", symbol, amount, *balance);

    if(symbol == '-'){
        *balance -= amount;
    }
    else{
        *balance += amount;
    }

    int rem = 0, dividend = *balance;

    if(dividend < 0){
        content[64] = '(';
        content[77] = ')';
    }    

    if(*balance >= 1000000000 || *balance <= -1000000000){
        for(int i=76; i>64; i--){

            if(i == 74) content[i--] = '.';

            if(i == 70 || i == 66) content[i--] = ','; 

            content[i] = '?';
        } 
        return;        
    }

    if(dividend < 0){
        dividend = 0 - dividend;
    }

    for(int i=76; i>64; i--){
        rem = (int)(dividend%10);
        dividend = (int)dividend/10;

        //fprintf(fp, "rem: %d, dividend: %d\n", rem, dividend);

        if(i == 74) content[i--] = '.';

        if(i == 70 || i == 66) content[i--] = ','; 

        content[i] = '0' + rem;

        if(dividend <= 0) break;
    }  


}

static void printOutData(FILE* fp, MyDataElem* data, int* balance, char* content){

    memset(content, ' ', format * sizeof(char));

    *content = '|';
    *(content + 18) = '|';
    *(content + 45) = '|';
    *(content + 62) = '|';
    *(content + 79) = '|';

    bool overflow = FALSE;

    dateProcess(data->timestamp, content);

    descriptionProcess(data->description, content);

    symbolProcess(data->symbol, content);

    amountProcess(data->amount, content, &overflow);

    balanceProcess(data->symbol, data->amount, balance, content);


    for(int i=0; i<format; i++){
        fprintf(stdout, "%c", content[i]);
    }

    fprintf(stdout, "\n");

}

static void resultTable(My402List *pList, int num_items){

    My402ListElem *elem=NULL;

    if (My402ListLength(pList) != num_items) {
        fprintf(stderr, "List length is not %1d in resultTable().\n", num_items);
        exit(1);
    }

    FILE *fp = stdout;

    printOutSeperateLine(fp);
    printfOutTitle(fp);
    printOutSeperateLine(fp);

    int balance = 0;

    char* content = (char*)malloc(format * sizeof(char)); 
     
    if(content == NULL){
        printf("[printOutData]cannot allocate memory");
        exit(1);
    }

    for (elem=My402ListFirst(pList); elem != NULL; elem=My402ListNext(pList, elem)) {
        MyDataElem* data =(MyDataElem*)(elem->obj);
        printOutData(fp, data, &balance, content);
        //break;
    }

    free(content);

    printOutSeperateLine(fp);
}

static void UnlinkList(My402List *pList){

    My402ListElem *elem=NULL;

    for (elem=My402ListFirst(pList); elem != NULL; elem=My402ListNext(pList, elem)) {
        MyDataElem* data =(MyDataElem*)(elem->obj);
        free(data);
        My402ListUnlink(pList, elem);
    }    

}

static void Process(FILE *fp){

    //int size = 0;

    My402List list;

    memset(&list, 0, sizeof(My402List));

    (void)My402ListInit(&list);

    // internal buffer with size = 1024
    internal = (char*)malloc(internal_size * sizeof(char));

    int num_items = 0;

    char* buf = readLine(fp, num_items);

    while(buf != NULL)
    {
        //fprintf(stdout, "size: %d, string: %s \n", size, buf);
        num_items++;
        Parse(buf, &list, num_items);
        buf = readLine(fp, num_items);
    }

    free(internal);

    if (gnDebug > 0) PrintTestList(&list, num_items);


    BubbleSortForwardList(&list, num_items);

    if (gnDebug > 0) PrintTestList(&list, num_items);

    resultTable(&list, num_items);

    UnlinkList(&list);
   
}

static void ProcessOptions(int argc, char *argv[])
{
    argc--; argv++;

    int reading_from_file = 0;

    //fprintf(stdout, "argc=%d \n", argc);

    if(argc < 1){
        Usage();
    }
    else if(strcmp(*argv, "sort") != 0){
        Usage();
    }
    else if(argc > 1){
        reading_from_file = 1;
    }

    FILE *fp = NULL;
    int fd;
    struct stat sb;

    if(reading_from_file){

        fp = fopen(argv[1], "r");

        if(fp == NULL){
            fprintf(stderr, "input file %s fopen failed due to: %s\n", argv[1], (char*)strerror(errno));
            exit(1);
        }

        fd = fileno(fp);
        fstat(fd, &sb);

        if(S_ISDIR(sb.st_mode)){
            fprintf(stderr, "input file %s is a directory \n", argv[1]);
            exit(1);
        }
        Process(fp);
        fclose(fp);

    }
    else{
        Process(stdin);
    }
}

/* ----------------------- main() ----------------------- */

int main(int argc, char *argv[])
{
    SetProgramName(*argv);

    ProcessOptions(argc, argv);

    return(0);
}
