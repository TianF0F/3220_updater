package com.example.myapplicationjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);

        Button button1 = (Button) findViewById(R.id.button_4);
        button1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                startActivity(intent);

                Toast.makeText(SecondActivity.this, "返回中...",
                        Toast.LENGTH_SHORT).show();
            }
        });

        Button button2 = (Button) findViewById(R.id.button_41);
        button2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(SecondActivity.this, ip_test.class);
                startActivity(intent);

                Toast.makeText(SecondActivity.this, "查看中",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Intent newIntent(Context packageContext, String context){
        Intent intent = new Intent(packageContext,ip_test.class);
        intent.putExtra("CONTEXT",context);
        return intent;
    }
}