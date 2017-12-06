package cs.ubbcluj.ro.deliveryservice;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

public class EditProductActivity extends AppCompatActivity {

    private Integer sendBack;
    private Integer pos;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        this.sendBack = getIntent().getIntExtra("edit", -1);

        ((EditText) findViewById(R.id.editName)).setText(getIntent().getStringExtra("name"));
        ((EditText) findViewById(R.id.editDescription)).setText(getIntent().getStringExtra("description"));
    }

    public void addProduct(View view) {
        Intent intent = new Intent();
        Editable name = ((EditText) findViewById(R.id.editName)).getText();
        Editable description = ((EditText) findViewById(R.id.editDescription)).getText();


        if (name.toString().equals("")) {
            Snackbar snackbar = Snackbar.make(view, "Name cannot be empty", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;

        }

        if (description.toString().equals("")) {
            Snackbar snackbar = Snackbar.make(view, "Description cannot be empty", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;

        }
        intent.putExtra("name", name.toString());
        intent.putExtra("description", description.toString());
        intent.putExtra("edit", this.sendBack);


        setResult(RESULT_OK, intent);
        finish();
    }
}
