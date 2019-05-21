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

public final class ProductCategory {
    public String categoryID;
    private String categoryIDCopy;
    public HashMap<String, String> name;

    public ProductCategory(String categoryID, HashMap<String, String> name) {
        this.categoryID = categoryID;
        this.categoryIDCopy = categoryID;
        this.name = name;
    }

    protected Map<String, Object> getValues() {
        Map<String, Object> out = new ArrayMap<>();
        out.put("categoryID", categoryID);
        out.put("name", name);
        return out;
    }

    public static List<Product> getProductCategories() throws JSONException {
        //Create JSON object
        JSONObject critera = new JSONObject();
        JSONObject request = new JSONObject()
                .put("requestType", "getProductCategories")
                .put("requestData", new JSONObject()
                        .put("criteria", critera)
                );

        return null;
    }

    public static void addProductCategory(ProductCategory category)  throws JSONException, Exceptions.MissingArgument {
        if (!category.name.containsKey("en")) {
            throw new Exceptions.MissingArgument("Product name requires at minimum an English translation.");
        }

        //Create request
        JSONObject request = new JSONObject()
                .put("username", loginFragment.currentUser.username)
                .put("token", loginFragment.currentUser.token)
                .put("requestType", "addProductCategory")
                .put("requestData", new JSONObject()
                        .put("categoryID", category.categoryID)
                        .put("name", new JSONObject(category.name))
                );

        JSONObject response = Connection.Send(request);
    }

    public static void deleteProductCategory(String categoryID) throws JSONException {
        //Create request
        JSONObject request = new JSONObject()
                .put("username", loginFragment.currentUser.username)
                .put("token", loginFragment.currentUser.token)
                .put("requestType", "deleteProductCategory")
                .put("requestData", new JSONObject()
                        .put("categoryID", categoryID)
                );

        Connection.Send(request);
    }

    public static void updateProductCategory(ProductCategory category) throws JSONException{
        //Create request
        JSONObject request = new JSONObject()
                .put("username", loginFragment.currentUser.username)
                .put("token", loginFragment.currentUser.token)
                .put("requestType", "updateProductCategory")
                .put("requestData", new JSONObject()
                        .put("categoryID", category.categoryID)
                        .put("name", new JSONObject(category.name))
                );

        Connection.Send(request);
    }
}