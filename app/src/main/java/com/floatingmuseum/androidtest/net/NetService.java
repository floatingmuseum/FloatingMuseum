package com.floatingmuseum.androidtest.net;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Floatingmuseum on 2017/3/7.
 */

public interface NetService {

    @Streaming
    @GET
    Observable<ResponseBody> dowonloadApk(@Header("RANGE") String start, @Url String url);

    @GET("random/data/Android/10")
    Observable<Response<ResponseBody>> getRandomArticle();
}
