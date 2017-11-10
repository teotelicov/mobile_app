package cs.ubbcluj.ro.deliveryservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);}

        public void sendEmail(View view) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{((EditText) findViewById(R.id.editText3)).getText().toString()});
            intent.putExtra(Intent.EXTRA_SUBJECT, ((EditText) findViewById(R.id.editText)).getText().toString());
            intent.putExtra(Intent.EXTRA_TEXT, ((EditText) findViewById(R.id.editText2)).getText().toString());

            startActivity(Intent.createChooser(intent, "Send email!"));

        }
}

