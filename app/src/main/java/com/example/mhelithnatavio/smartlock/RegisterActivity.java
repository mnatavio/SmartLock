package com.example.mhelithnatavio.smartlock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import <com.example.mhelithnatavio.smartlock>.R;


public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etFname = (EditText) findViewById(R.id.etFname);
        final EditText etLname = (EditText) findViewById(R.id.etLname);
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final EditText etLock = (EditText) findViewById(R.id.etLock);

        final Button bRegister = (Button) findViewById(R.id.bRegister);

        etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fname = etFname.getText().toString();
                final String lname = etLname.getText().toString();
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                final String lock = etLock.getText().toString();

            }
        });

    }
}
