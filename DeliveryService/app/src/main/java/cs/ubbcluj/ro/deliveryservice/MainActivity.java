package cs.ubbcluj.ro.deliveryservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cs.ubbcluj.ro.deliveryservice.domain.DeliveryService;
import cs.ubbcluj.ro.deliveryservice.domain.Product;
import cs.ubbcluj.ro.deliveryservice.exceptions.DeliveryServiceNotFoundException;
import cs.ubbcluj.ro.deliveryservice.exceptions.ProductNotFoundException;
import cs.ubbcluj.ro.deliveryservice.repository.InMemoryRepository;
import cs.ubbcluj.ro.deliveryservice.repository.Repository;

public class MainActivity extends AppCompatActivity {

    final Integer REQUEST_CODE = 0;
    private ArrayAdapter adapter;
    private Repository repository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.repository = new InMemoryRepository();

        ListView listView = (ListView) findViewById(R.id.listview1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                String[] products = new String[MainActivity.this.repository.getProducts().size()];
                for (int i = 0; i < MainActivity.this.repository.getProducts().size(); ++i) {
                    products[i] = MainActivity.this.repository.getProducts().get(i).toString();
                }
                intent.putExtra("products", products);
                intent.putExtra("edit", position);
                try {
                    intent.putExtra("name", MainActivity.this.repository.getDeliveryService(position).getName());
                    intent.putExtra("address", MainActivity.this.repository.getDeliveryService(position).getAddress());
                } catch (DeliveryServiceNotFoundException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        refresh();
    }

    public void refresh() {
        ListView listView = (ListView) findViewById(R.id.listview1);
        this.adapter = new ArrayAdapter<DeliveryService>(this, android.R.layout.simple_list_item_1, repository.getDeliveryServices());
        listView.setAdapter(adapter);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, SendActivity.class);
        startActivity(intent);
    }

    public void goToAddIntent(View view) {
        Intent intent =  new Intent(this, EditActivity.class);
        String[] products = new String[this.repository.getProducts().size()];
        for (int i = 0; i < this.repository.getProducts().size(); ++i) {
            products[i] = this.repository.getProducts().get(i).toString();
        }
        intent.putExtra("products", products);
        intent.putExtra("name", "");
        intent.putExtra("address", "");
        intent.putExtra("edit", -1);
        startActivityForResult(intent, REQUEST_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                if (data.getIntExtra("edit", -1) == -1) {
                    this.repository.addDeliveryService(new DeliveryService(
                            data.getStringExtra("name"),
                            data.getStringExtra("address"),
                            this.repository.getProductByName(data.getStringExtra("product"))
                    ));
                } else {
                    this.repository.editDeliveryService(
                            data.getIntExtra("edit", -1),
                            new DeliveryService(
                                    data.getStringExtra("name"),
                                    data.getStringExtra("address"),
                                    this.repository.getProductByName(data.getStringExtra("product"))
                            )
                    );
                }
            } catch (ProductNotFoundException e) {
                e.printStackTrace();
            }
            this.adapter.notifyDataSetChanged();
        }
    }
}
