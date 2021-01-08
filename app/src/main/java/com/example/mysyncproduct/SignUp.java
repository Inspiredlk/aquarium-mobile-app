package com.example.mysyncproduct;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    EditText emailEditText,passwordEditText,cpasswordEditText;
    Button signupButton;

    //Variable
    LoginSignUpDb db;
    String result = null;
    String dbUserName,dbEmail;
    boolean alreadyRegister;
    String email,password,cpassword;

    TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Signup");

        db = new LoginSignUpDb(this);

        emailEditText = (EditText) findViewById(R.id.editTextEmail);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        cpasswordEditText = (EditText) findViewById(R.id.editTextConfirm);
        signupButton = (Button) findViewById(R.id.buttonContinue);

        login = (TextView) findViewById( R.id.textViewLogin);

        login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        } );
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isConnected() == true){
                    try {
                        SelectUserDetails();
                        insertUser();
                    }catch (Exception e){

                    }
                }else{
                    Toast.makeText(SignUp.this,"Check your Internet Connection!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    //insert data in to sqlite database
    public  void insertUser() {

        email =   emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        cpassword = cpasswordEditText.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            emailEditText.setError("Email is Required");
        }else if(TextUtils.isEmpty(password)){
            passwordEditText.setError("Password is Required");
        }
        else{
            if(alreadyRegister == true){
                if(password.equals(cpassword) && passwordVaildation() == true && emailVaildation() == true){
                    boolean isInserted = db.insertUser(email,password);
                    InsertUserDetails();
                    sendEmail();
                    if(isInserted == true){
                        Toast.makeText(SignUp.this,"Data Insert Successful!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignUp.this, ProductList.class);
                        intent.putExtra("EXTRA_EMAIL_HOMEPAGE", email);
                        startActivity(intent);
                    }else{
                        Toast.makeText(SignUp.this,"Data Insert Fail!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(SignUp.this,"Password not match!", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(SignUp.this,"User Already Registered", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Check user in a online database
    public void SelectUserDetails(){

        dbUserName = null;
        dbEmail = null;
        email =   emailEditText.getText().toString().trim();

        class SendPostReqAsyncTask extends AsyncTask<String,Void,String> {

            @Override
            protected String doInBackground(String... strings) {

                try {
                    URL url = new URL("http://inspiredlk.com/AQSelectUser.php?email="+ email);
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
                    dbEmail = (json_data.getString("Email"));
                } catch (Exception e) {
                    Log.e("Fail 3", e.toString());

                }
                return "Data Select Successfully";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(TextUtils.isEmpty(dbEmail)){
                    alreadyRegister = true;
                }else{
                    alreadyRegister = false;
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    //Validation
    public boolean passwordVaildation(){
        CharSequence password = passwordEditText.getText().toString().trim();
        Pattern passwordPattern = Pattern.compile("[a-zA-Z0-9\\!\\@\\#\\$]{8,24}");
        Matcher passwordMatcher = passwordPattern.matcher(password);

        //Password Validation
        if(passwordMatcher.matches()){
            return true;
        }else{
            passwordEditText.setError("Password not match");
            return false;
        }
    }

    public boolean emailVaildation(){
        CharSequence email = emailEditText.getText().toString().trim();
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        Matcher emailMatcher = emailPattern.matcher(email);

        //Email Validation
        if(emailMatcher.matches()){
            return true;
        }else{
            emailEditText.setError("E-mail not match");
            return false;
        }
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

    //Add data into online database
    public void InsertUserDetails(){
        email =   emailEditText.getText().toString().trim();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email",email));

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
               // Toast.makeText(SignUp.this, "Data Insert Successfully", Toast.LENGTH_LONG).show();
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    private void sendEmail() {
        //Getting content for email
        String emailacc = email;
        String subject = "You are successfully registerd";
        String message = "Congratulation! \n you have been successfully registerd.To activate your account check your email and confirm your registration.";

        //Creating SendMail object
        SendMail sm = new SendMail(this, emailacc, subject, message);

        //Executing sendmail to send email
        sm.execute();
    }
}
