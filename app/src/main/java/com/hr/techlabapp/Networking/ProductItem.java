package com.hr.techlabapp.Networking;

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

    /**
     * Returns the items of the given product types.
     *
     * @param productIds An array of product id's whose items to return.
     * @return A map containing a list of items per specified product.
     * @throws JSONException
     */
    public static HashMap<String, List<ProductItem>> getProductItems(String... productIds)
        throws JSONException {
        //Create JSON object
        JSONObject request = new JSONObject()
                .put("requestType", "getProductItems")
                .put("username", loginFragment.currentUser.username)
                .put("token", loginFragment.currentUser.token)
                .put("requestData", new JSONObject()
                        .put("products", new JSONArray(Arrays.asList(productIds)))
                );

        JSONObject responseData = (JSONObject) Connection.Send(request);

        HashMap<String, List<ProductItem>> out = new HashMap<>();
        Iterator<String> itr = responseData.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            JSONArray items = ((JSONObject)responseData).getJSONArray(key);
            List<ProductItem> outSubset = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = (JSONObject) items.get(i);
                outSubset.add(new ProductItem(
                        (Integer) item.opt("id"),
                        (String) item.opt("product")
                ));
            }
            out.put(key, outSubset);
        }

        return out;
    }
}
