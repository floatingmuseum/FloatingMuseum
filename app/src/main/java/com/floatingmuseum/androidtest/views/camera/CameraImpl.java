package com.floatingmuseum.androidtest.views.camera;


import android.content.Context;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

public abstract class CameraImpl {

    private static String TAG = CameraImpl.class.getSimpleName();
    protected Context context;
    protected CameraPreview preview;
    protected CameraStateCallback stateCallback;
    private static final String SAVE_STATE_KEY = "saveStateKey";
    private static final int SAVE_STATE_SUCCESS = 0;
    private static final int SAVE_STATE_FAILED = 1;

    private static Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    public CameraImpl(Context context, CameraPreview preview, CameraStateCallback stateCallback) {
        this.context = context;
        this.preview = preview;
        this.stateCallback = stateCallback;
    }

    public abstract void setOutputs(int facing, int width, int height);

    public abstract void configureTransform(int width, int height);

    public abstract void openCamera();

    public abstract void takePhoto();

    protected void savePhoto(ImageReader reader){
        final Image image = reader.acquireNextImage();
        if (image != null) {
            // TODO: 2017/8/9 保存地址待可选
            File dir = new File(Environment.getExternalStorageDirectory() + "/FloatingMuseum-Pictures");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            final File photoFile = new File(dir, image.getTimestamp() + ".jpg");
            Log.d(TAG, "Photo path:" + photoFile.getAbsolutePath());

            new Thread() {
                @Override
                public void run() {
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(photoFile);
                        output.write(bytes);
                        Message message = UIHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putInt(SAVE_STATE_KEY, SAVE_STATE_SUCCESS);
                        message.setData(bundle);
                        UIHandler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        image.close();
                        if (null != output) {
                            try {
                                output.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.run();
        } else {
            Log.d(TAG, "Image not available.");
        }
    }
}
