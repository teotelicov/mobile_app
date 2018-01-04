package cs.ubbcluj.ro.deliveryservice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.ubbcluj.ro.deliveryservice.adapter.ProductAdapter;
import cs.ubbcluj.ro.deliveryservice.api.VolleySingleton;
import cs.ubbcluj.ro.deliveryservice.domain.Product;
import cs.ubbcluj.ro.deliveryservice.helper.DatabaseHelper;


public class MainActivity extends AppCompatActivity {

    final Integer REQUEST_CODE = 0;
    public static final String URL_SAVE_PRODUCT = "http://192.168.1.103:85/DeliveryServiceApi/saveProduct.php";
    public static final String URL_DELETE_PRODUCT = "http://192.168.1.103:85/DeliveryServiceApi/deleteProduct.php?id=";

    //database helper object
    private DatabaseHelper db;

    //View objects
    private ListView listViewProducts;

    //List to store all the products
    private List<Product> products;


    public static final int DATA_SYNCED_WITH_SERVER = 1;
    public static final int ADD_NOT_SYNCED_WITH_SERVER = 2;
    public static final int DELETE_NOT_SYNCED_WITH_SERVER = 3;
    public static final int UPDATE_NOT_SYNCED_WITH_SERVER = 4;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    //adapterobject for list view
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //initializing views and objects
        db = new DatabaseHelper(this);
        products = new ArrayList<>();


        listViewProducts = (ListView) findViewById(R.id.listview1);
        listViewProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Show_Alert_box(view.getContext(),"Please select action.",position);

            }
        });

        //calling the method to load all the stored products
        loadProducts();

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

    public void Show_Alert_box(Context context, String message, int position)
    {
        final int pos = position;

        final AlertDialog alertDialog = new  AlertDialog.Builder(context).create();
        alertDialog.setTitle("Product options");

        alertDialog.setButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                goToDeleteIntent(pos);

            } });
        alertDialog.setButton2("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                goToUpdateIntent(pos);
               // refresh();
            } });
        alertDialog.setButton3("See details and offers", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                goToDetailsIntent(pos);
              //  refresh();
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
                                //db.updateProductStatus(id, DELETE_NOT_SYNCED_WITH_SERVER);
                                updateStatusDeleteProductToLocalStorage(id,name,description, DELETE_NOT_SYNCED_WITH_SERVER);
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
                        //db.updateProductStatus(id, DELETE_NOT_SYNCED_WITH_SERVER);
                        updateStatusDeleteProductToLocalStorage(id,name,description, DELETE_NOT_SYNCED_WITH_SERVER);

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


    public void goToAddIntent(View view) {

//        Intent intent =  new Intent(this, EditActivity.class);
//        String[] products = new String[this.db.productDao().getEntries().size()];
//        for (int i = 0; i < this.db.productDao().getEntries().size(); ++i) {
//            products[i] = this.db.productDao().getEntries().get(i).getName();
//        }
//        String[] deliveries = new String[this.db.deliveryServiceDao().getEntries().size()];
//        for (int i = 0; i < this.db.deliveryServiceDao().getEntries().size(); ++i) {
//            deliveries[i] = this.db.deliveryServiceDao().getEntries().get(i).getName();
//        }
//        intent.putExtra("products", products);
//        intent.putExtra("deliveries", deliveries);
//        intent.putExtra("price", "");
//        intent.putExtra("date", new Date());
//        intent.putExtra("edit", 3);
//        startActivityForResult(intent, REQUEST_CODE);
    }

    public void goToDetailsIntent(int id) {

//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//
//        Intent intent =  new Intent(this, DetailsActivity.class);
//
//        intent.putExtra("name", MainActivity.this.db.productDao().getEntries().get(id).getName());
//        intent.putExtra("description", MainActivity.this.db.productDao().getEntries().get(id).getDescription());
//
//        int realId = MainActivity.this.db.productDao().getEntries(MainActivity.this.db.productDao().getEntries().get(id).getName()).getId();
//        String[] offers = new String[this.db.offerDao().getEntriesByProduct(realId).size()];
//        ArrayList<String> prices  = new ArrayList<>(this.db.offerDao().getEntriesByProduct(realId).size());
//
//        for (int i = 0; i < this.db.offerDao().getEntriesByProduct(realId).size(); ++i) {
//            if(this.db.offerDao().getEntriesByProduct(realId).get(i).getProduct_id() == realId)
//            {
//                DeliveryServiceEntity ds = this.db.deliveryServiceDao().getEntries(this.db.offerDao().getDeliveryService(
//                        (this.db.offerDao().getEntriesByProduct(realId).get(i).getDelivery_id()))
//                        .getName());
//                offers[i] = ds.toString() + "PRICE: " + String.valueOf(this.db.offerDao().getEntriesByProduct(realId).get(i).getPrice()) + " LEI "
//                +'\n' + "STARTS AT: " + df.format(this.db.offerDao().getEntriesByProduct(realId).get(i).getDate()) +'\n';
//
//                prices.add(String.valueOf(this.db.offerDao().getEntriesByProduct(realId).get(i).getPrice()));
//            }
//            }
//        intent.putExtra("offers", offers);
//        intent.putExtra("prices",prices);
//        startActivityForResult(intent, REQUEST_CODE);
    }

    public void goToUpdateIntent(int id) {

//        Intent intent =  new Intent(this, EditProductActivity.class);
//        intent.putExtra("name", MainActivity.this.db.productDao().getEntries().get(id).getName());
//        intent.putExtra("description", MainActivity.this.db.productDao().getEntries().get(id).getDescription());
//        intent.putExtra("edit", MainActivity.this.db.productDao().getEntries().get(id).getId());
//       startActivityForResult(intent, REQUEST_CODE);
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
                else if(data.getIntExtra("edit", -1) == 3)
                {

//                    OfferEntity offer = new OfferEntity();
//                    offer.setProduct_id(this.db.productDao().getEntries(data.getStringExtra("product")).getId());
//                    offer.setDelivery_id(this.db.deliveryServiceDao().getEntries(data.getStringExtra("delivery")).getId());
//                    offer.setPrice(Double.valueOf(data.getStringExtra("price")));
//                    try {
//
//                        offer.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(data.getStringExtra("date")));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
//                    this.db.offerDao().insert(offer);
//                    this.db.offerDao().getEntries();
                    //this.adapter.notifyDataSetChanged();
                    //refresh();
                }
                else{

//                    ProductEntity product = new ProductEntity();
//                    product.setId( data.getIntExtra("edit", -1));
//                    product.setName(data.getStringExtra("name"));
//                    product.setDescription(data.getStringExtra("description"));
//
//
//                    this.db.productDao().loadProduct(product.getId());
//                    this.db.productDao().update(product);
//                    this.db.productDao().getEntries();
//                    this.adapter.notifyDataSetChanged();
//                    refresh();



                }

            }

    }
}

