package com.floatingmuseum.androidtest.functions.socket;

import android.os.Handler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Floatingmuseum on 2017/3/23.
 */

public class ServerThread extends Thread {

    private Handler handler;
    private String address;
    private int port;
    private boolean isRunning = false;
    private List<String> clients = new ArrayList<>();
    private ServerSocket serverSocket;

    public ServerThread(String address, int port, Handler handler) {
        this.handler = handler;
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        isRunning = true;
        try {
            serverSocket = new ServerSocket(port);
            while (isRunning) {
                Socket socket = serverSocket.accept();
                saveNewClient(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveNewClient(Socket socket) {
        InetAddress inetAddress = socket.getInetAddress();
        if (!clients.contains(inetAddress.getHostAddress())) {
            clients.add(inetAddress.getHostAddress());
            sendMessage(socket, "Connect to " + address + ":" + port + " success.welcome.");
        }
    }

    private void sendMessage(Socket socket, String message) {
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print(message);
            printStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    public void stopThread() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            isRunning = false;
        }
    }
}
