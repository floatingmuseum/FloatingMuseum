package com.floatingmuseum.androidtest.functions.threads;

import com.orhanobut.logger.Logger;

/**
 * Created by Floatingmuseum on 2017/5/3.
 */

public class TestThread implements Runnable {

    ThreadActivity activity;

    public TestThread(ThreadActivity activity){
        this.activity = activity;
    }

    @Override
    public void run() {
        while (activity.getTickets()>0) {

            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            Logger.d("线程:" + Thread.currentThread().getName() + "...售出第" + tickets + "票");
//            tickets--;
        }
    }
}
