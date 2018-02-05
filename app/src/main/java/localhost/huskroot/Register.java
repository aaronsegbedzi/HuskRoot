package localhost.huskroot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Aaron Segbedzi on 11/24/2017.
 */

public class Register extends AppCompatActivity {

    private AlertDialog alertDialog;
    private EditText Email, Phone, Password1, Password2, FirstName, LastName, SecurityAnswer;
    private Spinner SecurityQuestion, UserType;
    private TextView btnLogin;
    private Button btnRegister;
    private Snackbar snackbar;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Email = (EditText) findViewById(R.id.email);
        Phone = (EditText) findViewById(R.id.phone);
        Password1 = (EditText) findViewById(R.id.password1);
        Password2 = (EditText) findViewById(R.id.password2);
        FirstName = (EditText) findViewById(R.id.firstName);
        LastName = (EditText) findViewById(R.id.lastName);
        SecurityAnswer = (EditText) findViewById(R.id.securityAnswer);

        SecurityQuestion = (Spinner)findViewById(R.id.securityQuestion);
        UserType = (Spinner)findViewById(R.id.userType);

        String[] items = new String[]{"What is your father's middle name?", "What is your favorite color?"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_single, items);
        SecurityQuestion.setAdapter(adapter);

        String[] items1 = new String[]{"Buyer", "Farmer"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_single, items1);
        UserType.setAdapter(adapter1);

        btnLogin = (TextView)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, MainActivity.class));
                finish();
            }
        });

        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Email.getText().toString();
                String phone = Phone.getText().toString();
                String password1 = Password1.getText().toString();
                String password2 = Password2.getText().toString();
                String firstName = FirstName.getText().toString();
                String lastName = LastName.getText().toString();
                String securityQuestion = SecurityQuestion.getSelectedItem().toString();
                String securityAnswer = SecurityAnswer.getText().toString();
                String preUserType = UserType.getSelectedItem().toString();
                onPreRegister(email, phone, password1, password2, firstName, lastName, securityQuestion, securityAnswer, preUserType);
            }
        });

    }

    protected void onPreRegister(String email, String phone, String password1, String password2, String firstName,
                              String lastName, String securityQuestion, String securityAnswer, String preUserType) {

        if(email.matches("") || phone.matches("") || password1.matches("") || password2.matches("")
                || firstName.matches("") || lastName.matches("") || securityAnswer.matches("")){
            snackbar = Snackbar.make(findViewById(android.R.id.content), "All fields are required. Please try again.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorDanger));
            snackbar.show();
        }else if (!password1.equals(password2)){
            snackbar = Snackbar.make(findViewById(android.R.id.content), "Passwords do not match. Please try again.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorWarning));
            snackbar.show();
        }else{
            String postUserType = "0";
            if (preUserType.equals("Farmer")){postUserType = "1";}
            RegisterWorker registerWorker = new RegisterWorker(this);
            registerWorker.execute(email, phone, password1, firstName, lastName, securityQuestion, securityAnswer, postUserType);
        }
    }

    protected void postRegister(){
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Registration successful. Please log in to continue.");
        alertDialog.show();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                startActivity(new Intent(Register.this, MainActivity.class));
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
                            Register.this.onPreRegister(Email.getText().toString(),Phone.getText().toString(),Password1.getText().toString(),Password2.getText().toString(),
                            FirstName.getText().toString(),LastName.getText().toString(),SecurityQuestion.getSelectedItem().toString(),SecurityAnswer.getText().toString(),
                            UserType.getSelectedItem().toString());
                        }
                    })
                    .setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView();
            mySbView.setBackgroundColor(getResources().getColor(R.color.ColorDanger));
            TextView textView = (TextView) mySbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }else if(response.equals("REGISTER_FALSE")){
            snackbar = Snackbar.make(findViewById(android.R.id.content), "Registration failed. Please try again.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorDanger));
            snackbar.show();
        }
    }
}
