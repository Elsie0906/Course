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
 * @(#)$Id: warmup2.h,v 1.1 2018/09/18 20:00:00 Elsie Exp $
 */

#ifndef _WARMUP2_H_
#define _WARMUP2_H_

#include "cs402.h"
#include "my402list.h"

#define DEFAULT_LAMBDA 1
#define DEFAULT_MU 0.35
#define DEFAULT_R 1.5
#define DEFAULT_B 10
#define DEFAULT_P 3
#define DEFAULT_NUM 20

#define S_MS 1000
#define SEC_TO_USEC 1000000
#define MAX_TOKEN_ARRIVAL_TIME 10000
#define MAX_INTER_ARRIVAL_TIME 10000
#define MAX_SERVICE_TIME 10000

#define internal_size 1026

typedef struct tagMySetting {
	My402List packetList;
	My402List firstQ;
	My402List secondQ;
	long start_time;
	long end_time;
	long token_arrival_time;
	long sumTokens;		
	double lambda;
	double mu;
	double r;
	int bucket_depth;
	int req_token;
	int total_packet_num;
	int curTokens;
	int drop_tokens;
	int delivered_packets;
	int processed_packets;
	int drop_packets;
	bool isFile;
	bool isFirst;
	bool isCtrlC;
	char * filename;
} MySetting;

typedef struct tagPacket {
    long service_time;
    long inter_arrival_time;
    long packet_arrives_time;
    long enter_q1;
    long leave_q1;
    long enter_q2;
    long leave_q2;
    long begin_service;
    long end_service;
    double real_inter_arrival_time;
    double real_service_time;
    double time_in_q1;
    double time_in_q2;
    double total_process_time;
    int req_tokens;
    int packet_ID;
    int server;
} Packet;
							

#endif /*_WARMUP2_H_*/
