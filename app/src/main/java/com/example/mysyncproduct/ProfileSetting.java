package com.example.mysyncproduct;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class ProfileSetting extends AppCompatActivity {

    TextView update,delete,logout;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        email = getIntent().getStringExtra("EXTRA_EMAIL_HOMEPAGE");

        update = findViewById(R.id.textViewUpdateProfile);
        delete = findViewById(R.id.textViewDeleteProfile);
        logout = findViewById(R.id.textViewLogOut);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Setting");

        try{
        //goto update account page
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                    Intent intent = new Intent(ProfileSetting.this, UpdateProfile.class);
                    intent.putExtra("EXTRA_EMAIL_PROFILE_UPDATE", email);
                    startActivity(intent);
                    }catch (Exception e){
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });

            //goto delete account page
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                    Intent intent = new Intent(ProfileSetting.this, DeleteAccount.class);
                    intent.putExtra("EXTRA_EMAIL_PROFILE_DELETE", email);
                    startActivity(intent);
                    }catch (Exception e){
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });

            //goto login page
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                    Intent intent = new Intent(ProfileSetting.this, Login.class);
                    startActivity(intent);
                    }catch (Exception e){
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();

        }
        finally {
            clearCache();
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
                        Intent intent1 = new Intent(ProfileSetting.this, ProductList.class);
                        intent1.putExtra("EXTRA_EMAIL_HOMEPAGE",email);
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
