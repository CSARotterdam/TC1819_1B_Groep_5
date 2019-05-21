package com.hr.techlabapp.Networking;

import android.graphics.Bitmap;
import android.support.v4.util.ArrayMap;
import android.util.Base64;
import android.util.Log;

import com.hr.techlabapp.Fragments.AddProductFragment;
import com.hr.techlabapp.Fragments.loginFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.in;

public final class Product {
    public String productID;
    private String productIDCopy;
    public String manufacturer;
    public String categoryID;
    public HashMap<String, String> name;
    public Bitmap image;

    public Product(String productID, String manufacturer, HashMap<String, String> name){
        this(productID, manufacturer, "uncategorized", name, null);
    }

    public Product(String productID, String manufacturer, String categoryID, HashMap<String, String> name){
        this(productID, manufacturer, categoryID, name, null);
    }

    public Product(String productID, String manufacturer, String categoryID, HashMap<String, String> name, Bitmap image) {
        this.productID = productID;
        this.productIDCopy = productID;
        this.manufacturer = manufacturer;
        this.categoryID = categoryID;
        this.name = name;
        this.image = image;
    }

    protected Map<String, Object> getValues() {
        Map<String, Object> out = new ArrayMap<>();
        out.put("id", productID);
        out.put("manufacturer", manufacturer);
        out.put("category", categoryID);
        out.put("name", name);
        out.put("image", image);
        return out;
    }

    public static List<Product> GetProducts() throws JSONException {
        //Create JSON object
        JSONObject critera = new JSONObject();
        JSONObject request = new JSONObject()
            .put("requestType", "registerUser")
            .put("requestData", new JSONObject()
                .put("criteria", critera)
            );

        return null;
    }

    public static void AddProduct(Product product) throws JSONException
    {
        Object encodedImage;
        if(product.image != null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            product.image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            encodedImage = null;
        }

        if(!product.name.containsKey("en")){
            throw new Exceptions.MissingArgument("Product name requires at minimum an English translation.");
        }

        //Create request
        JSONObject request = new JSONObject()
            .put("username", loginFragment.currentUser.username)
            .put("token", loginFragment.currentUser.token)
            .put("requestType", "addProduct")
            .put("requestData", new JSONObject()
                .put("productID", product.productID)
                .put("categoryID", product.categoryID)
                .put("manufacturer", product.manufacturer)
                .put("image", encodedImage)
                .put("name", new JSONObject(product.name))
            );

        Connection.Send(request);
    }

    public static void deleteProduct(String productID) throws JSONException{
        //Create request
        JSONObject request = new JSONObject()
            .put("username", loginFragment.currentUser.username)
            .put("token", loginFragment.currentUser.token)
            .put("requestType", "deleteProduct")
            .put("requestData", new JSONObject()
                .put("productID", productID)
            );

        Connection.Send(request);
    }

    public static void updateProduct(Product product) throws JSONException{
        Object encodedImage;
        if(product.image != null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            product.image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            encodedImage = null;
        }

        //Create request
        JSONObject request = new JSONObject()
            .put("username", loginFragment.currentUser.username)
            .put("token", loginFragment.currentUser.token)
            .put("requestType", "updateProduct")
            .put("requestData", new JSONObject()
                .put("productID", product.productIDCopy)
                .put("newProductID", product.productID)
                .put("categoryID", product.categoryID)
                .put("manufacturer", product.manufacturer)
                .put("image", encodedImage)
                .put("name", new JSONObject(product.name))
            );
        Connection.Send(request);
    }
}