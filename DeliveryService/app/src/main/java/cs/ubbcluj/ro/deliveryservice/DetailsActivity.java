package cs.ubbcluj.ro.deliveryservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cs.ubbcluj.ro.deliveryservice.chart.PieChart;

public class DetailsActivity extends AppCompatActivity {

    int count1  = 0;
    int count2  = 0;
    int count3  = 0;
    int count4  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        ArrayList<String> prices = getIntent().getStringArrayListExtra("prices");
        ArrayList<Double> prices_double = new ArrayList<>(prices.size());
        for(int i=0; i<prices.size();++i)
        {
            prices_double.add(Double.valueOf(prices.get(i)));
        }

        for(int i=0; i<prices_double.size();++i)
        {
            if( 5<=(prices_double.get(i)) && prices_double.get(i)<10)
            {
                count1++;
            }
            else if( 10<=prices_double.get(i) && prices_double.get(i)<20)
            {
                count2++;
            }
            else if( 20<=prices_double.get(i) && prices_double.get(i)<30)
            {
                count3++;
            }
            else if(30<=(prices_double.get(i)) && prices_double.get(i)<40)
            {
                count4++;
            }

        }

        final float procent1 = ( 100.0f* count1)/prices_double.size();
        final float procent2 = ( 100.0f* count2)/prices_double.size();
        final float procent3 = ( 100.0f* count3)/prices_double.size();
        final float procent4 = ( 100.0f* count4)/prices_double.size();


        ListView listView = (ListView) findViewById(R.id.list_details);
        List<String> details = new ArrayList<>(2);
        details.add(getIntent().getStringExtra("name"));
        details.add("Description: " + getIntent().getStringExtra("description"));
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, details);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ListView listView2 = (ListView) findViewById(R.id.list_deliveries);
        List<String> deliveries = new ArrayList<>(1000);
        ArrayList<String> offers = getIntent().getStringArrayListExtra("offers");
        ArrayAdapter<String> adapter2 =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, offers);
        listView2.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();
        
        Button genBarChart=(Button)findViewById(R.id.generateChart);
        genBarChart.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {Intent achartIntent = new PieChart(procent1,procent2,procent3,procent4).execute(v.getContext());
            startActivity(achartIntent);
            }
        });
    }

}
