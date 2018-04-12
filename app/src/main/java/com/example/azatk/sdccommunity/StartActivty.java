package com.example.azatk.sdccommunity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartActivty extends AppCompatActivity {

    private Button mRegBtn;
    private Button mLoginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mRegBtn=(Button)findViewById(R.id.start_reg_btn);
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent=new Intent(StartActivty.this,RegisterActivity.class);
                startActivity(reg_intent);
            }
        });

        mLoginBtn=(Button)findViewById(R.id.start_login_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent=new Intent(StartActivty.this,LoginActivity.class);
                startActivity(reg_intent);
            }
        });

    }
}
