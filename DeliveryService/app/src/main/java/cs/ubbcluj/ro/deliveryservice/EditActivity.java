package cs.ubbcluj.ro.deliveryservice;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Arrays;

public class EditActivity extends AppCompatActivity {

    private Integer sendBack;

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
        this.sendBack = getIntent().getIntExtra("edit", -1);
        ((EditText) findViewById(R.id.editText4)).setText(getIntent().getStringExtra("name"));
        ((EditText) findViewById(R.id.editText5)).setText(getIntent().getStringExtra("address"));

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Arrays.asList(products));
        spinner.setAdapter(adapter);
    }
    public void addAnnouncement(View view) {
        Intent intent = new Intent();
        Editable name = ((EditText) findViewById(R.id.editText4)).getText();
        Editable address = ((EditText) findViewById(R.id.editText5)).getText();
        if (name.toString().equals("") || address.toString().equals("")) {
            Snackbar snackbar = Snackbar.make(view, "Name and address cannot be empty", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;

        }
        intent.putExtra("name", name.toString());
        intent.putExtra("address", address.toString());
        intent.putExtra("product", ((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString());
        intent.putExtra("edit", this.sendBack);
        setResult(RESULT_OK, intent);
        finish();
    }
}
