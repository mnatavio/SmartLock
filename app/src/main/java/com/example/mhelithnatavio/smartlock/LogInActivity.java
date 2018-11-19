package com.example.mhelithnatavio.smartlock;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
//import <com.example.mhelithnatavio.smartlock>.R;


public class LogInActivity extends AppCompatActivity
{
//    EditText etUsername = (EditText) findViewById(R.id.etUsername);
//    EditText etPassword = (EditText) findViewById(R.id.etPassword);
//    CheckBox chShow = (CheckBox) findViewById(R.id.chShow);
//    Button bLogin = (Button) findViewById(R.id.bLogin);
//    TextView registerLink = (TextView) findViewById(R.id.tvRegister);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final CheckBox chShow = (CheckBox) findViewById(R.id.chShow);
        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final TextView registerLink = (TextView) findViewById(R.id.tvRegister);


        // register link goes to register page
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LogInActivity.this, RegisterActivity.class);
                LogInActivity.this.startActivity(registerIntent);
            }
        });

        // show check box enables user to show/hide their password
        chShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        // log in button calls validate function
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                 String username = etUsername.getText().toString();
                 String password = etPassword.getText().toString();

                 errInfo(username, password);

                 validate(username,password);


            }
        });

    }

    private void errInfo(String username, String password)
    {
        if (username.isEmpty() && !password.isEmpty())
        {
            Toast.makeText(LogInActivity.this, "Please enter valid Email", Toast.LENGTH_SHORT).show();
        }
        else if (!username.isEmpty() && password.isEmpty())
        {
            Toast.makeText(LogInActivity.this, "Please enter valid Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(LogInActivity.this, "Invalid Email and Password", Toast.LENGTH_SHORT).show();
        }
    }

    // checks if username and password are correct
    private void validate(String userName, String userPassword)
    {
//        if(!userName.equals(""))
//        {
//            Toast.makeText(LogInActivity.this, "Please enter valid username", Toast.LENGTH_SHORT).show();
//        }
        if(userName.equals("Admin") && userPassword.equals("pass"))
        {
            Intent login = new Intent(LogInActivity.this, MainActivity.class);
            LogInActivity.this.startActivity(login);
        }
        else
        {
        }


    }



}
