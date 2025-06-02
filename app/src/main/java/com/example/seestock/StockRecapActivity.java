package com.example.seestock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
//    private Button btnBackFromRecap;

    ImageView backButton;

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
//        btnBackFromRecap = findViewById(R.id.btnBackFromRecap);
        backButton = findViewById(R.id.btnBackArrow);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(StockRecapActivity.this, homepage.class);
                startActivity(backIntent);
            }
        });


        recyclerViewStockRecap.setLayoutManager(new LinearLayoutManager(this));

        loadStockRecapData();

//        btnBackFromRecap.setOnClickListener(v -> finish()); // Tombol kembali
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    private void loadStockRecapData() {
        List<Produk> allProduk = dbHelper.getAllProdukIncludingDeleted();
        adapter = new StockRecapAdapter(this, allProduk);
        recyclerViewStockRecap.setAdapter(adapter);

        updateSummary(allProduk);
    }

    private void updateSummary(List<Produk> produkList) {
        int totalActiveProducts = 0;
        int totalOverallStock = 0;

        for (Produk produk : produkList) {
            if (produk.deleteAt == null) { // Hanya hitung produk aktif
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
        loadStockRecapData(); // Refresh data setiap kali kembali ke halaman ini
    }
}