//
// Created by cxj on 2020-2-28.
//

#include "CjCallJava.h"

CjCallJava::CjCallJava(_JavaVM *javaVM, JNIEnv *env, jobject *obj) {

    this->javaVM = javaVM;
    this->jniEnv = env;
    this->jobj = *obj;
    this->jobj = env->NewGlobalRef(jobj);

    jclass  jlz = jniEnv->GetObjectClass(jobj);
    if(!jlz)
    {
        if(LOG_DEBUG)
        {
            LOGE("get jclass wrong");
        }
        return;
    }

    jmid_parpared = env->GetMethodID(jlz, "onCallParpared", "()V");
    jmid_load = env->GetMethodID(jlz, "onCallLoad", "(Z)V");
    jmid_timeinfo = env->GetMethodID(jlz, "onCallTimeInfo", "(II)V");
    jmid_error = env->GetMethodID(jlz, "onCallError", "(ILjava/lang/String;)V");
    jmid_complete = env->GetMethodID(jlz, "onCallComplete", "()V");
    jmid_valumedb = env->GetMethodID(jlz, "onCallValumeDB", "(I)V");
    jmid_pcmtoaac = env->GetMethodID(jlz, "encodecPcmToAAc", "(I[B)V");
    jmid_pcminfo = env->GetMethodID(jlz, "onCallPcmInfo", "([BI)V");
    jmid_pcmrate = env->GetMethodID(jlz, "onCallPcmRate", "(I)V");
    jmid_renderyuv = env->GetMethodID(jlz, "onCallRenderYUV", "(II[B[B[B)V");
    jmid_supportvideo = env->GetMethodID(jlz, "onCallIsSupportMediaCodec", "(Ljava/lang/String;)Z");
    jmid_initmediacodec = env->GetMethodID(jlz, "initMediaCodec", "(Ljava/lang/String;II[B[B)V");
    jmid_decodeavpacket = env->GetMethodID(jlz, "decodeAVPacket", "(I[B)V");

}

void CjCallJava::onCallParpared(int type) {

    if(type == MAIN_THREAD)
    {
        jniEnv->CallVoidMethod(jobj, jmid_parpared);
    }
    else if(type == CHILD_THREAD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            if(LOG_DEBUG)
            {
                LOGE("get child thread jnienv worng");
            }
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_parpared);
        javaVM->DetachCurrentThread();
    }

}

void CjCallJava::onCallLoad(int type, bool load) {

    if(type == MAIN_THREAD)
    {
        jniEnv->CallVoidMethod(jobj, jmid_load, load);
    }
    else if(type == CHILD_THREAD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            if(LOG_DEBUG)
            {
                LOGE("call onCallLoad worng");
            }
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_load, load);
        javaVM->DetachCurrentThread();
    }


}

void CjCallJava::onCallTimeInfo(int type, int curr, int total) {
    if(type == MAIN_THREAD)
    {
        jniEnv->CallVoidMethod(jobj, jmid_timeinfo, curr, total);
    }
    else if(type == CHILD_THREAD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            if(LOG_DEBUG)
            {
                LOGE("call onCallTimeInfo worng");
            }
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_timeinfo, curr, total);
        javaVM->DetachCurrentThread();
    }
}

CjCallJava::~CjCallJava() {

}

void CjCallJava::onCallError(int type, int code, char *msg) {
    if(type == MAIN_THREAD)
    {
        jstring jmsg = jniEnv->NewStringUTF(msg);
        jniEnv->CallVoidMethod(jobj, jmid_error, code, jmsg);
        jniEnv->DeleteLocalRef(jmsg);
    }
    else if(type == CHILD_THREAD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            if(LOG_DEBUG)
            {
                LOGE("call onCallError worng");
            }
            return;
        }
        jstring jmsg = jniEnv->NewStringUTF(msg);
        jniEnv->CallVoidMethod(jobj, jmid_error, code, jmsg);
        jniEnv->DeleteLocalRef(jmsg);
        javaVM->DetachCurrentThread();
    }
}

void CjCallJava::onCallComplete(int type) {
    if(type == MAIN_THREAD)
    {
        jniEnv->CallVoidMethod(jobj, jmid_complete);
    }
    else if(type == CHILD_THREAD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            if(LOG_DEBUG)
            {
                LOGE("call onCallComplete worng");
            }
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_complete);
        javaVM->DetachCurrentThread();
    }
}

void CjCallJava::onCallValumeDB(int type, int db) {
    if(type == MAIN_THREAD)
    {
        jniEnv->CallVoidMethod(jobj, jmid_valumedb, db);
    }
    else if(type == CHILD_THREAD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            if(LOG_DEBUG)
            {
                LOGE("call onCallComplete worng");
            }
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_valumedb, db);
        javaVM->DetachCurrentThread();
    }
}

void CjCallJava::onCallPcmToAAc(int type, int size, void *buffer) {

    if(type == MAIN_THREAD)
    {
        jbyteArray jbuffer = jniEnv->NewByteArray(size);
        jniEnv->SetByteArrayRegion(jbuffer, 0, size, static_cast<const jbyte *>(buffer));

        jniEnv->CallVoidMethod(jobj, jmid_pcmtoaac, size, jbuffer);

        jniEnv->DeleteLocalRef(jbuffer);

    }
    else if(type == CHILD_THREAD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            if(LOG_DEBUG)
            {
                LOGE("call onCallComplete worng");
            }
            return;
        }
        jbyteArray jbuffer = jniEnv->NewByteArray(size);
        jniEnv->SetByteArrayRegion(jbuffer, 0, size, static_cast<const jbyte *>(buffer));

        jniEnv->CallVoidMethod(jobj, jmid_pcmtoaac, size, jbuffer);

        jniEnv->DeleteLocalRef(jbuffer);

        javaVM->DetachCurrentThread();
    }

}

void CjCallJava::onCallPcmInfo(void *buffer, int size) {
    JNIEnv *jniEnv;
    if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
    {
        if(LOG_DEBUG)
        {
            LOGE("call onCallComplete worng");
        }
        return;
    }
    jbyteArray jbuffer = jniEnv->NewByteArray(size);
    jniEnv->SetByteArrayRegion(jbuffer, 0, size, static_cast<const jbyte *>(buffer));

    jniEnv->CallVoidMethod(jobj, jmid_pcminfo, jbuffer, size);

    jniEnv->DeleteLocalRef(jbuffer);

    javaVM->DetachCurrentThread();
}

void CjCallJava::onCallPcmRate(int samplerate) {
    JNIEnv *jniEnv;
    if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
    {
        if(LOG_DEBUG)
        {
            LOGE("call onCallComplete worng");
        }
        return;
    }
    jniEnv->CallVoidMethod(jobj, jmid_pcmrate, samplerate);

    javaVM->DetachCurrentThread();
}


void CjCallJava::onCallRenderYUV(int width, int height, uint8_t *fy, uint8_t *fu, uint8_t *fv) {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            if(LOG_DEBUG)
            {
                LOGE("call onCallComplete worng");
            }
            return;
        }

        jbyteArray y = jniEnv->NewByteArray(width * height);
        jniEnv->SetByteArrayRegion(y, 0, width * height, reinterpret_cast<const jbyte *>(fy));

        jbyteArray u = jniEnv->NewByteArray(width * height / 4);
        jniEnv->SetByteArrayRegion(u, 0, width * height / 4, reinterpret_cast<const jbyte *>(fu));

        jbyteArray v = jniEnv->NewByteArray(width * height / 4);
        jniEnv->SetByteArrayRegion(v, 0, width * height / 4, reinterpret_cast<const jbyte *>(fv));

        jniEnv->CallVoidMethod(jobj, jmid_renderyuv, width, height, y, u, v);

        jniEnv->DeleteLocalRef(y);
        jniEnv->DeleteLocalRef(u);
        jniEnv->DeleteLocalRef(v);

        javaVM->DetachCurrentThread();

}

bool CjCallJava::onCallIsSupportVideo(const char *ffcodecname) {
    bool support = false;
    JNIEnv *jniEnv;
    if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
    {
        if(LOG_DEBUG)
        {
            LOGE("call onCallComplete worng");
        }
        return support;
    }

    jstring type = jniEnv->NewStringUTF(ffcodecname);
    support = jniEnv->CallBooleanMethod(jobj, jmid_supportvideo, type);
    jniEnv->DeleteLocalRef(type);
    javaVM->DetachCurrentThread();
    return support;
}

void CjCallJava::onCallInitMediacodec(const char *mime, int width, int height, int csd0_size,
                                      int csd1_size, uint8_t *csd_0, uint8_t *csd_1) {

    JNIEnv *jniEnv;
    if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
    {
        if(LOG_DEBUG)
        {
            LOGE("call onCallComplete worng");
        }
    }

    jstring type = jniEnv->NewStringUTF(mime);
    jbyteArray csd0 = jniEnv->NewByteArray(csd0_size);
    jniEnv->SetByteArrayRegion(csd0, 0, csd0_size, reinterpret_cast<const jbyte *>(csd_0));
    jbyteArray csd1 = jniEnv->NewByteArray(csd1_size);
    jniEnv->SetByteArrayRegion(csd1, 0, csd1_size, reinterpret_cast<const jbyte *>(csd_1));

    jniEnv->CallVoidMethod(jobj, jmid_initmediacodec, type, width, height, csd0, csd1);

    jniEnv->DeleteLocalRef(csd0);
    jniEnv->DeleteLocalRef(csd1);
    jniEnv->DeleteLocalRef(type);
    javaVM->DetachCurrentThread();

}


void CjCallJava::onCallDecodeAVPacket(int datasize, uint8_t *packetdata) {
    JNIEnv *jniEnv;
    if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
    {
        if(LOG_DEBUG)
        {
            LOGE("call onCallComplete worng");
        }
    }
    jbyteArray data = jniEnv->NewByteArray(datasize);
    jniEnv->SetByteArrayRegion(data, 0, datasize, reinterpret_cast<const jbyte *>(packetdata));
    jniEnv->CallVoidMethod(jobj, jmid_decodeavpacket, datasize, data);
    jniEnv->DeleteLocalRef(data);
    javaVM->DetachCurrentThread();

}

