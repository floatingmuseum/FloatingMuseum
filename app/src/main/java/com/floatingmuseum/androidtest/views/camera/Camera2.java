package com.floatingmuseum.androidtest.views.camera;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;

import com.floatingmuseum.androidtest.utils.SystemUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Floatingmuseum on 2017/8/16.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2 extends CameraImpl {

    private static String TAG = Camera2.class.getSimpleName();
    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    private static final int CAPTURE_STATE_PREVIEW = 0;
    private static final int CAPTURE_STATE_PRECAPTURE = 1;
    private static final int CAPTURE_STATE_NON_PRECAPTURE = 2;
    private static final int CAPTURE_STATE_WAITING_LOCK = 3;
    private static final int CAPTURE_STATE_TAKEN_PICTURE = 4;

    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private int captureState;
    private final CameraManager manager;
    private String backCameraID;
    private String frontCameraID;
    private String externalCameraID;
    private ImageReader imageReader;
    private Integer sensorOrientation;
    private Size previewSize;
    private Boolean flashSupported;
    private String cameraID;
    private Display display;
    private CameraDevice device;
    private CaptureRequest.Builder previewRequestBuilder;
    private CameraCaptureSession captureSession;
    private int flashMode = CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH;
    private CaptureRequest previewCaptureRequest;
    private Integer defaultFacing;

    public Camera2(Context context, CameraPreview preview, CameraStateCallback stateCallback) {
        super(context, preview, stateCallback);
        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    @Override
    public void setOutputs(int facing, int width, int height) {
        // TODO: 2017/8/19 获取设备有几个相机和后面的参数设置可以分成两个方法,不需要多次获取相机
        try {
            String[] ids = manager.getCameraIdList();
            for (String id : ids) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                Camera2ConfigManager.getInstance().init(id, characteristics);
                Integer cameraFacing = Camera2ConfigManager.getInstance().getCameraFacing(id);

                if (cameraFacing != null) {
                    if (cameraFacing == CameraCharacteristics.LENS_FACING_BACK) {
                        backCameraID = id;
                        if (CameraParam.CAMERA_FACING_BACK == facing) {
                            defaultFacing = cameraFacing;
                        }
                    } else if (cameraFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                        frontCameraID = id;
                        if (CameraParam.CAMERA_FACING_FRONT == facing) {
                            defaultFacing = cameraFacing;
                        }
                    } else if (cameraFacing == CameraCharacteristics.LENS_FACING_EXTERNAL) {
                        externalCameraID = id;
                    }


                    if (defaultFacing != null && defaultFacing.equals(cameraFacing)) {
                        //默认选择了最大的图片比例
                        Size largest = Camera2ConfigManager.getInstance().getOutputSize(id);
                    /*maxImages*/
                        imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/2);
//                imageReader = ImageReader.newInstance(240, 320, ImageFormat.JPEG, /*maxImages*/2);
                        imageReader.setOnImageAvailableListener(imageAvailableListener, null);

                        sensorOrientation = Camera2ConfigManager.getInstance().getSensorOrientation(id);
                        display = ((Activity) context).getWindowManager().getDefaultDisplay();
                        int displayRotation = display.getRotation();
                        boolean swappedDimensions = false;
                        switch (displayRotation) {
                            case Surface.ROTATION_0:
                            case Surface.ROTATION_180:
                                if (sensorOrientation == 90 || sensorOrientation == 270) {
                                    swappedDimensions = true;
                                }
                                break;
                            case Surface.ROTATION_90:
                            case Surface.ROTATION_270:
                                if (sensorOrientation == 0 || sensorOrientation == 180) {
                                    swappedDimensions = true;
                                }
                                break;
                            default:
                                Log.d(TAG, " Display rotation is invalid: " + displayRotation);
                        }

                        Point displaySize = new Point();
                        display.getSize(displaySize);
                        int rotatedPreviewWidth = width;
                        int rotatedPreviewHeight = height;
                        int maxPreviewWidth = displaySize.x;
                        int maxPreviewHeight = displaySize.y;

                        if (swappedDimensions) {
                            rotatedPreviewWidth = height;
                            rotatedPreviewHeight = width;
                            maxPreviewWidth = displaySize.y;
                            maxPreviewHeight = displaySize.x;
                        }

                        if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                            maxPreviewWidth = MAX_PREVIEW_WIDTH;
                        }

                        if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                            maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                        }

                        // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                        // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                        // garbage capture data.
                        previewSize = chooseOptimalSize(Camera2ConfigManager.getInstance().getOutputSizes(id, SurfaceTexture.class), rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth, maxPreviewHeight, largest);
                        // We fit the aspect ratio of TextureView to the size of preview we picked.
                        int orientation = context.getResources().getConfiguration().orientation;
                        Log.d(TAG, "PreviewSize:" + previewSize.toString() + "...屏幕分辨率...width:" + SystemUtil.getScreenWidth() + "...height:" + SystemUtil.getScreenHeight() + "...方向:" + orientation);
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                        cameraView.setAspectRatio(SystemUtil.getScreenWidth(), SystemUtil.getScreenHeight());
                            preview.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
                        } else {
//                        cameraView.setAspectRatio(SystemUtil.getScreenWidth(), SystemUtil.getScreenHeight());
                            preview.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
                        }

                        // Check if the flash is supported.
                        flashSupported = Camera2ConfigManager.getInstance().isSupportFlash(id);
                        Log.d(TAG, "是否支持闪光灯:" + flashSupported);
                        cameraID = id;
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {
        for (Size size : choices) {
            Log.d(TAG, "optimalSize...choices..." + size.toString());
        }
        Log.d(TAG, "optimalSize...textureViewWidth:" + textureViewWidth + "...textureViewHeight:" + textureViewHeight + "...maxWidth:" + maxWidth + "...maxHeight:" + maxHeight + "...aspectRatio:" + aspectRatio.toString());
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight && option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        Log.d(TAG, "BigEnough:" + bigEnough);
        Log.d(TAG, "notBigEnough:" + notBigEnough);

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new Camera2ConfigManager.CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new Camera2ConfigManager.CompareSizesByArea());
        } else {
            Log.d(TAG, " Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    @Override
    public void configureTransform(int width, int height) {
        if (null == previewSize) {
            return;
        }
        int rotation = display.getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, width, height);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) height / previewSize.getHeight(), (float) width / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        preview.setTransform(matrix);
    }

    @Override
    public void openCamera() {
        try {
            manager.openCamera(cameraID, cameraStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void createPreviewSession() throws CameraAccessException {
        Surface surface = preview.getSurface(previewSize.getWidth(), previewSize.getHeight());
        previewRequestBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        if (surface != null) {
            previewRequestBuilder.addTarget(surface);
            device.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (device == null) {
                        return;
                    }
                    captureSession = session;
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                    setFlashMode(previewRequestBuilder);
                    // Finally, we start displaying the camera preview.
                    previewCaptureRequest = previewRequestBuilder.build();
                    try {
                        captureSession.setRepeatingRequest(previewCaptureRequest, captureCallback, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.d(TAG, "Camera " + cameraID + "...Create capture session failed.");
                }
            }, null);
        } else {
            Log.d(TAG, "Surface is null.");
        }
    }

    private void setFlashMode(CaptureRequest.Builder previewRequestBuilder) {
        if (flashSupported) {
            //设置闪光灯为自动模式
            switch (flashMode) {
                case CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH:
                    Logger.d(TAG + "...闪光灯模式:始终打开");
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, flashMode);
                    break;
                case CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH:
                    Logger.d(TAG + "...闪光灯模式:自动");
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, flashMode);
                    break;
                case CaptureRequest.FLASH_MODE_OFF:
                    Logger.d(TAG + "...闪光灯模式:始终关闭");
                    previewRequestBuilder.set(CaptureRequest.FLASH_MODE, flashMode);
                    break;
            }
        } else {
            //不支持闪光灯,有时是因为某个镜头不包含闪光灯功能,比如前置摄像头
            Log.d(TAG, "Camera:" + cameraID + " not support flash.");
//            ToastUtil.show("Flash is not supported.");
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in {@link #captureCallback} from {@link #lockFocus()}.
     */
    private void runPreCaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            captureState = CAPTURE_STATE_PRECAPTURE;
            captureSession.capture(previewRequestBuilder.build(), captureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void takePhoto() {
        lockFocus();
    }

    private void lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            captureState = CAPTURE_STATE_WAITING_LOCK;
            captureSession.capture(previewRequestBuilder.build(), captureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void captureStillPicture() {
        if (device != null) {
            try {
                // This is the CaptureRequest.Builder that we use to take a picture.
                final CaptureRequest.Builder captureBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(imageReader.getSurface());

                // Use the same AE and AF modes as the preview.
                captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                setFlashMode(captureBuilder);

                // Orientation
                int rotation = display.getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

                CameraCaptureSession.CaptureCallback CaptureCallback
                        = new CameraCaptureSession.CaptureCallback() {

                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                                   @NonNull CaptureRequest request,
                                                   @NonNull TotalCaptureResult result) {
//                        ToastUtil.show("Saved: " + photoFile);
//                        Logger.d(TAG + "..." + photoFile.toString());
                        unlockFocus();
                    }
                };

                captureSession.stopRepeating();
                captureSession.capture(captureBuilder.build(), CaptureCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setFlashMode(previewRequestBuilder);
            captureSession.capture(previewRequestBuilder.build(), captureCallback, null);
            // After this, the camera will go back to the normal state of preview.
            captureState = CAPTURE_STATE_PREVIEW;
            captureSession.setRepeatingRequest(previewCaptureRequest, captureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + sensorOrientation + 270) % 360;
    }

    ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            savePhoto(reader);
        }
    };

    CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, "CameraDevice...onOpened");
            device = camera;
            try {
                createPreviewSession();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d(TAG, "CameraDevice...onDisconnected");
            camera.close();
            device = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.d(TAG, "CameraDevice...onError:" + error);
            camera.close();
            device = null;
        }
    };

    @Override
    public void switchCameraFacing(int facing) {
        if (CameraParam.CAMERA_FACING_BACK == facing && !cameraID.equals(backCameraID)) {
            defaultFacing = CameraCharacteristics.LENS_FACING_BACK;
            switchCamera();
        } else if (CameraParam.CAMERA_FACING_FRONT == facing && !cameraID.equals(frontCameraID)) {
            defaultFacing = CameraCharacteristics.LENS_FACING_FRONT;
            switchCamera();
        } else {
            throw new IllegalArgumentException("Wrong facing argument..");
        }
    }

    private void switchCamera() {
        closeCamera();
        setOutputs(defaultFacing, preview.previewWidth, preview.previewHeight);
        configureTransform(preview.previewWidth, preview.previewHeight);
        openCamera();
    }

    @Override
    public void closeCamera() {
        if (null != captureSession) {
            captureSession.close();
            captureSession = null;
        }
        if (null != device) {
            device.close();
            device = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }

    @Override
    public int getCameraFacing() {
        Log.d(TAG, "CameraID:" + cameraID + "...backID:" + backCameraID + "...FrontID:" + frontCameraID);
        if (TextUtils.isEmpty(cameraID)) {
            return -1;
        } else {
            if (cameraID.equals(backCameraID)) {
                return CameraParam.CAMERA_FACING_BACK;
            } else if (cameraID.equals(frontCameraID)) {
                return CameraParam.CAMERA_FACING_FRONT;
            } else {
                return -2;
            }
        }
    }

    @Override
    public void switchFlashMode(int mode) {
        if (flashSupported) {
            if (CameraParam.FLASH_MODE_AUTO == mode) {
                flashMode = CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH;
            } else if (CameraParam.FLASH_MODE_CLOSE == mode) {
                flashMode = CaptureRequest.FLASH_MODE_OFF;
            } else if (CameraParam.FLASH_MODE_OPEN == mode) {
                flashMode = CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH;
            } else {
                throw new IllegalArgumentException("Wrong flash mode.");
            }
        } else {
            Log.d(TAG, "This camera not supported flash.");
        }
    }

    @Override
    public int getFlashMode() {
        if (flashSupported) {
            if (CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH == flashMode) {
                return CameraParam.FLASH_MODE_AUTO;
            } else if (CaptureRequest.FLASH_MODE_OFF == flashMode) {
                return CameraParam.FLASH_MODE_CLOSE;
            } else if (CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH == flashMode) {
                return CameraParam.FLASH_MODE_OPEN;
            } else {
                return -2;
            }
        } else {
            return -1;
        }
    }

    CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (captureState) {
                case CAPTURE_STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case CAPTURE_STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState || CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            captureState = CAPTURE_STATE_TAKEN_PICTURE;
                            captureStillPicture();
                        } else {
                            runPreCaptureSequence();
                        }
                    }
                    break;
                }
                case CAPTURE_STATE_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        captureState = CAPTURE_STATE_NON_PRECAPTURE;
                    }
                    break;
                }
                case CAPTURE_STATE_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        captureState = CAPTURE_STATE_TAKEN_PICTURE;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            process(result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }

        @Override
        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }

        @Override
        public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
        }

        @Override
        public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
            super.onCaptureBufferLost(session, request, target, frameNumber);
        }
    };
}
