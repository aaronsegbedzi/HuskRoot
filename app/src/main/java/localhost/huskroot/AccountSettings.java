package localhost.huskroot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Aaron Segbedzi on 11/26/2017.
 */

public class AccountSettings extends AppCompatActivity {

    private EditText FirstName, LastName, Phone, Email;
    private Button BtnSubmit, BtnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Account Settings - Personal Details");

        SharedPreferences user = getSharedPreferences("MyInfo", AccountSettings.MODE_PRIVATE);

        FirstName = (EditText)findViewById(R.id.firstName);
        FirstName.setText(user.getString("first_name", "undefined"));

        LastName = (EditText)findViewById(R.id.lastName);
        LastName.setText(user.getString("last_name", "undefined"));

        Phone = (EditText)findViewById(R.id.phone);
        Phone.setText(user.getString("phone", "undefined"));

        Email = (EditText)findViewById(R.id.email);
        Email.setText(user.getString("email", "undefined"));

        BtnSubmit = (Button)findViewById(R.id.btnSubmit);
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = FirstName.getText().toString();
                String lastName = LastName.getText().toString();
                String phone = Phone.getText().toString();
                String email = Email.getText().toString();
                AccountSettings.this.onSettingsChange(firstName, lastName, phone, email);
            }
        });

        BtnReset = (Button)findViewById(R.id.btnReset);
        BtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences user = getSharedPreferences("MyInfo", AccountSettings.MODE_PRIVATE);
                FirstName.setText(user.getString("first_name", "undefined"));
                LastName.setText(user.getString("last_name", "undefined"));
                Phone.setText(user.getString("phone", "undefined"));
                Email.setText(user.getString("email", "undefined"));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_explore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, Dashboard.class));
                return true;
            case R.id.refresh:
                finish(); startActivity(getIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onSettingsChange(String firstName, String lastName, String phone, String email ){
        AccountWorker accountWorker = new AccountWorker(this);
        accountWorker.execute("ACTION_DETAILS",firstName, lastName, phone, email);
    }

    protected void showNotification(String response){
        View view = findViewById(android.R.id.content);
        if (response == null){
            Snackbar snackbar = Snackbar.make(view, "Check your connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AccountSettings.this.onSettingsChange(FirstName.toString(), LastName.toString(), Phone.toString(), Email.toString());
                        }
                    })
                    .setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView();
            mySbView.setBackgroundColor(getResources().getColor(R.color.ColorDanger));
            TextView textView = (TextView) mySbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }else if(response.equals("")){
            Snackbar snackbar = Snackbar.make(view, "Your personal details has been updated.", Snackbar.LENGTH_SHORT)
                    .setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView();
            mySbView.setBackgroundColor(getResources().getColor(R.color.ColorSuccess));
            TextView textView = (TextView) mySbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }
}
