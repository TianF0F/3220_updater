package com.example.myapplicationjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private String host = "tcp://192.168.65.21"; // 根据emqx端信息填写（）cmd ipconfig获取，需要手动/动态获取
    private String userName = "admin"; // // emqx端制定？
    private String passWord = "fang20239"; // emqx端制定？ 用户密码fang20239 ?
    private String mqtt_id = "NNN"; // 此处拟定，网页显示客户端（Client ID）
    private int i = 1;
    private Handler handler;
    private MqttClient client;
    private String mqtt_sub_topic = "ruking/feedback"; // 此处发起拟定，订阅topic的名称
    private String mqtt_pub_topic = "ruking/command"; // 此处发起拟定，发布topic的名称
    private MqttConnectOptions options;
    private ScheduledExecutorService scheduler;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main,menu);
//        return true;
//    }
    // 未添加成功，关联到main.xml文件然下述toast因为“constant expression required”未成功
    // layout问题？无最上方信息栏。。

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.add_item:
//                Toast.makeText(this,"You clicked Add",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.remove_item:
//                Toast.makeText(this,"You clicked Remove",Toast.LENGTH_SHORT).show();
//                break;
//            default:
//        }
//        return true;
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView text_condition = findViewById(R.id.text_condition);

        init();
//        Mqtt_init(); // Mqtt 初始化函数
//        Mqtt_connect(); // Mqtt 连接函数
//        startReconnect(); // Mqtt 重连函数

        handler = new Handler(Looper.myLooper()){
            @SuppressLint("SetTextI18n")
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what){
                    case 1: // 开机校准更新回传
                        break;
                    case 2: // 反馈回转
                        break;
                    case 3: // MQTT收到消息回传
//                        text_condition.setText(msg.obj.toString());
                        break;
                    case 30: // 连接失败 Toast提示
                        Toast.makeText(MainActivity.this,"< 连接失败 >",Toast.LENGTH_SHORT).show();
                        text_condition.setText("            连接失败");
                        break;
                    case 31: // 连接成功 Toast提示
                        Toast.makeText(MainActivity.this,"---连接成功！---",Toast.LENGTH_SHORT).show();
                        try{
                            client.subscribe(mqtt_sub_topic,1);
                        }catch(MqttException e) {
                            e.printStackTrace();
                        }
                        String message = "From frontpage";
                        publishmessageplus(mqtt_pub_topic,message);
                        Toast.makeText(MainActivity.this,"Command发送成功！",Toast.LENGTH_SHORT).show();
                        text_condition.setText("            连接成功");
                        break;
                        default:
                            break;
                }
            }
        };

        Button button1 = (Button) findViewById(R.id.button_1);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, ThirdActivity.class);
                startActivity(intent);
//  editText爆红         String inputText = editText.getText().toString();
//                Toast.makeText(MainActivity.this, "发起SanJose...",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        Button button2 = (Button) findViewById(R.id.button_2);
        button2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);

//                Toast.makeText(MainActivity.this, "刷新中...",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        Button button3 = (Button) findViewById(R.id.button_3);
        button3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, ip_test.class);
                startActivity(intent);

//                Toast.makeText(MainActivity.this, "过渡中...",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        Button button_c = (Button) findViewById(R.id.button_c);
        button_c.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Mqtt_connect();
                Toast.makeText(MainActivity.this, "连接中...", Toast.LENGTH_SHORT).show();
            }
        });

//        Button btn_1 = (Button) findViewById(R.id.button_1);
//        Button btn_2 = (Button) findViewById(R.id.button_2);
//        Button btn_3 = (Button) findViewById(R.id.button_3);
//
//        btn_1.setOnClickListener(this);
//        btn_2.setOnClickListener(this);
//        btn_3.setOnClickListener(this);
//    }
//    @suppressLint("NonConstantResourceId")
//            @Override
//            public void onClick(View v){
//        switch(v.getId())
//        {
//            case R.id.button_1:
//                Toast.makeText(MainActivity.this,"Congratulations!",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.button_2:
//                Toast.makeText(MainActivity.this,"Game Over!",Toast.LENGTH_SHORT).show();
//                break;
//        }

        Log.d("MainActivity","onCreate execute");
    }


    // 程序内置功能 连接wifi下获取本地网络IP地址（局域网地址）需实体机进行试验
    // 另一种方法 手机端连接局域网后查询IP地址然后手动复制输入进信息栏在程序中捕获然后进入线程，须保证进入退出过程中进程不被杀死
//    public static String getLocalIPAddress(Context context){
//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        if (wifiManager !=  null){
//            @SuppressLint("MissingPermission")WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//            String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
//            return ipAddress;
//        }
//        return "";
//    }
    private void init() {
        try {
            client = new MqttClient(host, mqtt_id, new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(userName);
            options.setPassword(passWord.toCharArray());
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("connectionLost------");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("deliveryComplete------"
                            + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    System.out.println("messageArrived------");
                    Message msg = new Message();
                    msg.what = 3;
//                    msg.obj = topicName + "---" + message.toString();
                    msg.obj = message.toString();
                    handler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void Mqtt_init() { // MQTT初始化
//        try {
//            client = new MqttClient(host, mqtt_id,
//                    new MemoryPersistence());
//
//            MqttConnectOptions options = new MqttConnectOptions();
//
//            options.setCleanSession(false);
//            // 设置是否清空session，false表示服务器会保留客户端连接记录
//            options.setUserName(userName);
//            // 设置连接的用户名
//            options.setPassword(passWord.toCharArray());
//            // 设置连接的密码
//            options.setConnectionTimeout(10);
//            // 设置超时时间 单位为秒
//            options.setKeepAliveInterval(20);
//            // 设置会话心跳时间，单位为秒，服务器每间隔1.5*20秒向客户端发送个信息判断客户端是否在线
//            client.setCallback(new MqttCallback() {
//                @Override
//                public void connectionLost(Throwable cause) {
//                    System.out.println("connectionLost--------");
//                }
//
//                @Override
//                public void deliveryComplete(IMqttDeliveryToken token) {
//                    System.out.println("deliveryComplete--------" +
//                            token.isComplete());
//                }
//
//                @Override
//                public void messageArrived(String topicName, MqttMessage message)
//                        throws Exception {
//
//                    System.out.println("messageArrived--------");
//                    Message msg = new Message();
//                    msg.what = 3;
//                    msg.obj = message.toString();
//                    handler.sendMessage(msg);
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void Mqtt_connect(){ // MQTT连接函数
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    if(!(client.isConnected()))
                    {
                        MqttConnectOptions options = null;
                        client.connect(options);
                        Message msg = new Message();
                        msg.what = 31;
                        handler.sendMessage(msg);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 30;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void startReconnect(){ // 重新连接函数
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable(){
            @Override
            public void run(){
                if (!client.isConnected()){
                    Mqtt_connect();
                }
            }
        }, 0*1000,10 * 1000, TimeUnit.MILLISECONDS);
    }

    private void publishmessageplus(String topic,String message2) // 订阅函数（下发任务/命令）
    {
        if(client == null || !client.isConnected()){
            return;
        }
        MqttMessage message = new MqttMessage();
        message.setPayload(message2.getBytes());
        try{
            client.publish(topic,message);
        }catch(MqttException e){
            e.printStackTrace();
        }
    }

    public static String intIP2StringIP(int ip){
        return (ip & 0xFF) + "." +
                ((ip>>8)&0xFF) + "." +
                ((ip>>16)&0xFF) + "." +
                (ip>>24&0xFF);
    }
    public static String getLocalIPAddress(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null){
            @SuppressLint("MissingPermission") WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
            return ipAddress;
        }
        return "";
    }
}