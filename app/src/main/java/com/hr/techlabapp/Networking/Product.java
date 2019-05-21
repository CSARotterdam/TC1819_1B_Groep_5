package com.hr.techlabapp.Networking;

import android.graphics.Bitmap;
import android.support.v4.util.ArrayMap;
import android.util.Base64;
import android.util.Log;

import com.hr.techlabapp.Fragments.AddProductFragment;
import com.hr.techlabapp.Fragments.loginFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.in;

public final class Product {
    public String id;
    public String manufacturer;
    public String category;
    public String name;
    public String image;

    public Product(String id, String manufacturer, String category, String name, String image) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.category = category;
        this.name = name;
        this.image = image;
    }

    protected Map<String, Object> getValues() {
        Map<String, Object> out = new ArrayMap<>();
        out.put("id", id);
        out.put("manufacturer", manufacturer);
        out.put("category", category);
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

    public static void AddProduct(String productID, String categoryID, String manufacturer, HashMap<String, String> name) throws
            JSONException,
            Exceptions.MissingArgumentException,
            Exceptions.AccessDeniedException,
            Exceptions.UnexpectedServerResponseException,
            Exceptions.AlreadyExistsException,
            Exceptions.NoSuchProductCategoryException
    {
        AddProduct(productID, categoryID, manufacturer, name, null);
    }
    public static void AddProduct(String productID, String categoryID, String manufacturer, HashMap<String, String> name, Bitmap image) throws
            JSONException,
            Exceptions.MissingArgumentException,
            Exceptions.AccessDeniedException,
            Exceptions.UnexpectedServerResponseException,
            Exceptions.AlreadyExistsException,
            Exceptions.NoSuchProductCategoryException
    {

        String encodedImage;
        if(image != null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            encodedImage = null;
        }

        if(!name.containsKey("en")){
            throw new Exceptions.MissingArgumentException("Product name requires at minimum an English translation.");
        }

        //Create base request
        JSONObject request = new JSONObject()
            .put("username", loginFragment.currentUser.username)
            .put("token", loginFragment.currentUser.token)
            .put("requestType", "addProduct")
            .put("requestData", new JSONObject()
                .put("productID", productID)
                .put("categoryID", categoryID)
                .put("manufacturer", manufacturer)
                .put("image", encodedImage)
                .put("name", new JSONObject(name))
            );
        Log.i("test", request.toString());

        JSONObject response = Connection.Send(request);
        JSONObject requestData = (JSONObject) response.get("requestData");
        Log.i("test", requestData.toString());
        Boolean success = requestData.getBoolean("success");
        String reason = requestData.getString("reason");

        if(reason.equals("AlreadyExists")) {
            throw new Exceptions.AlreadyExistsException();
        } else if(reason.equals("AccessDenied")){
            throw new Exceptions.AccessDeniedException();
        } else if(reason.equals("NoSuchProductCategory")) {
            throw new Exceptions.NoSuchProductCategoryException();
        } else if(reason.equals("MissingArguments")){
            throw  new Exceptions.MissingArgumentException();
        } else if(!success && reason != null){
            throw new Exceptions.UnexpectedServerResponseException();
        }
    }
}