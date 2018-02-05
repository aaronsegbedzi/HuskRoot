package localhost.huskroot;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Aaron Segbedzi on 11/26/2017.
 */

public class ChangePassword extends AppCompatActivity {

    private EditText Password, ConfirmPassword;
    private Button BtnSubmit, BtnReset;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Account Settings - Password");

        Password = (EditText)findViewById(R.id.password1);
        ConfirmPassword = (EditText)findViewById(R.id.password2);

        BtnSubmit = (Button)findViewById(R.id.btnSubmit);
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = Password.getText().toString();
                String confirmPassword = ConfirmPassword.getText().toString();
                ChangePassword.this.onPasswordChange(password, confirmPassword);
            }
        });

        BtnReset = (Button)findViewById(R.id.btnReset);
        BtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Password.setText("");
                ConfirmPassword.setText("");
            }
        });

    }

    protected void onPasswordChange(String password, String confirmPassword){
        if (password.matches("") || confirmPassword.matches("")) {
            snackbar = Snackbar.make(findViewById(android.R.id.content), "Password fields are required.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorDanger));
            snackbar.show();
        }else if (password.equals(confirmPassword)) {
            AccountWorker accountWorker = new AccountWorker(this);
            accountWorker.execute("ACTION_PASSWORD", password);
        }else{
            snackbar = Snackbar.make(findViewById(android.R.id.content), "Password fields do not match. Please try again.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorWarning));
            snackbar.show();
        }

    }


    protected void showNotification(String response){
        View view = findViewById(android.R.id.content);
        if (response == null){
            Snackbar snackbar = Snackbar.make(view, "Check your connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ChangePassword.this.onPasswordChange(Password.toString(), ConfirmPassword.toString());
                        }
                    })
                    .setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView();
            mySbView.setBackgroundColor(getResources().getColor(R.color.ColorDanger));
            TextView textView = (TextView) mySbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }else if(response.equals("")){
            snackbar = Snackbar.make(findViewById(android.R.id.content), "Password has been changed.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorSuccess));
            snackbar.show();
        }else if(response.equals("PASSWORD_FAILED")){
            snackbar = Snackbar.make(findViewById(android.R.id.content), "Service is unavailable. Please try again later.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorDanger));
            snackbar.show();
        }
    }
}
