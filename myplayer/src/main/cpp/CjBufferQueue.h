//
// Created by cxj on 2020-12-3.
//

#ifndef WLPLAYER_BUFFERQUEUE_H
#define WLPLAYER_BUFFERQUEUE_H

#include "deque"
#include "CjPlaystatus.h"
#include "CjPcmBean.h"

extern "C"
{
#include <libavcodec/avcodec.h>
#include "pthread.h"
};

class CjBufferQueue {

public:
    std::deque<CjPcmBean *> queueBuffer;
    pthread_mutex_t mutexBuffer;
    pthread_cond_t condBuffer;
    CjPlaystatus *cjPlayStatus = NULL;

public:
    CjBufferQueue(CjPlaystatus *playStatus);
    ~CjBufferQueue();
    int putBuffer(SAMPLETYPE *buffer, int size);
    int getBuffer(CjPcmBean **pcmBean);
    int clearBuffer();

    void release();
    int getBufferSize();

    int noticeThread();
};


#endif //WLPLAYER_BUFFERQUEUE_H
