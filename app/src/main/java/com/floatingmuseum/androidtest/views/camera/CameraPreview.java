package com.floatingmuseum.androidtest.views.camera;

import android.content.Context;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.Size;
import android.view.Surface;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

public abstract class CameraPreview {

    protected Context context;
    protected PreviewCallback previewCallback;
    protected int previewWidth;
    protected int previewHeight;

    public CameraPreview(Context context, PreviewCallback previewCallback) {
        this.context = context;
        this.previewCallback = previewCallback;
    }

    public abstract void setAspectRatio(int width, int height);

    public abstract void setTransform(Matrix matrix);

    @Nullable
    public abstract Surface getSurface(int width, int height);

    public abstract boolean isAvailable();
}
