package com.s224776282.a91lostandfoundapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LostAndFoundDB";
    private static final int DATABASE_VERSION = 2; // Changed database version

    private static final String TABLE_ITEMS = "items";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_ITEMS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "postType TEXT, " +
                "name TEXT, " +
                "phone TEXT, " +
                "description TEXT, " +
                "date TEXT, " +
                "location TEXT, " +
                "latitude REAL, " +
                "longitude REAL, " +
                "category TEXT, " +
                "imageUri TEXT, " +
                "timestamp INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // Insert a new advert
    public long insertItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("postType", item.getPostType());
        values.put("name", item.getName());
        values.put("phone", item.getPhone());
        values.put("description", item.getDescription());
        values.put("date", item.getDate());
        values.put("location", item.getLocation());
        values.put("latitude", item.getLatitude());
        values.put("longitude", item.getLongitude());
        values.put("category", item.getCategory());
        values.put("imageUri", item.getImageUri());
        values.put("timestamp", item.getTimestamp());

        return db.insert(TABLE_ITEMS, null, values);
    }

    // Get all items or filter by category
    public List<Item> getItems(String categoryFilter) {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_ITEMS;
        Cursor cursor;

        if (categoryFilter == null || categoryFilter.equals("All") || categoryFilter.isEmpty()) {
            cursor = db.rawQuery(query + " ORDER BY timestamp DESC", null);
        } else {
            query += " WHERE category = ?";
            cursor = db.rawQuery(query + " ORDER BY timestamp DESC", new String[]{categoryFilter});
        }

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(cursor.getInt(0));
                item.setPostType(cursor.getString(1));
                item.setName(cursor.getString(2));
                item.setPhone(cursor.getString(3));
                item.setDescription(cursor.getString(4));
                item.setDate(cursor.getString(5));
                item.setLocation(cursor.getString(6));
                item.setLatitude(cursor.getDouble(7));
                item.setLongitude(cursor.getDouble(8));
                item.setCategory(cursor.getString(9));
                item.setImageUri(cursor.getString(10));
                item.setTimestamp(cursor.getLong(11));
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}