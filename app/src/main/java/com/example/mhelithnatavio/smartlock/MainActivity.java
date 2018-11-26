package com.example.mhelithnatavio.smartlock;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
//import java.util.Base64;




public class MainActivity extends AppCompatActivity {
    private TextView logOut;
    private TextView tvname;
    private TextView tvemail;
    private Button btn;
    String token;
    String[] splitToken;
    String userId;
    String status = null;


    // from get
    String gLock, gId, gName, gEmail, gPassword;
    int gV;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        new GetAsyncTask().execute("https://sdsmartlock.com/api/users/5be9c7764fe9ac33a3e2f685");
        // save all information from when users logs in
        Intent info = getIntent();
        token = info.getStringExtra("token");
        splitToken = token.split("\\.");


        tvname = (TextView) findViewById(R.id.name);
        tvemail = (TextView) findViewById(R.id.email);

        try {
            userId = decode(splitToken[1]);
            new GetAsyncTask().execute("https://sdsmartlock.com/api/users/"+userId);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn = (Button) findViewById(R.id.btnSwitch);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = "5bd603af9dfa7d068ceb70dd";
                if (status == null || status == "true")
                    status = "false";
                else status = "true";

                JSONObject object = new JSONObject();
                try {
                    object.put("lockid", id);
                    object.put("userid", userId);
                    object.put("status", status);
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


    private static String decode(String strEncoded) throws UnsupportedEncodingException, JSONException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        String res = new String(decodedBytes, "UTF-8");
        JSONObject jObj = new JSONObject(res);
        String lock = jObj.optString("_id");
        int ait = jObj.optInt("ait");
        return lock;
    }



    public class GetAsyncTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection con = null;
            BufferedReader reader = null;
            String jsonRes = null;

            try {
                URL url = new URL(params[0]);
                con = (HttpURLConnection) url.openConnection();
                con.connect();

                InputStream stream = con.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null)
                {
                    buffer.append(line);
                }
                buffer.toString();
                JSONObject myJson = new JSONObject(buffer.toString());
                // use myJson as needed, for example
                gLock = myJson.optString("locks");
                gId = myJson.optString("_id");
                gName = myJson.optString("name");
                gEmail = myJson.optString("email");
                gPassword = myJson.optString("password");
                gV = myJson.optInt("__v");
                return gName + "\n" + gEmail;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(con != null){
                    con.disconnect();
                }
                try {
                    if (reader != null){
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
            String[] res = result.split("\n");
            tvname.setText(res[0]);
            tvemail.setText(res[1]);
        }
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
                        JSONObject myJson = new JSONObject(buffer.toString());
                       String id = myJson.optString("_id");
                       String lockid = myJson.optString("lockid");
                       String userid = myJson.optString("userid");
                       String date = myJson.optString("date");
                       String status = myJson.getString("status");
                       int v = myJson.getInt("__v");

                        return date + "\n" + status;
                    }
                } else {
                    return null;
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
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            String stat;
            String[] res = result.split("\n");
            tvname.setText(status);
            tvemail.setText(res[1]);

            Toast toast = Toast.makeText(MainActivity.this, "door is " +res[1]+ "ed by "+gName+" at "+res[0], Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if( v != null) v.setGravity(Gravity.CENTER);
            toast.show();


        }
    }



}