package com.example.mysyncproduct;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ItemDescription extends AppCompatActivity {

    ImageView addBtn,removeBtn;
    Button addtoCart;
    TextView quantity;
    boolean datain;
    int qty = 0;
    TextView numberView;
    int valueofQuantity;
    double valueofPrice;
    double newprice, newp = 1;
    int newQ = 1;
    SQLiteHelperCart sqLiteHelper;
    String Name,Image,Description,Price,Quantity,Email;

    String dbTitle,dbQuantity,dbPrice;
    ImageView itemImage;
    TextView itemName,itemDescription,itemPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_description);


        sqLiteHelper = new SQLiteHelperCart(this, "FoodDB.sqlite", null, 1);

        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS CART(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, shortdesc TEXT, quantity TEXT, price TEXT, imagepath TEXT,image BLOB)");


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Item Description");

        addBtn = (ImageView)findViewById(R.id.imageViewAddButton);
        removeBtn = (ImageView) findViewById(R.id.imageViewRemoveButton);
        quantity = (TextView) findViewById(R.id.textViewItemQuantity);
        addtoCart =(Button) findViewById(R.id.buttonAddtoCart);

        itemName = (TextView) findViewById(R.id.textViewItemName);
        itemImage =(ImageView) findViewById(R.id.imageViewItemImage);
        itemDescription =(TextView) findViewById(R.id.textViewItemDescription);
        itemPrice =(TextView) findViewById(R.id.textViewItemPrice);

        Intent intent = getIntent();

        Email = intent.getStringExtra("EXTRA_EMAIL");
        Name = intent.getStringExtra("EXTRA_NAME");
        Description = intent.getStringExtra("EXTRA_DESCRIPTION");
        Price = intent.getStringExtra("EXTRA_PRICE");
        Quantity = intent.getStringExtra("EXTRA_QUANTITY");

        valueofQuantity = Integer.parseInt(Quantity);
        valueofPrice = Double.parseDouble(Price);
        itemName.setText(Name);

        Bundle extras = getIntent().getExtras();
        byte[] foodImage = extras.getByteArray("EXTRA_IMAGE");
        Bitmap bitmap = BitmapFactory.decodeByteArray(foodImage, 0, foodImage.length);
        itemImage.setImageBitmap(bitmap);
        itemDescription.setText(Description);
        itemPrice.setText("Rs." +Price);

        qty = Integer.parseInt(quantity.getText().toString());

        //Quantity value
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(qty<valueofQuantity)
                    quantity.setText(String.valueOf(qty = qty+1));

            }

        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(qty>1)
                quantity.setText(String.valueOf(qty = qty-1));
            }
        });

        addtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newprice = valueofPrice * Integer.parseInt(quantity.getText().toString());
               //   Toast.makeText(ItemDescription.this, "Data Insert Successful!", Toast.LENGTH_SHORT).show();

                Cursor res = sqLiteHelper.getCartProduct(itemName.getText().toString());

                while (res.moveToNext()) {
                    dbTitle = res.getString(1);
                    dbQuantity = res.getString(3);
                    dbPrice = res.getString(4);
                }

                try{
                    newp = valueofPrice * Integer.parseInt(quantity.getText().toString()) +  Double.parseDouble( dbPrice );
                }catch (Exception e){
                    newp = newprice;
                }
                try {
                    newQ = Integer.parseInt( dbQuantity ) + Integer.parseInt(quantity.getText().toString());
                }catch (Exception e){
                    newQ =  Integer.parseInt(quantity.getText().toString()) ;
                }
                sqLiteHelper.deleteData(itemName.getText().toString());
                sqLiteHelper.insertData(itemName.getText().toString(),itemDescription.getText().toString(), newQ,newp,"image",imageViewToByte(itemImage));
                Intent intent = new Intent(ItemDescription.this, CartList.class);
                intent.putExtra("EXTRA_EMAIL", Email);
                startActivity(intent);
            }
        });


    }
    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
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
                Intent intent1 = new Intent(ItemDescription.this, ProductList.class);
                intent1.putExtra("EXTRA_EMAIL_HOMEPAGE", Email);
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
