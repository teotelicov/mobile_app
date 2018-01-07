package cs.ubbcluj.ro.deliveryservice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.ubbcluj.ro.deliveryservice.adapter.ProductAdapter;
import cs.ubbcluj.ro.deliveryservice.api.VolleySingleton;
import cs.ubbcluj.ro.deliveryservice.domain.DeliveryService;
import cs.ubbcluj.ro.deliveryservice.domain.Offer;
import cs.ubbcluj.ro.deliveryservice.domain.Product;
import cs.ubbcluj.ro.deliveryservice.helper.DatabaseHelper;
import cs.ubbcluj.ro.deliveryservice.helper.SessionManager;


public class MainActivity extends AppCompatActivity {

    final Integer REQUEST_CODE = 0;
    public static final String URL_SAVE_PRODUCT = "http://192.168.0.106:85/DeliveryServiceApi/saveProduct.php";
    public static final String URL_DELETE_PRODUCT = "http://192.168.0.106:85/DeliveryServiceApi/deleteProduct.php?id=";
    public static final String URL_UPDATE_PRODUCT = "http://192.168.0.106:85/DeliveryServiceApi/updateProduct.php";
    public static final String URL_SAVE_OFFER = "http://192.168.0.106:85/DeliveryServiceApi/saveOffer.php";
    // Server user login url
    public static final String URL_LOGIN = "http://192.168.0.106:85/DeliveryServiceApi/login.php";
    // Server user register url
    public static String URL_REGISTER = "http://192.168.0.106:85/DeliveryServiceApi/register.php";

    //database helper object
    private DatabaseHelper db;

    private SessionManager session;

    //View objects
    private ListView listViewProducts;

    //List to store all the products
    private List<Product> products;

    //List to store all the deliveries
    private List<DeliveryService> deliveryServices;

    public static final int DATA_SYNCED_WITH_SERVER = 1;
    public static final int ADD_NOT_SYNCED_WITH_SERVER = 2;
    public static final int DELETE_NOT_SYNCED_WITH_SERVER = 3;
    public static final int UPDATE_NOT_SYNCED_WITH_SERVER = 4;
    public static final int OFFER_NOT_SYNCED_WITH_SERVER = 5;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    //adapter object for list view
    private ProductAdapter productAdapter;

    private Button btnLogout;
    public static FloatingActionButton btnAddProduct;
    public static FloatingActionButton btnAddOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnAddProduct = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        btnAddOffer = (FloatingActionButton) findViewById(R.id.floatingActionButton3);

        final String admin = getIntent().getStringExtra("admin");

        if(admin.equalsIgnoreCase("0"))
        {
            btnAddProduct.setVisibility(View.GONE);
            btnAddOffer.setVisibility(View.GONE);
        }
        //initializing views and objects
        db = new DatabaseHelper(this);
        products = new ArrayList<>();
        deliveryServices = new ArrayList<>();

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }


        listViewProducts = (ListView) findViewById(R.id.listview1);
        listViewProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Show_Alert_box(view.getContext(),"Please select action.",position,admin);

            }
        });

        //calling the method to load all the stored products
        loadProducts();
        loadDeliveries();
        //the broadcast receiver to update sync status
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //loading the products again
                loadProducts();
            }
        };

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }


    public void Show_Alert_box(Context context, String message, int position,String admin)
    {
        final int pos = position;

        final AlertDialog alertDialog = new  AlertDialog.Builder(context).create();
        alertDialog.setTitle("Product options");

        if(admin.equalsIgnoreCase("1")) {

            alertDialog.setButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    goToDeleteIntent(pos);

                }
            });
            alertDialog.setButton2("Update", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    goToUpdateIntent(pos);

                }
            });
        }
        alertDialog.setButton3("See details and offers", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                goToDetailsIntent(pos);

            } });

        alertDialog.show();
    }

    /*
    * this method will
    * load the products from the database
    * with updated sync status
    * */

    private void loadProducts() {
        products.clear();
        Cursor cursor = db.getProducts();
        if (cursor.moveToFirst()) {
            do {
                Product name = new Product(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                products.add(name);
            } while (cursor.moveToNext());
        }

        productAdapter = new ProductAdapter(this, R.layout.products, products);
        listViewProducts.setAdapter(productAdapter);
    }

    private void loadDeliveries() {
        deliveryServices.clear();
        Cursor cursor = db.getDeliveries();
        if (cursor.moveToFirst()) {
            do {
                DeliveryService deliveryService = new DeliveryService(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS))
                );
                deliveryServices.add(deliveryService);
            } while (cursor.moveToNext());
        }
    }

    /*
    * this method will simply refresh the list
    * */
    private void refreshList() {
        productAdapter.notifyDataSetChanged();
    }

    /*
    * this method is saving the product to the server
    * */


    public void sendMessage(View view) {
        Intent intent = new Intent(this, SendActivity.class);
        startActivity(intent);
    }

    private void saveProductToServer(String n, String d) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Product...");
        progressDialog.show();

        final String name = n;
        final String description = d;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_PRODUCT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //if there is a success
                                //storing the product to sqlite with status synced
                                saveProductToLocalStorage(name,description, DATA_SYNCED_WITH_SERVER);
                            } else {
                                //if there is some error
                                //saving the product to sqlite with status unsynced
                                saveProductToLocalStorage(name,description, ADD_NOT_SYNCED_WITH_SERVER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        //on error storing the product to sqlite with status unsynced
                        saveProductToLocalStorage(name, description, ADD_NOT_SYNCED_WITH_SERVER);
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

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    //saving the product to local storage
    private void saveProductToLocalStorage(String name,String description, int status) {

        db.addProduct(name,description, status);
        Product n = new Product(name,description, status);
        products.add(n);
        refreshList();
    }

    //update status of the product
    private void updateStatusDeleteProductToLocalStorage(int id, String name, String description, int status) {

        db.updateProductStatus(name,description,status);
        products.get(id).setStatus(status);
        refreshList();
    }

    private void deleteProductFromServer(int i,String n, String d) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting Product...");
        progressDialog.show();

        final String name = n;
        final String description = d;
        final int id = i;
        int ID = 0;
        Cursor cursor = db.getProductId(name, description);
        if (cursor.moveToFirst()) {
            do {
                ID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            } while (cursor.moveToNext());
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DELETE_PRODUCT+String.valueOf(ID),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //if there is a success
                                //delete the product from sqlite
                                deleteProductFromLocalStorage(id, name, description);
                            } else {
                                //if there is some error
                                //saving the product to sqlite with status unsynced

                                updateStatusDeleteProductToLocalStorage(id,name,description, DELETE_NOT_SYNCED_WITH_SERVER);
                                products.remove(id);
                                refreshList();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        //on error storing the product to sqlite with status unsynced
                        updateStatusDeleteProductToLocalStorage(id,name,description, DELETE_NOT_SYNCED_WITH_SERVER);
                        products.remove(id);
                        refreshList();

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

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    //delete the product from local storage
    private void deleteProductFromLocalStorage(int id,String name, String description) {

        Cursor cursor = db.getProductId(name, description);
        if (cursor.moveToFirst()) {
            do {
                int ID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                db.deleteProduct(ID);
                products.remove(id);
                refreshList();
            } while (cursor.moveToNext());
        }
    }

    private void updateProductToServer(int i, String n, String d) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Product...");
        progressDialog.show();

        final int id = i;
        final String name = n;
        final String description = d;


        int ID = 0;
        Cursor cursor = db.getProductId(products.get(id).getName(),products.get(id).getDescription());
        if (cursor.moveToFirst()) {
            do {
                ID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            } while (cursor.moveToNext());
        }

        final int ID2 = ID;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATE_PRODUCT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //if there is a success
                                //storing the product to sqlite with status synced
                                updateProductToLocalStorage(id,name,description, DATA_SYNCED_WITH_SERVER);
                            } else {
                                //if there is some error
                                //saving the product to sqlite with status unsynced
                                updateProductToLocalStorage(id,name,description, UPDATE_NOT_SYNCED_WITH_SERVER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        //on error storing the product to sqlite with status unsynced
                        updateProductToLocalStorage(id,name,description, UPDATE_NOT_SYNCED_WITH_SERVER);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(ID2));
                params.put("name", name);
                params.put("description", description);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    //updating the product to local storage
    private void updateProductToLocalStorage(int id, String name,String description, int status) {

        Cursor cursor = db.getProductId(products.get(id).getName(), products.get(id).getDescription());
        if (cursor.moveToFirst()) {
            do {
                int ID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                db.updateProduct(ID,name,description, status);
                products.get(id).setName(name);
                products.get(id).setDescription(description);
                products.get(id).setStatus(status);
                refreshList();
            } while (cursor.moveToNext());
        }
    }

    private void saveOfferToServer(int product_id, int delivery_id, double price, final String added_at) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Offer...");
        progressDialog.show();

        final int pid = product_id;
        final int did = delivery_id;
        final double p = price;
        final String at = added_at;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_OFFER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //if there is a success
                                //storing the offer to sqlite with status synced
                                saveOfferToLocalStorage(pid, did, p, added_at, DATA_SYNCED_WITH_SERVER);
                            } else {
                                //if there is some error
                                //saving the product to sqlite with status unsynced
                                saveOfferToLocalStorage(pid, did, p, added_at, OFFER_NOT_SYNCED_WITH_SERVER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        //on error storing the product to sqlite with status unsynced
                        saveOfferToLocalStorage(pid, did, p, added_at, OFFER_NOT_SYNCED_WITH_SERVER);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("product_id", String.valueOf(pid));
                params.put("delivery_id",String.valueOf(did));
                params.put("price",String.valueOf(p));
                params.put("added_at",at);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    //saving the offer to local storage
    private void saveOfferToLocalStorage(int product_id, int delivery_id, double price, String added_at, int status) {

        db.addOffer(product_id, delivery_id, price, added_at, status);
        refreshList();
    }


    public void goToAddIntent(View view) {

        Intent intent =  new Intent(this, EditActivity.class);
        String[] pr = new String[products.size()];
        for (int i = 0; i < products.size(); ++i) {
            pr[i] = products.get(i).getName();
        }
        String[] deliveries = new String[deliveryServices.size()];
        for (int i = 0; i < deliveryServices.size(); ++i) {
            deliveries[i] = deliveryServices.get(i).getName();
        }
        intent.putExtra("products", pr);
        intent.putExtra("deliveries", deliveries);
        intent.putExtra("price", "");
        intent.putExtra("added_at", new Date());
        intent.putExtra("edit", -2);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void goToDetailsIntent(int id) {

        Intent intent =  new Intent(this, DetailsActivity.class);
        intent.putExtra("name", products.get(id).getName());
        intent.putExtra("description", products.get(id).getDescription());

        int ID = 0;
        Cursor cursor = db.getProductId(products.get(id).getName(),products.get(id).getDescription());
        if (cursor.moveToFirst()) {
            do {
                ID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            } while (cursor.moveToNext());
        }
        ArrayList<Offer> offers = new ArrayList<>(100);
        ArrayList<String> deliveries = new ArrayList<>(100);
        Cursor cursor2 = db.getEntriesByProduct(ID);
        if (cursor2.moveToFirst()) {
            do {
                Offer offer = new Offer(cursor2.getInt(cursor2.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID)),cursor2.getInt(cursor2.getColumnIndex(DatabaseHelper.COLUMN_DELIVERY_ID)),
                        cursor2.getDouble(cursor2.getColumnIndex(DatabaseHelper.COLUMN_PRICE)),cursor2.getString(cursor2.getColumnIndex(DatabaseHelper.COLUMN_ADDED_AT)));
                offers.add(offer);
                deliveries.add(cursor2.getString(cursor2.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
            } while (cursor2.moveToNext());
        }

        ArrayList<String> offers2  = new ArrayList<>(offers.size());
        ArrayList<String> prices  = new ArrayList<>(offers.size());
        int count = 0 ;

        for(Offer o : offers)
        {

            String str = deliveries.get(count).toString() + "PRICE: " + String.valueOf(o.getPrice()) + " LEI "
                +'\n' + "STARTS AT: " + o.getAddedAt() +'\n';
            offers2.add(str);

            prices.add(String.valueOf(o.getPrice()));
        }

        intent.putExtra("offers", offers2);
        intent.putExtra("prices",prices);
        startActivityForResult(intent, REQUEST_CODE);

    }

    public void goToUpdateIntent(int id) {

        Intent intent =  new Intent(this, EditProductActivity.class);
        intent.putExtra("name", products.get(id).getName());
        intent.putExtra("description", products.get(id).getDescription());
        intent.putExtra("edit", id);
       startActivityForResult(intent, REQUEST_CODE);
    }

    public void goToDeleteIntent(int id) {

        deleteProductFromServer(id,products.get(id).getName(),products.get(id).getDescription());

    }

    public void goToAddIntentProduct(View view) {

        Intent intent = new Intent(this, EditProductActivity.class);
        intent.putExtra("name", "");
        intent.putExtra("description", "");
        intent.putExtra("edit", -1);
        startActivityForResult(intent, REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
                if (data.getIntExtra("edit", -1) == -1) {

                    saveProductToServer(data.getStringExtra("name"),data.getStringExtra("description"));

                }
                else if(data.getIntExtra("edit", -1) == -2)
                {
                    int ID_product = 0;
                    Cursor cursor = db.getProductByName(data.getStringExtra("product"));
                    if (cursor.moveToFirst()) {
                        do {
                            ID_product = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                        } while (cursor.moveToNext());
                    }

                    Log.v("prod name",data.getStringExtra("product"));
                    Log.v("p_id",String.valueOf(ID_product));


                    int ID_delivery = 0;
                    Cursor cursor2 = db.getDeliveryByName(data.getStringExtra("delivery"));
                    if (cursor2.moveToFirst()) {
                        do {
                            ID_delivery = cursor2.getInt(cursor2.getColumnIndex(DatabaseHelper.COLUMN_ID));
                        } while (cursor2.moveToNext());
                    }

                    Log.v("d_id",String.valueOf(ID_delivery));
                    Log.v("price",data.getStringExtra("price"));
                    Log.v("added_at",data.getStringExtra("added_at"));
                    saveOfferToServer(ID_product,ID_delivery,Double.valueOf(data.getStringExtra("price")),data.getStringExtra("added_at"));

                }
                else{

                    updateProductToServer(data.getIntExtra("edit", -1),data.getStringExtra("name"),data.getStringExtra("description"));

                }

            }

    }
}

