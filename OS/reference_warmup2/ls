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
 * Author:      William Chia-Wei Cheng (bill.cheng@acm.org)
 *
 * @(#)$Id: listtest.c,v 1.1 2017/05/15 15:17:09 william Exp $
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/time.h>
#include <time.h>
#include <unistd.h>
#include <pthread.h>
#include <math.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <signal.h>


#include "cs402.h"

#include "my402list.h"
#include "packet.h"


/* ----------------------- Utility Functions ----------------------- */

#define DEFAULT_LAMBDA 1
#define DEFAULT_MU 0.35
#define DEFAULT_R 1.5
#define DEFAULT_B 10
#define DEFAULT_P 3
#define DEFAULT_NUM 20
#define R 0
#define LAMBDA 1
#define MU 2


#define MAX_INTEGER 2147483647
#define BUCKET_DEPTH 10
#define SEC_TO_USEC 1000000
#define S_MS 1000
#define MAX_TOKEN_ARRIVAL_TIME 10000
#define MAX_INTER_ARRIVAL_TIME 10000
#define MAX_SERVICE_TIME 10000

long service_time = -1;
long inter_arrival_time = -1;
long token_arrival_time = -1;
long total_number_packet = -1;
long bucket_depth = -1;
long req_tokens = -1;
int isCtrlC = 0;


pthread_t packetTrasport, tokenStore, serverF, serverS, monitor, success;
pthread_mutex_t mutex=PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cv1 = PTHREAD_COND_INITIALIZER;
pthread_cond_t cv2 = PTHREAD_COND_INITIALIZER;
sigset_t set;


My402List allPackets,firstQ , secondQ;

struct timeval timestamp;
long e_sec = -1;
long e_usec = -1;
long start_time = -1;
double cur_time = -1;
int curTokens = 0;
int isFirst = 1;
long sumTokens = 0;

int isFile = 0;
char * filename;

double lambda = -1;
double mu = -1;
double r = -1;

int packet_enter_q2 = 0;
int remain_packet_num = 0;
int drop_tokens = 0;
int drop_packets = 0;
double end_time = 0;
int cur_serve_packets = 0;

void initP(Packet * packet, int num) {
  packet -> service_time = service_time;
  packet -> inter_arrival_time = inter_arrival_time;
  packet -> req_tokens = req_tokens;
  packet -> num = num;
  packet -> arrives_time = 0;
  packet -> enter_q1 = 0;
  packet -> exit_q1 = 0;
  packet -> enter_q2 = 0;
  packet -> exit_q2 = 0;
  packet -> begin_services = 0;
  packet -> service_done = 0;
  packet -> server = 0;
  packet -> true_inter_arr_time = -1;
  packet -> true_service_time = -1;
  packet -> time_in_q1 = -1;
  packet -> time_in_q2 = -1;
  packet -> time_in_sys = -1;
}

long getNum(char* buffer, int type){
  long cur = 0;
  int lo = 0;
  long tmp = 0;

  while (buffer[lo] >= '0' && buffer[lo] <='9') {
    tmp = tmp*10 + (buffer[lo] - '0');
    lo++;
  }
  if( lo == 0 || (buffer[lo] != '.'&& buffer[lo] != '\0')){
    fprintf(stderr,"Error: malformed command\n");
    exit(1);
  }
  int decL = lo;
  if (buffer[lo] == '.') {
    lo++;
    while (buffer[lo] >= '0' && buffer[lo] <= '9') {
      tmp = tmp*10 + (buffer[lo] - '0');
      lo++;
    }
    if(decL==lo || buffer[lo] != '\0'){
      fprintf(stderr,"Error: malformed command\n");
      exit(1);
    }
  }
  double number = tmp;
  while(lo - decL > 1){
    number /=10;
    lo--;
  }
  if(type == LAMBDA){
    lambda = number;
    cur = (long)(1*SEC_TO_USEC/number);
    return cur;
  }
  if(type == MU ) {
    mu = number;
    cur = (long)(1*SEC_TO_USEC/number);
    return cur;
  }
  if(type == R ) {
    r = number;
    cur = (long)(1*SEC_TO_USEC/number);
    return cur;
  }
  cur = (long)(1*S_MS/number);
  return cur;
}

long getIntNum(char* num){
  int lo = 0;
  long tmp = 0;
  while (num[lo] >= '0' && num[lo] <='9') {
    tmp = tmp*10 + (num[lo] - '0');
    lo++;
  }
  if(lo == 0 || num[lo] != '\0'){
    fprintf(stderr,"Error: malformed command\n");
    exit(1);
  }
  return tmp;
}

void commandLine(int argc, char **argv){
  int i = 1;
  while ( i < argc) {
    if(strcmp(argv[i],"-lambda")==0){
      if (i+1 >= argc) {
        fprintf(stderr, "Error: malformed command\n");
        exit(1);
      }
      inter_arrival_time = getNum(argv[i+1],LAMBDA);
      if(inter_arrival_time >  S_MS * MAX_INTER_ARRIVAL_TIME){
        inter_arrival_time =  S_MS * MAX_INTER_ARRIVAL_TIME;
      }
      i+=2;
      continue;
    }
    if (strcmp(argv[i],"-mu")==0) {
      if (i+1 >=argc) {
        fprintf(stderr, "Error: malformed command\n");
        exit(1);
      }
      service_time = getNum(argv[i+1],MU);
      if(service_time > S_MS * MAX_SERVICE_TIME){
        service_time = S_MS * MAX_SERVICE_TIME;
      }
      i+=2;
      continue;
    }
    if (strcmp(argv[i],"-r")==0) {
      if (i+1 >=argc) {
        fprintf(stderr, "Error: malformed command\n");
        exit(1);
      }
      token_arrival_time = getNum(argv[i+1],R);
      if(token_arrival_time > S_MS * MAX_TOKEN_ARRIVAL_TIME){
        token_arrival_time = S_MS * MAX_TOKEN_ARRIVAL_TIME;
      }
      i+=2;
      continue;
    }
    if (strcmp(argv[i],"-B")==0) {
      if (i+1 >=argc) {
        fprintf(stderr, "Error: malformed command\n");
        exit(1);
      }
       bucket_depth = getIntNum(argv[i+1]);
       if(bucket_depth > MAX_INTEGER) {
         fprintf(stderr, "Error: B is too large\n");
         exit(1);
       }
       i+=2;
       continue;
    }
    if (strcmp(argv[i],"-P")==0) {
      if (i+1 >=argc) {
        fprintf(stderr, "Error: malformed command\n");
        exit(1);
      }
      req_tokens = getIntNum(argv[i+1]);
      if(req_tokens > MAX_INTEGER) {
        fprintf(stderr, "Error: P is too large\n");
        exit(1);
      }
      i+=2;
      continue;
    }
    if (strcmp(argv[i],"-n")==0) {
      if (i+1 >=argc) {
        fprintf(stderr, "Error: malformed command\n");
        exit(1);
      }
      total_number_packet = getIntNum(argv[i+1]);
      if(total_number_packet > MAX_INTEGER) {
        fprintf(stderr, "Error: Num is too large\n");
        exit(1);
      }
      i+=2;
      continue;
    }
    if (strcmp(argv[i],"-t")==0) {
      if (i+1 >=argc) {
        fprintf(stderr, "Error: malformed command\n");
        exit(1);
      }
      isFile = 1;
      filename = argv[i+1];
      i += 2;
      continue;
    }
    fprintf(stderr, "Error: malformed command\n");
    exit(1);
  }
  if (lambda == -1) {
    lambda = DEFAULT_LAMBDA;
  }
  if (mu == -1) {
    mu = DEFAULT_MU;
  }
  if (token_arrival_time == -1) {
    r = DEFAULT_R;
    token_arrival_time = (1*SEC_TO_USEC)/DEFAULT_R;
  }
  if (bucket_depth == -1) {
    bucket_depth = DEFAULT_B;
  }

}

void printPara() {
  fprintf(stdout, "Emulation Parameters:\n");
  fprintf(stdout, "\tnumber to arrive = %ld\n", total_number_packet);
  if(isFile==1){
    fprintf(stdout, "\tr = %g\n", r);
    fprintf(stdout, "\tB = %ld\n", bucket_depth);
    fprintf(stdout, "\ttsfile = %s\n", filename);
  }
  else{
      fprintf(stdout, "\tlambda = %g\n",lambda);
      fprintf(stdout, "\tmu = %g\n",mu);
      fprintf(stdout, "\tr = %g\n", r);
      fprintf(stdout, "\tB = %ld\n", bucket_depth);
      fprintf(stdout, "\tP = %ld\n", req_tokens);
  }
}

long getIntFromFile(char *buffer, int num_line){
  int lo = 0;
  long tmp = 0;
  while (buffer[lo] >= '0' && buffer[lo] <='9') {
    tmp = tmp*10 + (buffer[lo] - '0');
    lo++;
  }
  if(lo == 0 || (buffer[lo] != '\0' && buffer[lo] != '\n')){
      fprintf(stderr,"Error: malformed input - line %d is not just a number\n", num_line);
      exit(1);
  }
  return tmp;
}

void processLine(char *buffer, int num_line){
  int lo = 0;
  int pre = 0;
  long tmp = 0;

  while (buffer[lo] >= '0' && buffer[lo] <='9') {
    tmp = tmp*10 + (buffer[lo] - '0');
    lo++;
    if(tmp > MAX_INTEGER){
      fprintf(stderr,"Error: illegal number(too greater number) in line %d \n",num_line);
      exit(1);
    }
  }
  if(pre == lo ){
    fprintf(stderr,"Error: illegal type in line %d \n",num_line);
    exit(1);
  }
  inter_arrival_time = tmp*S_MS;
  tmp = 0;
  pre = lo;
  while (buffer[lo] == '\t' || buffer[lo] ==' ') {
    lo++;
  }
  if(pre == lo ){
    fprintf(stderr,"Error: illegal type in line %d \n",num_line);
    exit(1);
  }
  pre = lo;

  while (buffer[lo] >= '0' && buffer[lo] <='9') {
    tmp = tmp*10 + (buffer[lo] - '0');
    lo++;
    if(tmp > MAX_INTEGER){
      fprintf(stderr,"Error: illegal number(too greater number) in line %d \n",num_line);
      exit(1);
    }
  }
  if(pre == lo ){
    fprintf(stderr,"Error: illegal type in line %d \n",num_line);
    exit(1);
  }
  req_tokens = tmp;
  tmp = 0;
  pre = lo;
  while (buffer[lo] == '\t' || buffer[lo] ==' ') {
    lo++;
  }
  if(pre == lo ){
    fprintf(stderr,"Error: illegal type in line %d \n",num_line);
    exit(1);
  }
  pre = lo;

  while (buffer[lo] >= '0' && buffer[lo] <='9') {
    tmp = tmp*10 + (buffer[lo] - '0');
    lo++;
    if(tmp > MAX_INTEGER){
      fprintf(stderr,"Error: illegal number(too greater number) in line %d \n",num_line);
      exit(1);
    }
  }
  if(pre == lo ){
    fprintf(stderr,"Error: illegal type in line %d \n",num_line);
    exit(1);
  }
  service_time = tmp*S_MS;
  if(buffer[lo]!='\0'&&buffer[lo]!='\n'){
    fprintf(stderr,"Error: illegal type in line %d \n",num_line);
    exit(1);
  }
}

void trace_driven(){
    FILE *file;
    if (!(file = fopen(filename, "r"))) {
      perror(filename);
      exit(1);
    }
    int buff_size = 1024;
    char buffer[buff_size + 1];
    buffer[buff_size-1] = '\0';
    int num_line = 1;
    while (fgets(buffer, buff_size + 1, file)) {
      if(buffer[buff_size-1] != '\0'){
        fprintf(stderr,"Error: Line %d is too long\n",num_line);
        exit(1);
      }

      if(num_line == 1){
        total_number_packet = getIntFromFile(buffer, num_line);
      }
      else{
        Packet *packet = (Packet*)malloc(sizeof(Packet));
        processLine(buffer, num_line);
        initP(packet, num_line-1);
        My402ListAppend(&allPackets, (void*)packet);
      }
      num_line++;

    }
    if(num_line != total_number_packet + 2){
      fprintf(stderr,"Error: the number of packets not equal to n\n");
      exit(1);
    }
    fclose(file);
}

void  deterministic() {
  if (token_arrival_time == -1) {
    r = DEFAULT_R;
    token_arrival_time = (long)((1*SEC_TO_USEC)/DEFAULT_R);
  }
  if (total_number_packet == -1) {
    total_number_packet = DEFAULT_NUM;
  }
  if (inter_arrival_time ==-1) {
    inter_arrival_time = (long)(1*SEC_TO_USEC/DEFAULT_LAMBDA);
  }
  if (service_time == -1) {
    service_time = (long)(1*SEC_TO_USEC/DEFAULT_MU);
  }
  if (bucket_depth == -1) {
    bucket_depth = DEFAULT_B;
  }

  if (req_tokens == -1) {
    req_tokens = DEFAULT_P;
  }
  int num = 1;
  Packet *packet = (Packet*)malloc(sizeof(Packet));
  initP(packet, num);
  My402ListAppend(&allPackets, (void*)packet);
}

void addAllPackets(){
  if (isFile==1) {
    trace_driven();
  }
  else{
    deterministic();
  }
  remain_packet_num = total_number_packet;
}

void initQ() {
  memset(&firstQ, 0, sizeof(My402List));
  (void)My402ListInit(&firstQ);
  memset(&secondQ, 0, sizeof(My402List));
  (void)My402ListInit(&secondQ);
  memset(&allPackets, 0, sizeof(My402List));
  (void)My402ListInit(&allPackets);
}

void statistics() {
  double avg_inter_arr_time = 0;
  double avg_service_time = 0;
  double p_in_q1 = 0;
  double p_in_q2 = 0;
  double p_in_s1 = 0;
  double p_in_s2 = 0;
  double avg_time_sys = 0;
  double sta_dev = 0;
  double t_drop_prob = 0;
  double p_drop_prob = 0;
  int all_valid_num = 0;
  int completed_num = 0;
  My402ListElem * iterator = My402ListFirst(&allPackets);
  while (iterator != NULL) {
    Packet * packet = (Packet *)iterator->obj;
    if(packet -> true_inter_arr_time != -1){
      avg_inter_arr_time += packet -> true_inter_arr_time;
      all_valid_num ++;
      if(packet -> time_in_sys != -1){
        completed_num ++;
        avg_service_time += packet -> true_service_time;
        p_in_q1 += packet -> time_in_q1;
        p_in_q2 += packet ->time_in_q2;
        if(packet -> server == 1)
          p_in_s1 += packet -> true_service_time;
        else
          p_in_s2 += packet -> true_service_time;
        avg_time_sys += packet -> time_in_sys;
        sta_dev += (packet -> time_in_sys) * (packet -> time_in_sys);
      }
    }
    iterator = My402ListNext(&allPackets, iterator);
  }
  avg_inter_arr_time = (all_valid_num==0)? 0 : avg_inter_arr_time/all_valid_num;
  avg_service_time = (completed_num==0)? 0 : avg_service_time/completed_num;
  p_in_q1 /= end_time;
  p_in_q2 /= end_time;
  p_in_s1 /= end_time;
  p_in_s2 /= end_time;
  avg_time_sys = (completed_num==0)? 0 : avg_time_sys/completed_num;
  sta_dev = (completed_num==0) ? 0 : sqrt((sta_dev/completed_num) - avg_time_sys*avg_time_sys);
  t_drop_prob = (sumTokens == 0)? 0:(double)drop_tokens / sumTokens;
  p_drop_prob = (all_valid_num==0)? 0 : (double)drop_packets / all_valid_num;
  fprintf(stdout, "\nStatistics:\n");
  fprintf(stdout, "\n\taverage packet inter-arrival time = %.6g\n", avg_inter_arr_time);
  fprintf(stdout, "\taverage packet service time = %.6g\n", avg_service_time);
  fprintf(stdout, "\n\taverage number of packets in Q1 = %.6g\n", p_in_q1);
  fprintf(stdout, "\taverage number of packets in Q2 = %.6g\n", p_in_q2);
  fprintf(stdout, "\taverage number of packets at S1 = %.6g\n", p_in_s1);
  fprintf(stdout, "\taverage number of packets at S2 = %.6g\n", p_in_s2);
  fprintf(stdout, "\n\taverage time a packet spent in system = %.6g\n", avg_time_sys);
  fprintf(stdout, "\tstandard deviation for time spent in system = %.6g\n", sta_dev);
  fprintf(stdout, "\n\ttoken drop probability = %.6g\n", t_drop_prob);
  fprintf(stdout, "\tpacket drop probability = %.6g\n", p_drop_prob);
  exit(1);

}

void *processPacket(void *arg) {
  My402ListElem* cur = My402ListFirst(&allPackets);
  int num = 2;
  for (;;) {

    if(isFile != 1){
      pthread_mutex_lock(&mutex);
      if (num <= total_number_packet) {
        Packet *packet = (Packet*)malloc(sizeof(Packet));
        initP(packet, num);
        My402ListAppend(&allPackets, (void*)packet);
        num++;
      }
      pthread_mutex_unlock(&mutex);
    }
    if(cur != NULL){
      Packet* packet = (Packet*) (cur -> obj);
      long inter_arrival_time = packet-> inter_arrival_time;

      usleep(inter_arrival_time);
      pthread_mutex_lock(&mutex);
      gettimeofday(&timestamp, NULL);
      e_sec = timestamp.tv_sec;
      e_usec = timestamp.tv_usec;
      packet->arrives_time = e_sec* SEC_TO_USEC + e_usec;
      cur_time = (double)(packet->arrives_time - start_time)/S_MS;
      if (cur == My402ListFirst(&allPackets)) {
        packet->true_inter_arr_time = cur_time;
        if(packet->req_tokens <= bucket_depth){
          fprintf(stdout, "%012.3fms: packet p%ld arrives, needs %d tokens, inter-arrival time = %.3fms\n", cur_time, packet->num, packet->req_tokens, cur_time);
        }
        else{
          drop_packets++;
          fprintf(stdout, "%012.3fms: packet p%ld arrives, needs %d tokens, inter-arrival time = %.3fms, dropped\n", cur_time, packet->num, packet->req_tokens, cur_time);
        }
      }
      else{
        My402ListElem* pre = My402ListPrev(&allPackets, cur);
        double time_interval =  ((double)((packet->arrives_time) -((Packet*)pre->obj)->arrives_time))/S_MS;
        packet->true_inter_arr_time = time_interval;
        if(packet->req_tokens <= bucket_depth){
          fprintf(stdout, "%012.3fms: packet p%ld arrives, needs %d tokens, inter-arrival time = %.3fms\n", cur_time, packet->num, packet->req_tokens, time_interval);
        }
        else{
          drop_packets++;
          fprintf(stdout, "%012.3fms: packet p%ld arrives, needs %d tokens, inter-arrival time = %.3fms, dropped\n", cur_time, packet->num, packet->req_tokens, time_interval);
        }
      }
      if(packet->req_tokens <= bucket_depth){
        My402ListAppend(&firstQ,(void*)packet);
        gettimeofday(&timestamp,NULL);
        e_sec = timestamp.tv_sec;
        e_usec= timestamp.tv_usec;
        packet -> enter_q1 = e_sec * SEC_TO_USEC + e_usec;
        cur_time = (double)(packet->enter_q1 - start_time)/S_MS;
        fprintf(stdout, "%012.3fms: p%ld enters Q1\n", cur_time, packet->num);
      }
      cur = My402ListNext(&allPackets, cur);
    }

    if(My402ListFirst(&firstQ) && curTokens >= (((Packet*)(My402ListFirst(&firstQ)->obj))->req_tokens)){
      Packet* first = (Packet*)My402ListFirst(&firstQ)->obj;
      My402ListUnlink(&firstQ, My402ListFirst(&firstQ));
      gettimeofday(&timestamp, NULL);
      e_sec = timestamp.tv_sec;
      e_usec= timestamp.tv_usec;
      first -> exit_q1 = e_sec * SEC_TO_USEC + e_usec;
      curTokens -= first -> req_tokens;
      cur_time = (double)(first -> exit_q1 - start_time)/S_MS;
      first -> time_in_q1 = (double)(first -> exit_q1 - first -> enter_q1)/S_MS;
      fprintf(stdout, "%012.3fms: p%ld leaves Q1, time in Q1 = %.3fms, token bucket now has %d token\n",cur_time,  first->num,  first -> time_in_q1, curTokens);
      My402ListAppend(&secondQ, (void*)first);
      gettimeofday(&timestamp,NULL);
      e_sec = timestamp.tv_sec;
      e_usec= timestamp.tv_usec;
      first -> enter_q2 = e_sec * SEC_TO_USEC + e_usec;
      packet_enter_q2 ++ ;
      cur_time = (first -> enter_q2 - start_time)/S_MS;
      fprintf(stdout, "%012.3fms: p%ld enters Q2\n",cur_time,  first->num);
      pthread_cond_signal(&cv2);
      pthread_mutex_unlock(&mutex);
    }
    if (packet_enter_q2 + drop_packets == total_number_packet && remain_packet_num != drop_packets) {
      pthread_cond_signal(&cv2);
      pthread_mutex_unlock(&mutex);
    }
    if(remain_packet_num == drop_packets){
      pthread_cond_signal(&cv2);
      pthread_cond_signal(&cv2);
      pthread_mutex_unlock(&mutex);
      return 0;
    }
    pthread_mutex_unlock(&mutex);
    }
}

void *processToken(void *arg) {
  for (;;) {
    if(packet_enter_q2 + drop_packets == total_number_packet){
      return 0;
    }
    usleep(token_arrival_time);
    pthread_mutex_lock(&mutex);
    sumTokens++;
    gettimeofday(&timestamp, NULL);
    e_sec = timestamp.tv_sec;
    e_usec= timestamp.tv_usec;
    cur_time = (double)( e_sec * SEC_TO_USEC + e_usec - start_time)/S_MS;
    if(curTokens < bucket_depth){
      curTokens ++;
      fprintf(stdout, "%012.3fms: token t%ld arrives, token bucket now has %d token\n", cur_time, sumTokens, curTokens);
    }
    else{
      drop_tokens ++;
      fprintf(stdout, "%012.3fms: token t%ld arrives, dropped\n", cur_time, sumTokens);
    }
    pthread_mutex_unlock(&mutex);
  }
}

void *serve(void *arg) {
  for (;;) {
    pthread_mutex_lock(&mutex);
    if(remain_packet_num == drop_packets){
      pthread_cond_signal(&cv1);
      pthread_mutex_unlock(&mutex);
      return 0;
    }
    if(isCtrlC==1 && cur_serve_packets==0){
      pthread_cond_signal(&cv1);
      pthread_mutex_unlock(&mutex);
      return 0;
    }
    if(My402ListEmpty(&secondQ)){
      pthread_cond_wait(&cv2, &mutex);
    }
    if(isCtrlC==1 && cur_serve_packets!=0){
      pthread_mutex_unlock(&mutex);
      return 0;
    }
    My402ListElem *cur = My402ListFirst(&secondQ);
    if(cur == NULL){
      pthread_mutex_unlock(&mutex);
      continue;
    }
    cur_serve_packets ++;
    Packet* packet = (Packet*)(cur->obj);
    if(isFirst == 1){
      packet -> server = 1;
      isFirst = 0;
    }
    else{
      packet -> server = 2;
      isFirst = 1;
    }
    long service_time = (long)(((double)packet->service_time)/S_MS + 0.5);
    My402ListUnlink(&secondQ,cur);
    gettimeofday(&timestamp, NULL);
    e_sec = timestamp.tv_sec;
    e_usec= timestamp.tv_usec;
    packet -> exit_q2 = e_sec * SEC_TO_USEC + e_usec;
    cur_time = (double)( e_sec * SEC_TO_USEC + e_usec - start_time)/S_MS;
    packet -> time_in_q2 = (double)(packet -> exit_q2 - packet -> enter_q2)/S_MS;
    fprintf(stdout, "%012.3fms: p%ld leaves Q2, time in Q2 = %.3fms\n",cur_time,  packet->num,  packet -> time_in_q2);
    gettimeofday(&timestamp, NULL);
    e_sec = timestamp.tv_sec;
    e_usec= timestamp.tv_usec;
    packet -> begin_services = e_sec * SEC_TO_USEC + e_usec;
    cur_time = (double)( e_sec * SEC_TO_USEC + e_usec - start_time)/S_MS;
    fprintf(stdout, "%012.3fms: p%ld begins service at S%d, requesting %ldms of service\n",cur_time, packet->num, packet->server, service_time);
    pthread_mutex_unlock(&mutex);
    usleep(service_time * S_MS);
    pthread_mutex_lock(&mutex);
    gettimeofday(&timestamp, NULL);
    e_sec = timestamp.tv_sec;
    e_usec = timestamp.tv_usec;
    cur_time = (double)( e_sec * SEC_TO_USEC + e_usec - start_time)/S_MS;
    packet -> service_done = e_sec * SEC_TO_USEC + e_usec;
    packet -> true_service_time = (double)(packet -> service_done - packet-> begin_services)/S_MS;
    packet -> time_in_sys = (double)(packet ->  service_done - packet-> arrives_time)/S_MS;
    fprintf(stdout, "%012.3fms: p%ld departs from S%d, service time = %.3fms, time in system = %.3fms\n",cur_time, packet->num, packet->server, packet -> true_service_time, packet -> time_in_sys);
    remain_packet_num--;
    cur_serve_packets--;
    pthread_mutex_unlock(&mutex);
  }
}

void *processCtrlC(void *arg) {
  int sig = 0;
  sigwait(&set,&sig);
  pthread_mutex_lock(&mutex);
  gettimeofday(&timestamp, NULL);
  e_sec = timestamp.tv_sec;
  e_usec = timestamp.tv_usec;
  cur_time = (double)( e_sec * SEC_TO_USEC + e_usec - start_time) / S_MS;
  fprintf(stdout, "\n%012.3fms: SIGINT caught, no new packets or tokens will be allowed\n", cur_time);
  isCtrlC = 1;
  pthread_cancel(packetTrasport);
  pthread_cancel(tokenStore);
  My402ListElem * iterator = My402ListFirst(&firstQ);
  while (iterator != NULL) {
    Packet * packet = (Packet *)(iterator -> obj);
    gettimeofday(&timestamp, NULL);
    e_sec = timestamp.tv_sec;
    e_usec = timestamp.tv_usec;
    cur_time = (double)( e_sec * SEC_TO_USEC + e_usec - start_time) / S_MS;
    fprintf(stdout, "%012.3fms: p%ld removed from Q1\n", cur_time, packet -> num);
    iterator = My402ListNext(&firstQ, iterator);
  }
  iterator = My402ListFirst(&secondQ);
  while (iterator != NULL) {
    Packet * packet = (Packet *)(iterator -> obj);
    gettimeofday(&timestamp, NULL);
    e_sec = timestamp.tv_sec;
    e_usec = timestamp.tv_usec;
    cur_time = (double)( e_sec * SEC_TO_USEC + e_usec - start_time) / S_MS;
    fprintf(stdout, "%012.3fms: p%ld removed from Q2\n", cur_time, packet -> num);
    iterator = My402ListNext(&secondQ, iterator);
  }
  if(cur_serve_packets == 0){
    gettimeofday(&timestamp, NULL);
    e_sec = timestamp.tv_sec;
    e_usec= timestamp.tv_usec;
    cur_time = (double)( e_sec * SEC_TO_USEC + e_usec - start_time)/S_MS;
    fprintf(stdout, "%012.3fms: emulation ends\n",cur_time);
    end_time = cur_time;
    statistics();
    return 0;
  }
  if(cur_serve_packets == 2 || cur_serve_packets == 1){
    pthread_cond_wait(&cv1, &mutex);
    gettimeofday(&timestamp, NULL);
    e_sec = timestamp.tv_sec;
    e_usec= timestamp.tv_usec;
    cur_time = (double)( e_sec * SEC_TO_USEC + e_usec - start_time)/S_MS;
    fprintf(stdout, "%012.3fms: emulation ends\n",cur_time);
    end_time = cur_time;
    statistics();
    return 0;
  }
  pthread_cond_broadcast(&cv2);
  pthread_mutex_unlock(&mutex);
  return 0;
}

void *end(void *arg) {
    pthread_mutex_lock(&mutex);
    pthread_cond_wait(&cv1, &mutex);
    gettimeofday(&timestamp, NULL);
    e_sec = timestamp.tv_sec;
    e_usec= timestamp.tv_usec;
    cur_time = (double)( e_sec * SEC_TO_USEC + e_usec - start_time)/S_MS;
    fprintf(stdout, "%012.3fms: emulation ends\n",cur_time);
    end_time = cur_time;
    statistics();
    return 0;
}

int main(int argc, char *argv[]){
    sigemptyset(&set);
    sigaddset(&set,SIGINT);
    sigprocmask(SIG_BLOCK,&set, 0);
    initQ();
    commandLine(argc, argv);
    addAllPackets();
    printPara();
    gettimeofday(&timestamp,NULL);
    e_sec = timestamp.tv_sec;
    e_usec= timestamp.tv_usec;
    start_time = e_sec * SEC_TO_USEC + e_usec;
    cur_time = (double)( e_sec * SEC_TO_USEC + e_usec - start_time)/S_MS;
    fprintf(stdout, "\n%012.3fms: emulation begins\n", cur_time);
    pthread_create(&packetTrasport, 0, processPacket, (void *)0);
    pthread_create(&tokenStore, 0, processToken, (void *)0);
    pthread_create(&serverF, 0, serve, (void *)0);
    pthread_create(&serverS, 0, serve, (void *)0);
    pthread_create(&monitor, 0, processCtrlC, (void *)0);
    pthread_create(&success, 0, end, (void *)0);
    pthread_join(packetTrasport, 0);
    pthread_join(tokenStore, 0);
    pthread_join(serverF, 0);
    pthread_join(serverS, 0);
    pthread_join(monitor,0);
    pthread_join(success,0);
    return(0);
}
