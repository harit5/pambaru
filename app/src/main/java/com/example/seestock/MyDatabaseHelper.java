package com.example.seestock;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log; // Untuk logging

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyApp.db";
    private static final int DATABASE_VERSION = 2; // Versi database saat ini

    // Nama tabel dan kolom (praktik yang baik untuk mendefinisikannya sebagai konstanta)
    public static final String TABLE_PRODUK = "produk";
    public static final String COLUMN_PRODUK_ID = "id";
    public static final String COLUMN_PRODUK_NAME = "name";
    public static final String COLUMN_PRODUK_HARGA = "harga";
    public static final String COLUMN_PRODUK_STOCK = "stock";
    public static final String COLUMN_PRODUK_FOTO = "foto_produk";
    public static final String COLUMN_PRODUK_DESKRIPSI = "deskripsi"; // Kolom yang menyebabkan masalah
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
        // Membuat tabel user
        db.execSQL("CREATE TABLE user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userName TEXT," +
                "email TEXT," +
                "password TEXT)");

        // Membuat tabel produk
        db.execSQL("CREATE TABLE " + TABLE_PRODUK + " (" +
                COLUMN_PRODUK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PRODUK_NAME + " TEXT," +
                COLUMN_PRODUK_HARGA + " REAL," +
                COLUMN_PRODUK_DELETE_AT + " TEXT DEFAULT NULL," +
                COLUMN_PRODUK_FOTO + " BLOB DEFAULT NULL," +
                COLUMN_PRODUK_STOCK + " INTEGER," +
                COLUMN_PRODUK_DESKRIPSI + " TEXT)"); // Kolom 'deskripsi' sudah ada di sini
        Log.d("MyDatabaseHelper", "Table 'produk' created with 'deskripsi' column.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Menangani upgrade database jika skema kamu berubah
        Log.d("MyDatabaseHelper", "onUpgrade called: oldVersion=" + oldVersion + ", newVersion=" + newVersion);

        // Jika versi lama kurang dari 2, berarti kita perlu menambahkan kolom 'deskripsi'
        // (asumsi 'deskripsi' ditambahkan di versi 2)
        if (oldVersion < 2) {
            // Periksa apakah kolom 'deskripsi' sudah ada sebelum menambahkannya
            if (!columnExists(db, TABLE_PRODUK, COLUMN_PRODUK_DESKRIPSI)) {
                db.execSQL("ALTER TABLE " + TABLE_PRODUK + " ADD COLUMN " + COLUMN_PRODUK_DESKRIPSI + " TEXT;");
                Log.d("MyDatabaseHelper", "Kolom '" + COLUMN_PRODUK_DESKRIPSI + "' berhasil ditambahkan ke tabel '" + TABLE_PRODUK + "'.");
            } else {
                Log.d("MyDatabaseHelper", "Kolom '" + COLUMN_PRODUK_DESKRIPSI + "' sudah ada di tabel '" + TABLE_PRODUK + "', tidak perlu menambahkan.");
            }
        }
        // Jika di masa depan kamu memiliki perubahan skema untuk versi 3, tambahkan blok if (oldVersion < 3) di sini
        // Contoh:
        // if (oldVersion < 3) {
        //     // db.execSQL("ALTER TABLE another_table ADD COLUMN new_column TEXT;");
        // }
    }

    /**
     * Metode helper untuk memeriksa apakah sebuah kolom sudah ada di tabel tertentu.
     * @param db Database SQLite
     * @param tableName Nama tabel yang akan diperiksa
     * @param columnName Nama kolom yang akan dicari
     * @return true jika kolom ada, false jika tidak.
     */
    private boolean columnExists(SQLiteDatabase db, String tableName, String columnName) {
        Cursor cursor = null;
        try {
            // Menggunakan PRAGMA table_info untuk mendapatkan informasi kolom
            cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex("name"); // Dapatkan indeks kolom 'name'
                if (nameIndex == -1) { // Jika kolom 'name' tidak ditemukan (seharusnya tidak terjadi)
                    Log.e("MyDatabaseHelper", "Kolom 'name' tidak ditemukan di PRAGMA table_info.");
                    return false;
                }
                do {
                    String name = cursor.getString(nameIndex);
                    if (columnName.equalsIgnoreCase(name)) {
                        return true; // Kolom ditemukan
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("MyDatabaseHelper", "Error saat memeriksa keberadaan kolom: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false; // Kolom tidak ditemukan
    }


    // --- Metode CRUD lainnya (tidak ada perubahan signifikan) ---

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
        // Pastikan kamu memilih semua kolom yang diperlukan untuk objek Produk, termasuk ID
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PRODUK_ID + ", " + COLUMN_PRODUK_NAME + ", " +
                COLUMN_PRODUK_HARGA + ", " + COLUMN_PRODUK_STOCK + ", " +
                COLUMN_PRODUK_FOTO + ", " + COLUMN_PRODUK_DESKRIPSI + ", " + COLUMN_PRODUK_DELETE_AT +
                " FROM " + TABLE_PRODUK +
                " WHERE " + COLUMN_PRODUK_DELETE_AT + " IS NULL", null);

        if (cursor.moveToFirst()) {
            do {
                // Pastikan konstruktor Produk sesuai dengan kolom yang diambil
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
     * Memperbarui stok untuk produk tertentu.
     * @param productId ID produk yang akan diperbarui.
     * @param newStock Kuantitas stok baru.
     * @return true jika pembaruan berhasil, false jika tidak.
     */
    public boolean updateProductStock(int productId, int newStock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUK_STOCK, newStock);
        int rowsAffected = db.update(TABLE_PRODUK, values, COLUMN_PRODUK_ID + " = ?", new String[]{String.valueOf(productId)});
        return rowsAffected > 0;
    }

    /**
     * Menandai produk sebagai dihapus dengan mengatur timestamp deleteAt.
     * @param productId ID produk yang akan dihapus.
     * @return true jika pembaruan berhasil, false jika tidak.
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
     * Mengambil semua produk dari database, termasuk yang ditandai sebagai dihapus.
     * @return Sebuah daftar objek Produk.
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
                " FROM " + TABLE_PRODUK, null); // Tidak ada klausa WHERE untuk menyertakan semua

        if (cursor.moveToFirst()) {
            do {
                // Pastikan konstruktor Produk sesuai dengan kolom yang diambil
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
        // db.close(); // Kelola siklus hidup koneksi db dengan hati-hati
        return list;
    }
}
