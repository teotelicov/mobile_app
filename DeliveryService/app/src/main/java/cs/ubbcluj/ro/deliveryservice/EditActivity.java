package cs.ubbcluj.ro.deliveryservice;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import cs.ubbcluj.ro.deliveryservice.picker.DatePickerFragment;

public class EditActivity extends AppCompatActivity implements DatePickerFragment.DateDialogListener{

    private Integer sendBack;
    private static final String DIALOG_DATE = "DialogDate";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        String[] products = getIntent().getStringArrayExtra("products");
        String[] deliveries = getIntent().getStringArrayExtra("deliveries");
        this.sendBack = getIntent().getIntExtra("edit", -1);
        ((EditText) findViewById(R.id.editPrice)).setText(getIntent().getStringExtra("price"));

        Spinner spinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);
        ArrayAdapter<String> adapterProduct = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Arrays.asList(products));
        spinnerProduct.setAdapter(adapterProduct);

        Spinner spinnerDelivery = (Spinner) findViewById(R.id.spinnerDelivery);
        ArrayAdapter<String> adapterDelivery = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Arrays.asList(deliveries));
        spinnerDelivery.setAdapter(adapterDelivery);

        ImageView calendarImage = (ImageView)findViewById(R.id.image_view_hire_date);

        calendarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager manager = getSupportFragmentManager();
                DatePickerFragment dialog = new DatePickerFragment();
                dialog.show(manager, DIALOG_DATE);
            }
        });

    }

    @Override
    public void onFinishDialog(Date date) {

        String str = formatDate(date);
        ((EditText) findViewById(R.id.edit_date)).setText(str);

    }

    public String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String addedDate = sdf.format(date);
        return addedDate;
    }

    public void addOffer(View view) {
        Intent intent = new Intent();
        Editable price = ((EditText) findViewById(R.id.editPrice)).getText();
        try {
            Double.parseDouble(price.toString());
        }
        catch(NumberFormatException e)
        {
            Snackbar snackbar = Snackbar.make(view, "Price cannot be a string", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        if (price.toString().equals("")) {
            Snackbar snackbar = Snackbar.make(view, "Price cannot be empty", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;

        }
        intent.putExtra("price", price.toString());
        intent.putExtra("edit", this.sendBack);
        intent.putExtra("product", ((Spinner) findViewById(R.id.spinnerProduct)).getSelectedItem().toString());
        intent.putExtra("delivery", ((Spinner) findViewById(R.id.spinnerDelivery)).getSelectedItem().toString());
        intent.putExtra("added_at",((EditText)findViewById(R.id.edit_date)).getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
