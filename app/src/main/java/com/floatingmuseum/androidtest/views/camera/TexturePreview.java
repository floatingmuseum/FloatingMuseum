package com.floatingmuseum.androidtest.views.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.floatingmuseum.androidtest.R;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

public class TexturePreview extends CameraPreview implements TextureView.SurfaceTextureListener {

    private final TextureView textureView;

    public TexturePreview(Context context, ViewGroup viewParent) {
        View view = View.inflate(context, R.layout.texture_preview, viewParent);
        textureView = (TextureView) view.findViewById(R.id.texture_view);
        textureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        openCamera(width,height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
