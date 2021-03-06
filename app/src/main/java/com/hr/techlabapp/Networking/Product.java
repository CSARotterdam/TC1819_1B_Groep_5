package com.hr.techlabapp.Networking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.hr.techlabapp.AppConfig;

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
    public String ID;
    public String productIDCopy;
    public String manufacturer;
    public String categoryID;
    public HashMap<String, String> name;
    public HashMap<String, String> description;
    public Bitmap image;
    public String imageId;

    public Product(String ID, String manufacturer, String categoryID){
        this(ID, manufacturer, categoryID, null, null, null);
    }

    public Product(String ID, String manufacturer, String categoryID, HashMap<String, String> name){
        this(ID, manufacturer, categoryID, name, null, null);
    }

    public Product(String ID, String manufacturer, String categoryID, HashMap<String, String> name, HashMap<String, String> description){
        this(ID, manufacturer, categoryID, name, description, null);
    }

    public Product(String ID, String manufacturer, String categoryID, HashMap<String, String> name, HashMap<String, String> description, String imageID){
        this(ID, manufacturer, categoryID, name, description, imageID, null);
    }

    public Product(String ID, String manufacturer, String categoryID, HashMap<String, String> name, HashMap<String, String> description, String imageID, Bitmap image) {
        this.ID = ID;
        this.productIDCopy = ID;
        this.manufacturer = manufacturer;
        this.categoryID = categoryID;
        this.name = name;
        this.image = image;
        this.imageId = imageID;
        this.description = description;
    }

    /**
     * Retrieves this product's image from the server. This image is also automatically stored
     * in the product's image property.
     * @return The image, as bitmap
     * @throws JSONException Shouldn't happen.
     */
    public Bitmap getImage() throws JSONException{
        ArrayList<String> IDs = new ArrayList<>();
        IDs.add(imageId);
        Bitmap img = getImages(imageId);
        if(img == null){
            img = getImages("default");
        }
        image = img;
        return image;
    }

    public static Bitmap getImages(String ID) throws JSONException{
        ArrayList IDs = new ArrayList();
        IDs.add(ID);
        return (Bitmap)getImages(IDs).get(ID);
    }

    public static HashMap<String, Bitmap> getImages(ArrayList<String> IDs) throws JSONException{
        JSONArray images = new JSONArray(IDs);
        JSONArray columns = new JSONArray();
        columns.put("data");
        JSONObject request = new JSONObject()
                .put("username", AppConfig.currentUser.username)
                .put("token", AppConfig.currentUser.token)
                .put("requestType", "getImages")
                .put("requestData", new JSONObject()
                        .put("images", images)
                        .put("columns", columns)
                );

        HashMap<String, Bitmap> result = new HashMap<>();
        JSONObject responseData = (JSONObject) Connection.Send(request);
        Iterator<String> itr = responseData.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            JSONObject imageData = responseData.getJSONObject(key);
            String encodedImage = (String) imageData.get("data");
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            result.put(key, image);
        }

        return result;
    }

    public String getName() {
        return getName(AppConfig.getLanguage());
    }
    public String getName(@Nullable String language) {
        if (name.containsKey(language)) return name.get(language);
        else if (name.containsKey("en")) return name.get("en"); // Primary fallback is english
        else if (name.containsKey("id")) return name.get("id"); // Secondary fallback is to id. (if present)
        return ""; // Final fallback is blank
    }

    public String getDescription() {
        return getDescription(AppConfig.getLanguage());
    }
    public String getDescription(@Nullable String language) {
        if (description.containsKey(language)) return description.get(language);
        else if (description.containsKey("en")) return description.get("en"); // Primary fallback is english
        else if (description.containsKey("id")) return description.get("id"); // Secondary fallback is to id. (if present)
        return ""; // Final fallback is blank
    }

    /**
     * Gets all products matching the given criteria.
     * @param criteria A map of field names and conditions.
     * @param languages A string array of language codes of the languages to return. If null, the
     *                  id of the translation will be returned instead. If the array is empty,
     *                  all translations will be returned.
     * @return A list of products.
     * @throws JSONException
     */
    public static List<Product> getProducts(@Nullable HashMap<String, String> criteria,
                                            @Nullable String[] languages)
            throws JSONException {
        return getProducts(null, criteria, languages, null, null);
    }

    /**
     * Returns a list of products based on the given parameters.
     *
     * @param fields An array containing the names of the fields to return.
     * @param criteria A map of field names and conditions.
     * @param languages A string array of language codes of the languages to return. If null, the
     *                  id of the translation will be returned instead. If the array is empty,
     *                  all translations will be returned.
     * @param start The amount of potential results to skip before returning anything.
     * @param amount The amount of results to return.
     * @return A list of results. Length may be lower than 'amount'.
     * @throws JSONException Shouldn't happen.
     */
    public static List<Product> getProducts(@Nullable String[] fields,
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

        // Force inclusion of the primary fallback "en"
        if(!Arrays.asList(languages).contains("en")) {
            List<String> temp = new ArrayList<>(Arrays.asList(languages));
            temp.add("en");
            languages = temp.toArray(languages);
        }

        JSONObject request = new JSONObject()
                .put("requestType", "getProducts")
                .put("username", AppConfig.currentUser.username)
                .put("token", AppConfig.currentUser.token)
                .put("requestData", new JSONObject()
                        .put("columns", fields)
                        .put("criteria", requestCriteria)
                        .put("language", languages == null ? new JSONArray() : new JSONArray(Arrays.asList(languages)))
                        .put("start", start)
                        .put("amount", amount)
                );

        JSONArray responseData = (JSONArray) Connection.Send(request);

        List<Product> out = new ArrayList<>();
        for (int i = 0; i < responseData.length(); i++) {
            JSONObject product = (JSONObject) responseData.get(i);
            HashMap<String, String> name = new HashMap<>();
            // Create a dictionary and fill it with the proper language data for name
            if (product.has("name") && languages != null) {
                Iterator<String> itr = ((JSONObject)product.get("name")).keys();
                while (itr.hasNext()) {
                    String key = itr.next();
                    String value = ((JSONObject)product.get("name")).getString(key);
                    if(value.equals("null")){
                        value = null;
                    }
                    name.put(key, value);
                }
            } else if (product.has("name"))
                name.put("id", product.getString("name"));

            // Same but for description
            HashMap<String, String> description = new HashMap<>();
            if (product.has("description") && languages != null) {
                Iterator<String> itr = ((JSONObject)product.get("description")).keys();
                while (itr.hasNext()) {
                    String key = itr.next();
                    String value = ((JSONObject)product.get("description")).getString(key);
                    if(value.equals("null")){
                        value = null;
                    }
                    description.put(key, value);
                }
            } else if (product.has("description"))
                description.put("id", product.getString("description"));
            out.add(new Product(
                    (String) product.opt("id"),
                    (String) product.opt("manufacturer"),
                    (String) product.opt("category"),
                    name,
                    description,
                    (String) product.opt("image")
            ));
        }

        return out;
    }

    public static void addProduct(Product product) throws JSONException
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
            throw new Exceptions.MissingArguments("Product name requires at least an English translation.");
        }
        if(!product.description.containsKey("en")){
            throw new Exceptions.MissingArguments("Product description requires at least an English translation.");
        }

        // Determine whether the description has been set and should be included in the request
        boolean includeDesc = false;
        for (Map.Entry<String, String> entry : product.description.entrySet()){
            if (entry.getValue() != null) {
                includeDesc = true;
                break;
            }
        }

        //Create request
        JSONObject request = new JSONObject()
            .put("username", AppConfig.currentUser.username)
            .put("token", AppConfig.currentUser.token)
            .put("requestType", "addProduct")
            .put("requestData", new JSONObject()
                .put("productID", product.ID)
                .put("categoryID", product.categoryID)
                .put("manufacturer", product.manufacturer)
                .put("image", product.image != null // only add image if not null
                    ? new JSONObject()
                        .put("data", encodedImage)
                        .put("extension", ".png")
                    : null
                )
                .put("name", new JSONObject(product.name))
                .put("description", includeDesc ? new JSONObject(product.description) : null)
            );
        Connection.Send(request);
    }

    public static void deleteProduct(String ID) throws JSONException{
        //Create request
        JSONObject request = new JSONObject()
            .put("username", AppConfig.currentUser.username)
            .put("token", AppConfig.currentUser.token)
            .put("requestType", "deleteProduct")
            .put("requestData", new JSONObject()
                .put("productID", ID)
            );

        Connection.Send(request);
    }

    public static void deleteProductItems(String productID, int count) throws JSONException{
        //Create request
        JSONObject request = new JSONObject()
                .put("username", AppConfig.currentUser.username)
                .put("token", AppConfig.currentUser.token)
                .put("requestType", "deleteProductItems")
                .put("requestData", new JSONObject()
                        .put("productID", productID)
                        .put("count", count)
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

        String newID = product.ID;
        if(product.productIDCopy.equals(newID)){
            newID = null;
        }

        //Create request
        JSONObject request = new JSONObject()
            .put("username", AppConfig.currentUser.username)
            .put("token", AppConfig.currentUser.token)
            .put("requestType", "updateProduct")
            .put("requestData", new JSONObject()
                .put("productID", product.productIDCopy)
                .put("newProductID", newID)
                .put("categoryID", product.categoryID)
                .put("manufacturer", product.manufacturer)
                .put("image", encodedImage)
                .put("name", new JSONObject(product.name))
                .put("description", new JSONObject(product.description))
            );
        Log.i("tag", request.toString());
        Connection.Send(request);
    }
}