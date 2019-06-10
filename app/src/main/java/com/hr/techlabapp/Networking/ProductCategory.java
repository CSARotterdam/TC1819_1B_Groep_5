package com.hr.techlabapp.Networking;

import com.hr.techlabapp.AppConfig;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class ProductCategory {
    public String categoryID;
    private String categoryIDCopy;
    public HashMap<String, String> name;

    public ProductCategory(String categoryID, HashMap<String, String> name) {
        this.categoryID = categoryID;
        this.categoryIDCopy = categoryID;
        this.name = name;
    }

    /**
     * Returns the name of this product using the language specified in AppConfig
     */
    public String getName() {
        return getName(AppConfig.language);
    }

    /**
     * Returns the name of this product in the desired language.
     *
     * Fallback order is 'param > English > name_id > empty string'
     * @param language The desired translation to get.
     */
    public String getName(@Nullable String language) {
        if (name.containsKey(language)) return name.get(language);
        else if (name.containsKey("en")) return name.get("en"); // Primary fallback is english
        else if (name.containsKey("id")) return name.get("id"); // Secondary fallback is to id. (if present)
        return ""; // Final fallback is blank
    }

    /**
     * Gets all product categories.
     * @param languages A string array of language codes of the languages to return. If null, the
     *                  id of the translation will be returned instead. If the array is empty,
     *                  all translations will be returned.
     * @return A list of product categories.
     * @throws JSONException
     */
    public static List<ProductCategory> getProductCategories(@Nullable String[] languages) throws JSONException {
        return getProductCategories(null, null, languages, null, null);
    }

    /**
     * Returns a list of product categories based on the given parameters.
     *
     * @param fields An array containing the names of the fields to return.
     * @param criteria A map of field names and conditions.
     * @param languages A string array of language codes of the languages to return. If null, the
     *                  id of the translation will be returned instead. If the array is empty,
     *                  all translations will be returned.
     * @param start The amount of potential results to skip before returning anything.
     * @param amount The amount of results to return.
     * @return A list of results. Length may be lower than 'amount'.
     * @throws JSONException
     */
    public static List<ProductCategory> getProductCategories(@Nullable String[] fields,
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
                .put("requestType", "getProductCategories")
                .put("username", AppConfig.currentUser.username)
                .put("token", AppConfig.currentUser.token)
                .put("requestData", new JSONObject()
                        .put("columns", fields)
                        .put("criteria", requestCriteria)
                        .put("language", languages == null ? null : new JSONArray(Arrays.asList(languages)))
                        .put("start", start)
                        .put("amount", amount)
                );

        JSONArray responseData = (JSONArray) Connection.Send(request);

        List<ProductCategory> out = new ArrayList<>();
        for (int i = 0; i < responseData.length(); i++) {
            JSONObject product = (JSONObject) responseData.get(i);
            HashMap<String, String> name = null;
            if (product.has("name") && languages != null) {
                name = new HashMap<>();
                Iterator<String> itr = ((JSONObject)product.get("name")).keys();
                while (itr.hasNext()) {
                    String key = itr.next();
                    name.put(key, ((JSONObject)product.get("name")).getString(key));
                }
            }
            out.add(new ProductCategory(
                    (String) product.opt("id"),
                    name
            ));
        }

        return out;
    }

    public static void addProductCategory(ProductCategory category)  throws JSONException {
        if (!category.name.containsKey("en")) {
            throw new Exceptions.MissingArguments("Product name requires at minimum an English translation.");
        }

        //Create request
        JSONObject request = new JSONObject()
                .put("username", AppConfig.currentUser.username)
                .put("token", AppConfig.currentUser.token)
                .put("requestType", "addProductCategory")
                .put("requestData", new JSONObject()
                        .put("categoryID", category.categoryID)
                        .put("name", new JSONObject(category.name))
                );
        Connection.Send(request);
    }

    public static void deleteProductCategory(String categoryID) throws JSONException {
        //Create request
        JSONObject request = new JSONObject()
                .put("username", AppConfig.currentUser.username)
                .put("token", AppConfig.currentUser.token)
                .put("requestType", "deleteProductCategory")
                .put("requestData", new JSONObject()
                        .put("categoryID", categoryID)
                );

        Connection.Send(request);
    }

    public static void updateProductCategory(ProductCategory category) throws JSONException{
        //Create request
        JSONObject request = new JSONObject()
                .put("username", AppConfig.currentUser.username)
                .put("token", AppConfig.currentUser.token)
                .put("requestType", "updateProductCategory")
                .put("requestData", new JSONObject()
                        .put("categoryID", category.categoryID)
                        .put("newCategoryID", category.categoryIDCopy)
                        .put("name", new JSONObject(category.name))
                );

        Connection.Send(request);
    }
}