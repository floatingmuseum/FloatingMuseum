package com.floatingmuseum.androidtest.functions.camera;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.BlackLevelPattern;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Range;
import android.util.Rational;
import android.util.Size;
import android.util.SizeF;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Floatingmuseum on 2017/8/1.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2ConfigManager {

    private static Camera2ConfigManager manager;
    private String tag = Camera2ConfigManager.class.getSimpleName() + "日志";
    private String currentCameraID;
    private Map<String, Size> outputSize = new HashMap<>();
    private Map<String, List<Size>> outputSizes = new HashMap<>();
    private Map<String, CameraCharacteristics> cameraConfig = new HashMap<>();

    private Camera2ConfigManager() {
    }

    public static Camera2ConfigManager getInstance() {
        if (manager == null) {
            synchronized (Camera2ConfigManager.class) {
                if (manager == null) {
                    manager = new Camera2ConfigManager();
                }
            }
        }
        return manager;
    }

    public void init(String cameraID, CameraCharacteristics characteristics) {

        //所有可以设置的CaptureRequest Key
        List<CaptureRequest.Key<?>> availableCaptureRequestKeys = characteristics.getAvailableCaptureRequestKeys();
        //所有可以获取的CaptureResult Key
        List<CaptureResult.Key<?>> availableCaptureResultKeys = characteristics.getAvailableCaptureResultKeys();
        //
        List<CameraCharacteristics.Key<?>> keys = characteristics.getKeys();
        Logger.d(tag + "...availableCaptureRequestKeys:" + availableCaptureRequestKeys.size() + "...availableCaptureResultKeys:" + availableCaptureResultKeys.size() + "...characteristicsKeys:" + keys.size());
//        for (CaptureRequest.Key<?> requestKey : availableCaptureRequestKeys) {
//            Logger.d(tag + "...RequestKey:" + requestKey.getName());
//        }
//        for (CaptureResult.Key<?> resultKey : availableCaptureResultKeys) {
//            Logger.d(tag + "...ResultKey:" + resultKey.getName());
//        }
//        for (CameraCharacteristics.Key<?> key : keys) {
//            Logger.d(tag + "...CameraCharacteristicsKey:" + key.getName());
//        }

        cameraConfig.put(cameraID, characteristics);

        if (true) {
            return;
        }


        //像差校正模式?
        int[] aberrationCorrectionModes = characteristics.get(CameraCharacteristics.COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES);
        //自动曝光反冲带模式?
        int[] autoExposureAntibandingModes = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES);
        //自动曝光模式?
        int[] autoExposureModes = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
        //帧率范围
        Range<Integer>[] frameRateRanges = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
        Range<Integer> exposureCompensationRanges = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
        Rational exposureCompensationSteps = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Boolean isSupportAELock = characteristics.get(CameraCharacteristics.CONTROL_AE_LOCK_AVAILABLE);
        }
        int[] autoFocusModes = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
        int[] colorEffects = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int[] controlModes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_MODES);
        }
        int[] sceneModes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);
        int[] videoStabilizationModes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);
        int[] autoWhiteBalanceModes = characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Boolean isSupportAWBLock = characteristics.get(CameraCharacteristics.CONTROL_AWB_LOCK_AVAILABLE);
        }
        Integer controlMaxRegionsAE = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE);
        Integer controlMaxRegionsAF = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF);
        Integer controlMaxRegionsAWB = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Range<Integer> boostsRange = characteristics.get(CameraCharacteristics.CONTROL_POST_RAW_SENSITIVITY_BOOST_RANGE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Boolean isExclusive = characteristics.get(CameraCharacteristics.DEPTH_DEPTH_IS_EXCLUSIVE);
        }
        int[] edgeEnhancementModes = characteristics.get(CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES);
        Boolean hasFlashUnit = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        int[] pixelCorrectionModes = characteristics.get(CameraCharacteristics.HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES);
        Integer supportedHardwareLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        Size[] JPEGThumbnailSizes = characteristics.get(CameraCharacteristics.JPEG_AVAILABLE_THUMBNAIL_SIZES);
        //摄像头方向
        Integer cameraFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
        float[] apertureSizeValues = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
        float[] neutralDensityFilterValues = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FILTER_DENSITIES);
        float[] focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
        int[] opticalImageStabilizationModes = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION);
        Integer lensFocusDistanceCalibrationQuality = characteristics.get(CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION);
        Float hyperfocalDistance = characteristics.get(CameraCharacteristics.LENS_INFO_HYPERFOCAL_DISTANCE);
        Float shortestDistance = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            float[] intrinsicCalibration = characteristics.get(CameraCharacteristics.LENS_INTRINSIC_CALIBRATION);
            float[] poseRotation = characteristics.get(CameraCharacteristics.LENS_POSE_ROTATION);
            float[] poseTranslation = characteristics.get(CameraCharacteristics.LENS_POSE_TRANSLATION);
            float[] radialDistortion = characteristics.get(CameraCharacteristics.LENS_RADIAL_DISTORTION);
        }
        int[] noiseReductionModes = characteristics.get(CameraCharacteristics.NOISE_REDUCTION_AVAILABLE_NOISE_REDUCTION_MODES);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Integer maxCaptureStall = characteristics.get(CameraCharacteristics.REPROCESS_MAX_CAPTURE_STALL);
        }
        int[] availableCapabilties = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Integer maxInputStreams = characteristics.get(CameraCharacteristics.REQUEST_MAX_NUM_INPUT_STREAMS);
        }
        Integer maxOutputProc = characteristics.get(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC);
        Integer maxOutputProcStalling = characteristics.get(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC_STALLING);
        Integer maxOutputRaw = characteristics.get(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_RAW);
        Integer partialResultCount = characteristics.get(CameraCharacteristics.REQUEST_PARTIAL_RESULT_COUNT);
        Byte pipelineMaxDepth = characteristics.get(CameraCharacteristics.REQUEST_PIPELINE_MAX_DEPTH);
        Float scalerAvailableMaxDigitalZoom = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
        Integer cropType = characteristics.get(CameraCharacteristics.SCALER_CROPPING_TYPE);
        StreamConfigurationMap streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        int[] sensorTestPatternModes = characteristics.get(CameraCharacteristics.SENSOR_AVAILABLE_TEST_PATTERN_MODES);
        BlackLevelPattern blackLevelPattern = characteristics.get(CameraCharacteristics.SENSOR_BLACK_LEVEL_PATTERN);
        ColorSpaceTransform sensorCalibrationTransform1 = characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM1);
        ColorSpaceTransform sensorCalibrationTransform2 = characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM2);
        ColorSpaceTransform sensorColorTransform1 = characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM1);
        ColorSpaceTransform sensorColorTransform2 = characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM2);
        ColorSpaceTransform sensorForwardMatrix1 = characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX1);
        ColorSpaceTransform sensorForwardMatrix2 = characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX2);
        Rect sensorInfoActiveArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        Integer arrangementOfColorFilters = characteristics.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT);
        Range<Long> rangeOfExposureTimes = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Boolean lensShadingApplied = characteristics.get(CameraCharacteristics.SENSOR_INFO_LENS_SHADING_APPLIED);
        }
        Long maxFrameDuration = characteristics.get(CameraCharacteristics.SENSOR_INFO_MAX_FRAME_DURATION);
        SizeF physicalDimensionOffullPixelArray = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        Size fullPixelArray = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Rect preCorrectionActiveArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE);
        }
        Range<Integer> sensitivitiesRange = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
        Integer timestampSource = characteristics.get(CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE);
        Integer maxRawValue = characteristics.get(CameraCharacteristics.SENSOR_INFO_WHITE_LEVEL);
        Integer maxAnalogSensitivity = characteristics.get(CameraCharacteristics.SENSOR_MAX_ANALOG_SENSITIVITY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Rect[] blackRegions = characteristics.get(CameraCharacteristics.SENSOR_OPTICAL_BLACK_REGIONS);
        }
        Integer sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        Integer sensorReferenceIlluminant1 = characteristics.get(CameraCharacteristics.SENSOR_REFERENCE_ILLUMINANT1);
        Byte sensorReferenceIlluminant2 = characteristics.get(CameraCharacteristics.SENSOR_REFERENCE_ILLUMINANT2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int[] shadingModes = characteristics.get(CameraCharacteristics.SHADING_AVAILABLE_MODES);
        }
        int[] faceDetectionModes = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);
        boolean[] hotPixelMapOutputModes = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_HOT_PIXEL_MAP_MODES);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int[] lensShadingMapOutputModes = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_LENS_SHADING_MAP_MODES);
        }
        Integer maxFaceCount = characteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT);
        Integer maxLatency = characteristics.get(CameraCharacteristics.SYNC_MAX_LATENCY);
        int[] tonemappingModes = characteristics.get(CameraCharacteristics.TONEMAP_AVAILABLE_TONE_MAP_MODES);
        Integer maxCurvePoints = characteristics.get(CameraCharacteristics.TONEMAP_MAX_CURVE_POINTS);
    }

    /**
     * 摄像头朝向
     */
    public Integer getCameraFacing(String cameraID) {
        return cameraConfig.get(cameraID).get(CameraCharacteristics.LENS_FACING);
    }

    /**
     * 获取输出分辨率,默认是最大分辨率
     */
    public Size getOutputSize(String cameraID) {
        Logger.d(tag + "...当前默认输出分辨率:" + outputSize.toString());
        if (outputSize.containsKey(cameraID)) {
            return outputSize.get(cameraID);
        } else {
            List<Size> outputSizeList = getOutputSizes(cameraID);
            if (outputSizeList.isEmpty()) {
                // 应该不会出现null吧
                return null;
            } else {
                Size size = Collections.max(outputSizeList, new CompareSizesByArea());
                outputSize.put(cameraID, size);
                return size;
            }
        }
    }

    /**
     * 可输出分辨率
     */
    public List<Size> getOutputSizes(String cameraID) {
        if (outputSizes.containsKey(cameraID)) {
            return outputSizes.get(cameraID);
        } else {
            StreamConfigurationMap map = cameraConfig.get(cameraID).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map != null) {
                Size[] sizes = map.getOutputSizes(ImageFormat.JPEG);
                List<Size> newSizeList = new ArrayList<>();
                newSizeList.addAll(Arrays.asList(sizes));
                outputSizes.put(cameraID, newSizeList);
                return newSizeList;
            } else {
                return new ArrayList<>();
            }
        }
    }

    /**
     * 可输出分辨率
     */
    @Nullable
    public <T> Size[] getOutputSizes(String cameraID, Class<T> clazz) {
        return cameraConfig.get(cameraID).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(clazz);
    }

    /**
     * 可输出格式
     */
    public int[] getOutputFormats(String cameraID) {
        return cameraConfig.get(cameraID).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputFormats();
    }

    /**
     * 获取传感器方向
     */
    public Integer getSensorOrientation(String cameraID) {
        return cameraConfig.get(cameraID).get(CameraCharacteristics.SENSOR_ORIENTATION);
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    public static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}
