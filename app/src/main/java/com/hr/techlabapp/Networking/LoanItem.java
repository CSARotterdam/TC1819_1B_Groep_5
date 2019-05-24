package com.hr.techlabapp.Networking;

import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

public class LoanItem {
    public int productItemID;
    public LocalDate start;
    public LocalDate end;
    public int ID;
    public LoanItem(int ID, int productItemID, LocalDate start, LocalDate end){
        this.ID = ID;
        this.productItemID = productItemID;
        this.start = start;
        this.end = end;
    }

    /**
     * Attempts to create a new loan item
     * @param product The product that will be loaned.
     * @param start Start reservation date.
     * @param end End reservation date.
     * @return The LoanItem that was created.
     * @throws JSONException
     */
    public static void addLoan(Product product, LocalDate start, LocalDate end) throws JSONException{
        addLoan(product.productID, start, end);
    }

    /**
     * Attempts to create a new loan item
     * @param productID The product ID of the product that will be loaned.
     * @param start Start reservation date.
     * @param end End reservation date.
     * @return The LoanItem that was created.
     * @throws JSONException
     */
    public static LoanItem addLoan(String productID, LocalDate start, LocalDate end) throws JSONException {
        //Create JSON object
        JSONObject request;
        request = new JSONObject()
                .put("requestType", "addLoan")
                .put("requestData", new JSONObject()
                        .put("productID", productID)
                        .put("start", start.toString())
                        .put("end", end.toString())
                );

        JSONObject response = (JSONObject)Connection.Send(request);
        return new LoanItem(response.getInt("ID"), response.getInt("product_item"), start, end);
    }

    /**
     * Attempts to extend an existing loan
     * @param loan The LoanItem to extend.
     * @param start The new start date.
     * @param end The new end date.
     * @return True if successful, otherwise false.
     */
    public static boolean extendLoan(LoanItem loan, LocalDate start, LocalDate end) throws JSONException {
        return extendLoan(loan.ID, start, end);
    }

    /**
     * Attempts to extend an existing loan
     * @param loanItemID The ID of LoanItem that will be extended.
     * @param start The new start date.
     * @param end The new end date.
     * @return True if successful, otherwise false.
     */
    public static boolean extendLoan(int loanItemID, LocalDate start, LocalDate end) throws JSONException {
        //Create JSON object
        JSONObject request;
        request = new JSONObject()
                .put("requestType", "extendLoan")
                .put("requestData", new JSONObject()
                        .put("loanItemID", loanItemID)
                        .put("start", start.toString())
                        .put("end", end.toString())
                );
        JSONObject response = (JSONObject) Connection.Send(request);
        return response.getBoolean("success");
    }

    /**
     * Gets a loanItem from the server.
     * @param loanItemID The ID of the LoanItem that will be retrieved.
     * @return The LoanItem.
     * @throws JSONException
     */
    public LoanItem getLoan(int loanItemID) throws JSONException{
        //Create JSON object
        JSONObject request;
        request = new JSONObject()
                .put("requestType", "getLoan")
                .put("requestData", new JSONObject()
                        .put("loanItemID", loanItemID)
                );

        JSONObject response = (JSONObject)Connection.Send(request);
        return new LoanItem(response.getInt("ID"), response.getInt("product_item"), start, end);
    }
}
