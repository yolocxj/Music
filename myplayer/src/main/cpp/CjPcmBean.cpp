//
// Created by cxj on 2020-4-1.
//

#include "CjPcmBean.h"

CjPcmBean::CjPcmBean(SAMPLETYPE *buffer, int size) {

    this->buffer = (char *) malloc(size);
    this->buffsize = size;
    memcpy(this->buffer, buffer, size);

}

CjPcmBean::~CjPcmBean() {
    free(buffer);
    buffer = NULL;
}
