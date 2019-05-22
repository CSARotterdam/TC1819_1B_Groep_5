package com.hr.techlabapp.Networking;

import android.graphics.Bitmap;
import android.support.v4.util.ArrayMap;
import android.util.Base64;

import com.hr.techlabapp.Fragments.loginFragment;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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



    public static List<Product> GetProducts(@Nullable HashMap<String, String> criteria,
                                            @Nullable String[] languages)
            throws JSONException {
        return GetProducts(null, criteria, languages, null, null);
    }

    public static List<Product> GetProducts(@Nullable String[] fields,
                                            @Nullable HashMap<String, String> criteria,
                                            @Nullable String[] languages,
                                            @Nullable Integer start,
                                            @Nullable Integer amount)
            throws JSONException {
        //Create JSON object
        JSONObject requestCriteria = null;
        if (criteria != null) {
            requestCriteria = new JSONObject();
            for (HashMap.Entry<String, String> entry : criteria.entrySet())
                requestCriteria.put(entry.getKey(), entry.getValue());
        }
        JSONObject request = new JSONObject()
                .put("requestType", "getProducts")
                .put("username", loginFragment.currentUser.username)
                .put("token", loginFragment.currentUser.token)
                .put("requestData", new JSONObject()
                        .put("columns", fields)
                        .put("criteria", requestCriteria)
                        .put("language", languages == null ? null : new JSONArray(Arrays.asList(languages)))
                        .put("start", start)
                        .put("amount", amount)
                );

        JSONArray responseData = (JSONArray) Connection.Send(request);

        List<Product> out = new ArrayList<>();
        for (int i = 0; i < responseData.length(); i++) {
            JSONObject product = (JSONObject) responseData.get(i);
            HashMap<String, String> name = null;
            if (product.has("name")) {
                name = new HashMap<>();
                Iterator<String> itr = ((JSONObject)product.get("name")).keys();
                while (itr.hasNext()) {
                    String key = itr.next();
                    name.put(key, ((JSONObject)product.get("name")).getString(key));
                }
            }
            out.add(new Product(
                    (String) product.opt("id"),
                    (String) product.opt("manufacturer"),
                    (String) product.opt("category"),
                    name
            ));
        }

        return out;
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