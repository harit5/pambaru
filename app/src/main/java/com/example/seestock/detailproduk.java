package com.example.seestock;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class detailproduk extends AppCompatActivity {

    private ImageView imageViewProdukDetail;
    private TextView txtNamaProdukDetail;
    private TextView txtHargaProdukDetail;
    private TextView txtStokProdukDetail;
    private TextView txtDeskripsiProdukDetail;

    ImageView tambahstok, kurangstok;
    Button back;
    private MyDatabaseHelper dbHelper;

    int produkid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailproduk);

        dbHelper = new MyDatabaseHelper(this);

        // Bind UI components
        tambahstok = findViewById(R.id.btnIncrementStock);
        kurangstok = findViewById(R.id.btnDecrementStock);
        imageViewProdukDetail = findViewById(R.id.imageViewProduk);
        txtNamaProdukDetail = findViewById(R.id.txtNamaProduk);
        txtHargaProdukDetail = findViewById(R.id.txtHargaProduk);
        txtStokProdukDetail = findViewById(R.id.txtStokProduk);
        txtDeskripsiProdukDetail = findViewById(R.id.longtextview);
        back = findViewById(R.id.btnkembali);

        // Get product ID from intent
        Intent intent = getIntent();
        String idIntent = intent.getStringExtra("id");
        produkid = Integer.parseInt(idIntent);
        if (produkid != -1) {
            loadProdukDetail(produkid);
        } else {
            Toast.makeText(this, "ID produk tidak valid", Toast.LENGTH_SHORT).show();
            finish();
        }

        back.setOnClickListener(v -> {
            finish();
            Toast.makeText(this, "Kembali", Toast.LENGTH_SHORT).show();
        });

        tambahstok.setOnClickListener(v -> tambahStok(produkid));
        kurangstok.setOnClickListener(v -> kurangstok(produkid));
    }

    void loadProdukDetail(int id) {
        Cursor cursor = dbHelper.getProduk(id);
        if (cursor != null && cursor.moveToFirst()) {
            String nama = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            double harga = cursor.getDouble(cursor.getColumnIndexOrThrow("harga"));
            int stok = cursor.getInt(cursor.getColumnIndexOrThrow("stock"));
            String deskripsi = cursor.getString(cursor.getColumnIndexOrThrow("deskripsi"));
            byte[] fotoProduk = cursor.getBlob(cursor.getColumnIndexOrThrow("foto_produk"));

            txtNamaProdukDetail.setText(nama);
            txtHargaProdukDetail.setText("Rp. " + (int) harga);
            txtStokProdukDetail.setText(String.valueOf(stok));
            txtDeskripsiProdukDetail.setText(deskripsi);

            if (fotoProduk != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(fotoProduk, 0, fotoProduk.length);
                imageViewProdukDetail.setImageBitmap(bitmap);
            } else {
                imageViewProdukDetail.setImageResource(R.drawable.ic_launcher_foreground);
            }

            cursor.close();
        } else {
            Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    void tambahStok(int idProduk) {
        int currentStock = Integer.parseInt(txtStokProdukDetail.getText().toString());
        int newStock = currentStock + 1;
        dbHelper.updateProductStock(idProduk, newStock);
        txtStokProdukDetail.setText(String.valueOf(newStock));
    }

    void kurangstok(int idProduk) {
        int currentStock = Integer.parseInt(txtStokProdukDetail.getText().toString());
        if (currentStock > 0) {
            int newStock = currentStock - 1;
            dbHelper.updateProductStock(idProduk, newStock);
            txtStokProdukDetail.setText(String.valueOf(newStock));
        } else {
            Toast.makeText(this, "Stok tidak dapat dikurangi lagi.", Toast.LENGTH_SHORT).show();
        }
    }
}
