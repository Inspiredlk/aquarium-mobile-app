package com.example.mysyncproduct;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UpdateProfile extends AppCompatActivity {

    EditText editTextFirstName;
    EditText editTextTelephone;
    EditText editTextEmail;
    Button buttonUpdate;

    String result=null;
    int code;
    InputStream is=null;
    String email,userName,telephone,address;
    String intentEmai;
    int i,timeToWait =1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        intentEmai = getIntent().getStringExtra("EXTRA_EMAIL_PROFILE_UPDATE");
        editTextFirstName = (EditText)findViewById(R.id.editTextFirstName);
        editTextTelephone =(EditText) findViewById(R.id.editTextTelephone);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        buttonUpdate = (Button)findViewById(R.id.buttonUpdate);

        if(intentEmai.isEmpty()){
            Toast.makeText(UpdateProfile.this,"Email is Empty", Toast.LENGTH_LONG).show();
        }else{
            SelectUserDetails();
        }

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected() == true){
                    UpdateUserDetails();
                    Intent intent1 = new Intent(UpdateProfile.this, ProductList.class);
                    intent1.putExtra("EXTRA_EMAIL_HOMEPAGE", editTextEmail.getText().toString().trim());
                    startActivity(intent1);
                }else{
                    Toast.makeText(UpdateProfile.this,"Check your Internet Connection!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    //update online database
    public void UpdateUserDetails(){

        userName =   editTextFirstName.getText().toString().trim();
        telephone =   editTextTelephone.getText().toString().trim();
        email =   editTextEmail.getText().toString().trim();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... strings) {

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("username",userName));
                nameValuePairs.add(new BasicNameValuePair("telephone",telephone));
                nameValuePairs.add(new BasicNameValuePair("email",email));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String ServerURL = "http://inspiredlk.com/AQUpdateUser.php";
                    HttpPost httpPost = new HttpPost(ServerURL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity entity = httpResponse.getEntity();
                }
                catch(Exception e) {
                    Log.e("Fail 1", e.toString());
                    Toast.makeText(getApplicationContext(), "Invalid IP Address",
                            Toast.LENGTH_LONG).show();
                }

                try {
                    URL url = new URL("http://inspiredlk.com/AQUpdateUser.php");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
                    String line = null;
                    StringBuilder sb = new StringBuilder();

                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                    Log.e("pass 2", "connection success ");
                }
                catch(Exception e) {
                    Log.e("Fail 2", e.toString());
                }

                try {
                    JSONObject json_data = new JSONObject(result);
                    code=(json_data.getInt("code"));
                }
                catch(Exception e) {
                    Log.e("Fail 3", e.toString());

                }
                return "Data Update Successfully";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if(code==1)
                {
                    Toast.makeText(UpdateProfile.this, "Data Update Successful!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(UpdateProfile.this, "Data Update Fail!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }


    //read data from online database
    public void SelectUserDetails(){

        class SendPostReqAsyncTask extends AsyncTask<String,Void,String>{

            String dbUserName, dbTelephone,dbEmail;

            @Override
            protected String doInBackground(String... strings) {

                try {
                    URL url = new URL("http://inspiredlk.com/AQSelectUser.php?email="+intentEmai);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));

                    String line = null;
                    StringBuilder sb = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                    Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());
                }

                try {
                    JSONObject json_data = new JSONObject(result);
                    dbUserName = (json_data.getString("UserName"));
                    dbTelephone = (json_data.getString("Telephone"));
                    dbEmail = (json_data.getString("Email"));
                } catch (Exception e) {
                    Log.e("Fail 3", e.toString());

                }
                return "Data Select Successfully";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                editTextFirstName.setText(dbUserName);
                editTextTelephone.setText(dbTelephone);
                editTextEmail.setText(dbEmail);
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    //Delete data from online database
    public void DeleteUserDetails(){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email","akil@gmail.com"));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String ServerURL = "http://inspiredlk.com/AQDeleteUser.php";
                    HttpPost httpPost = new HttpPost(ServerURL);

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity entity = httpResponse.getEntity();

                }
                catch(Exception e) {
                    Log.e("Fail 1", e.toString());
                    Toast.makeText(getApplicationContext(), "Invalid IP Address",
                            Toast.LENGTH_LONG).show();
                }

                try {
                    URL url = new URL("http://inspiredlk.com/AQDeleteUser.php");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
                    String line = null;
                    StringBuilder sb = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    result = sb.toString();
                    Log.e("pass 2", "connection success ");
                }
                catch(Exception e) {
                    Log.e("Fail 2", e.toString());
                }

                try {
                    JSONObject json_data = new JSONObject(result);
                    code=(json_data.getInt("code"));
                }
                catch(Exception e) {
                    Log.e("Fail 3", e.toString());

                }
                return "Data Delete Successfully";
            }

            @Override
            protected void onPostExecute(String result) {

                super.onPostExecute(result);
                if(code==1) {
                    Toast.makeText(UpdateProfile.this, "Data Delete Successful!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(UpdateProfile.this, "Data Delete Fail!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    //insert data into online database
    public void InsertUserDetails(){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email","akil@gmail.com"));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String ServerURL = "http://inspiredlk.com/AQInsertUser.php";
                    HttpPost httpPost = new HttpPost(ServerURL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity entity = httpResponse.getEntity();

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "Data Inserted Successfully";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Toast.makeText(UpdateProfile.this, "Data Insert Successfully", Toast.LENGTH_LONG).show();
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    //cheack internet connection
    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            // Toast.makeText(SignUp.this,"Internet Successful!", Toast.LENGTH_LONG).show();
            return true;
        } else {
            // Toast.makeText(SignUp.this,"Internet Fail!", Toast.LENGTH_LONG).show();
            return false;

        }
    }

    //side menu icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
        switch (item.getItemId()) {
            case R.id.home:
                try{
                Intent intent1 = new Intent(UpdateProfile.this, ProductList.class);
                intent1.putExtra("EXTRA_EMAIL_HOMEPAGE", editTextEmail.getText().toString().trim());
                startActivity(intent1);
                return true;
                }catch (Exception e){
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
