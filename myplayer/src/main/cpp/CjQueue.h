//
// Created by cxj on 2020/2/7.
//

#ifndef MYMUSIC_CJQUEUE_H
#define MYMUSIC_CJQUEUE_H

#include "queue"
#include "pthread.h"
#include "AndroidLog.h"
#include "CjPlaystatus.h"

extern "C"
{
#include "libavcodec/avcodec.h"
};
class CjQueue {
public:
    std::queue<AVPacket *> queuePacket;
    pthread_mutex_t mutexPacket;
    pthread_cond_t condPacket;
    CjPlaystatus *playstatus = NULL;

public:

    CjQueue(CjPlaystatus *playstatus);
    ~CjQueue();

    int putAvpacket(AVPacket *packet);
    int getAvpacket(AVPacket *packet);

    int getQueueSize();
    void clearAvpacket();
    void noticeQueue();

};


#endif //MYMUSIC_CJQUEUE_H
