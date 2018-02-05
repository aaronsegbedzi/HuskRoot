package localhost.huskroot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Aaron Segbedzi on 11/24/2017.
 */

public class ForgotPassword extends AppCompatActivity {

    TextView btnLogin;
    Button btnForgotPassword;
    EditText Username, SecurityAnswer;
    Spinner SecurityQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Username = (EditText)findViewById(R.id.username);
        SecurityQuestion = (Spinner)findViewById(R.id.securityQuestion);
        SecurityAnswer = (EditText)findViewById(R.id.securityAnswer);

        String[] items = new String[]{"What is your father's middle name?", "What is your favorite color?"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_single, items);
        SecurityQuestion.setAdapter(adapter);

        btnForgotPassword = (Button)findViewById(R.id.btnForgotPassword);
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgotPassword.this.onRecoverPassword();
            }
        });

        btnLogin = (TextView)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                finish();
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

    protected void onRecoverPassword(){
        String username = Username.getText().toString();
        String securityQuestion = SecurityQuestion.getSelectedItem().toString();
        String securityAnswer = SecurityAnswer.getText().toString();
        ForgotPasswordWorker forgotPasswordWorker = new ForgotPasswordWorker(this);
        forgotPasswordWorker.execute(username, securityQuestion, securityAnswer);
    }

    protected void postRecoverPassword(String response){
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Password recovered. Login with this new pass code:");
        alertDialog.setMessage(response);
        alertDialog.show();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                finish();
            }
        });
    }

    protected void showNotification(String response){
        View view = findViewById(android.R.id.content);
        if (response == null){
            Snackbar snackbar = Snackbar.make(view, "Check your connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ForgotPassword.this.onRecoverPassword();
                        }
                    })
                    .setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView();
            mySbView.setBackgroundColor(getResources().getColor(R.color.ColorDanger));
            TextView textView = (TextView) mySbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }else if(response.equals("RECOVERY_FAILED")){
            Snackbar snackbar = Snackbar.make(view, "Username, Security Question or Security Answer is incorrect. Please try again.", Snackbar.LENGTH_SHORT)
                    .setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView();
            mySbView.setBackgroundColor(getResources().getColor(R.color.ColorWarning));
            TextView textView = (TextView) mySbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }
}
