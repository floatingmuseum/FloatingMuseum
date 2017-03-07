package com.floatingmuseum.androidtest.net;

/**
 * Created by Floatingmuseum on 2017/3/7.
 */

public class RetrofitFactory {
    private static NetService service = new FloatingMuseumRetrofit().getService();

    public static NetService getInstance() {
        return service;
    }
}
