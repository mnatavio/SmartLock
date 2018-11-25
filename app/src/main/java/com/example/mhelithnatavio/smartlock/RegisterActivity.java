package com.example.mhelithnatavio.smartlock;


import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RegisterActivity extends AppCompatActivity {
    private EditText etname;
    private EditText etemail;
    private EditText etpassword;
    //    private EditText etlock;
    private Button bReg;

    String name;
    String email;
    String password;
    String lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etname = (EditText) findViewById(R.id.name);
        etemail = (EditText) findViewById(R.id.etUsername);
        etpassword = (EditText) findViewById(R.id.etPassword);
        bReg = (Button) findViewById(R.id.bRegister);

        bReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etname.getText().toString();
                email = etemail.getText().toString();
                password = etpassword.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty())
                    Toast.makeText(RegisterActivity.this, "Oops, you forgot to fill in some fields", Toast.LENGTH_SHORT).show();
                else
                 {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("name", name);
                        object.put("email", email);
                        object.put("password", password);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                     new PostAsyncTask().execute(object.toString());
                 }

            }
        });

    }

    public class PostAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("https://sdsmartlock.com/api/users");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                //set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);

                // json data
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();

                if (urlConnection.getResponseCode() == 200) {
                    //input stream
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String inputLine;
                    while ((inputLine = reader.readLine()) != null)
                        buffer.append(inputLine + "\n");
                    if (buffer.length() == 0) {
                        // Stream was empty. No point in parsing.
                        return null;
                    } else {
                        // convert buffer to string
                        return JsonResponse = buffer.toString();
                    }
                } else {
                    return null;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do something with result
            if (result == null)
            {
                Toast.makeText(RegisterActivity.this, "User already registered.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegisterActivity.this, "Account registerd succesfully.", Toast.LENGTH_SHORT).show();

                Intent reg = new Intent(RegisterActivity.this, LogInActivity.class);
                RegisterActivity.this.startActivity(reg);

            }
        }
    }
}


