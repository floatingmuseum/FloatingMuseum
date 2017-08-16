package com.floatingmuseum.androidtest.views.camera;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

public abstract class PreviewCallback {
    abstract void onPreviewAvailable(int width, int height);
    abstract void onPreviewSizeChanged(int width,int height);
}
