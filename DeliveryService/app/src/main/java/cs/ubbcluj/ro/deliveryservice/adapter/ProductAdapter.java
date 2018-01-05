package cs.ubbcluj.ro.deliveryservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


import cs.ubbcluj.ro.deliveryservice.R;
import cs.ubbcluj.ro.deliveryservice.domain.Product;

/**
 * Created by Teo on 28.12.2017.
 */


public class ProductAdapter extends ArrayAdapter<Product> {

    //storing all the products in the list
    private List<Product> products;

    //context object
    private Context context;

    //constructor
    public ProductAdapter(Context context, int resource, List<Product> products) {
        super(context, resource, products);
        this.context = context;
        this.products = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //getting the layoutinflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //getting listview itmes
        View listViewItem = inflater.inflate(R.layout.products, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        ImageView imageViewStatus = (ImageView) listViewItem.findViewById(R.id.imageViewStatus);

        //getting the current product
        Product product = products.get(position);

        //setting the product to textview
        textViewName.setText(product.getName());

        //if the synced status is 0 displaying
        //queued icon
        //else displaying synced icon
        if (product.getStatus() == 2 || product.getStatus() == 3 || product.getStatus() == 4 )
            imageViewStatus.setBackgroundResource(R.drawable.stopwatch);
        else
            imageViewStatus.setBackgroundResource(R.drawable.success);

        return listViewItem;
    }
}
