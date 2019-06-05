package com.hr.techlabapp.Networking;

import com.hr.techlabapp.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LoanItem {
    public int productItemID;
    public Date start;
    public Date end;
    public int ID;

    public LoanItem(int ID, int productItemID, Date start, Date end){
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
        DateFormat format = SimpleDateFormat.getDateInstance();
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