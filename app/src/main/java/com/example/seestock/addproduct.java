package com.example.seestock;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
    TextView tvFileDipilih;
    EditText etNamaProduk, etIsi, jmlstok;

    EditText etHarga;

    Spinner spinnerStok;

    ImageView imageView6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addproduct);

        btnPilihFile = findViewById(R.id.btnPilihFile);
        tvFileDipilih = findViewById(R.id.btnPilihFile);
        etNamaProduk = findViewById(R.id.etNamaProduk); // pastikan ID sesuai XML
        etIsi = findViewById(R.id.etIsi);               // pastikan ID sesuai XML
        jmlstok = findViewById(R.id.jmlstok);   // pastikan ID sesuai XML
        etHarga = findViewById(R.id.harga); // pastikan ID ini sesuai XML kamu




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

        Button btnSimpan = findViewById(R.id.btnSimpan); // Tambahkan di XML juga
        btnSimpan.setOnClickListener(v -> saveProduct());

        ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            String fileName = getFileName(imageUri);
            tvFileDipilih.setText(fileName);

            try {
                InputStream iStream = getContentResolver().openInputStream(imageUri);
                imageBytes = getBytes(iStream);
            } catch (IOException e) {
                e.printStackTrace();
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
        String hargaString = etHarga.getText().toString().trim(); // ✅ Ambil nilai harga

        // Validasi input
        if (namaProduk.isEmpty() || deskripsi.isEmpty() || stokString.isEmpty() || hargaString.isEmpty() || imageBytes == null) {
            tvFileDipilih.setText("Isi semua field dan pilih gambar!");
            return;
        }

        int stok;
        double harga; // ✅ tipe harga adalah double
        try {
            stok = Integer.parseInt(stokString);
            harga = Double.parseDouble(hargaString); // ✅ parsing harga
        } catch (NumberFormatException e) {
            tvFileDipilih.setText("Stok dan harga harus berupa angka!");
            return;
        }

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", namaProduk);
        values.put("harga", harga); // ✅ simpan harga ke database
        values.put("stock", stok);
        values.put("foto_produk", imageBytes);
        values.put("deskripsi", deskripsi);

        db.insert("produk", null, values);
        db.close();

        // Navigasi ke homepage setelah simpan
        Intent intent = new Intent(addproduct.this, homepage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
