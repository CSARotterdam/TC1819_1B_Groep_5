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

    public static List<ProductItem> GetProductItems(String productId) throws JSONException {
        HashMap<String, String> criteria = new HashMap<>();
        criteria.put("product", productId);
        return GetProductItems(null, criteria, null, null);
    }

    public static List<ProductItem> GetProductItems(@Nullable String[] fields,
                                                    @Nullable HashMap<String, String> criteria,
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
                .put("requestType", "getProductItems")
                .put("username", loginFragment.currentUser.username)
                .put("token", loginFragment.currentUser.token)
                .put("requestData", new JSONObject()
                        .put("columns", fields)
                        .put("criteria", requestCriteria)
                        .put("start", start)
                        .put("amount", amount)
                );

        JSONArray responseData = (JSONArray) Connection.Send(request);

        List<ProductItem> out = new ArrayList<>();
        for (int i = 0; i < responseData.length(); i++) {
            JSONObject item = (JSONObject) responseData.get(i);
            out.add(new ProductItem(
                    (Integer) item.opt("id"),
                    (String) item.opt("product")
            ));
        }

        return out;
    }
}
