// detailproduk.java
package com.example.seestock;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class detailproduk extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private int produkId = -1; // Default -1 jika tidak ada ID yang diterima
    private Produk currentProduk; // Objek Produk yang sedang ditampilkan/diedit

    // UI Elements (disesuaikan dengan XML baru Anda)
    private ImageView imageViewProduk;
    private TextView txtNamaProduk;
    private TextView txtHargaProduk;
    private TextView txtStokProduk;
    private TextView longtextview; // Untuk deskripsi
    private ImageView btnIncrementStock; // Untuk menambah stok
    private ImageView btnDecrementStock; // Untuk mengurangi stok
    private Button btnkembali; // Tombol kembali

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailproduk); // Pastikan layout ini ada

        // Initialize database helper
        dbHelper = new MyDatabaseHelper(this);

        // Initialize UI elements (disesuaikan dengan ID dari XML baru Anda)
        imageViewProduk = findViewById(R.id.imageViewProduk);
        txtNamaProduk = findViewById(R.id.txtNamaProduk);
        txtHargaProduk = findViewById(R.id.txtHargaProduk);
        txtStokProduk = findViewById(R.id.txtStokProduk);
        longtextview = findViewById(R.id.longtextview);
        btnIncrementStock = findViewById(R.id.btnIncrementStock);
        btnDecrementStock = findViewById(R.id.btnDecrementStock);
        btnkembali = findViewById(R.id.btnkembali);

        // Ambil ID produk dari Intent
        if (getIntent().hasExtra("id")) {
            produkId = Integer.parseInt(getIntent().getStringExtra("id"));
            loadProdukData(produkId);
        } else {
            Toast.makeText(this, "ID Produk tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish(); // Tutup activity jika ID tidak ada
            return;
        }

        // Set listeners
        btnkembali.setOnClickListener(v -> onBackPressed()); // Kembali ke halaman sebelumnya

        btnIncrementStock.setOnClickListener(v -> updateStock(1)); // Tambah stok
        btnDecrementStock.setOnClickListener(v -> updateStock(-1)); // Kurangi stok

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadProdukData(int id) {
        Cursor cursor = dbHelper.getProduk(id);
        if (cursor != null && cursor.moveToFirst()) {
            currentProduk = new Produk(
                    cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_PRODUK_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_PRODUK_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_PRODUK_HARGA)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_PRODUK_STOCK)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_PRODUK_FOTO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_PRODUK_DESKRIPSI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_PRODUK_DELETE_AT))
            );
            cursor.close();

            // Set UI elements with product data
            txtNamaProduk.setText(currentProduk.name);
            txtHargaProduk.setText("Rp. " + (int) currentProduk.harga); // Format harga
            txtStokProduk.setText("Stok: " + currentProduk.stock);
            longtextview.setText(currentProduk.deskripsi);

            if (currentProduk.foto_produk != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(currentProduk.foto_produk, 0, currentProduk.foto_produk.length);
                imageViewProduk.setImageBitmap(bitmap);
            } else {
                imageViewProduk.setImageResource(R.drawable.ic_launcher_foreground); // Placeholder
            }

            // Jika produk sudah dihapus (deleteAt tidak null), nonaktifkan kontrol stok
            if (currentProduk.deleteAt != null && !currentProduk.deleteAt.isEmpty()) {
                btnIncrementStock.setVisibility(View.GONE);
                btnDecrementStock.setVisibility(View.GONE);
                Toast.makeText(this, "Produk ini telah dihapus.", Toast.LENGTH_LONG).show();
            } else {
                btnIncrementStock.setVisibility(View.VISIBLE);
                btnDecrementStock.setVisibility(View.VISIBLE);
            }

        } else {
            Toast.makeText(this, "Produk tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateStock(int delta) {
        if (currentProduk != null) {
            int newStock = currentProduk.stock + delta;
            // Pastikan stok tidak kurang dari 0
            if (newStock < 0) {
                Toast.makeText(this, "Stok tidak bisa kurang dari 0.", Toast.LENGTH_SHORT).show();
                return;
            }

            currentProduk.stock = newStock;

            // Perbarui stok di database
            // Di sini Anda perlu metode di MyDatabaseHelper untuk hanya memperbarui stok.
            // Jika updateProduct() memperbarui semua field, kita bisa menggunakannya.
            // Mari kita tambahkan metode khusus updateProductStock() di MyDatabaseHelper.
            int rowsAffected = dbHelper.updateProductStock(currentProduk.id, newStock);

            if (rowsAffected > 0) {
                txtStokProduk.setText("Stok: " + currentProduk.stock); // Perbarui UI
                Toast.makeText(this, "Stok berhasil diperbarui.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Gagal memperbarui stok.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Karena tidak ada fungsionalitas edit penuh (gambar, nama, harga, deskripsi),
    // metode onActivityResult, getBytes, saveProductChanges, showDeleteConfirmationDialog,
    // dan performSoftDelete tidak diperlukan dalam versi ini.
    // Jika Anda ingin menambahkan kembali fungsionalitas edit/hapus penuh,
    // Anda perlu menambahkan kembali elemen UI yang sesuai ke XML Anda
    // dan mengaktifkan kembali logika di sini.

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data jika ada perubahan saat kembali dari activity lain (misal dari homepage)
        if (produkId != -1) {
            loadProdukData(produkId);
        }
    }
}