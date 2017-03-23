package com.floatingmuseum.androidtest.functions.socket;

import android.os.Handler;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Floatingmuseum on 2017/3/23.
 */

public class ClientThread extends Thread {

    private String serverAddress;
    private int port;
    private Handler handler;
    private Socket socket;

    public ClientThread(String serverAddress, int port, Handler handler){
        this.serverAddress = serverAddress;
        this.port = port;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverAddress,port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
