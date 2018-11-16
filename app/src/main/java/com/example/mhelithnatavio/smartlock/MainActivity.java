package com.example.mhelithnatavio.smartlock;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;
    private String id;
    private String status;
    private int v;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final TextView logOut = (TextView) findViewById(R.id.tvLogout);
        final Button btnHit = (Button) findViewById(R.id.btnHit);
        tvStatus = (TextView) findViewById(R.id.etStatus);

        new JSONTask().execute("https://sdsmartlock.com/api/locks");

        btnHit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(status == "false")
                   btnHit.setText("LOCK");
                else
                    btnHit.setText("UNLOCK");
            }
        });



        logOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent logoutIntent = new Intent(MainActivity.this, LogInActivity.class);
                MainActivity.this.startActivity(logoutIntent);
            }
        });



    }


    public class JSONTask extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try
            {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null)
                {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                JSONArray jarray = new JSONArray(finalJson);

                JSONObject object = jarray.getJSONObject(0);

                id = object.getString("_id");
                status = object.getString("status");
                v = object.getInt("__v");


                return status;

            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally
            {
                if(connection != null)
                {
                    connection.disconnect();
                }
                try
                {
                    if(reader != null)
                    {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected  void onPostExecute(String result)
        {
            super.onPostExecute(result);
            if(result == "false")
                tvStatus.setText("Status: UNLOCK");
            else
                tvStatus.setText("Status: LOCK");
        }
    }
}