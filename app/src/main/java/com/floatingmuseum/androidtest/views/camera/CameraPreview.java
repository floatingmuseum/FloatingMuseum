package com.floatingmuseum.androidtest.views.camera;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

public class CameraPreview {

    protected int previewWidth;
    protected int previewHeight;

    protected void openCamera(int previewWidth, int previewHeight) {
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
    }
}
