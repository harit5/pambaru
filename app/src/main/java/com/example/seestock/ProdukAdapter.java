package com.example.seestock;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ViewHolder> {

    private Context context;
    private List<Produk> produkList;

    public ProdukAdapter(Context context, List<Produk> produkList) {
        this.context = context;
        this.produkList = produkList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_produk, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Produk produk = produkList.get(position);
        holder.nama.setText(produk.name);
        holder.harga.setText("Rp. " + (int) produk.harga);
        holder.stok.setText("Stok: " + produk.stock);

        if (produk.foto_produk != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(produk.foto_produk, 0, produk.foto_produk.length);
            holder.gambar.setImageBitmap(bitmap);
        } else {
            holder.gambar.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Klik seluruh item (buka detail produk)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, detailproduk.class);
            // Pass all necessary data, including the product ID
            intent.putExtra("id", String.valueOf(produk.id));

            MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public void setProdukList(List<Produk> newProdukList) {
        this.produkList = newProdukList;
        notifyDataSetChanged(); // Important to refresh the RecyclerView
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama, harga, stok;
        ImageView gambar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.txtNamaProduk);
            harga = itemView.findViewById(R.id.txtHargaProduk);
            stok = itemView.findViewById(R.id.txtStokProduk);
            gambar = itemView.findViewById(R.id.imageViewProduk);
        }
    }
}