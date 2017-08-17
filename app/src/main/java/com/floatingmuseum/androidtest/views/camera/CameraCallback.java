package com.floatingmuseum.androidtest.views.camera;

import java.io.File;

/**
 * Created by Floatingmuseum on 2017/8/17.
 */

public interface CameraCallback {
    void onCameraOpened();

    void onPhotoTaken(File file);

    void onCameraClosed();

    void onError(Exception e);
}
