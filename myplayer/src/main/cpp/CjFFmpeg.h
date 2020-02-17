//
// Created by cxj on 2020-2-28.
//

#ifndef MYMUSIC_WLFFMPEG_H
#define MYMUSIC_WLFFMPEG_H

#include "CjCallJava.h"
#include "pthread.h"
#include "CjAudio.h"
#include "CjVideo.h"
#include "CjPlaystatus.h"

extern "C"
{
#include "libavformat/avformat.h"
#include <libavutil/time.h>
};


class CjFFmpeg {

public:
    CjCallJava *callJava = NULL;
    const char* url = NULL;
    pthread_t decodeThread;
    AVFormatContext *pFormatCtx = NULL;
    CjAudio *audio = NULL;
    CjVideo *video = NULL;
    CjPlaystatus *playstatus = NULL;
    pthread_mutex_t init_mutex;
    bool exit = false;
    int duration = 0;
    pthread_mutex_t seek_mutex;
    bool supportMediacodec = false;
    const AVBitStreamFilter *bsFilter = NULL;

public:
    CjFFmpeg(CjPlaystatus *playstatus, CjCallJava *callJava, const char *url);
    ~CjFFmpeg();

    void parpared();
    void decodeFFmpegThread();
    void start();

    void pause();

    void resume();

    void release();

    void seek(int64_t secds);

    void setVolume(int percent);

    void setMute(int mute);

    void setPitch(float pitch);

    void setSpeed(float speed);

    int getSampleRate();

    void startStopRecord(bool start);

    bool cutAudioPlay(int start_time, int end_time, bool showPcm);
    int getCodecContext(AVCodecParameters *codecpar, AVCodecContext **avCodecContext);

};


#endif //MYMUSIC_WLFFMPEG_H
