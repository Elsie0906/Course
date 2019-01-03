#include "cs402.h"

typedef struct tagPacket {
    long service_time;
    long inter_arrival_time;
    int req_tokens;
    long num;
    long arrives_time;
    long enter_q1;
    long exit_q1;
    long enter_q2;
    long exit_q2;
    long begin_services;
    long service_done;
    int server;
    double true_inter_arr_time;
    double true_service_time;
    double time_in_q1;
    double time_in_q2;
    double time_in_sys;
} Packet;
