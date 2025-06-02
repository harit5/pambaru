package com.example.seestock;

public class Produk {
    public int id;
    public String name;
    public double harga;
    public int stock;
    public byte[] foto_produk;
    public String deskripsi; // Menambahkan deskripsi
    public String deleteAt; // Menambahkan kolom deleteAt

    // Constructor asli
    public Produk(int id, String name, double harga, int stock, byte[] foto_produk) {
        this.id = id;
        this.name = name;
        this.harga = harga;
        this.stock = stock;
        this.foto_produk = foto_produk;
        this.deskripsi = null; // Default atau bisa diisi null
        this.deleteAt = null; // Default null
    }

    // Constructor baru dengan deskripsi dan deleteAt
    public Produk(int id, String name, double harga, int stock, byte[] foto_produk, String deskripsi, String deleteAt) {
        this.id = id;
        this.name = name;
        this.harga = harga;
        this.stock = stock;
        this.foto_produk = foto_produk;
        this.deskripsi = deskripsi;
        this.deleteAt = deleteAt;
    }

    public int getStock() {
        return stock;
    }

    // Tambahkan getter untuk deleteAt jika diperlukan
    public String getDeleteAt() {
        return deleteAt;
    }
}