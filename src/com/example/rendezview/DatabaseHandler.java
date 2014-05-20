package com.example.rendezview;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "RendezViewDB";
 
    // Contacts table name
    private static final String TABLE_FRIENDS = "friends";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LNG = "longitude";
    private static final String KEY_LOCATED = "located";
    
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    // 	Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FRIENDS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FRIENDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_LAT + " REAL" + KEY_LNG + " REAL" + KEY_LOCATED + " INTEGER"  + ")";
        db.execSQL(CREATE_FRIENDS_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // Drop older table if existed
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS); 
//        // Create tables again
//        onCreate(db);
    }
    
    // Adding new friend
    void addFriend(UserInfo friend) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_ID, friend.getUserId());
        values.put(KEY_NAME, friend.getUserName());
        values.put(KEY_LAT, friend.getUserLocation().latitude);
        values.put(KEY_LNG, friend.getUserLocation().longitude);
        values.put(KEY_LOCATED, friend.getLocated());
 
        // Inserting Row
        db.insert(TABLE_FRIENDS, null, values);
        db.close(); // Closing database connection
    }
 
    // Getting single contact
    UserInfo getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_FRIENDS, new String[] { KEY_ID,
                KEY_NAME, KEY_LAT, KEY_LNG, KEY_LOCATED }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        UserInfo ui = new UserInfo(cursor.getString(1),
                new LatLng(Double.valueOf(cursor.getString(2)), Double.valueOf(cursor.getString(1))), 
                Integer.valueOf(cursor.getString(1)), Integer.valueOf(cursor.getString(5)));
        // return contact
        return ui;
    }
     
    // Getting All Friends
    public List<UserInfo> getAllFriends() {
        List<UserInfo> friendsList = new ArrayList<UserInfo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FRIENDS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserInfo ui = new UserInfo();
                ui.setUserId(Integer.parseInt(cursor.getString(0)));
                ui.setUserName(cursor.getString(1));
                ui.setUserLocation(new LatLng(Double.valueOf(cursor.getString(2)), Double.valueOf(cursor.getString(3))));
                if (Integer.valueOf(cursor.getString(4)) == 1)
                	ui.locate();
                else
                	ui.unlocate();
                // Adding contact to list
                friendsList.add(ui);
            } while (cursor.moveToNext());
        }
 
        // return friends list
        return friendsList;
    }
 
    // Updating single friend
    public int updateFriendInfo(UserInfo ui) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, ui.getUserName());
        values.put(KEY_LAT, ui.getUserLocation().latitude);
        values.put(KEY_LNG, ui.getUserLocation().longitude);
        values.put(KEY_LOCATED, ui.getLocated());
 
        // updating row
        return db.update(TABLE_FRIENDS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(ui.getUserId()) });
    }
 
    // Deleting single friend
    public void deleteFriend(UserInfo ui) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FRIENDS, KEY_ID + " = ?",
                new String[] { String.valueOf(ui.getUserId()) });
        db.close();
    }
 
 
    // Getting friends count
    public int getFriendsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_FRIENDS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
}
