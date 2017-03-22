package com.floatingmuseum.androidtest.functions.hotspot;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Floatingmuseum on 2017/3/22.
 */

public class ClientActivity extends BaseActivity {

    private Button bt;
    private TextView tv;
    private Socket socket;
    private String serverIpAddress = "192.168.1.104";

    private static final int REDIRECTED_SERVERPORT = 6000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        bt = (Button) findViewById(R.id.myButton);
        tv = (TextView) findViewById(R.id.myTextView);
        try {
            InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
            socket = new Socket(serverAddr, REDIRECTED_SERVERPORT);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    EditText et = (EditText) findViewById(R.id.EditText01);
                    String str = et.getText().toString();
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    out.println(str);
                    Log.d("Client", "Client sent message");
                } catch (UnknownHostException e) {
                    tv.setText("Error1");
                    e.printStackTrace();
                } catch (IOException e) {
                    tv.setText("Error2");
                    e.printStackTrace();
                } catch (Exception e) {
                    tv.setText("Error3");
                    e.printStackTrace();
                }
            }
        });
    }
}
