//
// Created by cxj on 2020-4-1.
//

#ifndef WLMUSIC_PCMBEAN_H
#define WLMUSIC_PCMBEAN_H

#include <SoundTouch.h>

using namespace soundtouch;

class CjPcmBean {

public:
    char *buffer;
    int buffsize;

public:
    CjPcmBean(SAMPLETYPE *buffer, int size);
    ~CjPcmBean();


};


#endif //WLMUSIC_PCMBEAN_H
