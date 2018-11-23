package com.example.mhelithnatavio.smartlock;


import android.content.Intent;
import android.os.AsyncTask;
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
import java.net.ProtocolException;
import java.net.URL;
//import <com.example.mhelithnatavio.smartlock>.R;


public class LogInActivity extends AppCompatActivity
{
    private EditText email;
    private EditText password;
    private Button bLogin;
    private CheckBox chShow;
    private TextView regLink;

    String username;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        email = (EditText) findViewById(R.id.etUsername);
        password = (EditText) findViewById(R.id.etPassword);
        bLogin = (Button) findViewById(R.id.bLogin);
        chShow = (CheckBox) findViewById(R.id.chShow);
        regLink = (TextView) findViewById(R.id.tvRegister);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            username = email.getText().toString();
            pass = password.getText().toString();

                JSONObject object = new JSONObject();
                try {
                    object.put("email", username);
                    object.put("password",pass);

                }catch (JSONException e)
                {
                    e.printStackTrace();
                }

            new PostAsyncTask().execute(object.toString());
            }
        });


        // register link goes to register page
        regLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, RegisterActivity.class);
                LogInActivity.this.startActivity(intent);
            }
        });

        // show check box enables user to show/hide their password
        chShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
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
                URL url = new URL("https://sdsmartlock.com/api/auth");
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
                    }
                    else {
                        // convert buffer to string
                        JsonResponse = buffer.toString();
                        JSONObject myJson = new JSONObject(JsonResponse);
                        // use myJson as needed, for example
                        String locks = myJson.optString("locks");
                        int id = myJson.optInt("_id");
                        String name = myJson.optString("name");
                        String email = myJson.optString("email");
                        String password = myJson.optString("password");
                        int v = myJson.optInt("__v");

                        return name + "\n" + email;
                    }
                } else {
                    return urlConnection.getResponseMessage();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
            if (result == null) {
                Toast.makeText(LogInActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
            } else {
                String[] logInfo = result.split("\n");

                Intent login = new Intent(getApplicationContext(), MainActivity.class);
                login.putExtra("name", logInfo[0]);
                login.putExtra("email", logInfo[1]);

                LogInActivity.this.startActivity(login);
            }
        }
    }




}
