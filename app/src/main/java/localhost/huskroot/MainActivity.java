package localhost.huskroot;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends AppCompatActivity {

    EditText Username, Password;
    TextView btnforgotPassword, btnRegister;
    Button btnLogin;
    SharedPreferences user;
    Snackbar snackbar;

    static final int MULTIPLE_PERMISSIONS = 0;
    static final String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndRequestPermissions(this, this);

        Username = (EditText) findViewById(R.id.username);
        Password = (EditText) findViewById(R.id.password);

        btnforgotPassword = (TextView) findViewById(R.id.btnForgotPassword);
        btnforgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            }
        });

        btnRegister = (TextView) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = Username.getText().toString();
                String password = Password.getText().toString();
                MainActivity.this.onPreLogin(username, password);
            }
        });

    }

    protected void onPreLogin(String username, String password){
        if (!username.matches("") && !password.matches("")) {
            LoginWorker loginWorker = new LoginWorker(this);
            loginWorker.execute(username, password);
        }else{
            snackbar = Snackbar.make(findViewById(android.R.id.content), "Username and password fields are required.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorDanger));
            snackbar.show();
        }
    }

    protected void onPostLogin(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            SharedPreferences.Editor editor = getSharedPreferences("MyInfo", MODE_PRIVATE).edit();
            editor.putString("first_name", jsonObject.getString("first_name"));
            editor.putString("last_name", jsonObject.getString("last_name"));
            editor.putString("person_id", jsonObject.getString("person_id"));
            editor.putString("username", jsonObject.getString("username"));
            editor.putString("email", jsonObject.getString("email"));
            editor.putString("type", jsonObject.getString("type"));
            editor.putString("phone", jsonObject.getString("phone"));
            editor.putString("token", jsonObject.getString("token"));
            editor.putString("expire", jsonObject.getString("expire"));
            editor.apply();
        } catch (JSONException e) {e.printStackTrace();}
        startActivity(new Intent(this, Dashboard.class));
        finish();
    }

    protected void showNotification(String response){
        if (response == null){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Check your connection", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String username = Username.getText().toString();
                            String password = Password.getText().toString();
                            MainActivity.this.onPreLogin(username, password);
                        }
                    })
                    .setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView();
            mySbView.setBackgroundColor(getResources().getColor(R.color.ColorDanger));
            snackbar.show();
        }else if(response.equals("LOGIN_FAILED")){
            snackbar = Snackbar.make(findViewById(android.R.id.content), "Username and password are incorrect. Please try again.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorWarning));
            snackbar.show();
        }
    }

    public static boolean checkAndRequestPermissions(Activity activity, Context context) {
        int result;
        List<String> permissionsNeeded = new ArrayList<>();
        for (String permission:permissions) {
            result = ContextCompat.checkSelfPermission(context, permission);
            if (result != PackageManager.PERMISSION_GRANTED) { permissionsNeeded.add(permission); }
        }
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsNeeded.toArray(new String[permissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        TokenWorker tokenWorker = new TokenWorker(this);
        tokenWorker.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    snackbar = Snackbar.make(findViewById(android.R.id.content), "Some permissions are required to use application.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
                    View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorDanger));
                    snackbar.show();
                }
            }
        }

    }



}
