package com.example.mysyncproduct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class PlaceSuccess extends AppCompatActivity {
    Button ContinueShopping;
    String Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_success);

        ContinueShopping = (Button) findViewById(R.id.buttonContinueShopping);

        Intent intent = getIntent();
        Email = intent.getStringExtra("EXTRA_EMAIL_");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle( "Place Order" );

        ContinueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceSuccess.this, ProductList.class);
                intent.putExtra("EXTRA_EMAIL_HOMEPAGE", Email);
                startActivity(intent);
            }
        });
    }
}
