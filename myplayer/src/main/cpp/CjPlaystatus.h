//
// Created by cxj on 2020-3-6.
//

#ifndef MYMUSIC_WLPLAYSTATUS_H
#define MYMUSIC_WLPLAYSTATUS_H


class CjPlaystatus {

public:
    bool exit = false;
    bool load = true;
    bool seek = false;
    bool pause = false;

public:
    CjPlaystatus();
    ~CjPlaystatus();

};


#endif //MYMUSIC_WLPLAYSTATUS_H
