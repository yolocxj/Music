package cxj.com.myplayer.listener;

public interface CjOnPcmInfoListener {
    void onPcmInfo(byte[] buffer, int buffersize);

    void onPcmRate(int samplerate, int bit, int channels);
}
