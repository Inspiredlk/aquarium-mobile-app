package com.example.mysyncproduct;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by Quoc Nguyen on 13-Dec-16.
 */

public class SQLiteHelperCart extends SQLiteOpenHelper {

    public SQLiteHelperCart(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData(String title, String shortdesc, String quantity, String price, String imagepath){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO CART VALUES (NULL, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, title);
        statement.bindString(2, shortdesc);
        statement.bindString(3, quantity);
        statement.bindString(4, price);
        statement.bindString(5, imagepath);
        statement.executeInsert();
    }

    public void insertData(String title, String shortdesc, int quantity, double price, String imagepath,byte[] image){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO CART VALUES (NULL, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, title);
        statement.bindString(2, shortdesc);
        statement.bindString(3, Integer.toString(quantity));
        statement.bindString(4, Double.toString(price));
        statement.bindString(5, imagepath);
        statement.bindBlob(6, image);
        statement.executeInsert();
    }

    public void updateData(byte[] image,String imagepath) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE CART SET image = ? WHERE imagepath = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindBlob(1, image);
        statement.bindString(2, imagepath);

        statement.execute();
        database.close();
    }

    public void updateData(String name, String price, byte[] image, int id) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE CART SET title = ?, price = ?, image = ? WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, name);
        statement.bindString(2, price);
        statement.bindBlob(3, image);
        statement.bindDouble(4, (double)id);

        statement.execute();
        database.close();
    }

    public  void deleteData(String name) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM CART WHERE title = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, name);

        statement.execute();
        database.close();
    }

    public Cursor getAllProduct() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM CART", null);
        return res;
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    public Cursor getCartProduct(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM CART WHERE title = '" + name + "'", null);
        return res;
    }

    public void updateDataQty(String name, String quantity) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE CART SET quantity = ? WHERE title = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, quantity);
        statement.bindString(2, name);

        statement.execute();
        database.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
