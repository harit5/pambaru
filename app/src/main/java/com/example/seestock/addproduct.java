// File: addproduct.java
package com.example.seestock;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast; // Import Toast

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class addproduct extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private byte[] imageBytes;

    Button btnPilihFile;
    TextView tvFileDipilih; // Tetap dideklarasikan sebagai TextView
    EditText etNamaProduk, etIsi, jmlstok;
    EditText etHarga;
    Spinner spinnerStok; // Spinner ini tidak digunakan di kode Anda
    ImageView imageView6;

    private int currentUserId; // Variabel untuk menyimpan userId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addproduct);

        btnPilihFile = findViewById(R.id.btnPilihFile);
        // Penting: Jika R.id.btnPilihFile adalah ID untuk Button,
        // maka tvFileDipilih sebenarnya akan merujuk ke Button tersebut.
        // Jika Anda ingin menampilkan nama file di TextView terpisah,
        // Anda perlu TextView baru di layout XML dengan ID yang berbeda.
        tvFileDipilih = findViewById(R.id.btnPilihFile); // Mempertahankan inisialisasi ini seperti yang diminta

        etNamaProduk = findViewById(R.id.etNamaProduk);
        etIsi = findViewById(R.id.etIsi);
        jmlstok = findViewById(R.id.jmlstok);
        etHarga = findViewById(R.id.harga);
        imageView6 = findViewById(R.id.imageView6);

        // Ambil userId dari Intent
        currentUserId = getIntent().getIntExtra("userId", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Kesalahan: ID pengguna tidak ditemukan. Harap login kembali.", Toast.LENGTH_LONG).show();
            finish(); // Tutup activity jika userId tidak valid
            return;
        }

        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(addproduct.this, homepage.class);
                // Opsional: Jika homepage perlu userId saat kembali, teruskan di sini juga
                // back.putExtra("userId", currentUserId);
                startActivity(back);
                finish(); // Selesai dari activity addproduct
            }
        });

        btnPilihFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnSimpan = findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(v -> saveProduct());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            String fileName = getFileName(imageUri);
            // Ini akan mengubah teks pada tombol btnPilihFile karena tvFileDipilih mengacu pada ID yang sama.
            // Jika ini yang Anda inginkan, tidak masalah.
            // Alternatif yang lebih baik adalah menggunakan TextView terpisah untuk menampilkan nama file.
            tvFileDipilih.setText("File terpilih: " + fileName); // Mempertahankan pemanggilan setText()

            try {
                InputStream iStream = getContentResolver().openInputStream(imageUri);
                imageBytes = getBytes(iStream);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memuat gambar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void saveProduct() {
        String namaProduk = etNamaProduk.getText().toString().trim();
        String deskripsi = etIsi.getText().toString().trim();
        String stokString = jmlstok.getText().toString().trim();
        String hargaString = etHarga.getText().toString().trim();

        // Validasi input
        if (namaProduk.isEmpty() || deskripsi.isEmpty() || stokString.isEmpty() || hargaString.isEmpty()) {
            Toast.makeText(this, "Semua field teks wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageBytes == null) {
            Toast.makeText(this, "Harap pilih gambar produk!", Toast.LENGTH_SHORT).show();
            return;
        }

        int stok;
        double harga;
        try {
            stok = Integer.parseInt(stokString);
            harga = Double.parseDouble(hargaString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Stok dan harga harus berupa angka yang valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Panggil metode addProduct dari MyDatabaseHelper
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        long result = dbHelper.addProduct(namaProduk, harga, stok, imageBytes, deskripsi, currentUserId);

        if (result > 0) {
            Toast.makeText(this, "Produk berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal menambahkan produk. Silakan coba lagi.", Toast.LENGTH_SHORT).show();
        }

        // Navigasi kembali ke homepage setelah simpan
        Intent intent = new Intent(addproduct.this, homepage.class);
        // Pastikan userId juga diteruskan kembali ke homepage jika diperlukan
        intent.putExtra("userId", currentUserId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Menutup activity addproduct
    }
}