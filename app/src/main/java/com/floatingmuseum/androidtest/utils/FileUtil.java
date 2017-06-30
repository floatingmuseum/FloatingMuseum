package com.floatingmuseum.androidtest.utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.math.BigDecimal;

/**
 * Created by Floatingmuseum on 2016/12/1.
 */

public class FileUtil {

    /**
     * 获取带扩展名的文件名
     *
     * @param path
     * @return
     */
    public static String getFileName(@NonNull String path) {
        int lastSlashIndex = path.lastIndexOf("/");
        if (lastSlashIndex == -1) {
            return path;
        } else {
            return path.substring(lastSlashIndex + 1);
        }
    }

    /**
     * 获取不带扩展名的文件名
     *
     * @param path
     * @return
     */
    public static String getFileNameWithoutExtension(@NonNull String path) {
        String fileName = getFileName(path);
        int lastPointIndex = fileName.lastIndexOf(".");
        if (lastPointIndex == -1) {
            return fileName;
        } else {
            return fileName.substring(0, lastPointIndex);
        }
    }

    /**
     * 获取扩展名
     *
     * @param path
     * @return
     */
    public static String getFileExtension(@NonNull String path) {
        String fileName = getFileName(path);
        int lastPointIndex = fileName.lastIndexOf(".");
        if (lastPointIndex == -1) {
            return "";
        } else {
            return fileName.substring(lastPointIndex + 1);
        }
    }

    public static void initDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
