package com.example.mysyncproduct;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EmptyCart extends AppCompatActivity {
String Email;
Button ContinueShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_empty_cart );

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle( "Cart" );

        ContinueShopping = (Button) findViewById(R.id.buttonContinueShopping);


        Intent intent = getIntent();
        Email = intent.getStringExtra("EXTRA_EMAIL_");


        ContinueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmptyCart.this, ProductList.class);
                intent.putExtra("EXTRA_EMAIL_HOMEPAGE", Email);
                startActivity(intent);
            }
        });

    }
}
