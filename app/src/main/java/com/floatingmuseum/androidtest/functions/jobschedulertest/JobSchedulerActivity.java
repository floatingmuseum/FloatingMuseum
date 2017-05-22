package com.floatingmuseum.androidtest.functions.jobschedulertest;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;

/**
 * Created by Floatingmuseum on 2017/5/11.
 */

public class JobSchedulerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_scheduler);

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            JobInfo.Builder builder = new JobInfo.Builder(1,new ComponentName(this,NetService.class));
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
            jobScheduler.schedule(builder.build());
        }
    }
}
