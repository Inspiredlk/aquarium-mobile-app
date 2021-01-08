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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Quoc Nguyen on 13-Dec-16.
 */

public class ProductList extends AppCompatActivity {

    GridView gridView;
    ArrayList<Product> list;
    ProductListAdapter adapter = null;
    String email;
    public static SQLiteHelperProduct sqLiteHelper;
    public static SQLiteHelperCart sqLiteHelperCart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_list_activity);

        sqLiteHelperCart = new SQLiteHelperCart(this, "FoodDB.sqlite", null, 1);

        sqLiteHelperCart.queryData("CREATE TABLE IF NOT EXISTS CART(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, shortdesc TEXT, quantity TEXT, price TEXT, imagepath TEXT,image BLOB)");



        gridView = (GridView) findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new ProductListAdapter(this, R.layout.food_items, list);
        gridView.setAdapter(adapter);

        email = getIntent().getStringExtra( "EXTRA_EMAIL_HOMEPAGE" );
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Shop");

        sqLiteHelper = new SQLiteHelperProduct(this, "FoodDB.sqlite", null, 1);
        // get all data from sqlite
        Cursor cursor = sqLiteHelper.getData("SELECT * FROM FOOD");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title  = cursor.getString(1);
            String shortdesc  = cursor.getString(2);
            int quantity   = cursor.getInt(3);
            double price = cursor.getDouble(4);
            String imagepath    = cursor.getString(5);
            byte[] image = cursor.getBlob(6);

            list.add(new Product(title, shortdesc, quantity, price,imagepath,image,id));
        }
        adapter.notifyDataSetChanged();


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent detailIntent = new Intent( ProductList.this,ItemDescription.class);

                TextView titleText = view.findViewById(R.id.txtName);
                TextView DesText = view.findViewById(R.id.txtDes);
                TextView priceText = view.findViewById(R.id.txtPrice);
                TextView qtyText = view.findViewById(R.id.txtQty);
                ImageView imageView = view.findViewById(R.id.imgFood);
                byte[] image =  imageViewToByte(imageView);

                String title = titleText.getText().toString();
                String description = DesText.getText().toString();
                String price = priceText.getText().toString();
                String quntity = qtyText.getText().toString();

                detailIntent.putExtra("EXTRA_EMAIL", (email));
                detailIntent.putExtra("EXTRA_NAME", (title));
                detailIntent.putExtra("EXTRA_IMAGE", (image));
                detailIntent.putExtra("EXTRA_DESCRIPTION", (description));
                detailIntent.putExtra("EXTRA_PRICE", (price));
                detailIntent.putExtra("EXTRA_QUANTITY",quntity);

                startActivity(detailIntent);

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

    //side menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                try{

                   // Toast.makeText(this,"HELLOW WORLD", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ProductList.this, ProfileSetting.class);
                    intent.putExtra("EXTRA_EMAIL_HOMEPAGE", email);
                     startActivity(intent);
                    return true;
                }catch (Exception e){
                    // TODO Auto-generated catch block
                   // e.printStackTrace();
                    System.out.println(e);
                }
                finally {
                    clearCache();
                }
            case R.id.cart:
                try{
                    Intent intent1 = new Intent(ProductList.this, CartList.class);
                    intent1.putExtra("EXTRA_EMAIL", email);
                    startActivity(intent1);
                    return true;
                }catch (Exception e){
                    // TODO Auto-generated catch block
                   // e.printStackTrace();
                    System.out.println(e);
                }
                finally {
                    clearCache();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean clearCache() {
        try {

            // create an array object of File type for referencing of cache files
            File[] files = getBaseContext().getCacheDir().listFiles();

            // use a for etch loop to delete files one by one
            for (File file : files) {

                /* you can use just [ file.delete() ] function of class File
                 * or use if for being sure if file deleted
                 * here if file dose not delete returns false and condition will
                 * will be true and it ends operation of function by return
                 * false then we will find that all files are not delete
                 */
                if (!file.delete()) {
                    return false;         // not success
                }
            }

            // if for loop completes and process not ended it returns true
            return true;      // success of deleting files

        } catch (Exception e) {}

        // try stops deleting cache files
        return false;       // not success
    }
}