// homepage.java
package com.example.seestock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences; // Import SharedPreferences
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AlertDialog;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.ArrayList;

public class homepage extends AppCompatActivity {

    Button btntambahproduk, btndetail, button5, btnStockRecap;
    MyDatabaseHelper dbHelper;
    TextView textView;
    RecyclerView recyclerView;
    ProdukAdapter adapter;
    SearchView searchView;
    ImageView logout;

    private int currentUserId = -1; // Variabel untuk menyimpan ID pengguna yang sedang login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        dbHelper = new MyDatabaseHelper(this);
        textView = findViewById(R.id.textView);
        button5 = findViewById(R.id.button5);
        btntambahproduk = findViewById(R.id.button);
        btnStockRecap = findViewById(R.id.btnStockRecap);
        recyclerView = findViewById(R.id.recyclerViewProduk);
        searchView = findViewById(R.id.searchView);
        logout = findViewById(R.id.imageView);

        // *** Ambil userId dari SharedPreferences ***
        SharedPreferences sharedPref = getSharedPreferences(loginpage.PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPref.getInt(loginpage.PREF_USER_ID, -1); // -1 adalah nilai default jika tidak ditemukan

        if (currentUserId == -1) {
            // Jika userId tidak ditemukan (belum login), arahkan kembali ke halaman login
            Toast.makeText(this, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(homepage.this, loginpage.class);
            startActivity(intent);
            finish();
            return; // Penting untuk menghentikan eksekusi lebih lanjut
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // *** Panggil getAllProduk dengan userId ***
        List<Produk> produkList = dbHelper.getAllProduk(currentUserId);
        adapter = new ProdukAdapter(this, produkList);
        recyclerView.setAdapter(adapter);

        btntambahproduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // *** Teruskan userId ke addproduct activity ***
                Intent tambahproduk = new Intent(homepage.this, addproduct.class);
                tambahproduk.putExtra("userId", currentUserId); // Kirim userId
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
                // *** Teruskan userId ke StockRecapActivity ***
                Intent intent = new Intent(homepage.this, StockRecapActivity.class);
                intent.putExtra("userId", currentUserId); // Kirim userId
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProduk(newText);
                return true;
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(homepage.this)
                        .setTitle("Konfirmasi Logout")
                        .setMessage("Apakah Anda yakin ingin logout?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // *** Hapus userId dari SharedPreferences saat logout ***
                                SharedPreferences sharedPref = getSharedPreferences(loginpage.PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.remove(loginpage.PREF_USER_ID);
                                editor.apply();

                                Intent intent = new Intent(homepage.this, loginpage.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void filterProduk(String query) {
        List<Produk> filteredList = new ArrayList<>();
        // *** Panggil getAllProduk dengan userId saat memfilter ***
        List<Produk> allProduk = dbHelper.getAllProduk(currentUserId);

        for (Produk produk : allProduk) {
            if (produk.name.toLowerCase().contains(query.toLowerCase()) ||
                    (produk.deskripsi != null && produk.deskripsi.toLowerCase().contains(query.toLowerCase()))) {
                filteredList.add(produk);
            }
        }
        adapter.setProdukList(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // *** Pastikan userId masih valid sebelum memuat data ***
        SharedPreferences sharedPref = getSharedPreferences(loginpage.PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPref.getInt(loginpage.PREF_USER_ID, -1);

        if (currentUserId != -1) {
            // Memperbarui data produk setiap kali halaman kembali aktif
            List<Produk> produkList = dbHelper.getAllProduk(currentUserId);
            adapter.setProdukList(produkList);
        } else {
            // Jika userId tidak valid, arahkan kembali ke login
            Intent intent = new Intent(homepage.this, loginpage.class);
            startActivity(intent);
            finish();
        }
    }
}