package com.hr.techlabapp.Networking;

import android.util.JsonToken;
import android.util.Log;

import com.hr.techlabapp.AppConfig;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class LoanItem {
    public int ID;
    public int productItemID;
    public String userId;
    public Date start;
    public Date end;
    public boolean isAcquired = false;

    public LoanItem(int ID, int productItemID, Date start, Date end) {
        this(ID, AppConfig.currentUser != null ? AppConfig.currentUser.username : null, productItemID, start, end, false);
    }

    public LoanItem(int ID, String userId, int productItemID, Date start, Date end, boolean isAcquired){
        this.ID = ID;
        this.productItemID = productItemID;
        this.userId = userId;
        this.start = start;
        this.end = end;
        this.isAcquired = isAcquired;
    }

    /**
     * Attempts to create a new loan item
     * @param product The product that will be loaned.
     * @param start Start reservation date.
     * @param end End reservation date.
     * @return The LoanItem that was created.
     * @throws JSONException
     */
    public static LoanItem addLoan(Product product, Date start, Date end) throws JSONException{
        return addLoan(product.ID, start, end);
    }

    /**
     * Attempts to create a new loan item
     * @param productID The product ID of the product that will be loaned.
     * @param start Start reservation date.
     * @param end End reservation date.
     * @return The LoanItem that was created.
     * @throws JSONException
     */
    public static LoanItem addLoan(String productID, Date start, Date end) throws JSONException {
        DateFormat format = AppConfig.dateFormat;
        //Create JSON object
        JSONObject request;
        request = new JSONObject()
            .put("username", AppConfig.currentUser.username)
            .put("token", AppConfig.currentUser.token)
            .put("requestType", "addLoan")
            .put("requestData", new JSONObject()
                .put("productId", productID)
                .put("start", format.format(start))
                .put("end", format.format(end))
            );

        JSONObject response = (JSONObject)Connection.Send(request);
        return new LoanItem(response.getInt("loanId"), response.getInt("productItem"), start, end);
    }

    /**
     * Attempts to extend an existing loan
     * @param loan The LoanItem to extend.
     * @param start The new start date.
     * @param end The new end date.
     * @return True if successful, otherwise false.
     */
    public static boolean extendLoan(LoanItem loan, Date start, Date end) throws JSONException {
        return extendLoan(loan.ID, start, end);
    }

    /**
     * Attempts to extend an existing loan
     * @param loanItemID The ID of LoanItem that will be extended.
     * @param start The new start date.
     * @param end The new end date.
     * @return True if successful, otherwise false.
     */
    public static boolean extendLoan(int loanItemID, Date start, Date end) throws JSONException {
        DateFormat format = SimpleDateFormat.getDateInstance();
        //Create JSON object
        JSONObject request;
        request = new JSONObject()
            .put("username", AppConfig.currentUser.username)
            .put("token", AppConfig.currentUser.token)
            .put("requestType", "extendLoan")
            .put("requestData", new JSONObject()
                .put("loanItemID", loanItemID)
                .put("start", format.format(start))
                .put("end", format.format(end))
            );
        JSONObject response = (JSONObject) Connection.Send(request);
        return response.getBoolean("success");
    }

    /**
     * Gets a set of LoanItems from the server.
     * @param productItemIds A set of product ids to get associated loans from.
     * @param username The user from whom to get loans. Requires Collaborator permission or higher to take effect.
     * @param loanItemID The id of a specific loan to return. This limits the list size to 1 element.
     * @param start The Date the loans must end after.
     * @param end The Date the loans must start before.
     * @return A set of LoanItems matching the conditions.
     */
    public static List<LoanItem> getLoans(@Nullable List<Integer> productItemIds,
                                          @Nullable String username,
                                          @Nullable Integer loanItemID,
                                          @Nullable Date start,
                                          @Nullable Date end
    ) throws JSONException {
        DateFormat format = SimpleDateFormat.getDateInstance();
        //Create JSON object
        JSONObject request;
        request = new JSONObject()
            .put("username", AppConfig.currentUser.username)
            .put("token", AppConfig.currentUser.token)
            .put("requestType", "getLoans")
            .put("requestData", new JSONObject()
                .put("productItemIds", productItemIds)
                .put("userId", username)
                .put("loanItemID", loanItemID)
                .put("start", start != null ? format.format(start) : null)
                .put("end", end != null ? format.format(end) : null)
            );

        JSONArray response = (JSONArray) Connection.Send(request);

        List<LoanItem> outList = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            JSONObject loanJSON = response.getJSONObject(i);
            outList.add(new LoanItem(
                loanJSON.optInt("id"),
                loanJSON.optString("user"),
                loanJSON.optInt("product_item"),
                loanJSON.has("start") ? new Date(loanJSON.getLong("start")) : null,
                loanJSON.has("end") ? new Date(loanJSON.getLong("end")) : null,
                loanJSON.optBoolean("is_item_acquired")
            ));
        }

        return outList;
    }

    /**
     * Sets the specified loan's acquired value in the database. This value specifies
     * if the item associated with this loan has been taken from stock.
     *
     * Requires Collaborator permissions or higher.
     * @param value The value to set the acquired value to.
     * @throws JSONException
     */
    public void setAcquired(boolean value) throws JSONException {
        // Create request JSON
        JSONObject request = new JSONObject()
            .put("username", AppConfig.currentUser.username)
            .put("token", AppConfig.currentUser.token)
            .put("requestType", "getLoan")
            .put("requestData", new JSONObject()
                .put("loanItemID", this.ID)
                .put("value", value)
            );
        Connection.Send(request);
        this.isAcquired = value;
    }

    /**
     * Gets a list of unavailable dates for loans on a specific product. These dates will be within
     * the start and end parameters.
     * @param productId
     * @param start The start of the date range to scan for unavailable dates.
     * @param end The end of the date range to scan for unavailable dates.
     * @return A list of dates where loans cannot be placed
     * @throws JSONException
     */
    public static List<Date> getUnavailableDates(String productId, Date start, Date end) throws JSONException {
        DateFormat format = AppConfig.dateFormat;
        JSONObject request = new JSONObject()
            .put("username", AppConfig.currentUser.username)
            .put("token", AppConfig.currentUser.token)
            .put("requestType", "getUnavailableDates")
            .put("requestData", new JSONObject()
                .put("productId", productId)
                .put("start", format.format(start))
                .put("end", format.format(end))
            );
        JSONArray response = (JSONArray) Connection.Send(request);
        List<Date> outList = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            outList.add(new Date(response.getLong(i)));
        }
        return outList;
    }
}