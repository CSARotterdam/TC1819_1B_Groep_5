package com.hr.techlabapp.Networking;

import android.support.v4.util.ArrayMap;

import com.hr.techlabapp.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static List<Product> GetProducts(int amount) throws JSONException {
        JSONObject json = new JSONObject()
                .put("username", MainActivity.currentUser.username)
                .put("token", MainActivity.currentUser.token)
                .put("requestType", "getProductList")
                .put("requestData", new JSONObject()
                        .put("criteria", new JSONObject()));
        JSONObject response = Connection.Send(json);
        System.out.println(response);
        JSONArray result = (JSONArray) (((JSONObject)response.get("requestData")).get("foundProducts"));
        List<Product> out = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            JSONArray values = ((JSONArray) result.get(i));
            out.add(new Product(
                    (String) values.get(0),
                    (String) values.get(1),
                    (String) values.get(2),
                    (String) values.get(3),
                    (String) values.get(4)
            ));
        }
        return out;
    }
}