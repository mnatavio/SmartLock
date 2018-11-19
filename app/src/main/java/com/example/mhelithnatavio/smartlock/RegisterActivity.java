package com.example.mhelithnatavio.smartlock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import <com.example.mhelithnatavio.smartlock>.R;


public class RegisterActivity extends AppCompatActivity {

    private EditText etname = (EditText) findViewById(R.id.name);
    private EditText etUsername = (EditText) findViewById(R.id.etUsername);
    private EditText etPassword = (EditText) findViewById(R.id.etPassword);
    private EditText etLock = (EditText) findViewById(R.id.etLock);
    private Button bRegister = (Button) findViewById(R.id.bRegister);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }


}
