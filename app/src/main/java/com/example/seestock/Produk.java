package com.example.seestock;

public class Produk {
    public int id;
    public String name;
    public double harga;
    public int stock;
    public byte[] foto_produk;

    public Produk(int id, String name, double harga, int stock, byte[] foto_produk) {
        this.id = id;
        this.name = name;
        this.harga = harga;
        this.stock = stock;
        this.foto_produk = foto_produk;
    }

    public int getStock() {
        return stock;
    }


}
