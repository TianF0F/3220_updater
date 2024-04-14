package com.example.myapplicationjava;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.appcompat.widget.AppCompatEditText;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ip_test extends AppCompatActivity {

    private TextView ipTextView = null;
    private TextView nameTextView = null;
    private ConnectivityManager mConnectivityManager = null;
    private NetworkInfo mActiveNetInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_test);

        nameTextView = findViewById(R.id.nameTextview);
        ipTextView = findViewById(R.id.ipTextview);
        mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        mActiveNetInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mActiveNetInfo == null)
            ;
        else
            setUpInfo();

        Button button1 = (Button) findViewById(R.id.button_8);
        button1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(ip_test.this, SecondActivity.class);
                startActivity(intent);
            }
        });

        Button button2 = (Button) findViewById(R.id.btn_send);
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AppCompatEditText et_write = null;
                String context = et_write.getText().toString();
                Intent intent = SecondActivity.newIntent(ip_test.this,context);
                startActivity(intent);
            }
        });
    }

    public String getIPAddress() {
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if ((info.getType() == ConnectivityManager.TYPE_MOBILE) || (info.getType() == ConnectivityManager.TYPE_WIFI))
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
        } else {
            return null;
        }
        return null;
    }

    public void setUpInfo() {
        if (mActiveNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            nameTextView.setText("网络类型：WIFI");
            ipTextView.setText("IP地址： " + getIPAddress());
        } else if (mActiveNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            nameTextView.setText("网络类型： 3G/4G");
            ipTextView.setText("IP地址： " + getIPAddress());
        } else {
            nameTextView.setText("网络类型： 未知");
            ipTextView.setText("IP地址： ");
        }
    }
}

