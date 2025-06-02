// ProdukAdapter.java
package com.example.seestock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton; // Import ImageButton
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ViewHolder> {

    private Context context;
    private List<Produk> produkList;
    private MyDatabaseHelper dbHelper; // Add database helper instance

    public ProdukAdapter(Context context, List<Produk> produkList) {
        this.context = context;
        this.produkList = produkList;
        this.dbHelper = new MyDatabaseHelper(context); // Initialize dbHelper
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

            // MyDatabaseHelper dbHelper = new MyDatabaseHelper(context); // Not needed here, already initialized
            context.startActivity(intent);
        });

        // Set OnClickListener for the delete button
        holder.btnDelete.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Produk")
                    .setMessage("Apakah Anda yakin ingin menghapus produk ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        // User confirmed deletion
                        boolean isDeleted = dbHelper.softDeleteProduct(produk.id);
                        if (isDeleted) {
                            Toast.makeText(context, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show();
                            // Remove item from list and notify adapter
                            produkList.remove(position);
                            notifyItemRemoved(position);
                            // It's good practice to also notify about range changed if positions shift
                            notifyItemRangeChanged(position, produkList.size());
                        } else {
                            Toast.makeText(context, "Gagal menghapus produk", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Tidak", (dialog, which) -> {
                        // User cancelled deletion
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public void setProdukList(List<Produk> newProdukList) {
        this.produkList.clear(); // Clear existing data
        this.produkList.addAll(newProdukList); // Add new data
        notifyDataSetChanged(); // Important to refresh the RecyclerView
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama, harga, stok;
        ImageView gambar;
        ImageButton btnDelete; // Declare ImageButton

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.txtNamaProduk);
            harga = itemView.findViewById(R.id.txtHargaProduk);
            stok = itemView.findViewById(R.id.txtStokProduk);
            gambar = itemView.findViewById(R.id.imageViewProduk);
            btnDelete = itemView.findViewById(R.id.btnDelete); // Initialize ImageButton
        }
    }
}