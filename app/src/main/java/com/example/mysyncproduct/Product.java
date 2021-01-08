package com.example.mysyncproduct;

/**
 * Created by Quoc Nguyen on 13-Dec-16.
 */

public class Product {
    private int id;
    private String title;
    private String shortdesc;
    private int quantity;
    private double price;
    private String imagepath;
    private byte[] image;

    public Product(String name, double price, byte[] image, int id) {
        this.title = title;
        this.price = price;
        this.image = image;
        this.id = id;
    }

    public Product(int id, String title, String shortdesc, int quantity, double price, String imagepath) {
        this.id = id;
        this.title = title;
        this.shortdesc = shortdesc;
        this.quantity = quantity;
        this.price = price;
        this.imagepath = imagepath;
    }

    public Product(String title, String shortdesc, int quantity, double price, String imagepath, byte[] image, int id) {
        this.id = id;
        this.title = title;
        this.shortdesc = shortdesc;
        this.quantity = quantity;
        this.price = price;
        this.imagepath = imagepath;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortdesc() {
        return shortdesc;
    }

    public void setShortdesc(String shortdesc) {
        this.shortdesc = shortdesc;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
