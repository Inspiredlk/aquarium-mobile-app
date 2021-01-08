package com.example.mysyncproduct;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DeliveryDetails extends AppCompatActivity {
    TextView date,email;
    Button continued;
    String Email,Price;
    String result=null;
    int code;

    AlertDialog.Builder builder;

    String dbEmail,dbTel,dbDate,dbpostCode,dbAddress1,dbAddress2,dbReqDate;

    EditText Telephone,eText;

    DatePickerDialog picker;

    EditText address1, address2, address3;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);


        address1=(EditText) findViewById(R.id.editTextAddress1);
        address2=(EditText) findViewById(R.id.editTextAddress2);
        address3=(EditText) findViewById(R.id.editTextAddress3);
        Telephone=(EditText) findViewById(R.id.editTextTel);

        email=(TextView) findViewById(R.id.textViewEmail);
        date=(TextView)findViewById(R.id.textViewDateDisplay);

        builder = new AlertDialog.Builder(this);

        continued = findViewById(R.id.buttonContinue);

        getDateTime();

        Intent intent = getIntent();
        Email = intent.getStringExtra("EXTRA_EMAIL_");
        Price = intent.getStringExtra("EXTRA_PRICE_");
        email.setText(Email);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Delivery Details");

        eText=(EditText) findViewById(R.id.editTextDate);
        eText.setInputType( InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(DeliveryDetails.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                eText.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        continued.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(address1.getText().toString().trim())|| TextUtils.isEmpty(address2.getText().toString().trim()) || TextUtils.isEmpty(address3.getText().toString().trim())|| TextUtils.isEmpty(Telephone.getText().toString().trim())){
                    Toast.makeText(DeliveryDetails.this,"Fill all Details", Toast.LENGTH_LONG).show();
                }else{
                    alertDialog();
                }
            }
        });
    }

    private void getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String currentDateandTime = sdf.format(new Date());
        date.setText(currentDateandTime);
    }



    //Place order
    public void InsertPlaceOrderDetails(){

        dbpostCode =   address1.getText().toString().trim();
        dbAddress1 =   address2.getText().toString().trim();
        dbAddress2 =   address3.getText().toString().trim();
        dbTel =   Telephone.getText().toString().trim();
        dbEmail =   email.getText().toString().trim();
        dbDate = date.getText().toString().trim();
        dbReqDate = eText.getText().toString().trim();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email",Email));
                nameValuePairs.add(new BasicNameValuePair("price",Price));
                nameValuePairs.add(new BasicNameValuePair("postCode",dbpostCode));
                nameValuePairs.add(new BasicNameValuePair("address1",dbAddress1));
                nameValuePairs.add(new BasicNameValuePair("address2",dbAddress2));
                nameValuePairs.add(new BasicNameValuePair("telephone",dbTel));
                nameValuePairs.add(new BasicNameValuePair("date",dbDate));;
                nameValuePairs.add(new BasicNameValuePair("reqdate",dbReqDate));;

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String ServerURL = "http://inspiredlk.com/AQInsertPlaceOrder.php";
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
               //  Toast.makeText(DeliveryDetails.this, "Data Insert Successfully", Toast.LENGTH_LONG).show();
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }


    public void alertDialog(){
        builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to confirm this order ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            finish();
                            InsertPlaceOrderDetails();
                            Intent intent = new Intent( DeliveryDetails.this, PlaceSuccess.class );
                            intent.putExtra( "EXTRA_EMAIL_", Email );
                            startActivity( intent );
                            Login.sqLiteHelper.queryData( "DROP TABLE IF EXISTS CART" );
                        }catch (Exception e){

                        }
                        //UpdatePlaceOrderDetails();

                       // Toast.makeText(getApplicationContext(),"you choose yes action for alertbox", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        try {
                        dialog.cancel();
                       // Toast.makeText(getApplicationContext(),"you choose no action for alertbox",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(DeliveryDetails.this, ProductList.class);
                        intent.putExtra("EXTRA_EMAIL_HOMEPAGE", Email);
                        startActivity(intent);
                    }catch (Exception e){

                        }
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Confirmation!");
        alert.show();
    }

}
