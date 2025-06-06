// StockRecapActivity.java
package com.example.seestock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Import Toast

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockRecapActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private RecyclerView recyclerViewStockRecap;
    private StockRecapAdapter adapter;
    private TextView tvTotalProducts, tvTotalStock;
    ImageView backButton;

    private int currentUserId; // Variabel untuk menyimpan userId

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_recap);

        dbHelper = new MyDatabaseHelper(this);

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalStock = findViewById(R.id.tvTotalStock);
        recyclerViewStockRecap = findViewById(R.id.recyclerViewStockRecap);
        backButton = findViewById(R.id.btnBackArrow);

        // *** Ambil userId dari Intent ***
        currentUserId = getIntent().getIntExtra("userId", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Kesalahan: ID pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish(); // Tutup activity jika userId tidak valid
            return;
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(StockRecapActivity.this, homepage.class);
                startActivity(backIntent);
            }
        });

        recyclerViewStockRecap.setLayoutManager(new LinearLayoutManager(this));

        loadStockRecapData();
    }

    private void loadStockRecapData() {
        // *** Panggil getAllProdukIncludingDeleted dengan userId ***
        List<Produk> allProduk = dbHelper.getAllProdukIncludingDeleted(currentUserId);
        adapter = new StockRecapAdapter(this, allProduk);
        recyclerViewStockRecap.setAdapter(adapter);

        updateSummary(allProduk);
    }

    private void updateSummary(List<Produk> produkList) {
        int totalActiveProducts = 0;
        int totalOverallStock = 0;

        for (Produk produk : produkList) {
            if (produk.deleteAt == null) {
                totalActiveProducts++;
                totalOverallStock += produk.stock;
            }
        }

        tvTotalProducts.setText("Jumlah Produk Aktif: " + totalActiveProducts);
        tvTotalStock.setText("Total Stok Keseluruhan: " + totalOverallStock);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Pastikan userId masih valid sebelum memuat data
        if (currentUserId != -1) {
            loadStockRecapData(); // Refresh data setiap kali kembali ke halaman ini
        } else {
            // Jika userId tidak valid, arahkan kembali ke homepage (atau login)
            Intent intent = new Intent(StockRecapActivity.this, homepage.class); // atau loginpage.class
            startActivity(intent);
            finish();
        }
    }
}