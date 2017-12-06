package cs.ubbcluj.ro.deliveryservice;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cs.ubbcluj.ro.deliveryservice.database.DeliveryDatabase;
import cs.ubbcluj.ro.deliveryservice.database.DeliveryServiceEntity;
import cs.ubbcluj.ro.deliveryservice.database.OfferEntity;
import cs.ubbcluj.ro.deliveryservice.database.ProductEntity;
import cs.ubbcluj.ro.deliveryservice.repository.Repository;

public class MainActivity extends AppCompatActivity {

    final Integer REQUEST_CODE = 0;
    private ArrayAdapter adapter;
    private Repository repository;
    private DeliveryDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //in memory repository
        //this.repository = new InMemoryRepository();

       //room database
        db = Room.databaseBuilder(this,
                DeliveryDatabase.class, "deliveries.db")
                //           .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        //add delivery services
//
//        DeliveryServiceEntity delivery = new DeliveryServiceEntity();
//        delivery.setName("DELIVERY1");
//        delivery.setAddress("DESCR1");
//        this.db.deliveryServiceDao().insert(delivery);
//
//        DeliveryServiceEntity delivery2 = new DeliveryServiceEntity();
//        delivery2.setName("DELIVERY2");
//        delivery2.setAddress("DESCR2");
//        this.db.deliveryServiceDao().insert(delivery2);
//
//        DeliveryServiceEntity delivery4 = new DeliveryServiceEntity();
//        delivery4.setName("DELIVERY4");
//        delivery4.setAddress("DESCR4");
//        this.db.deliveryServiceDao().insert(delivery4);
//
//        DeliveryServiceEntity delivery3 = new DeliveryServiceEntity();
//        delivery3.setName("DELIVERY3");
//        delivery3.setAddress("DESCR3");
//        this.db.deliveryServiceDao().insert(delivery3);
//

        ListView listView = (ListView) findViewById(R.id.listview1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Show_Alert_box(view.getContext(),"Please select action.",position);

            }
        });
        refresh();
    }

    public void Show_Alert_box(Context context, String message, int position)
    {
        final int pos = position;

        final AlertDialog alertDialog = new  AlertDialog.Builder(context).create();
        alertDialog.setTitle("Product options");

        alertDialog.setButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                goToDeleteIntent(pos);
                refresh();
            } });
        alertDialog.setButton2("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                goToUpdateIntent(pos);
                refresh();
            } });
        alertDialog.setButton3("See details and offers", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                goToDetailsIntent(pos);
                refresh();
            } });

        alertDialog.show();
    }

    public void refresh() {
        ListView listView = (ListView) findViewById(R.id.listview1);
        this.adapter = new ArrayAdapter<ProductEntity>(this, android.R.layout.simple_list_item_1, this.db.productDao().getEntries());
        listView.setAdapter(adapter);

    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, SendActivity.class);
        startActivity(intent);
    }

    public void goToAddIntent(View view) {

        Intent intent =  new Intent(this, EditActivity.class);
        String[] products = new String[this.db.productDao().getEntries().size()];
        for (int i = 0; i < this.db.productDao().getEntries().size(); ++i) {
            products[i] = this.db.productDao().getEntries().get(i).getName();
        }
        String[] deliveries = new String[this.db.deliveryServiceDao().getEntries().size()];
        for (int i = 0; i < this.db.deliveryServiceDao().getEntries().size(); ++i) {
            deliveries[i] = this.db.deliveryServiceDao().getEntries().get(i).getName();
        }
        intent.putExtra("products", products);
        intent.putExtra("deliveries", deliveries);
        intent.putExtra("price", "");
        intent.putExtra("date", new Date());
        intent.putExtra("edit", 3);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void goToDetailsIntent(int id) {

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        Intent intent =  new Intent(this, DetailsActivity.class);

        intent.putExtra("name", MainActivity.this.db.productDao().getEntries().get(id).getName());
        intent.putExtra("description", MainActivity.this.db.productDao().getEntries().get(id).getDescription());

        int realId = MainActivity.this.db.productDao().getEntries(MainActivity.this.db.productDao().getEntries().get(id).getName()).getId();
        String[] offers = new String[this.db.offerDao().getEntriesByProduct(realId).size()];
        ArrayList<String> prices  = new ArrayList<>(this.db.offerDao().getEntriesByProduct(realId).size());

        for (int i = 0; i < this.db.offerDao().getEntriesByProduct(realId).size(); ++i) {
            if(this.db.offerDao().getEntriesByProduct(realId).get(i).getProduct_id() == realId)
            {
                DeliveryServiceEntity ds = this.db.deliveryServiceDao().getEntries(this.db.offerDao().getDeliveryService(
                        (this.db.offerDao().getEntriesByProduct(realId).get(i).getDelivery_id()))
                        .getName());
                offers[i] = ds.toString() + "PRICE: " + String.valueOf(this.db.offerDao().getEntriesByProduct(realId).get(i).getPrice()) + " LEI "
                +'\n' + "STARTS AT: " + df.format(this.db.offerDao().getEntriesByProduct(realId).get(i).getDate()) +'\n';

                prices.add(String.valueOf(this.db.offerDao().getEntriesByProduct(realId).get(i).getPrice()));
            }
            }
        intent.putExtra("offers", offers);
        intent.putExtra("prices",prices);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void goToUpdateIntent(int id) {

        Intent intent =  new Intent(this, EditProductActivity.class);
        intent.putExtra("name", MainActivity.this.db.productDao().getEntries().get(id).getName());
        intent.putExtra("description", MainActivity.this.db.productDao().getEntries().get(id).getDescription());
        intent.putExtra("edit", MainActivity.this.db.productDao().getEntries().get(id).getId());
       startActivityForResult(intent, REQUEST_CODE);
    }

    public void goToDeleteIntent(int id) {

        List<ProductEntity>  list_prod = this.db.productDao().getEntries();
        ProductEntity p = list_prod.get(id);
        this.db.productDao().delete(p);
        this.adapter.notifyDataSetChanged();

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

                    ProductEntity product = new ProductEntity();
                    product.setName(data.getStringExtra("name"));
                    product.setDescription(data.getStringExtra("description"));
                    this.db.productDao().insert(product);
                    this.db.productDao().getEntries();
                    this.adapter.notifyDataSetChanged();
                    refresh();
                }
                else if(data.getIntExtra("edit", -1) == 3)
                {

                    OfferEntity offer = new OfferEntity();
                    offer.setProduct_id(this.db.productDao().getEntries(data.getStringExtra("product")).getId());
                    offer.setDelivery_id(this.db.deliveryServiceDao().getEntries(data.getStringExtra("delivery")).getId());
                    offer.setPrice(Double.valueOf(data.getStringExtra("price")));
                    try {

                        offer.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(data.getStringExtra("date")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    this.db.offerDao().insert(offer);
                    this.db.offerDao().getEntries();
                    this.adapter.notifyDataSetChanged();
                    refresh();
                }
                else{

                    ProductEntity product = new ProductEntity();
                    product.setId( data.getIntExtra("edit", -1));
                    product.setName(data.getStringExtra("name"));
                    product.setDescription(data.getStringExtra("description"));

                    this.db.productDao().update(product);
                    this.db.productDao().getEntries();
                    this.adapter.notifyDataSetChanged();
                    refresh();


                }

            }

    }
}

