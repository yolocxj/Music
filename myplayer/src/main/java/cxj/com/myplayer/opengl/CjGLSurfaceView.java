package cxj.com.myplayer.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class CjGLSurfaceView extends GLSurfaceView {

    private CjRender cjRender;

    public CjGLSurfaceView(Context context) {
        this(context, null);
    }

    public CjGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        cjRender = new CjRender(context);
        setRenderer(cjRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        cjRender.setOnRenderListener(new CjRender.OnRenderListener() {
            @Override
            public void onRender() {
                requestRender();
            }
        });
    }

    public void setYUVData(int width, int height, byte[] y, byte[] u, byte[] v)
    {
        if(cjRender != null)
        {
            cjRender.setYUVRenderData(width, height, y, u, v);
            requestRender();
        }
    }

    public CjRender getWlRender() {
        return cjRender;
    }
}
