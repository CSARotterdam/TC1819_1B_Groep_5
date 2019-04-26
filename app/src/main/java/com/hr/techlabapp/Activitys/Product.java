package com.hr.techlabapp.Activitys;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.Arrays;

// a class to hold all the product data
public class Product {
    private String Name;
    private String Description;
    private String Manufacturer;
    private String ProductID;
    private String ProductCategory;
    private int ProductCount;
    private int ProductsAvailable;
    private Bitmap Image;

    public Product(String Name, String Description, String Manufacturer, String ProductID, String ProductCategory,
            int ProductCount, int ProductsAvailable) {
        this.Name = Name;
        this.Description = Description;
        this.Manufacturer = Manufacturer;
        this.ProductID = ProductID;
        this.ProductCategory = ProductCategory;
        this.ProductCount = ProductCount;
        this.ProductsAvailable = ProductsAvailable;
    }

    public String getName() {
        return this.Name;
    }

    public String getDescription() {
        return this.Description;
    }

    public String getManufacturer() {
        return this.Manufacturer;
    }

    public String getProductID() {
        return this.ProductID;
    }

    public String getProductCategory() {
        return this.ProductCategory;
    }

    public int getProductCount() {
        return this.ProductCount;
    }

    public int getProductsAvailable() {
        return this.ProductsAvailable;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        this.Image = image;
    }

    // sets drawable to an bitmap and saves it
    public void setImage(Drawable image) {
        // the easy way
        if (image instanceof BitmapDrawable) {
            this.Image = ((BitmapDrawable) image).getBitmap();
            return;
        }

        // the hard way
        Bitmap bitmap = null;
        // checks if it has actual values and makes a bitmap accordingly
        if (image.getIntrinsicWidth() <= 0 || image.getIntrinsicHeight() <= 0)
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        else
            bitmap = Bitmap.createBitmap(image.getIntrinsicWidth(), image.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
        // makes an canvas with the bitmap as image
        Canvas canvas = new Canvas(bitmap);
        // makes image as big as the canvas
        image.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        // draws the canvas with image and in turn on bitmap
        image.draw(canvas);
        // saves the bitmap
        this.Image = bitmap;
    }
}