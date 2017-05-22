package com.floatingmuseum.androidtest.functions.jobschedulertest;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.orhanobut.logger.Logger;

/**
 * Created by Floatingmuseum on 2017/5/11.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Logger.d("NetService...onStartJob");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Logger.d("NetService...onStopJob");
        return true;
    }
}
