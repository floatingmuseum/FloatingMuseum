package com.floatingmuseum.androidtest.views.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.functions.camera.AutoFitTextureView;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

//@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TexturePreview extends CameraPreview implements TextureView.SurfaceTextureListener {

    private final AutoFitTextureView textureView;

    public TexturePreview(Context context, ViewGroup viewParent, PreviewCallback previewCallback) {
        super(context, previewCallback);
        View view = View.inflate(context, R.layout.texture_preview, viewParent);
        textureView = (AutoFitTextureView) view.findViewById(R.id.texture_view);
        textureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        previewWidth = width;
        previewHeight = height;
        previewCallback.onPreviewAvailable(previewWidth, previewHeight);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        previewWidth = width;
        previewHeight = height;
        previewCallback.onPreviewSizeChanged(previewWidth, previewHeight);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void setAspectRatio(int width, int height) {
        textureView.setAspectRatio(width, height);
    }

    @Override
    public void setTransform(Matrix matrix) {
        textureView.setTransform(matrix);
    }

    @Override
    public Surface getSurface(int width, int height) {
        SurfaceTexture texture = textureView.getSurfaceTexture();
        if (texture != null) {
            texture.setDefaultBufferSize(width, height);
            return new Surface(texture);
        }
        return null;
    }

    @Override
    public boolean isAvailable() {
        return textureView.isAvailable();
    }
}
