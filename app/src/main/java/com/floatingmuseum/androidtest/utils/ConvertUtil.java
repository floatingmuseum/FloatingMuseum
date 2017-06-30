package com.floatingmuseum.androidtest.utils;

import java.math.BigDecimal;

/**
 * Created by Floatingmuseum on 2017/6/30.
 * <p>
 * 各类数值间的转换
 */

public class ConvertUtil {

    /**
     * mb to bytes
     *
     * @param mbs
     * @return
     */
    public static long mbToByte(long mbs) {
        return mbs * 1024 * 1024;
    }

    /**
     * mb to kb
     *
     * @param mbs
     * @return
     */
    public static long mbToKb(long mbs) {
        return mbs * 1024;
    }

    /**
     * kb to byte
     *
     * @param kbs
     * @return
     */
    public static long kbToByte(long kbs) {
        return kbs * 1024;
    }

    /**
     * byte to kb
     *
     * @param bytes
     * @return
     */
    public static float byteToKb(long bytes) {
        BigDecimal bdBytes = new BigDecimal(bytes);
        BigDecimal kb = new BigDecimal(1024);
        return bdBytes.divide(kb).floatValue();
    }

    /**
     * byte to mb
     *
     * @param bytes
     * @return
     */
    public static float byteToMb(long bytes) {
        BigDecimal bdBytes = new BigDecimal(bytes);
        BigDecimal mb = new BigDecimal(1024 * 1024);
        return bdBytes.divide(mb).floatValue();
    }
}
