package com.example.mhelithnatavio.smartlock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;




public class MainActivity extends AppCompatActivity {
    private TextView logOut;
    private TextView tvname;
    private Button btn;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // save all information from when users logs in
        Intent info = getIntent();
//        String lock = info.getStringExtra("lock");
//        String id = info.getStringExtra("id");
//        String name = info.getStringExtra("name");
//        String email = info.getStringExtra("email");
        token = info.getStringExtra("token");

        tvname = (TextView) findViewById(R.id.name);
//        tvname.setText(token);

        btn = (Button) findViewById(R.id.btnSwitch);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = "5bd603af9dfa7d068ceb70dd";

                JSONObject object = new JSONObject();
                try {
                    object.put("lockid", id);
                    object.put("userid", id);
                    object.put("status", "false");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new PostAsyncTask().execute(object.toString());

            }
        });


        logOut = (TextView) findViewById(R.id.tvLogout);
        logOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent logoutIntent = new Intent(MainActivity.this, LogInActivity.class);
                MainActivity.this.startActivity(logoutIntent);
            }
        });

    }




    public class PostAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params)
        {
            String JsonResponse = null;
            String JsonDATA = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("https://sdsmartlock.com/api/events");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                // output buffer writer
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("x-auth-token", token);
                urlConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
                urlConnection.setRequestProperty("X-Requested-With","XMLHttpRequest");


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
                        // Stream is empty. No point in parsing.
                        return null;
                    } else {
                        // convert buffer to string
//                        return "data here"
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
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
//            Do something with result
//            if(result == null)
//                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
//            else
//            byte[] byteValueBase64Decoded = Base64.getDecoder().decode(token);
//            String stringValueBase64Decoded = new String(byteValueBase64Decoded);
                tvname.setText(token);
        }
    }



}