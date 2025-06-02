package com.example.seestock;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log; // For logging

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyApp.db";
    private static final int DATABASE_VERSION = 2; // Increment the database version

    // Table and column names (good practice to define as constants)
    public static final String TABLE_PRODUK = "produk";
    public static final String COLUMN_PRODUK_ID = "id";
    public static final String COLUMN_PRODUK_NAME = "name";
    public static final String COLUMN_PRODUK_HARGA = "harga";
    public static final String COLUMN_PRODUK_STOCK = "stock";
    public static final String COLUMN_PRODUK_FOTO = "foto_produk";
    public static final String COLUMN_PRODUK_DESKRIPSI = "deskripsi";
    public static final String COLUMN_PRODUK_DELETE_AT = "deleteAt";


    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userName TEXT," +
                "email TEXT," +
                "password TEXT)");

        // Tabel produk
        db.execSQL("CREATE TABLE " + TABLE_PRODUK + " (" +
                COLUMN_PRODUK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PRODUK_NAME + " TEXT," +
                COLUMN_PRODUK_HARGA + " REAL," +
                COLUMN_PRODUK_DELETE_AT + " TEXT DEFAULT NULL," +
                COLUMN_PRODUK_FOTO + " BLOB DEFAULT NULL," +
                COLUMN_PRODUK_STOCK + " INTEGER," +
                COLUMN_PRODUK_DESKRIPSI + " TEXT)"); // Add this line
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if your schema changes
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_PRODUK + " ADD COLUMN " + COLUMN_PRODUK_DESKRIPSI + " TEXT;");
        }
    }

    public void registerUser(String userName, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", password);
        values.put("userName", userName);
        values.put("email", email);

        db.insert("user", null, values);
        db.close();
    }

    public Cursor getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM user WHERE id = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM user WHERE email = ?", new String[]{email});

    }

    public Cursor getProduk(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM produk WHERE id = ?", new String[]{String.valueOf(id)});
    }

    public List<Produk> getAllProduk() {
        List<Produk> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Ensure you select all necessary columns for the Produk object, including ID
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PRODUK_ID + ", " + COLUMN_PRODUK_NAME + ", " +
                COLUMN_PRODUK_HARGA + ", " + COLUMN_PRODUK_STOCK + ", " +
                COLUMN_PRODUK_FOTO + ", " + COLUMN_PRODUK_DESKRIPSI + ", " + COLUMN_PRODUK_DELETE_AT +
                " FROM " + TABLE_PRODUK +
                " WHERE " + COLUMN_PRODUK_DELETE_AT + " IS NULL", null);

        if (cursor.moveToFirst()) {
            do {
                // Pastikan constructor Produk sesuai dengan kolom yang diambil
                Produk produk = new Produk(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_HARGA)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_STOCK)),
                        cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_FOTO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_DESKRIPSI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_DELETE_AT))
                );
                list.add(produk);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }


    /**
     * Updates the stock for a specific product.
     * @param productId The ID of the product to update.
     * @param newStock The new stock quantity.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateProductStock(int productId, int newStock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUK_STOCK, newStock);
        int rowsAffected = db.update(TABLE_PRODUK, values, COLUMN_PRODUK_ID + " = ?", new String[]{String.valueOf(productId)});
        return rowsAffected > 0;
    }

    /**
     * Marks a product as deleted by setting the deleteAt timestamp.
     * @param productId The ID of the product to delete.
     * @return true if the update was successful, false otherwise.
     */
    public boolean softDeleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        values.put(COLUMN_PRODUK_DELETE_AT, timestamp);

        int rowsAffected = db.update(TABLE_PRODUK, values, COLUMN_PRODUK_ID + " = ?", new String[]{String.valueOf(productId)});
        db.close();
        return rowsAffected > 0;
    }

    /**
     * Retrieves all products from the database, including those marked as deleted.
     * @return A list of Produk objects.
     */
    public List<Produk> getAllProdukIncludingDeleted() {
        List<Produk> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " +
                COLUMN_PRODUK_ID + ", " +
                COLUMN_PRODUK_NAME + ", " +
                COLUMN_PRODUK_HARGA + ", " +
                COLUMN_PRODUK_STOCK + ", " +
                COLUMN_PRODUK_FOTO + ", " +
                COLUMN_PRODUK_DESKRIPSI + ", " +
                COLUMN_PRODUK_DELETE_AT +
                " FROM " + TABLE_PRODUK, null); // No WHERE clause to include all

        if (cursor.moveToFirst()) {
            do {
                // Pastikan constructor Produk sesuai dengan kolom yang diambil
                Produk produk = new Produk(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_HARGA)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_STOCK)),
                        cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_FOTO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_DESKRIPSI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUK_DELETE_AT))
                );
                list.add(produk);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // db.close(); // Manage db connection lifecycle carefully
        return list;
    }
}