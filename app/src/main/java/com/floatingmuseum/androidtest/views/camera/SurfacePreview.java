package com.floatingmuseum.androidtest.views.camera;

import android.content.Context;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.floatingmuseum.androidtest.R;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

class SurfacePreview extends CameraPreview {

    private final SurfaceView surfaceView;

    public SurfacePreview(Context context, ViewGroup viewParent, PreviewCallback previewCallback) {
        super(context, previewCallback);
        View view = View.inflate(context, R.layout.surface_preview,viewParent);
        surfaceView = (SurfaceView) view.findViewById(R.id.surface_preview);
    }

    @Override
    public void setAspectRatio(int width, int height) {

    }

    @Override
    public void setTransform(Matrix matrix) {

    }

    @Nullable
    @Override
    public Surface getSurface(int width, int height) {
//        surfaceView.getHolder().
        return null;
    }

    @Override
    public boolean isAvailable() {
        return true;
//        return surfaceView.getHolder().isCreating();
    }
}
