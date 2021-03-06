package com.example.mysyncproduct;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Login extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    TextView signupTextView;
    Button loginButton;

    LoginSignUpDb db;
    String dbEmail, dbPassword;
    String email, password;

    EditText edtName, edtPrice;
    Button btnChoose, btnAdd, btnList;
    ImageView imageView;
    private static final String URL_PRODUCTS = "http://inspiredlk.com/Api.php";
    final int REQUEST_CODE_GALLERY = 999;
    boolean datain;
    String imageURL;
    String Id;
    String title;
    String desc;
    String image;
    int quntity;
    double price;
    int timeToWait = 100;
    public static SQLiteHelperProduct sqLiteHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");

        init();
        sqLiteHelper = new SQLiteHelperProduct(this, "FoodDB.sqlite", null, 1);

        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS FOOD(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, shortdesc TEXT, quantity TEXT, price TEXT, imagepath TEXT,image BLOB)");


        emailEditText = (EditText) findViewById(R.id.editTextEmail);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        loginButton = (Button) findViewById(R.id.buttonLogin);
        signupTextView = (TextView) findViewById(R.id.textViewSignUp);

        db = new LoginSignUpDb(this);

        if(isConnected() == true){
            syncProducts();
        }else{
            Toast.makeText(Login.this,"Check your Internet Connection!", Toast.LENGTH_LONG).show();
        }

        //goto signup page
        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadUser();
            }
        });
    }


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

    //read sqlite database
    public void ReadUser(){
        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();

        Cursor res = db.getUserDetails(emailEditText.getText().toString().trim());
        if(res.getCount() == 0) {
            Toast.makeText(Login.this, "Please register first!", Toast.LENGTH_LONG).show();
            return;
        }
        else{
            while (res.moveToNext()) {
                dbEmail = res.getString(1);
                dbPassword =  res.getString(2);
            }
            if(email.equals(dbEmail) && password.equals(dbPassword)){
                Toast.makeText(Login.this, "Login successfull!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Login.this, ProductList.class);
                intent.putExtra("EXTRA_EMAIL_HOMEPAGE", email);
                startActivity(intent);
            }
            else
                Toast.makeText(Login.this, "Invalid Login Details", Toast.LENGTH_LONG).show();
        }
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);  // try for jpg also
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init(){
        edtName = (EditText) findViewById(R.id.edtName);
        edtPrice = (EditText) findViewById(R.id.edtPrice);
        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnList = (Button) findViewById(R.id.btnList);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    private  class DownloadImage extends AsyncTask<String,Void,Bitmap> {
        @Override
        protected Bitmap doInBackground(String... URL) {
            imageURL = URL[0];
            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
            Login.sqLiteHelper.updateData(Login.imageViewToByte(imageView),imageURL);
            syncProducts();
        }
    }


    private void syncProducts() {

        StringRequest stringRequest = new StringRequest( Request.Method.GET, URL_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                //adding the product to product list
                                Cursor res = sqLiteHelper.getAllProduct();


                                while (res.moveToNext()) {

                                    if (res.getString(1).equals(product.getString("title"))){
                                        datain = true;
                                        break;
                                    }

                                    else{
                                        datain = false;
                                    }
                                }




                                if(datain == false){
                                    Id = String.valueOf(product.getInt("id"));
                                    title = product.getString("title");
                                    desc= product.getString("shortdesc");
                                    quntity = product.getInt("quantity");
                                    price = product.getDouble("price");
                                    image = product.getString("image");
                                    sqLiteHelper.insertData(title,desc,quntity,price,image);
                                    new Login.DownloadImage().execute(image);

                                    try {
                                        for(i=0;i<timeToWait;i++){   // try sleep maximum
                                            Thread.sleep(1);
                                        }
                                    }catch (Exception e){
                                        Thread.currentThread().interrupt();
                                    }
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
