package com.example.mysyncproduct;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DeleteAccount extends AppCompatActivity {

    EditText emailEditText;
    Button deleteButton;
    LoginSignUpDb db;
    String intentEmai;

    String result=null;
    int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        deleteButton = (Button) findViewById(R.id.buttonDelete);
        emailEditText = (EditText) findViewById(R.id.editTextEmail);

        intentEmai = getIntent().getStringExtra("EXTRA_EMAIL_PROFILE_DELETE");
        emailEditText.setText(intentEmai);

        db = new LoginSignUpDb(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Deactivate Account");

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected() == true){
                    DeleteUser();
                    DeleteUserDetails();
                }else{
                    Toast.makeText(DeleteAccount.this,"Check your Internet Connection!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Delete user from sqllite database
    public void DeleteUser(){
        db.DeleteUser(emailEditText.getText().toString().trim());
        Intent intent = new Intent(DeleteAccount.this, Login.class);
        startActivity(intent);
    }

    //Delete user from online database
    public void DeleteUserDetails(){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email",intentEmai));

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
                    Toast.makeText(DeleteAccount.this, "Data Delete Successful!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(DeleteAccount.this, "Data Delete Fail!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    //check Internet connection
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
                Intent intent1 = new Intent(DeleteAccount.this, ProductList.class);
                intent1.putExtra("EXTRA_EMAIL_HOMEPAGE", emailEditText.getText().toString().trim());
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
