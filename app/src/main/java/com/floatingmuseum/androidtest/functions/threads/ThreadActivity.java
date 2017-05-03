package com.floatingmuseum.androidtest.functions.threads;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.orhanobut.logger.Logger;

/**
 * Created by Floatingmuseum on 2017/5/3.
 */

public class ThreadActivity extends BaseActivity {

    private int tickets = 50;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threads);
        TestThread thread = new TestThread(this);
        Thread thread1 = new Thread(thread, "窗口1");
        Thread thread2 = new Thread(thread, "窗口2");
        Thread thread3 = new Thread(thread, "窗口3");
        Thread thread4 = new Thread(thread, "窗口4");
        Thread thread5 = new Thread(thread, "窗口5");
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
    }

    public synchronized int getTickets() {
        if (tickets <= 0) {
            return 0;
        }
        int currentTicket = tickets;
        tickets--;
        Logger.d("线程:" + Thread.currentThread().getName() + "...售出第" + currentTicket + "票...剩余:" + tickets);
        return currentTicket;
    }
}
