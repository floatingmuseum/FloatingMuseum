package com.floatingmuseum.androidtest.net;

import com.floatingmuseum.androidtest.utils.RxUtil;
import com.liulishuo.filedownloader.i.IFileDownloadIPCCallback;
import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Floatingmuseum on 2017/3/20.
 */

public class Repository {

    private static Repository repository = null;
    private NetService netService = null;

    private Repository() {
        netService = RetrofitFactory.getInstance();
    }

    public static Repository getInstance() {
        if (repository == null) {
            synchronized (Repository.class) {
                if (repository == null) {
                    repository = new Repository();
                }
            }
        }
        return repository;
    }

    public void getRandomArticle() {
        netService.getRandomArticle()
                .compose(RxUtil.<Response>threadSwitch())
                .subscribe(new Observer<Response>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response response) {
                        Logger.d("getRandomArticle...onNext");
                        Logger.d("getRandomArticle...onNext...Code:" + response.code());
                        Logger.d("getRandomArticle...onNext...Headers:" + response.headers().toString());
                        Logger.d("getRandomArticle...onNext...Body:" + response.body().toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("getRandomArticle...onError");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
