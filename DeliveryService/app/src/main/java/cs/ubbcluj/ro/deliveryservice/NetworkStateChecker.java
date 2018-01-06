package cs.ubbcluj.ro.deliveryservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cs.ubbcluj.ro.deliveryservice.api.VolleySingleton;
import cs.ubbcluj.ro.deliveryservice.helper.DatabaseHelper;

/**
 * Created by Teo on 28.12.2017.
 */

public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private DatabaseHelper db;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new DatabaseHelper(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced products
                Cursor cursor = db.getUnsyncedProducts();
                if (cursor.moveToFirst()) {
                    do {
                        Log.v("Status", String.valueOf(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))));
                        if( cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS)) == MainActivity.DELETE_NOT_SYNCED_WITH_SERVER )
                        {
                         deleteProduct(
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION)));
                        }
                       else if( cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS)) == MainActivity.ADD_NOT_SYNCED_WITH_SERVER)
                        {
                            saveProduct(
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION))
                        );
                        }
                        else if( cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS)) == MainActivity.UPDATE_NOT_SYNCED_WITH_SERVER)
                        {
                            updateProduct(
                                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION))
                            );
                        }
                    } while (cursor.moveToNext());
                }
                Cursor cursor2 = db.getUnsyncedOffers();

                if (cursor2.moveToFirst()) {
                    do {
                        if( cursor2.getInt(cursor2.getColumnIndex(DatabaseHelper.COLUMN_STATUS)) == MainActivity.OFFER_NOT_SYNCED_WITH_SERVER)
                        {
                            saveOffer(
                                    cursor2.getInt(cursor2.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID)),
                                    cursor2.getInt(cursor2.getColumnIndex(DatabaseHelper.COLUMN_DELIVERY_ID)),
                                    cursor2.getDouble(cursor2.getColumnIndex(DatabaseHelper.COLUMN_PRICE)),
                                    cursor2.getString(cursor2.getColumnIndex(DatabaseHelper.COLUMN_ADDED_AT))
                            );
                        }

                    } while (cursor2.moveToNext());
                }
            }
        }
    }

    /*
    * method taking three arguments
    * name of the product, description of the product that is to be saved and id of the product from SQLite
    * if the name and description are successfully sent
    * we will update the status as synced in SQLite
    * */
    private void saveProduct(final int id, final String name, final String description) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.URL_SAVE_PRODUCT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {

                                //updating the status in sqlite
                                db.updateProductStatus(id, MainActivity.DATA_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("description", description);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void deleteProduct(final int id, final String name, final String description) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, MainActivity.URL_DELETE_PRODUCT+String.valueOf(id),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //delete from sqlite
                                db.deleteProduct(id);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("name", name);
                params.put("description", description);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void updateProduct(final int id, final String name, final String description) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.URL_UPDATE_PRODUCT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {

                                //updating the status in sqlite
                                db.updateProductStatus(id, MainActivity.DATA_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("name", name);
                params.put("description", description);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void saveOffer(final int product_id, final int delivery_id, final double price, final String added_at) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.URL_SAVE_OFFER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {

                                //updating the status in sqlite
                                db.updateOfferStatus(product_id,delivery_id, MainActivity.DATA_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("product_id", String.valueOf(product_id));
                params.put("delivery_id",String.valueOf(delivery_id));
                params.put("price",String.valueOf(price));
                params.put("added_at",added_at);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


}
