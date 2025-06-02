package com.example.seestock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView; // Import SearchView

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.ArrayList; // Import ArrayList

public class homepage extends AppCompatActivity {

    Button btntambahproduk, btndetail, button5, btnStockRecap;
    MyDatabaseHelper dbHelper;
    TextView textView;
    RecyclerView recyclerView; // Deklarasi RecyclerView
    ProdukAdapter adapter; // Deklarasi adapter
    SearchView searchView; // Deklarasi SearchView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage); // Pastikan Anda memiliki activity_homepage.xml dengan SearchView

        dbHelper = new MyDatabaseHelper(this);
        textView = findViewById(R.id.textView);
        button5 = findViewById(R.id.button5);
        btntambahproduk = findViewById(R.id.button);
        btnStockRecap = findViewById(R.id.btnStockRecap);
        recyclerView = findViewById(R.id.recyclerViewProduk); // Inisialisasi RecyclerView
        searchView = findViewById(R.id.searchView); // Inisialisasi SearchView (asumsi ID-nya searchView di homepage.xml)

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Produk> produkList = dbHelper.getAllProduk();
        adapter = new ProdukAdapter(this, produkList);
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

        btnStockRecap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homepage.this, StockRecapActivity.class);
                startActivity(intent);
            }
        });

        // Mengatur listener untuk SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Tidak menangani pengiriman query (saat tombol search ditekan)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Memfilter produk setiap kali teks berubah
                filterProduk(newText);
                return true;
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Metode untuk memfilter daftar produk berdasarkan teks pencarian.
     * @param query Teks pencarian dari SearchView.
     */
    private void filterProduk(String query) {
        List<Produk> filteredList = new ArrayList<>();
        List<Produk> allProduk = dbHelper.getAllProduk(); // Ambil semua produk dari database

        // Iterasi melalui semua produk dan tambahkan ke daftar yang difilter jika cocok
        for (Produk produk : allProduk) {
            // Pencarian tidak peka huruf besar/kecil pada nama produk atau deskripsi
            if (produk.name.toLowerCase().contains(query.toLowerCase()) ||
                    (produk.deskripsi != null && produk.deskripsi.toLowerCase().contains(query.toLowerCase()))) {
                filteredList.add(produk);
            }
        }
        // Perbarui adapter RecyclerView dengan daftar yang sudah difilter
        adapter.setProdukList(filteredList);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Memperbarui data produk setiap kali halaman kembali aktif
        // Ini juga akan mereset pencarian jika ada
        List<Produk> produkList = dbHelper.getAllProduk();
        adapter.setProdukList(produkList); // Gunakan setProdukList untuk memperbarui adapter yang sudah ada
    }
}
