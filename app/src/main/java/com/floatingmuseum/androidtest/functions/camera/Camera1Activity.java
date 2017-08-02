package com.floatingmuseum.androidtest.functions.camera;

import android.Manifest;
import android.content.pm.PackageManager;
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
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.RxUtil;
import com.floatingmuseum.androidtest.utils.SystemUtil;
import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by Floatingmuseum on 2017/7/26.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera1Activity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.camera_view)
    AutoFitTextureView cameraView;
    @BindView(R.id.bt_take_picture)
    Button btTakePicture;
    @BindView(R.id.iv_camera_facing)
    ImageView ivCameraFacing;
    @BindView(R.id.iv_flash_mode)
    ImageView ivFlashMode;
    @BindView(R.id.iv_settings)
    ImageView ivSettings;

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

    private String tag = Camera1Activity.class.getSimpleName();

    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;

    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;
    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;
    private Integer defaultFacing = CameraCharacteristics.LENS_FACING_BACK;
    /**
     * The current state of camera state for taking pictures.
     *
     * @see #captureCallback
     */
    private int state = STATE_PREVIEW;
    private int flashMode = CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH;
    private List<Size> imageResolution = new ArrayList<>();

    private CameraManager cameraManager;
    private String cameraID;
    private String frontCameraID;
    private String backCameraID;
    private String externalCameraID;
    private CameraCaptureSession captureSession;
    private CameraDevice device;
    private Size previewSize;
    private ImageReader imageReader;
    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private Boolean flashSupported;
    private CaptureRequest.Builder previewRequestBuilder;
    private CaptureRequest previewRequest;
    private Display defaultDisplay;
    private int sensorOrientation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera1);
        ButterKnife.bind(this);

        btTakePicture.setOnClickListener(this);
        ivCameraFacing.setOnClickListener(this);
        ivFlashMode.setOnClickListener(this);
        ivSettings.setOnClickListener(this);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        defaultDisplay = getWindowManager().getDefaultDisplay();

        cameraView.setSurfaceTextureListener(surfaceTextureListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_take_picture:
                takePicture();
                break;
            case R.id.iv_camera_facing:
                switchCamera();
                break;
            case R.id.iv_flash_mode:
                switchFlashMode();
                break;
            case R.id.iv_settings:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (cameraView.isAvailable()) {
            openCamera(defaultFacing, cameraView.getWidth(), cameraView.getHeight());
        } else {
            cameraView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    //    private void initPermission() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            requestPermission(Manifest.permission.CAMERA);
//        } else {
//        }
//    }

    private void requestPermission(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 1024);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1024) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ToastUtil.show("权限获取成功");
            } else {
                ToastUtil.show("权限获取被拒绝");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openCamera(Integer cameraFacing, int width, int height) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.CAMERA);
            return;
        }
        try {
            setUpCameraOutputs(cameraFacing, width, height);
            configureTransform(width, height);
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            cameraManager.openCamera(cameraID, deviceStateCallback, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Switch camera between back and front.
     */
    private void switchCamera() {
        if (TextUtils.isEmpty(cameraID)) {
            ToastUtil.show("No camera can be used.");
            return;
        }
        if (cameraID.equals(backCameraID)) {
            if (TextUtils.isEmpty(frontCameraID)) {
                ToastUtil.show("This device has no front camera.");
                return;
            }
            ivCameraFacing.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_back_white_36dp, null));
            closeCamera();
            defaultFacing = CameraCharacteristics.LENS_FACING_FRONT;
            openCamera(defaultFacing, cameraView.getWidth(), cameraView.getHeight());
        } else if (cameraID.equals(frontCameraID)) {
            if (TextUtils.isEmpty(backCameraID)) {
                ToastUtil.show("This device has no back camera.");
                return;
            }
            ivCameraFacing.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_front_white_36dp, null));
            closeCamera();
            defaultFacing = CameraCharacteristics.LENS_FACING_BACK;
            openCamera(defaultFacing, cameraView.getWidth(), cameraView.getHeight());
        }
    }

    /**
     * 切换闪关灯模式
     */
    private void switchFlashMode() {
        if (flashSupported) {
            if (flashMode == CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH) {//自动变始终关闭
                flashMode = CaptureRequest.FLASH_MODE_OFF;
                ivFlashMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off_white_36dp, null));
            } else if (flashMode == CaptureRequest.FLASH_MODE_OFF) {//始终关闭变始终打开
                flashMode = CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH;
                ivFlashMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on_white_36dp, null));
            } else if (flashMode == CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH) {//始终打开变自动
                flashMode = CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH;
                ivFlashMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_auto_white_36dp, null));
            }
        } else {
            ToastUtil.show("Flash is not supported.");
        }
    }

    private void setUpCameraOutputs(Integer cameraFacing, int width, int height) throws CameraAccessException {
        String[] ids = cameraManager.getCameraIdList();
        for (String id : ids) {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
            Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            if (facing != null) {
                if (facing.equals(CameraCharacteristics.LENS_FACING_BACK)) {
                    backCameraID = id;
                } else if (facing.equals(CameraCharacteristics.LENS_FACING_FRONT)) {
                    frontCameraID = id;
                } else if (facing.equals(CameraCharacteristics.LENS_FACING_EXTERNAL)) {
                    externalCameraID = id;
                }
            }
            //获取默认镜头
            if (facing != null && facing.equals(cameraFacing)) {
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map != null) {
                    Size[] outputSize = map.getOutputSizes(ImageFormat.JPEG);
                    for (Size size : outputSize) {
                        Logger.d(tag + "...可选择输出分辨率Size:" + size.toString());
                    }
                    //默认选择了最大的图片比例
                    Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                    Logger.d(tag + "...默认选择最大输出分辨率Size:" + largest.toString());
                    imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/2);
                    imageReader.setOnImageAvailableListener(imageAvailableListener, null);

                    int displayRotation = defaultDisplay.getRotation();
                    sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    Logger.d(tag + "...displayRotation:" + displayRotation + "...sensorOrientation:" + sensorOrientation);
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
                            Logger.e(tag + " Display rotation is invalid: " + displayRotation);
                    }

                    Point displaySize = new Point();
                    defaultDisplay.getSize(displaySize);
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
                    previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth, maxPreviewHeight, largest);
                    Logger.d(tag + "...PreviewSize:" + previewSize.toString());
                    // We fit the aspect ratio of TextureView to the size of preview we picked.
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        cameraView.setAspectRatio(SystemUtil.getScreenWidth(), SystemUtil.getScreenHeight());
//                        cameraView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
                    } else {
                        cameraView.setAspectRatio(SystemUtil.getScreenWidth(), SystemUtil.getScreenHeight());
//                        cameraView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
                    }

                    // Check if the flash is supported.
                    Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    flashSupported = available == null ? false : available;
                    cameraID = id;
                }
            }
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
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Logger.e(tag + " Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    private void configureTransform(int width, int height) {
        if (null == previewSize) {
            return;
        }
        int rotation = defaultDisplay.getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, width, height);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) height / previewSize.getHeight(),
                    (float) width / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        cameraView.setTransform(matrix);
    }

    private void takePicture() {
        lockFocus();
    }

    private void lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            state = STATE_WAITING_LOCK;
            captureSession.capture(previewRequestBuilder.build(), captureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreviewSession() throws CameraAccessException {
        SurfaceTexture texture = cameraView.getSurfaceTexture();
        if (texture != null) {
            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);
            // We set up a CaptureRequest.Builder with the output Surface.
            previewRequestBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);

            device.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == device) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            captureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                setFlashMode(previewRequestBuilder);

                                // Finally, we start displaying the camera preview.
                                previewRequest = previewRequestBuilder.build();
                                captureSession.setRepeatingRequest(previewRequest, captureCallback, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                            ToastUtil.show("onConfigureFailed");
                        }
                    }, null
            );
//            surface.release();
        }
    }

    private void setFlashMode(CaptureRequest.Builder previewRequestBuilder) {
        if (flashSupported) {
            //设置闪光灯为自动模式
            switch (flashMode) {
                case CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH:
                    Logger.d(tag + "...闪光灯模式:始终打开");
                case CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH:
                    Logger.d(tag + "...闪光灯模式:自动");
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, flashMode);
                    break;
                case CaptureRequest.FLASH_MODE_OFF:
                    Logger.d(tag + "...闪光灯模式:始终关闭");
                    previewRequestBuilder.set(CaptureRequest.FLASH_MODE, flashMode);
                    break;
            }
        } else {
            ToastUtil.show("Flash is not supported.");
        }
    }

    @Override
    public void onPause() {
        closeCamera();
//        stopBackgroundThread();
        super.onPause();
    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
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
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera(defaultFacing, width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    CameraDevice.StateCallback deviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Logger.d(tag + "...StateCallback...onOpened:" + camera.getId());
            mCameraOpenCloseLock.release();
            device = camera;
            try {
                createCameraPreviewSession();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Logger.d(tag + "...StateCallback...onDisconnected:" + camera.getId());
            mCameraOpenCloseLock.release();
            camera.close();
            device = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Logger.d(tag + "...StateCallback...onError:" + camera.getId() + "...error:" + error);
            mCameraOpenCloseLock.release();
            camera.close();
            device = null;
        }
    };

    ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            saveImage(reader.acquireNextImage());
        }
    };

    private void saveImage(final Image image) {
        if (image == null) {
            ToastUtil.show("No new image is available.");
            return;
        }
        Logger.d(tag + "...拍照信息:format:" + image.getFormat() + "...width:" + image.getWidth() + "...height:" + image.getHeight() + "...时间戳:" + image.getTimestamp() + "..." + image.getPlanes()[0].getBuffer().remaining());
        File photoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");
        Observable.just(photoFile)
                .map(new Function<File, File>() {
                    @Override
                    public File apply(@io.reactivex.annotations.NonNull File file) throws Exception {
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        FileOutputStream output = null;
                        try {
                            output = new FileOutputStream(file);
                            output.write(bytes);
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
                        return file;
                    }
                })
                .compose(RxUtil.<File>threadSwitch())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull File file) throws Exception {
                        if (file.exists()) {
                            ToastUtil.show("图片保存在:" + file.getAbsolutePath());
                            Logger.d(tag + "...图片保存成功:" + file.exists() + "..." + file.getAbsolutePath());
                        } else {
                            Logger.d(tag + "...图片保存失败:" + file.exists() + "..." + file.getAbsolutePath());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        Logger.d(tag + "...图片保存失败");
                        throwable.printStackTrace();
                    }
                });
    }

    CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (state) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            state = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPreCaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        state = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        state = STATE_PICTURE_TAKEN;
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
                int rotation = defaultDisplay.getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

                CameraCaptureSession.CaptureCallback CaptureCallback
                        = new CameraCaptureSession.CaptureCallback() {

                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                                   @NonNull CaptureRequest request,
                                                   @NonNull TotalCaptureResult result) {
//                        ToastUtil.show("Saved: " + photoFile);
//                        Logger.d(tag + "..." + photoFile.toString());
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
            state = STATE_PREVIEW;
            captureSession.setRepeatingRequest(previewRequest, captureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
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
            state = STATE_WAITING_PRECAPTURE;
            captureSession.capture(previewRequestBuilder.build(), captureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    private static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}
