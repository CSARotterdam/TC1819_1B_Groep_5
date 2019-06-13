package com.hr.techlabapp.Networking;

import com.hr.techlabapp.AppConfig;
import com.hr.techlabapp.Fragments.loginFragment;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class ProductItem {
    public Integer id;
    public String productId;

    public ProductItem(@Nullable Integer id, String productId) {
        this.id = id;
        this.productId = productId;
    }

    public static ArrayList<Integer> addProductItem(Product product)throws JSONException {
        return addProductItem(product.ID, 1);
    }

    public static ArrayList<Integer> addProductItem(String productID)throws JSONException {
        return addProductItem(productID, 1);
    }

    public static ArrayList<Integer> addProductItem(Product product, int count) throws JSONException {
        return addProductItem(product.ID, count);
    }

    public static ArrayList<Integer> addProductItem(String productID, int count) throws JSONException {
        //Create request
        JSONObject request = new JSONObject()
                .put("username", AppConfig.currentUser.username)
                .put("token", AppConfig.currentUser.token)
                .put("requestType", "addProductItem")
                .put("requestData", new JSONObject()
                        .put("productID", productID)
                        .put("count", count)
                );

        JSONArray array = (JSONArray)Connection.Send(request);
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i=0; i<array.length(); i++) {
            list.add( array.getInt(i) );
        }

        return list;
    }

    /**
     * Returns the items of the given product types.
     *
     * @param productIds An array of product id's whose items to return.
     * @param productIds An array containing the id's of the items to return.
     * @return A map containing a list of items per specified product.
     * @throws JSONException Shouldn't ever happen but whatever.
     */
    public static HashMap<String, List<ProductItem>> getProductItems(@Nullable String[] productIds,
                                                                     @Nullable Integer[] itemIds)
        throws JSONException {
        //Create JSON object
        JSONObject request = new JSONObject()
                .put("requestType", "getProductItems")
                .put("username", AppConfig.currentUser.username)
                .put("token", AppConfig.currentUser.token)
                .put("requestData", new JSONObject()
                        .put("products", productIds != null ? new JSONArray(Arrays.asList(productIds)) : null)
                        .put("itemIds", itemIds != null ? new JSONArray(Arrays.asList(itemIds)) : null)
                );

        JSONObject responseData = (JSONObject) Connection.Send(request);

        HashMap<String, List<ProductItem>> out = new HashMap<>();
        Iterator<String> itr = responseData.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            JSONArray items = responseData.getJSONArray(key);
            List<ProductItem> outSubset = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                outSubset.add(new ProductItem(
                        (Integer) items.opt(0),
                        key
                ));
            }
            out.put(key, outSubset);
        }

        return out;
    }
}
