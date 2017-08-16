package com.floatingmuseum.androidtest.views.camera;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2 extends CameraImpl {

    private final CameraManager manager;

    public Camera2(Context context, CameraPreview preview,  CameraView.CameraStateCallback stateCallback) {
        super();
        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    private void setCameraOutputs(int facing,int width,int height) throws CameraAccessException {
        String[] ids = manager.getCameraIdList();
        for (String id : ids) {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
            Camera2ConfigManager.getInstance().init(id, characteristics);
            Integer cameraFacing = com.floatingmuseum.androidtest.functions.camera.Camera2ConfigManager.getInstance().getCameraFacing(id);

        }
    }
}
