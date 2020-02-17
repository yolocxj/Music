//
// Created by cxj on 2020-12-3.
//

#include "CjBufferQueue.h"
#include "AndroidLog.h"

CjBufferQueue::CjBufferQueue(CjPlaystatus *playStatus) {
    cjPlayStatus = playStatus;
    pthread_mutex_init(&mutexBuffer, NULL);
    pthread_cond_init(&condBuffer, NULL);
}

CjBufferQueue::~CjBufferQueue() {
    cjPlayStatus = NULL;
    pthread_mutex_destroy(&mutexBuffer);
    pthread_cond_destroy(&condBuffer);
    if(LOG_DEBUG)
    {
        LOGE("CjBufferQueue 释放完了");
    }
}

void CjBufferQueue::release() {

    if(LOG_DEBUG)
    {
        LOGE("CjBufferQueue::release");
    }
    noticeThread();
    clearBuffer();

    if(LOG_DEBUG)
    {
        LOGE("CjBufferQueue::release success");
    }
}

int CjBufferQueue::putBuffer(SAMPLETYPE *buffer, int size) {
    pthread_mutex_lock(&mutexBuffer);
    CjPcmBean *pcmBean = new CjPcmBean(buffer, size);
    queueBuffer.push_back(pcmBean);
    pthread_cond_signal(&condBuffer);
    pthread_mutex_unlock(&mutexBuffer);
    return 0;
}

int CjBufferQueue::getBuffer(CjPcmBean **pcmBean) {

    pthread_mutex_lock(&mutexBuffer);

    while(cjPlayStatus != NULL && !cjPlayStatus->exit)
    {
        if(queueBuffer.size() > 0)
        {
            *pcmBean = queueBuffer.front();
            queueBuffer.pop_front();
            break;
        } else{
            if(!cjPlayStatus->exit)
            {
                pthread_cond_wait(&condBuffer, &mutexBuffer);
            }
        }
    }
    pthread_mutex_unlock(&mutexBuffer);
    return 0;
}

int CjBufferQueue::clearBuffer() {

    pthread_cond_signal(&condBuffer);
    pthread_mutex_lock(&mutexBuffer);
    while (!queueBuffer.empty())
    {
        CjPcmBean *pcmBean = queueBuffer.front();
        queueBuffer.pop_front();
        delete(pcmBean);
    }
    pthread_mutex_unlock(&mutexBuffer);
    return 0;
}

int CjBufferQueue::getBufferSize() {
    int size = 0;
    pthread_mutex_lock(&mutexBuffer);
    size = queueBuffer.size();
    pthread_mutex_unlock(&mutexBuffer);
    return size;
}


int CjBufferQueue::noticeThread() {
    pthread_cond_signal(&condBuffer);
    return 0;
}

