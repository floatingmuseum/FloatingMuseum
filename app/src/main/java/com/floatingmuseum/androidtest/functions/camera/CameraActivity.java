package com.floatingmuseum.androidtest.functions.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.memfis19.annca.Annca;
import io.github.memfis19.annca.internal.configuration.AnncaConfiguration;

/**
 * Created by Floatingmuseum on 2017/2/20.
 */

public class CameraActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.bt_default_camera)
    Button btDefaultCamera;
    @BindView(R.id.bt_custom_camera)
    Button btCustomCamera;

    private final static int CAPTURE_MEDIA = 6969;
    private final static int PERMISSION_RQ = 84;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
//        findViewById(R.id.launchCamera).setOnClickListener(this);
//        findViewById(R.id.launchCameraStillshot).setOnClickListener(this);
//        findViewById(R.id.launchFromFragment).setOnClickListener(this);
//        findViewById(R.id.launchFromFragmentSupport).setOnClickListener(this);
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            // Request permission to save videos in external storage
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_RQ);
//        }
        btDefaultCamera.setOnClickListener(this);
        btCustomCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_default_camera:
                openDefaultCamera();
                break;

            case R.id.bt_custom_camera:
                openCustomCamera();
                break;
        }
    }

    private void openDefaultCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        AnncaConfiguration.Builder builder = new AnncaConfiguration.Builder(this, CAPTURE_MEDIA);
        new Annca(builder.build()).launchCamera();
    }

    private void openCustomCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        AnncaConfiguration.Builder videoLimited = new AnncaConfiguration.Builder(this, CAPTURE_MEDIA);

        videoLimited.setMediaAction(AnncaConfiguration.MEDIA_ACTION_VIDEO);//模式
        videoLimited.setMediaQuality(AnncaConfiguration.MEDIA_QUALITY_AUTO);//清晰度
        videoLimited.setVideoFileSize(5 * 1024 * 1024);//视频文件大小
        videoLimited.setMinimumVideoDuration(5 * 60 * 1000);//视频长度
        new Annca(videoLimited.build()).launchCamera();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_MEDIA && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(AnncaConfiguration.Arguments.FILE_PATH);
            Logger.d("filePath:" + filePath);
        }
    }
//
//    @SuppressWarnings("ResultOfMethodCallIgnored")
//    @Override
//    public void onClick(View view) {
//        if (view.getId() == R.id.launchFromFragment) {
//            Intent intent = new Intent(this, FragmentActivity.class);
//            startActivity(intent);
//            return;
//        }
//        if (view.getId() == R.id.launchFromFragmentSupport) {
//            Intent intent = new Intent(this, FragmentActivity.class);
//            intent.putExtra("support", true);
//            startActivity(intent);
//            return;
//        }
//
//        File saveDir = null;
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            // Only use external storage directory if permission is granted, otherwise cache directory is used by default
//            saveDir = new File(Environment.getExternalStorageDirectory(), "MaterialCamera");
//            saveDir.mkdirs();
//        }
//
//        MaterialCamera materialCamera = new MaterialCamera(this)
//                .saveDir(saveDir)
//                .showPortraitWarning(true)
//                .allowRetry(true)
//                .defaultToFrontFacing(true)
//                .allowRetry(true)
//                .autoSubmit(false)
//                .labelConfirm(R.string.mcam_use_video);
//
//        if (view.getId() == R.id.launchCameraStillshot)
//            materialCamera
//                    .stillShot() // launches the Camera in stillshot mode
//                    .labelConfirm(R.string.mcam_use_stillshot);
//        materialCamera.start(CAMERA_RQ);
//    }
//
//    private String readableFileSize(long size) {
//        if (size <= 0) return size + " B";
//        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
//        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
//        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
//    }
//
//    private String fileSize(File file) {
//        return readableFileSize(file.length());
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Received recording or error from MaterialCamera
//        if (requestCode == CAMERA_RQ) {
//            if (resultCode == RESULT_OK) {
//                final File file = new File(data.getData().getPath());
//                ToastUtil.show(String.format("Saved to: %s, size: %s",
//                        file.getAbsolutePath(), fileSize(file)));
//            } else if (data != null) {
//                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
//                if (e != null) {
//                    e.printStackTrace();
//                    ToastUtil.show("Error: " + e.getMessage());
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//            // Sample was denied WRITE_EXTERNAL_STORAGE permission
//            ToastUtil.show("Videos will be saved in a cache directory instead of an external storage directory since permission was denied.");
//        }
//    }
}

