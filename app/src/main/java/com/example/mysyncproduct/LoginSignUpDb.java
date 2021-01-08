package com.example.mysyncproduct;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoginSignUpDb extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Users154.db";

    private static final String KEY_ID = "ID";
    public static final String TABLE_NAME = "Users";
    public static final String COLUMN_NAME_EMAIL = "EMAIL";
    public static final String COLUMN_NAME_PASSWORD = "PASSWORD";

    //Create User Table
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME_EMAIL + " TEXT," +
                    COLUMN_NAME_PASSWORD + " TEXT)";

    //Drop User Table
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    public LoginSignUpDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    //Insert Users into the table
    public boolean insertUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_EMAIL, email);
        contentValues.put(COLUMN_NAME_PASSWORD, password);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    //Read User Table using email
    public Cursor getUserDetails(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM '" + TABLE_NAME + "' WHERE EMAIL = '" + email + "'", null);
        return res;
    }

    //Read All User Details
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

    //Delete User
    public void DeleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_NAME_EMAIL + " LIKE ?";
        String[] selectionArgs = {email};
        int deletedRows = db.delete(TABLE_NAME, selection, selectionArgs);
    }

}
