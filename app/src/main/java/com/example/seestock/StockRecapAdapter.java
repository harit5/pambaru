package com.example.seestock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockRecapAdapter extends RecyclerView.Adapter<StockRecapAdapter.ViewHolder> {

    private Context context;
    private List<Produk> produkList;

    public StockRecapAdapter(Context context, List<Produk> produkList) {
        this.context = context;
        this.produkList = produkList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_produk_recap, parent, false);
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

        // Set status produk berdasarkan kolom deleteAt
        if (produk.deleteAt != null) {
            holder.status.setText("Status: Dihapus (" + produk.deleteAt.substring(0, 10) + ")"); // Menampilkan tanggal dihapus
            holder.status.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            // Optional: Mengubah tampilan item jika produk dihapus, misalnya warna latar belakang
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        } else {
            holder.status.setText("Status: Aktif");
            holder.status.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white)); // Atur kembali warna normal
        }
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public void setProdukList(List<Produk> newProdukList) {
        this.produkList = newProdukList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama, harga, stok, status;
        ImageView gambar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.txtNamaProdukRecap);
            harga = itemView.findViewById(R.id.txtHargaProdukRecap);
            stok = itemView.findViewById(R.id.txtStokProdukRecap);
            gambar = itemView.findViewById(R.id.imageViewProdukRecap);
            status = itemView.findViewById(R.id.txtStatusProdukRecap); // Inisialisasi TextView status
        }
    }
}