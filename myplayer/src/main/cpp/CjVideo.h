//
// Created by cxj on 2020/2/11.
//

#ifndef MYMUSIC_CJVIDEO_H
#define MYMUSIC_CJVIDEO_H

#include "CjQueue.h"
#include "CjCallJava.h"
#include "CjAudio.h"

#define CODEC_YUV 0
#define CODEC_MEDIACODEC 1

extern "C"
{
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
#include <libavutil/time.h>
#include <libavcodec/avcodec.h>
};

class CjVideo {

public:
    int streamIndex = -1;
    AVCodecContext *avCodecContext = NULL;
    AVCodecParameters *codecpar = NULL;
    CjQueue *queue = NULL;
    CjPlaystatus *playstatus = NULL;
    CjCallJava *cjCallJava = NULL;
    AVRational time_base;
    CjAudio *audio = NULL;
    pthread_t thread_play;
    double clock = 0;
    double delayTime = 0;
    double defaultDelayTime = 0.04;
    pthread_mutex_t codecMutex;
    int codectype = CODEC_YUV;
    AVBSFContext *abs_ctx = NULL;

public:
    CjVideo(CjPlaystatus *playstatus, CjCallJava *cjCallJava);
    ~CjVideo();

    void play();
    void release();
    //获取差值
    double getFrameDiffTime(AVFrame *avFrame, AVPacket *avPacket);

    double getDelayTime(double diff);



};


#endif //MYMUSIC_WLVIDEO_H
