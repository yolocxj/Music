package cxj.com.myplayer.player;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.text.TextUtils;
import android.view.Surface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import cxj.com.myplayer.CjTimeInfoBean;
import cxj.com.myplayer.listener.CjOnCompleteListener;
import cxj.com.myplayer.listener.CjOnErrorListener;
import cxj.com.myplayer.listener.CjOnLoadListener;
import cxj.com.myplayer.listener.CjOnParparedListener;
import cxj.com.myplayer.listener.CjOnPauseResumeListener;
import cxj.com.myplayer.listener.CjOnPcmInfoListener;
import cxj.com.myplayer.listener.CjOnRecordTimeListener;
import cxj.com.myplayer.listener.CjOnTimeInfoListener;
import cxj.com.myplayer.listener.CjOnValumeDBListener;
import cxj.com.myplayer.log.MyLog;
import cxj.com.myplayer.muteenum.MuteEnum;
import cxj.com.myplayer.opengl.CjGLSurfaceView;
import cxj.com.myplayer.opengl.CjRender;
import cxj.com.myplayer.util.CjVideoSupportUtil;

public class CjPlayer {
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("avcodec-58");
        System.loadLibrary("avdevice-58");
        System.loadLibrary("avfilter-7");
        System.loadLibrary("avformat-58");
        System.loadLibrary("avutil-56");
        System.loadLibrary("postproc-55");
        System.loadLibrary("swresample-3");
        System.loadLibrary("swscale-5");
    }

    private static String source;
    private static boolean playNext = false;
    private static CjTimeInfoBean cjTimeInfoBean;
    private CjOnParparedListener cjOnParparedListener;
    private static int duration = -1;
    private static int volumePercent = 100;
    private static float speed = 1.0f;
    private static float pitch = 1.0f;
    private static boolean initmediacodec = false;
    private static MuteEnum muteEnum = MuteEnum.MUTE_CENTER;
    private CjOnPauseResumeListener cjOnPauseResumeListener;
    private CjOnLoadListener cjOnLoadListener;
    private CjOnTimeInfoListener cjOnTimeInfoListener;
    private CjOnErrorListener cjOnErrorListener;
    private CjOnCompleteListener cjOnCompleteListener;
    private CjOnValumeDBListener cjOnValumeDBListener;
    private CjOnRecordTimeListener cjOnRecordTimeListener;
    private CjOnPcmInfoListener cjOnPcmInfoListener;
    private CjGLSurfaceView cjGLSurfaceView;

    private MediaFormat mediaFormat;
    private MediaCodec mediaCodec;
    private Surface surface;

    public CjPlayer(){
    }

    /**
     * 设置数据源
     * @param source
     */
    public void setSource(String source)
    {
        this.source = source;
    }

    public void setCjGLSurfaceView(CjGLSurfaceView cjGLSurfaceView) {
        this.cjGLSurfaceView = cjGLSurfaceView;
        cjGLSurfaceView.getWlRender().setOnSurfaceCreateListener(new CjRender.OnSurfaceCreateListener() {
            @Override
            public void onSurfaceCreate(Surface s) {
                if(surface == null)
                {
                    surface = s;
                    MyLog.d("onSurfaceCreate");
                }
            }
        });
    }

    public void setCjOnValumeDBListener(CjOnValumeDBListener cjOnValumeDBListener) {
        this.cjOnValumeDBListener = cjOnValumeDBListener;
    }

    public void setCjOnPcmInfoListener(CjOnPcmInfoListener cjOnPcmInfoListener) {
        this.cjOnPcmInfoListener = cjOnPcmInfoListener;
    }

    /**
     * 设置准备接口回调
     * @param cjOnParparedListener
     */

    public void setWlOnParparedListener(CjOnParparedListener cjOnParparedListener)
    {
        this.cjOnParparedListener = cjOnParparedListener;
    }
    public void setCjOnLoadListener(CjOnLoadListener cjOnLoadListener) {
        this.cjOnLoadListener = cjOnLoadListener;
    }
    public void setCjOnPauseResumeListener(CjOnPauseResumeListener cjOnPauseResumeListener) {
        this.cjOnPauseResumeListener = cjOnPauseResumeListener;
    }
    public void setCjOnTimeInfoListener(CjOnTimeInfoListener cjOnTimeInfoListener) {
        this.cjOnTimeInfoListener = cjOnTimeInfoListener;
    }

    public void setCjOnCompleteListener(CjOnCompleteListener cjOnCompleteListener) {
        this.cjOnCompleteListener = cjOnCompleteListener;
    }

    public void setCjOnErrorListener(CjOnErrorListener cjOnErrorListener) {
        this.cjOnErrorListener = cjOnErrorListener;
    }

    public void setCjOnRecordTimeListener(CjOnRecordTimeListener cjOnRecordTimeListener) {
        this.cjOnRecordTimeListener = cjOnRecordTimeListener;
    }

    public void parpared(){
        if(TextUtils.isEmpty(source))
        {
            MyLog.d("source not be empty");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                n_parpared(source);
            }
        }).start();
    }
    public void pause()
    {
        n_pause();
        if(cjOnPauseResumeListener != null)
        {
            cjOnPauseResumeListener.onPause(true);
        }
    }

    public void resume()
    {
        n_resume();
        if(cjOnPauseResumeListener != null)
        {
            cjOnPauseResumeListener.onPause(false);
        }
    }

  public void onCallRenderYUV(int width, int height, byte[] y, byte[] u, byte[] v)
    {
        MyLog.d("获取到视频的yuv数据");
        if(cjGLSurfaceView != null)
        {
            cjGLSurfaceView.getWlRender().setRenderType(CjRender.RENDER_YUV);
            cjGLSurfaceView.setYUVData(width, height, y, u, v);
        }
    }

    private void releaseMediacodec()
    {
        if(mediaCodec != null)
        {
            try{
                mediaCodec.flush();
                mediaCodec.stop();
                mediaCodec.release();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            mediaCodec = null;
            mediaFormat = null;
            info = null;
        }

    }
    private native void n_parpared(String source);
    private native void n_start();

    private native void n_pause();
    private native void n_resume();
    private native void n_stop();
    private native void n_seek(int secds);
    private native int n_duration();
    private native void n_volume(int percent);
    private native void n_mute(int mute);
    private native void n_pitch(float pitch);
    private native void n_speed(float speed);
    private native int n_samplerate();
    private native void n_startstoprecord(boolean start);
    private native boolean n_cutaudioplay(int start_time, int end_time, boolean showPcm);

    public void seek(int secds)
    {
        n_seek(secds);
    }

    public void start()
    {
        if(TextUtils.isEmpty(source))
        {
            MyLog.d("source is empty");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                setVolume(volumePercent);
                setMute(muteEnum);
                setPitch(pitch);
                setSpeed(speed);
                n_start();
            }
        }).start();
    }


    public void stop()
    {
        cjTimeInfoBean = null;
        duration = 0;
        stopRecord();
        new Thread(new Runnable() {
            @Override
            public void run() {
                n_stop();
                releaseMediacodec();
            }
        }).start();
    }
    public void playNext(String url)
    {
        source = url;
        playNext = true;
        stop();
    }


    public int getDuration()
    {
        if(duration < 0)
        {
            duration = n_duration();
        }
        return duration;
    }

    public void setVolume(int percent)
    {
        if(percent >=0 && percent <= 100)
        {
            volumePercent = percent;
            n_volume(percent);
        }
    }
    public int getVolumePercent()
    {
        return volumePercent;
    }
    public void setMute(MuteEnum mute)
    {
        muteEnum = mute;
        n_mute(mute.getValue());
    }
    public void setPitch(float p)
    {
        pitch = p;
        n_pitch(pitch);
    }

    public void setSpeed(float s)
    {
        speed = s;
        n_speed(speed);
    }
    public void startRecord(File outfile)
    {
        if(!initmediacodec)
        {
            audioSamplerate = n_samplerate();
            if(audioSamplerate > 0)
            {
                initmediacodec = true;
                initMediacodec(audioSamplerate, outfile);
                n_startstoprecord(true);
                MyLog.d("开始录制");
            }
        }
    }
    public void stopRecord()
    {
        if(initmediacodec)
        {
            n_startstoprecord(false);
            releaseMedicacodec();
            MyLog.d("完成录制");
        }
    }

    public void pauseRecord()
    {
        n_startstoprecord(false);
        MyLog.d("暂停录制");
    }

    public void resumeRcord()
    {
        n_startstoprecord(true);
        MyLog.d("继续录制");
    }
    public void cutAudioPlay(int start_time, int end_time, boolean showPcm)
    {
        if(n_cutaudioplay(start_time, end_time, showPcm))
        {
            start();
        }
        else
        {
            stop();
            onCallError(2001, "cutaudio params is wrong");
        }
    }

    /**
     * c++回调java的方法
     */
    public void onCallValumeDB(int db)
    {
        if(cjOnValumeDBListener != null)
        {
            cjOnValumeDBListener.onDbValue(db);
        }
    }
    public void onCallParpared()
    {
        if(cjOnParparedListener != null)
        {
            cjOnParparedListener.onParpared();
        }
    }

    public void onCallError(int code, String msg)
    {
        stop();
        if(cjOnErrorListener != null)
        {
            stop();
            cjOnErrorListener.onError(code, msg);
        }
    }

    public void onCallLoad(boolean load)
    {
        if(cjOnLoadListener != null)
        {
            cjOnLoadListener.onLoad(load);
        }
    }
    public void onCallComplete()
    {
        stop();
        if(cjOnCompleteListener != null)
        {
            cjOnCompleteListener.onComplete();
        }
    }
    public void onCallNext()
    {
        if(playNext)
        {
            playNext = false;
            parpared();
        }
    }
    public void onCallTimeInfo(int currentTime, int totalTime)
    {
        if(cjOnTimeInfoListener != null)
        {
            if(cjTimeInfoBean == null)
            {
                cjTimeInfoBean = new CjTimeInfoBean();
            }
            duration = totalTime;
            cjTimeInfoBean.setCurrentTime(currentTime);
            cjTimeInfoBean.setTotalTime(totalTime);
            cjOnTimeInfoListener.onTimeInfo(cjTimeInfoBean);
        }
    }
    public void onCallPcmInfo(byte[] buffer, int buffersize)
    {
        if(cjOnPcmInfoListener != null)
        {
            cjOnPcmInfoListener.onPcmInfo(buffer, buffersize);
        }
    }

    public void onCallPcmRate(int samplerate)
    {
        if(cjOnPcmInfoListener != null)
        {
            cjOnPcmInfoListener.onPcmRate(samplerate, 16, 2);
        }
    }





    //mediacodec

    private MediaFormat encoderFormat = null;
    private MediaCodec encoder = null;
    private FileOutputStream outputStream = null;
    private MediaCodec.BufferInfo info = null;
    private int perpcmsize = 0;
    private byte[] outByteBuffer = null;
    private int aacsamplerate = 4;
    private double recordTime = 0;
    private int audioSamplerate = 0;

    private void initMediacodec(int samperate, File outfile)
    {
        try {
            aacsamplerate = getADTSsamplerate(samperate);
            encoderFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, samperate, 2);
            encoderFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
            encoderFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            encoderFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4096);
            encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            info = new MediaCodec.BufferInfo();
            if(encoder == null)
            {
                MyLog.d("create encoder wrong");
                return;
            }
            recordTime = 0;
            encoder.configure(encoderFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            outputStream = new FileOutputStream(outfile);
            encoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //硬解码
    public void initMediaCodec(String codecName, int width, int height, byte[] csd_0, byte[] csd_1)
    {
        if(surface != null)
        {
            try {
                cjGLSurfaceView.getWlRender().setRenderType(CjRender.RENDER_MEDIACODEC);
                String mime = CjVideoSupportUtil.findVideoCodecName(codecName);
                mediaFormat = MediaFormat.createVideoFormat(mime, width, height);
                mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, width * height);
                mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(csd_0));
                mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(csd_1));
                MyLog.d(mediaFormat.toString());
                mediaCodec = MediaCodec.createDecoderByType(mime);

                info = new MediaCodec.BufferInfo();
                mediaCodec.configure(mediaFormat, surface, null, 0);
                mediaCodec.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            if(cjOnErrorListener != null)
            {
                cjOnErrorListener.onError(2001, "surface is null");
            }
        }
    }

    public void decodeAVPacket(int datasize, byte[] data)
    {
        if(surface != null && datasize > 0 && data != null&& mediaCodec != null)
        {
            try{
                int intputBufferIndex = mediaCodec.dequeueInputBuffer(10);
                if(intputBufferIndex >= 0)
                {
                    ByteBuffer byteBuffer = mediaCodec.getInputBuffers()[intputBufferIndex];
                    byteBuffer.clear();
                    byteBuffer.put(data);
                    mediaCodec.queueInputBuffer(intputBufferIndex, 0, datasize, 0, 0);
                }
                int outputBufferIndex = mediaCodec.dequeueOutputBuffer(info, 10);
                while(outputBufferIndex >= 0)
                {
                    mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                    outputBufferIndex = mediaCodec.dequeueOutputBuffer(info, 10);
                }
        }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void encodecPcmToAAc(int size, byte[] buffer)
    {
        if(buffer != null && encoder != null)
        {
            recordTime += size * 1.0 / (audioSamplerate * 2 * (16 / 8));
            MyLog.d("recordTime = " + recordTime);
            if(cjOnRecordTimeListener != null)
            {
                cjOnRecordTimeListener.onRecordTime((int) recordTime);
            }
            int inputBufferindex = encoder.dequeueInputBuffer(0);
            if(inputBufferindex >= 0)
            {
                ByteBuffer byteBuffer = encoder.getInputBuffers()[inputBufferindex];
                byteBuffer.clear();
                byteBuffer.put(buffer);
                encoder.queueInputBuffer(inputBufferindex, 0, size, 0, 0);
            }

            int index = encoder.dequeueOutputBuffer(info, 0);
            while(index >= 0)
            {
                try {
                    perpcmsize = info.size + 7;
                    outByteBuffer = new byte[perpcmsize];

                    ByteBuffer byteBuffer = encoder.getOutputBuffers()[index];
                    byteBuffer.position(info.offset);
                    byteBuffer.limit(info.offset + info.size);

                    addADtsHeader(outByteBuffer, perpcmsize, aacsamplerate);

                    byteBuffer.get(outByteBuffer, 7, info.size);
                    byteBuffer.position(info.offset);
                    outputStream.write(outByteBuffer, 0, perpcmsize);

                    encoder.releaseOutputBuffer(index, false);
                    index = encoder.dequeueOutputBuffer(info, 0);
                    outByteBuffer = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addADtsHeader(byte[] packet, int packetLen, int samplerate)
    {
        int profile = 2; // AAC LC
        int freqIdx = samplerate; // samplerate
        int chanCfg = 2; // CPE

        packet[0] = (byte) 0xFF; // 0xFFF(12bit) 这里只取了8位，所以还差4位放到下一个里面
        packet[1] = (byte) 0xF9; // 第一个t位放F
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }

    private int getADTSsamplerate(int samplerate)
    {
        int rate = 4;
        switch (samplerate)
        {
            case 96000:
                rate = 0;
                break;
            case 88200:
                rate = 1;
                break;
            case 64000:
                rate = 2;
                break;
            case 48000:
                rate = 3;
                break;
            case 44100:
                rate = 4;
                break;
            case 32000:
                rate = 5;
                break;
            case 24000:
                rate = 6;
                break;
            case 22050:
                rate = 7;
                break;
            case 16000:
                rate = 8;
                break;
            case 12000:
                rate = 9;
                break;
            case 11025:
                rate = 10;
                break;
            case 8000:
                rate = 11;
                break;
            case 7350:
                rate = 12;
                break;
        }
        return rate;
    }

    public boolean onCallIsSupportMediaCodec(String ffcodecname)
    {
        return CjVideoSupportUtil.isSupportCodec(ffcodecname);
    }

    private void releaseMedicacodec()
    {
        if(encoder == null)
        {
            return;
        }
        try {
            recordTime = 0;
            outputStream.close();
            outputStream = null;
            encoder.stop();
            encoder.release();
            encoder = null;
            encoderFormat = null;
            info = null;
            initmediacodec = false;

            MyLog.d("录制完成...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(outputStream != null)
            {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                outputStream = null;
            }
        }
    }
}


