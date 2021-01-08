package com.example.mysyncproduct;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Quoc Nguyen on 13-Dec-16.
 */

public class CartList extends AppCompatActivity {

    GridView gridView;
    ArrayList<Product> list;
    CartListAdapter adapter = null;
    String Email;
    double productTotal = 0;
    TextView CartPrice,CartEmp;
    Button check;
    public static SQLiteHelperProduct sqLiteHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_cart_list );

        check = findViewById(R.id.buttonPurchase);
        CartPrice = findViewById(R.id.textViewCartPrice);

        gridView = (GridView) findViewById( R.id.gridViewCart );
        list = new ArrayList<>();


        Intent intent = getIntent();
        Email = intent.getStringExtra("EXTRA_EMAIL");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle( "Cart" );

        sqLiteHelper = new SQLiteHelperProduct(this, "FoodDB.sqlite", null, 1);
        // get all data from sqlite
        Cursor cursor = sqLiteHelper.getData( "SELECT * FROM CART" );

            list.clear();
            while (cursor.moveToNext()) {
                productTotal = productTotal + cursor.getDouble( 4 );
                int id = cursor.getInt( 0 );
                String title = cursor.getString( 1 );
                String shortdesc = cursor.getString( 2 );
                int quantity = cursor.getInt( 3 );
                double price = cursor.getDouble( 4 );
                String imagepath = cursor.getString( 5 );
                byte[] image = cursor.getBlob( 6 );

                list.add( new Product( title, shortdesc, quantity, price, imagepath, image, id ) );
            }

        adapter = new CartListAdapter( this, R.layout.cart_items, list );
        gridView.setAdapter( adapter );

        if(cursor.getCount() == 0){
            Intent intent1 = new Intent(CartList.this, EmptyCart.class);
            intent1.putExtra("EXTRA_EMAIL_", Email);
            startActivity(intent1);
        }

        CartPrice.setText("Rs."+ String.valueOf(productTotal));
        adapter.notifyDataSetChanged();


        gridView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tid = view.findViewById(R.id.textViewId);

                int idc = Integer.parseInt( tid.getText().toString() );

                sqLiteHelper.deleteData(idc);
                updateFoodList();

                Toast.makeText(CartList.this,"Delete Item Successfully", Toast.LENGTH_LONG).show();


                return false;
            }
        }
        );


        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartList.this, DeliveryDetails.class);
                intent.putExtra("EXTRA_EMAIL_", Email);
                intent.putExtra("EXTRA_PRICE_", String.valueOf(productTotal));
                startActivity(intent);
            }
        });

    }

    private void updateFoodList(){
        // get all data from sqlite
        productTotal = 0;
        Cursor cursor = sqLiteHelper.getData( "SELECT * FROM CART" );
        list.clear();
        while (cursor.moveToNext()) {
            productTotal = productTotal + cursor.getDouble( 4 );
            int id = cursor.getInt( 0 );
            String title = cursor.getString( 1 );
            String shortdesc = cursor.getString( 2 );
            int quantity = cursor.getInt( 3 );
            double price = cursor.getDouble( 4 );
            String imagepath = cursor.getString( 5 );
            byte[] image = cursor.getBlob( 6 );

            list.add( new Product( title, shortdesc, quantity, price, imagepath, image, id ) );
        }
        CartPrice.setText("Rs."+ String.valueOf(productTotal));
        adapter.notifyDataSetChanged();
    }

    //side menu icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                try{
                Intent intent1 = new Intent(CartList.this, ProductList.class);
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
    }
}