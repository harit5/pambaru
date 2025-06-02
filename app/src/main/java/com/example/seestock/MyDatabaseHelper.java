// MyDatabaseHelper.java
package com.example.seestock;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyApp.db";
    private static final int DATABASE_VERSION = 3; // *** Ubah versi database menjadi 3 ***

    // Nama tabel dan kolom
    public static final String TABLE_PRODUK = "produk";
    public static final String COLUMN_PRODUK_ID = "id";
    public static final String COLUMN_PRODUK_NAME = "name";
    public static final String COLUMN_PRODUK_HARGA = "harga";
    public static final String COLUMN_PRODUK_STOCK = "stock";
    public static final String COLUMN_PRODUK_FOTO = "foto_produk";
    public static final String COLUMN_PRODUK_DESKRIPSI = "deskripsi";
    public static final String COLUMN_PRODUK_DELETE_AT = "deleteAt";
    public static final String COLUMN_PRODUK_USER_ID = "userId"; // *** Tambahkan kolom userId ***

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

        // Membuat tabel produk dengan kolom userId
        db.execSQL("CREATE TABLE " + TABLE_PRODUK + " (" +
                COLUMN_PRODUK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PRODUK_NAME + " TEXT," +
                COLUMN_PRODUK_HARGA + " REAL," +
                COLUMN_PRODUK_DELETE_AT + " TEXT DEFAULT NULL," +
                COLUMN_PRODUK_FOTO + " BLOB DEFAULT NULL," +
                COLUMN_PRODUK_STOCK + " INTEGER," +
                COLUMN_PRODUK_DESKRIPSI + " TEXT," +
                COLUMN_PRODUK_USER_ID + " INTEGER," + // *** Tambahkan kolom userId ***
                "FOREIGN KEY(" + COLUMN_PRODUK_USER_ID + ") REFERENCES user(id) ON DELETE CASCADE)"); // Foreign key
        Log.d("MyDatabaseHelper", "Table 'produk' created with 'deskripsi' and 'userId' columns.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("MyDatabaseHelper", "onUpgrade called: oldVersion=" + oldVersion + ", newVersion=" + newVersion);

        if (oldVersion < 2) {
            if (!columnExists(db, TABLE_PRODUK, COLUMN_PRODUK_DESKRIPSI)) {
                db.execSQL("ALTER TABLE " + TABLE_PRODUK + " ADD COLUMN " + COLUMN_PRODUK_DESKRIPSI + " TEXT;");
                Log.d("MyDatabaseHelper", "Kolom '" + COLUMN_PRODUK_DESKRIPSI + "' berhasil ditambahkan ke tabel '" + TABLE_PRODUK + "'.");
            }
        }
        // *** Tambahkan logika upgrade untuk versi 3 (menambahkan kolom userId) ***
        if (oldVersion < 3) {
            if (!columnExists(db, TABLE_PRODUK, COLUMN_PRODUK_USER_ID)) {
                db.execSQL("ALTER TABLE " + TABLE_PRODUK + " ADD COLUMN " + COLUMN_PRODUK_USER_ID + " INTEGER;");
                // Anda mungkin perlu mengisi nilai default untuk produk yang sudah ada
                // db.execSQL("UPDATE " + TABLE_PRODUK + " SET " + COLUMN_PRODUK_USER_ID + " = 1;"); // Contoh: set semua ke user ID 1
                Log.d("MyDatabaseHelper", "Kolom '" + COLUMN_PRODUK_USER_ID + "' berhasil ditambahkan ke tabel '" + TABLE_PRODUK + "'.");
            }
        }
    }

    private boolean columnExists(SQLiteDatabase db, String tableName, String columnName) {
        String result = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex("name");
                if (nameIndex == -1) {
                    Log.e("MyDatabaseHelper", "Kolom 'name' tidak ditemukan di PRAGMA table_info.");
                    return false;
                }
                do {
                    String name = cursor.getString(nameIndex);
                    if (columnName.equalsIgnoreCase(name)) {
                        return true;
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
        return false;
    }

    // --- Metode CRUD lainnya ---

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

    // *** Tambahkan metode untuk mendapatkan user ID berdasarkan email dan password (jika diperlukan) ***
    public int getUserId(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM user WHERE email = ? AND password = ?", new String[]{email, password});
        int userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        }
        if (cursor != null) {
            cursor.close();
        }
        return userId;
    }


    public Cursor getProduk(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM produk WHERE id = ?", new String[]{String.valueOf(id)});
    }

    // *** Modifikasi getAllProduk untuk memfilter berdasarkan userId ***
    public List<Produk> getAllProduk(int userId) { // Menerima userId sebagai parameter
        List<Produk> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PRODUK_ID + ", " + COLUMN_PRODUK_NAME + ", " +
                        COLUMN_PRODUK_HARGA + ", " + COLUMN_PRODUK_STOCK + ", " +
                        COLUMN_PRODUK_FOTO + ", " + COLUMN_PRODUK_DESKRIPSI + ", " + COLUMN_PRODUK_DELETE_AT +
                        " FROM " + TABLE_PRODUK +
                        " WHERE " + COLUMN_PRODUK_DELETE_AT + " IS NULL AND " + COLUMN_PRODUK_USER_ID + " = ?", // Filter berdasarkan userId
                new String[]{String.valueOf(userId)}); // Parameter userId

        if (cursor.moveToFirst()) {
            do {
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


    // Metode untuk menambahkan produk dengan userId
    public long addProduct(String name, double harga, int stock, byte[] fotoProduk, String deskripsi, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUK_NAME, name);
        values.put(COLUMN_PRODUK_HARGA, harga);
        values.put(COLUMN_PRODUK_STOCK, stock);
        values.put(COLUMN_PRODUK_FOTO, fotoProduk);
        values.put(COLUMN_PRODUK_DESKRIPSI, deskripsi);
        values.put(COLUMN_PRODUK_USER_ID, userId); // Simpan userId
        long result = db.insert(TABLE_PRODUK, null, values);
        db.close();
        return result;
    }

    /**
     * Mengambil semua produk dari database, termasuk yang ditandai sebagai dihapus.
     * @param userId ID pengguna yang produknya akan diambil.
     * @return Sebuah daftar objek Produk.
     */
    public List<Produk> getAllProdukIncludingDeleted(int userId) {
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
                        " FROM " + TABLE_PRODUK +
                        " WHERE " + COLUMN_PRODUK_USER_ID + " = ?", // Filter berdasarkan userId
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
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
     * Memperbarui detail produk yang ada di database.
     * @param produk Produk yang akan diperbarui. ID produk harus valid.
     * @return Jumlah baris yang terpengaruh (1 jika berhasil, 0 jika gagal).
     */
    public int updateProduct(Produk produk) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUK_NAME, produk.name);
        values.put(COLUMN_PRODUK_HARGA, produk.harga);
        values.put(COLUMN_PRODUK_STOCK, produk.stock);
        values.put(COLUMN_PRODUK_FOTO, produk.foto_produk);
        values.put(COLUMN_PRODUK_DESKRIPSI, produk.deskripsi);
        // userId tidak perlu diperbarui di sini karena seharusnya tidak berubah

        int rowsAffected = db.update(TABLE_PRODUK, values, COLUMN_PRODUK_ID + " = ?",
                new String[]{String.valueOf(produk.id)});
        db.close();
        return rowsAffected;
    }

    /**
     * Melakukan soft delete pada produk dengan menandai kolom deleteAt.
     * @param productId ID produk yang akan dihapus secara soft.
     * @return True jika berhasil melakukan soft delete, false jika gagal.
     */
    public boolean softDeleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // Format tanggal saat ini sebagai string untuk kolom deleteAt
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        values.put(COLUMN_PRODUK_DELETE_AT, timestamp);

        int result = db.update(TABLE_PRODUK, values, COLUMN_PRODUK_ID + " = ?",
                new String[]{String.valueOf(productId)});
        db.close();
        return result > 0;
    }

    // MyDatabaseHelper.java (tambahan)

    /**
     * Memperbarui hanya stok produk di database.
     * @param productId ID produk yang akan diperbarui.
     * @param newStock Nilai stok yang baru.
     * @return Jumlah baris yang terpengaruh (1 jika berhasil, 0 jika gagal).
     */
    public int updateProductStock(int productId, int newStock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUK_STOCK, newStock);

        int rowsAffected = db.update(TABLE_PRODUK, values, COLUMN_PRODUK_ID + " = ?",
                new String[]{String.valueOf(productId)});
        db.close();
        return rowsAffected;
    }

    // Metode updateProductStock dan softDeleteProduct tidak perlu menerima userId
    // karena mereka beroperasi pada productId yang sudah unik.
}