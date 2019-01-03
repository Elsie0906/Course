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
 * @(#)$Id: warmup2.c,v 1.1 2018/09/18 20:00:00 Elsie Exp $
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
#include <pthread.h>
#include <sys/wait.h>
#include <signal.h>
#include <math.h>

#include "cs402.h"
#include "my402list.h"
#include "warmup2.h"

static char* internal;
static char endChar = '\0';
static char gszProgName[MAXPATHLENGTH];

pthread_t tokenDeposit, packetRequest, server1, server2, monitor;
pthread_mutex_t mutex=PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t q2_not_empty = PTHREAD_COND_INITIALIZER;
sigset_t set;

static void checkParam(int idx, int count){
    if(idx >= count){
        fprintf(stderr, "malformed command\n");
        fprintf(stderr, "usage:%s %s\n", gszProgName,"[-lambda lambda] [-mu mu] [-r r] [-B B] [-P P] [-n num] [-t tsfile]");
        exit(1);        
    }
}

static void initSetting(MySetting* setting){

    setting->start_time = -1;
    setting->end_time = -1;

    setting->curTokens = 0;
    setting->drop_tokens = 0;
    setting->sumTokens = 0;
    setting->drop_packets = 0;
    setting->delivered_packets = 0;
    setting->processed_packets = 0;
    setting->isFirst = true;

    setting->lambda = DEFAULT_LAMBDA;
    setting->mu = DEFAULT_MU;
    setting->r = DEFAULT_R;
    setting->bucket_depth = DEFAULT_B;
    setting->req_token = DEFAULT_P;
    setting->total_packet_num = DEFAULT_NUM;
    setting->isFile = false;

    setting->isCtrlC = false;

    memset(&(setting->packetList), 0, sizeof(My402List));
    (void)My402ListInit(&(setting->packetList));

    memset(&(setting->firstQ), 0, sizeof(My402List));
    (void)My402ListInit(&(setting->firstQ));

    memset(&(setting->secondQ), 0, sizeof(My402List));
    (void)My402ListInit(&(setting->secondQ));     
}

static void ProcessOptions(int argc, char *argv[], MySetting* setting){

    initSetting(setting);

    for(int param = 1; param < argc; param += 2){
        if(strcmp(argv[param], "-lambda") == 0){
            checkParam(param+1, argc);
            setting->lambda = atof(argv[param+1]);
        }
        else if(strcmp(argv[param], "-mu") == 0){
            checkParam(param+1, argc);
            setting->mu = atof(argv[param+1]);
        }
        else if(strcmp(argv[param], "-r") == 0){
            checkParam(param+1, argc);
            setting->r = atof(argv[param+1]);
        }
        else if(strcmp(argv[param], "-B") == 0){
            checkParam(param+1, argc);
            setting->bucket_depth = atoi(argv[param+1]);
        }
        else if(strcmp(argv[param], "-P") == 0){
            checkParam(param+1, argc);
            setting->req_token = atoi(argv[param+1]);
        } 
        else if(strcmp(argv[param], "-n") == 0){
            checkParam(param+1, argc);
            setting->total_packet_num = atoi(argv[param+1]);
        }
        else if(strcmp(argv[param], "-t") == 0){
            checkParam(param+1, argc);
            setting->isFile = true;
            setting->filename = argv[param+1];
        }
        else{
            fprintf(stderr, "malformed command\n");
            fprintf(stderr, "usage:%s %s\n", gszProgName,"[-lambda lambda] [-mu mu] [-r r] [-B B] [-P P] [-n num] [-t tsfile]");
            exit(1);
        }       
    }

    setting->token_arrival_time = (long)(1*SEC_TO_USEC/setting->r);
    if(setting->token_arrival_time > S_MS * MAX_TOKEN_ARRIVAL_TIME){
        setting->token_arrival_time = S_MS * MAX_TOKEN_ARRIVAL_TIME;
    }

    if(setting->isFile){    // if -t option set, ignore -lambda, -mu, -P, and -n
        setting->lambda = -1;
        setting->mu = -1;
        setting->req_token = -1;
        setting->total_packet_num = -1;
    }

}

static void printOutParam(MySetting* setting){

    fprintf(stdout, "Emulation Parameters:\n");
    fprintf(stdout, "\tnumber to arrive = %d\n", setting->total_packet_num);

    if(!setting->isFile){
        fprintf(stdout, "\tlambda = %g\n",setting->lambda);
        fprintf(stdout, "\tmu = %g\n",setting->mu);
        fprintf(stdout, "\tr = %g\n", setting->r);
        fprintf(stdout, "\tB = %d\n", setting->bucket_depth);
        fprintf(stdout, "\tP = %d\n", setting->req_token);        
    }
    else{
        fprintf(stdout, "\tr = %g\n", setting->r);
        fprintf(stdout, "\tB = %d\n", setting->bucket_depth);
        fprintf(stdout, "\ttsfile = %s\n", setting->filename);
    }

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

static char* readLine(FILE *fp){

    memset(internal, '\0', internal_size * sizeof(char));

    if(fgets(internal, internal_size, fp) == NULL)    return NULL;

    if(checkEnd(internal) == FALSE){
        fprintf(stderr, "[readLine] a line is longer than 1,024 characters\n");
        exit(1);
    }    

    return internal;
}

static void Parse(char* buf, Packet *packet, int num){

    char* delim = " ";

    char* token = strtok(buf, delim);

    int i=0;

    packet->packet_ID = num;
    packet->packet_arrives_time = 0;
    packet->real_inter_arrival_time = -1;
    packet -> enter_q1 = 0;
    packet -> leave_q1 = 0;
    packet -> enter_q2 = 0;
    packet -> leave_q2 = 0;
    packet -> time_in_q1 = -1;
    packet -> time_in_q2 = -1;
    packet -> server = 0;
    packet -> begin_service = 0;
    packet -> end_service = 0;
    packet -> real_service_time = -1;
    packet -> total_process_time = -1;      

    while(token != NULL){

        switch(i){
            case 0:{

                if(!(token[0]>='0' && token[0]<='9')){
                    fprintf(stderr, "[Malformed Input] line 1 is not just a number\n");
                    exit(1);
                }
                    
                packet->inter_arrival_time = atol(token) * S_MS;
                //fprintf(stdout, "[Debug] inter_arrival_time:%s\n", token);
                break;
            }
            case 1:{
                packet->req_tokens = atoi(token);
                //fprintf(stdout, "[Debug] req_tokens:%s\n", token);
                break;
            }
            case 2:{
               
                packet->service_time = atol(token) * S_MS;
                //fprintf(stdout, "[Debug] service_time:%s\n", token);
                break;
            }
            default:{
                fprintf(stderr, "too many fields\n");
                exit(1);
                break;                
            }

        }

        token = strtok(NULL,delim);  
        i++;    
    }    

}

static void Process(FILE *fp, MySetting* setting){

    // internal buffer with size = 1024
    internal = (char*)malloc(internal_size * sizeof(char));
    
    char* buf = readLine(fp);

    if(buf != NULL){    // first line; adjust total_packet_num
        setting->total_packet_num = atoi(buf);
    }

    buf = readLine(fp);

    int num = 0;

    while(buf != NULL){
        num++;
        Packet *packet = (Packet*)malloc(sizeof(Packet));
        memset(packet, 0, sizeof(Packet));
        Parse(buf, packet, num);
        My402ListAppend(&(setting->packetList), (void*)packet);
        buf = readLine(fp);
    }

    if(num != setting->total_packet_num){
        fprintf(stderr, "[Process] packet num is not correct\n");
        exit(1);        
    }

    free(internal);    

}

static void trace_driven(MySetting* setting){
    FILE *fp = NULL;

    fp = fopen(setting->filename, "r");

    if(fp == NULL){
        fprintf(stderr, "input file %s fopen failed due to: %s\n", setting->filename, (char*)strerror(errno));
        exit(1);
    }

    Process(fp, setting);

    fclose(fp);
}

static void packetProcess(MySetting* setting, Packet *packet, int num){

    double lambda = setting->lambda;

    long inter_arrival_time = (long)(1*SEC_TO_USEC/lambda);

    if(inter_arrival_time > S_MS * MAX_INTER_ARRIVAL_TIME)
        packet->inter_arrival_time = S_MS * MAX_INTER_ARRIVAL_TIME;
    else
        packet->inter_arrival_time = inter_arrival_time; 

    packet->req_tokens = setting->req_token;

    double mu = setting->mu;

    long service_time = (long)(1*SEC_TO_USEC/mu);

    if(service_time > S_MS * MAX_SERVICE_TIME)
        packet->service_time = S_MS * MAX_SERVICE_TIME;
    else
        packet->service_time = service_time;

    packet->packet_ID = num;

    packet->packet_arrives_time = 0;
    packet->real_inter_arrival_time = -1;

    packet -> enter_q1 = 0;
    packet -> leave_q1 = 0;
    packet -> enter_q2 = 0;
    packet -> leave_q2 = 0;
    packet -> time_in_q1 = 0;
    packet -> time_in_q2 = 0;

    packet -> server = 0;
    packet -> begin_service = 0;
    packet -> end_service = 0;
    packet -> real_service_time = -1;
    packet -> total_process_time = 0; 
}

static void deterministic(MySetting* setting){

    Packet *packet = (Packet*)malloc(sizeof(Packet));
    memset(packet, 0, sizeof(Packet));
    packetProcess(setting, packet, 1);
    My402ListAppend(&(setting->packetList), (void*)packet);

/*
    int count = setting->total_packet_num;  // when n is a large num, it could be a disaster

    while(count > 0){

        Packet *packet = (Packet*)malloc(sizeof(Packet));
        memset(packet, 0, sizeof(Packet));
        packetProcess(setting, packet);
        My402ListAppend(&(setting->packetList), (void*)packet);
        count--;
    }
*/
}

static void initPacket(MySetting* setting){

    if(setting->isFile){
        trace_driven(setting);
    }
    else{
        deterministic(setting);
    }
}

static void startTimer(MySetting* setting){

    struct timeval timestamp;
    gettimeofday(&timestamp,NULL);

    long e_sec = timestamp.tv_sec;
    long e_usec= timestamp.tv_usec;

    setting->start_time = e_sec * SEC_TO_USEC + e_usec;
    double sys_time = (double)( e_sec * SEC_TO_USEC + e_usec - setting->start_time)/S_MS;
    fprintf(stdout, "\n%012.3fms: emulation begins\n", sys_time);
}

static double getCurTime(MySetting* setting){

    struct timeval timestamp;
    gettimeofday(&timestamp,NULL); 

    long e_sec = timestamp.tv_sec;
    long e_usec= timestamp.tv_usec;

    double sys_time = (double)( e_sec * SEC_TO_USEC + e_usec - setting->start_time)/S_MS;

    return sys_time;      
}
static long getTimeStamp(MySetting* setting){
    struct timeval timestamp;
    gettimeofday(&timestamp,NULL); 

    long e_sec = timestamp.tv_sec;
    long e_usec= timestamp.tv_usec;

    long time =  (long)e_sec * SEC_TO_USEC + e_usec;
    
    return time;   
}

static void *processToken(void *arg){

    MySetting* setting = (MySetting*) arg;

    //fprintf(stdout, "[pthread_create] Token Deposit Thread\n");

    for (;;) {

        usleep(setting->token_arrival_time);

        if(setting->delivered_packets + setting->drop_packets == setting->total_packet_num || setting->isCtrlC == true)   break;

        pthread_mutex_lock(&mutex);
        double sys_time = getCurTime(setting);

        setting->sumTokens++;

        if(setting->curTokens < setting->bucket_depth){
            setting->curTokens++;
            fprintf(stdout, "%012.3fms: token t%ld arrives, token bucket now has %d token\n", sys_time, setting->sumTokens, setting->curTokens);
        }
        else{
            setting->drop_tokens++;
            fprintf(stdout, "%012.3fms: token t%ld arrives, dropped\n", sys_time, setting->sumTokens);
        }

        pthread_mutex_unlock(&mutex);
    }

    //fprintf(stdout, "[Token Deposit Thread] exit\n");    

    return 0;
}

static void *processPacket(void *arg){

    MySetting* setting = (MySetting*) arg;

    //fprintf(stdout, "[pthread_create] Packet Request Thread\n");

    My402ListElem* cur = My402ListFirst(&setting->packetList);
    int curPacketNum = 1;

    for (;;){

        // create packet now when setting->isFile is false
        if(!setting->isFile && curPacketNum < setting->total_packet_num){
            curPacketNum++; 
            Packet *packet = (Packet*)malloc(sizeof(Packet));
            memset(packet, 0, sizeof(Packet));
            packetProcess(setting, packet, curPacketNum);
            pthread_mutex_lock(&mutex);
            My402ListAppend(&setting->packetList, (void*)packet);
            pthread_mutex_unlock(&mutex);
        }

        if(cur != NULL){
            Packet* packet = (Packet*) (cur -> obj);
            long inter_arrival_time = packet-> inter_arrival_time;

            usleep(inter_arrival_time);
            pthread_mutex_lock(&mutex);
            long timestamp = getTimeStamp(setting);
            packet->packet_arrives_time = timestamp;
            double cur_time = (double)(packet->packet_arrives_time - setting->start_time)/S_MS;
            if(My402ListPrev(&setting->packetList, cur) == NULL){
                packet->real_inter_arrival_time = cur_time;
            }
            else{
                My402ListElem* pre = My402ListPrev(&setting->packetList, cur);
                double time_interval =  ((double)((packet->packet_arrives_time) -((Packet*)pre->obj)->packet_arrives_time))/S_MS;
                packet->real_inter_arrival_time = time_interval;            
            }

            if(packet->req_tokens <= setting->bucket_depth){
                
                if(setting->isCtrlC == true){
                    pthread_cond_signal(&q2_not_empty);
                    pthread_mutex_unlock(&mutex); 
                    break;
                }
                  
                fprintf(stdout, "%012.3fms: packet p%d arrives, needs %d tokens, inter-arrival time = %.3fms\n", cur_time, packet->packet_ID, packet->req_tokens, packet->real_inter_arrival_time);
                My402ListAppend(&setting->firstQ,(void*)packet);
                packet->enter_q1 = getTimeStamp(setting);
                cur_time = (double)(packet->enter_q1 - setting->start_time)/S_MS;
                
                if(setting->isCtrlC == true){
                    My402ListElem* last = My402ListLast(&setting->firstQ);
                    My402ListUnlink(&setting->firstQ, last);
                    pthread_cond_signal(&q2_not_empty);
                    pthread_mutex_unlock(&mutex); 
                    break;                    
                }
                
            }
            else{
                setting->drop_packets++;
                fprintf(stdout, "%012.3fms: packet p%d arrives, needs %d tokens, inter-arrival time = %.3fms, dropped\n", cur_time, packet->packet_ID, packet->req_tokens, packet->real_inter_arrival_time);
            }

            cur = My402ListNext(&setting->packetList, cur);
            pthread_mutex_unlock(&mutex);            
        }
        
        if(setting->isCtrlC == true){
            pthread_cond_signal(&q2_not_empty);
            break;
        }
        
        My402ListElem* head = My402ListFirst(&setting->firstQ);

        if(head && (((Packet*)(head->obj))->req_tokens) <= setting->curTokens){
            pthread_mutex_lock(&mutex);
            Packet* packet = (Packet*) (head->obj);
            
            if(setting->isCtrlC == true){
                My402ListElem* last = My402ListLast(&setting->firstQ);
                My402ListUnlink(&setting->firstQ, last);                
                pthread_cond_signal(&q2_not_empty);
                pthread_mutex_unlock(&mutex); 
                break;
            }
                    
            My402ListUnlink(&setting->firstQ, head);
            packet->leave_q1 = getTimeStamp(setting);
            packet->time_in_q1 = (double)(packet->leave_q1 - packet->enter_q1)/S_MS;
            setting->curTokens -= packet -> req_tokens;
            double cur_time = (double)(packet -> leave_q1 - setting->start_time)/S_MS;
            fprintf(stdout, "%012.3fms: p%d leaves Q1, time in Q1 = %.3fms, token bucket now has %d token\n",cur_time,  packet->packet_ID,  packet->time_in_q1, setting->curTokens);
            
            if(setting->isCtrlC == true){
                pthread_cond_signal(&q2_not_empty);
                pthread_mutex_unlock(&mutex); 
                break;
            }
            
            My402ListAppend(&setting->secondQ,(void*)packet);
            
            if(setting->isCtrlC == true){
                My402ListElem* last = My402ListLast(&setting->secondQ);
                My402ListUnlink(&setting->secondQ, last);                
                pthread_cond_signal(&q2_not_empty);
                pthread_mutex_unlock(&mutex); 
                break;
            }
                                    
            packet->enter_q2 = getTimeStamp(setting);
            setting->delivered_packets++;            
            cur_time = (double)(packet->enter_q2 - setting->start_time)/S_MS;
            fprintf(stdout, "%012.3fms: p%d enters Q2\n", cur_time, packet->packet_ID);            
            pthread_cond_signal(&q2_not_empty);
            pthread_mutex_unlock(&mutex);            
        }

        if(setting->delivered_packets + setting->drop_packets == setting->total_packet_num){
            //fprintf(stdout, "[Debug] Packet Request Thread, drop:%d, delivered:%d\n", setting->drop_packets, setting->delivered_packets);
            pthread_cond_signal(&q2_not_empty);
            //pthread_cond_signal(&q2_not_empty); 
            break; 
        }
    }

    //fprintf(stdout, "[Packet Request Thread] exit\n");

    return 0;

}

static void takeTurn(Packet* packet, MySetting* setting){
    if(setting->isFirst){
      packet -> server = 1;
      setting->isFirst = false;
    }
    else{
      packet -> server = 2;
      setting->isFirst = true;
    }    
}

static void *serve(void *arg){

    MySetting* setting = (MySetting*) arg;

    //fprintf(stdout, "[pthread_create] Server Thread\n");

    for (;;){
        
        if(setting->total_packet_num - setting->processed_packets == setting->drop_packets){
            //unlock another server, move forward
            pthread_cond_signal(&q2_not_empty); 
            pthread_cancel(monitor);
            break;
        }

        
        if(My402ListEmpty(&setting->secondQ) && setting->isCtrlC){
            pthread_mutex_unlock(&mutex);
            break;
        }

        // If a server finds Q2 to be empty when it is woken up, it blocks

        pthread_mutex_lock(&mutex);
        
        if(My402ListEmpty(&setting->secondQ)){
            pthread_cond_wait(&q2_not_empty, &mutex);
        }

        My402ListElem *cur = My402ListFirst(&setting->secondQ);
        if(cur != NULL){
            Packet* packet = (Packet*)(cur->obj);
            takeTurn(packet, setting);
            long service_time = (long)(((double)packet->service_time)/S_MS + 0.5);
            My402ListUnlink(&setting->secondQ,cur);
            packet->leave_q2 = getTimeStamp(setting);
            double cur_time = (double)(packet -> leave_q2 - setting->start_time)/S_MS;
            packet -> time_in_q2 = (double)(packet -> leave_q2 - packet -> enter_q2)/S_MS;
           
            if(setting->isCtrlC == true){
                pthread_mutex_unlock(&mutex);
                break;
            }
            
            fprintf(stdout, "%012.3fms: p%d leaves Q2, time in Q2 = %.3fms\n",cur_time, packet->packet_ID, packet -> time_in_q2);

            packet->begin_service = getTimeStamp(setting);
            cur_time = (double)(packet->begin_service - setting->start_time)/S_MS;
            fprintf(stdout, "%012.3fms: p%d begins service at S%d, requesting %ldms of service\n",cur_time, packet->packet_ID, packet->server, service_time);

            pthread_mutex_unlock(&mutex);
            usleep(service_time*S_MS);
            pthread_mutex_lock(&mutex);

            packet->end_service = getTimeStamp(setting);
            cur_time = (double)(packet->end_service - setting->start_time)/S_MS;
            packet->real_service_time = (double)(packet->end_service - packet->begin_service)/S_MS;
            packet->total_process_time = (double)(packet->end_service - packet->packet_arrives_time)/S_MS;
            fprintf(stdout, "%012.3fms: p%d departs from S%d, service time = %.3fms, time in system = %.3fms\n",cur_time, packet->packet_ID, packet->server, packet->real_service_time, packet->total_process_time); 
            setting->processed_packets++;
        }

        //fprintf(stdout,"[Debug] total_packet_num:%d, processed_packets:%d,drop_packets:%d \n", setting->total_packet_num, setting->processed_packets, setting->drop_packets);

        pthread_mutex_unlock(&mutex);
    }

    //fprintf(stdout, "[Server Thread] exit\n");

    return 0;
}

static void *handleCtrlC(void *arg){

    MySetting* setting = (MySetting*) arg;

    //fprintf(stdout, "[pthread_create] Ctrl+C Thread\n");

    int sig = 0;
    sigwait(&set,&sig);
    pthread_mutex_lock(&mutex);

    double cur_time = getCurTime(setting);
    fprintf(stdout, "\n%012.3fms: SIGINT caught, no new packets or tokens will be allowed\n", cur_time);    
    pthread_cancel(tokenDeposit);
    pthread_cancel(packetRequest);
    setting->isCtrlC = true;

    // clean up Q1

    for(My402ListElem * elem = My402ListFirst(&setting->firstQ); elem != NULL; elem=My402ListNext(&setting->firstQ, elem)){
        Packet * packet = (Packet *)(elem -> obj);
        //My402ListUnlink(&setting->firstQ,elem);
        cur_time = getCurTime(setting);
        fprintf(stdout, "%012.3fms: p%d removed from Q1\n", cur_time, packet -> packet_ID);
    }

    // clean up Q2

    for(My402ListElem * elem = My402ListFirst(&setting->secondQ); elem != NULL; elem=My402ListNext(&setting->secondQ, elem)){
        Packet * packet = (Packet *)(elem -> obj);
        //My402ListUnlink(&setting->secondQ,elem);
        cur_time = getCurTime(setting);
        fprintf(stdout, "%012.3fms: p%d removed from Q2\n", cur_time, packet -> packet_ID);
    }
    pthread_cond_broadcast(&q2_not_empty);
    pthread_mutex_unlock(&mutex);

    //fprintf(stdout,"[Debug] leave ctrlC Thread\n");

    return 0;
}

static void statistics(MySetting* setting){

    double avg_inter_arr_time = 0;
    double avg_service_time = 0;
    double avg_time_sys = 0;
    double p_in_q1 = 0;
    double p_in_q2 = 0;
    double p_in_s1 = 0;
    double p_in_s2 = 0; 
    double sta_dev = 0;   

    int all_valid_packet = 0;
    int all_completed_packet = 0;

    for (My402ListElem *elem=My402ListFirst(&setting->packetList); elem != NULL; elem=My402ListNext(&setting->packetList, elem)){
        Packet *packet =(Packet *)(elem->obj);

        if(packet->real_inter_arrival_time != -1){
            avg_inter_arr_time += packet->real_inter_arrival_time;
            all_valid_packet++;
        }

        if(packet->real_service_time != -1){
            avg_service_time += packet->real_service_time;
            avg_time_sys += packet -> total_process_time;
            sta_dev += (packet -> total_process_time) * (packet -> total_process_time);
            if(packet->server == 1){
                p_in_s1 += packet->real_service_time;
            }
            else if(packet->server == 2){
                p_in_s2 += packet->real_service_time;
            }
            p_in_q1 += packet->time_in_q1;
            p_in_q2 += packet->time_in_q2;
            all_completed_packet++; 
        }
    }

    avg_inter_arr_time = (all_valid_packet==0)? 0 : (avg_inter_arr_time/all_valid_packet);
    avg_service_time = (all_completed_packet==0)? 0 : (avg_service_time/all_completed_packet);
    avg_time_sys = (all_completed_packet==0)? 0 : (avg_time_sys/all_completed_packet);
    sta_dev = (all_completed_packet==0) ? 0 : sqrt(((sta_dev/all_completed_packet) - avg_time_sys*avg_time_sys));

    avg_inter_arr_time /= S_MS;
    avg_service_time /= S_MS;
    avg_time_sys /= S_MS;
    sta_dev /= S_MS;

    p_in_q1 /= setting->end_time;
    p_in_q2 /= setting->end_time;
    p_in_s1 /= setting->end_time;
    p_in_s2 /= setting->end_time;

    //fprintf(stdout,"[Debug] drop:%d, sum:%ld\n", setting->drop_tokens, setting->sumTokens);

    double t_drop_prob = (setting->sumTokens == 0)? 0:(double)setting->drop_tokens/setting->sumTokens;
    double p_drop_prob = (setting->total_packet_num==0)? 0 : (double)setting->drop_packets/setting->total_packet_num;

    fprintf(stdout, "\nStatistics:\n");
    fprintf(stdout, "\n\taverage packet inter-arrival time = %.6g s\n", avg_inter_arr_time);
    fprintf(stdout, "\taverage packet service time = %.6g s\n", avg_service_time);
    fprintf(stdout, "\n\taverage number of packets in Q1 = %.6g\n", p_in_q1);
    fprintf(stdout, "\taverage number of packets in Q2 = %.6g\n", p_in_q2);    
    fprintf(stdout, "\taverage number of packets at S1 = %.6g\n", p_in_s1);
    fprintf(stdout, "\taverage number of packets at S2 = %.6g\n", p_in_s2);    
    fprintf(stdout, "\n\taverage time a packet spent in system = %.6g s\n", avg_time_sys);
    fprintf(stdout, "\tstandard deviation for time spent in system = %.6g s\n", sta_dev);
    fprintf(stdout, "\n\ttoken drop probability = %.6g\n", t_drop_prob);
    fprintf(stdout, "\tpacket drop probability = %.6g\n", p_drop_prob);

}
/*
static void traverse(MySetting* setting){
    
    My402ListElem *elem=NULL;

    for (elem=My402ListFirst(&setting->packetList); elem != NULL; elem=My402ListNext(&setting->packetList, elem)) {
        Packet *packet =(Packet *)(elem->obj);
        fprintf(stdout, "arrival:%ld, req:%d, service:%ld\n", packet->inter_arrival_time, packet->req_tokens, packet->service_time);
    }
        
}
*/
static void UnlinkList(MySetting* setting){
    My402ListElem *elem=NULL;

    for (elem=My402ListFirst(&setting->packetList); elem != NULL; elem=My402ListNext(&setting->packetList, elem)) {
        Packet *packet =(Packet *)(elem->obj);
        free(packet);
        My402ListUnlink(&setting->packetList, elem);
    }    
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
/* ----------------------- main() ----------------------- */

int main(int argc, char *argv[])
{

    MySetting* setting = (MySetting*) malloc(sizeof(MySetting));

    SetProgramName(*argv);

    ProcessOptions(argc, argv, setting);

    initPacket(setting);

    printOutParam(setting);

    //traverse(setting);

    startTimer(setting);    
    
    sigemptyset(&set);  // block ctrl+C
    sigaddset(&set,SIGINT);
    sigprocmask(SIG_BLOCK,&set, 0);
    
    pthread_create(&tokenDeposit, 0, processToken, (void *)setting);
    pthread_create(&packetRequest, 0, processPacket, (void *)setting);
    pthread_create(&server1, 0, serve, (void *)setting);
    pthread_create(&server2, 0, serve, (void *)setting);
    pthread_create(&monitor, 0, handleCtrlC, (void *)setting);

    pthread_join(tokenDeposit, 0);
    pthread_join(packetRequest, 0);
    pthread_join(server1, 0);
    pthread_join(server2, 0);
    pthread_join(monitor, 0);

    double sys_time = getCurTime(setting);
    fprintf(stdout, "%012.3fms: emulation ends\n",sys_time);
    setting->end_time = sys_time;

    statistics(setting);

    UnlinkList(setting);

    free(setting);

    return(0);
}
