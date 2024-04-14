package com.example.myapplicationjava;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThirdActivity extends AppCompatActivity {

    private String host = "tcp://192.168.65.21"; // 根据emqx端信息填写（）cmd ipconfig获取，需要手动/动态获取
    private String userName = "admin"; // // emqx端制定？
    private String passWord = "public"; // emqx端制定？ 用户密码fang20239 ? 此处没有输入即保持和下列相同
    private String mqtt_id = "DDDD"; // 此处拟定，网页显示客户端（Client ID）
    private int i = 1;
    private Handler handler;
    private MqttClient client;
    private String mqtt_sub_topic = "ruking/feedback"; // 订阅topic的名称
    private String mqtt_pub_topic = "ruking/feedback"; // 发布topic的名称
    private MqttConnectOptions options;
    private ScheduledExecutorService scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_layout);
        TextView text1 = findViewById(R.id.test1);
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
                    case 3: // MQTT收到消息回传，反馈到text1上，被动接收制定topic信息
                        text1.setText(msg.obj.toString());
                        break;
                    case 30: // 连接失败 Toast提示
                        Toast.makeText(ThirdActivity.this,"< 连接失败 >",Toast.LENGTH_SHORT).show();
                        text_condition.setText("            连接失败");
                        break;
                    case 31: // 连接成功 Toast提示
                        Toast.makeText(ThirdActivity.this,"---连接成功！---",Toast.LENGTH_SHORT).show();
                        try{
                            client.subscribe(mqtt_sub_topic,1);
                        }catch(MqttException e) {
                            e.printStackTrace();
                        }
                        text_condition.setText("            连接成功");
                        break;
                    case 32:
                        String message = "这是发布到File的信息";
                        publishmessageplus(mqtt_pub_topic,message);
                        Toast.makeText(ThirdActivity.this,"File发送成功！",Toast.LENGTH_SHORT).show();
                        break;
                        default:
                            break;
                }
            }
        };

        Button button1 = (Button) findViewById(R.id.button_5);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
//                Intent intent = new Intent(ThirdActivity.this, MainActivity.class);
//                startActivity(intent);
                String message = "一一一条简讯"; //这里是edittext要获取的信息
                publishmessageplus(mqtt_pub_topic,message);
                Toast.makeText(ThirdActivity.this,"信息发送中...",Toast.LENGTH_SHORT).show();
            }
        });

        Button button2 = (Button) findViewById(R.id.button_6);
        button2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Mqtt_connect();

            }
        });

        Button button3 = (Button) findViewById(R.id.button_7);
        button3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                try {
                    unSubscribe();
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
//                Intent intent = new Intent(ThirdActivity.this, SecondActivity.class);
//                startActivity(intent);

            }
        });

        Button button_s = (Button) findViewById(R.id.button_s);
        button_s.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                EditText editTexts = (EditText)findViewById(R.id.editText_s);
                String message1 = editTexts.getText().toString(); //publishmessage方法中的信息变量在按钮监听中获取，而不是oncreate之后，可能因为gettext()是个动作要被抓取？
                publishmessageplus(mqtt_pub_topic,message1);
                Toast.makeText(ThirdActivity.this,"发送中...",Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("MainActivity","onCreate execute");
    }

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
                    ;
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
                    msg.obj = " " + message.toString();
                    handler.sendMessage(msg); // handler 回传
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

    private void parseJsonobj(String jsonobj){
        try {
            JSONObject jsonObject = new JSONObject(jsonobj);
            String name = jsonObject.getString("name");
            String temp = jsonObject.getString("Temp");
            String humi = jsonObject.getString("Humi");

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class MqttListener implements IMqttActionListener{
        @Override
        public void onSuccess(IMqttToken asyncActionToken){

        }
        public void onFailure(IMqttToken asyncActionToken, Throwable exception){

        }
    }
    private void unSubscribe() throws MqttException{ //取消订阅的函数
        if(client != null){
            client.unsubscribe(mqtt_sub_topic);
        }
        Toast.makeText(ThirdActivity.this,"成功取消订阅",Toast.LENGTH_SHORT).show();
    }

    private void Subscribe() throws MqttException{ //订阅的函数
        if(client == null){
            client.subscribe(mqtt_sub_topic);
        }
        Toast.makeText(ThirdActivity.this,"订阅中...",Toast.LENGTH_SHORT).show();
    }

}