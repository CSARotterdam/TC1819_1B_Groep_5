package com.hr.techlabapp.Networking;

import android.util.Log;

import com.hr.techlabapp.AppConfig;
import com.hr.techlabapp.Fragments.loginFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Statistics {

    /**
     * Returns integers based on the specified product's availability. Values included;
     * total        -   The total amount of ProductItems,
     * reservations -   The total amount of open reservations for this item,
     * loanedOut    -   The total amount of ProductItems that have been loaned out,
     * inStock      -   The total amount of ProductItems that are currently in stock
     * @param productID The ID of the product that you want to retrieve the information for.
     * @return A hashmap of hashmaps, which each contain information about an individual product. The product IDs are used as keys.
     */
    public static HashMap<String, HashMap<String, Integer>> getProductAvailability(String productID) throws JSONException{
        ArrayList<String> list = new ArrayList<>();
        list.add(productID);
        return getProductAvailability(list);
    }

    /**
     * Returns integers based on the specified product's availability. Values included;
     * total        -   The total amount of ProductItems,
     * reservations -   The total amount of open reservations for this item,
     * loanedOut    -   The total amount of ProductItems that have been loaned out,
     * inStock      -   The total amount of ProductItems that are currently in stock
     * @param product The product object that you want to retrieve information for.
     * @return A hashmap of hashmaps, which each contain information about an individual product. The product IDs are used as keys.
     */
    public static HashMap<String, HashMap<String, Integer>> getProductAvailability(Product product) throws JSONException{
        ArrayList<String> list = new ArrayList<>();
        list.add(product.ID);
        return getProductAvailability(list);
    }

    /**
     * Returns integers based on the specified product's availability. Values included;
     * total        -   The total amount of ProductItems,
     * reservations -   The total amount of open reservations for this item,
     * loanedOut    -   The total amount of ProductItems that have been loaned out,
     * inStock      -   The total amount of ProductItems that are currently in stock
     * @param productIDs An ArrayList of product IDs that you want to retrieve information for.
     * @return A hashmap of hashmaps, which each contain information about an individual product. The product IDs are used as keys.
     */
    public static HashMap<String, HashMap<String, Integer>> getProductAvailability(ArrayList<String> productIDs) throws JSONException {
        //Create JSON object
        JSONObject request;
        request = new JSONObject()
            .put("username", AppConfig.currentUser.username)
            .put("token", AppConfig.currentUser.token)
            .put("requestType", "getProductAvailability")
            .put("requestData", new JSONObject()
                .put("products", new JSONArray(productIDs)));

        JSONObject response = (JSONObject) Connection.Send(request);

        //Convert JSONObject to hashmap
        HashMap<String, HashMap<String, Integer>> result = new HashMap<>();
        Iterator<String> keys = response.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            HashMap<String, Integer> entry = new HashMap<>();
            JSONObject entryObject = response.getJSONObject(key);
            entry.put("total", entryObject.getInt("total"));
            entry.put("reservations", entryObject.getInt("reservations"));
            entry.put("loanedOut", entryObject.getInt("loanedOut"));
            entry.put("inStock", entryObject.getInt("inStock"));
            result.put(key, entry);
        }
        return result;
    }
}
