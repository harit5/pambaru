package com.example.seestock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class homepage extends AppCompatActivity {

    Button btntambahproduk, btndetail, button5, btnStockRecap; // Tambahkan btnStockRecap
    MyDatabaseHelper dbHelper;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        dbHelper = new MyDatabaseHelper(this);
        textView = findViewById(R.id.textView);
        button5 = findViewById(R.id.button5);
        btntambahproduk = findViewById(R.id.button);
        btnStockRecap = findViewById(R.id.btnStockRecap); // Inisialisasi tombol rekap stok

        // Inisialisasi awal RecyclerView (pertama kali halaman dibuka)
        RecyclerView recyclerView = findViewById(R.id.recyclerViewProduk);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Produk> produkList = dbHelper.getAllProduk();
        ProdukAdapter adapter = new ProdukAdapter(this, produkList);
        recyclerView.setAdapter(adapter);

        btntambahproduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tambahproduk = new Intent(homepage.this, addproduct.class);
                startActivity(tambahproduk);
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homepage.this, maps.class);
                startActivity(intent);
            }
        });

        // Listener untuk tombol rekap stok
        btnStockRecap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homepage.this, StockRecapActivity.class);
                startActivity(intent);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh data produk setiap kali halaman kembali aktif
        List<Produk> produkList = dbHelper.getAllProduk();
        ProdukAdapter adapter = new ProdukAdapter(this, produkList);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewProduk);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}