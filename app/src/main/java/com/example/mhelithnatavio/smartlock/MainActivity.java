package com.example.mhelithnatavio.smartlock;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.List;
//import java.util.Base64;




public class MainActivity extends AppCompatActivity {
    private TextView logOut;
    private TextView tvname;
    private TextView tvemail;
    private ToggleButton btn;
    String token;
    String[] splitToken;
    String userId;
    String status = null;
//    String[] locks;
    List<String> locks = new ArrayList<String>();


    // from get
    String gLock, gId, gName, gEmail, gPassword;
    int gV;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // save token from login, split, decode, get user info from decoded userid
        Intent info = getIntent();
        token = info.getStringExtra("token");
        splitToken = token.split("\\.");
        try {
            userId = decode(splitToken[1]);
            new GetUserInfo().execute("https://sdsmartlock.com/api/users/"+userId);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // initialize name and mail textview
        tvname = (TextView) findViewById(R.id.name);
        tvemail = (TextView) findViewById(R.id.email);

//        TODO: needs to ask user to input locks (maybe spinner)

        Spinner spinner = (Spinner)findViewById(R.id.spinner) ;
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, locks);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
//        spinner.getSelectedItem().toString();

        Button addLock = (Button) findViewById(R.id.addlock);
        addLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder lockBuilder = new AlertDialog.Builder(MainActivity.this);
                View lockView = getLayoutInflater().inflate(R.layout.lock_dialog, null);
                final EditText lockDialog = (EditText) lockView.findViewById(R.id.LockId);
                Button add = (Button) lockView.findViewById(R.id.add);
                Button cancel = (Button) lockView.findViewById(R.id.cancel);
                lockBuilder.setView(lockView);
                final AlertDialog dialog = lockBuilder.create();
                dialog.show();
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(lockDialog.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this,
                                    "LockID can't be empty", Toast.LENGTH_SHORT).show();
                        }
                        else{
//                            locks.add(lockDialog.getText().toString());
                        }

                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


            }
        });



        // when button is clicked: take status of the lock
        // post to server
        btn = (ToggleButton) findViewById(R.id.btnSwitch);
        btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                new GetLockStatus().execute("https://sdsmartlock.com/api/locks/5bd603af9dfa7d068ceb70dd");
                String lockid = "5bd603af9dfa7d068ceb70dd";
                if(isChecked)
                {
                    status = "true";
                }
                else status = "false";

                JSONObject object = new JSONObject();
                try {
                    object.put("lockid", lockid);
                    object.put("userid", userId);
                    object.put("status", status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new PostAsyncTask().execute("https://sdsmartlock.com/api/events", object.toString());
            }
        });

        // logout button
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

    // decodes and parses decoded string
    private static String decode(String strEncoded) throws UnsupportedEncodingException, JSONException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        String res = new String(decodedBytes, "UTF-8");
        JSONObject jObj = new JSONObject(res);
        String lock = jObj.optString("_id");
        int ait = jObj.optInt("ait");
        return lock;
    }


    // function to get user's name and email
    // display to screen
    public class GetUserInfo extends AsyncTask<String, String, String>{

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
                gName = myJson.optString("name");
                gEmail = myJson.optString("email");
                JSONArray jsonArray = myJson.getJSONArray("locks");

                for(int i = 0; i < jsonArray.length(); i++)
                {

                    locks.add(jsonArray.getString(i));
                }

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

    // get Lock Status
    public class GetLockStatus extends AsyncTask<String, String, String>{

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
//                status = myJson.optString("status");
                return null;


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
        }
    }






    // post to server: changing status lock
    public class PostAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params)
        {
            String JsonResponse = null;
            String JsonDATA = params[1];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
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
                        return buffer.toString();
                        // convert buffer to string
//                            JSONObject myJson = new JSONObject(buffer.toString());
////                            String date = myJson.optString("date");
////                            String status = myJson.getString("status");
////                            return date + "\n" + status;
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
            String stat;
            String[] res = result.split("\n");


//            TODO: need to change false/true to unlock/lock on display
            Toast toast = Toast.makeText(MainActivity.this, "door is " +res[1]+ " by "+gName+" at "+res[0], Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if( v != null) v.setGravity(Gravity.CENTER);
            toast.show();

        }
    }



}