package localhost.huskroot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView userFullName, userEmail;
    SharedPreferences user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            MainActivity.checkAndRequestPermissions(this, this);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this, AddProduct.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        Menu menu = navigationView.getMenu();
        MenuItem opt1 = menu.findItem(R.id.option1);
        MenuItem opt2 = menu.findItem(R.id.option2);
        SpannableString spannableString1 = new SpannableString(opt1.getTitle());
        SpannableString spannableString2 = new SpannableString(opt2.getTitle());
        spannableString1 .setSpan(new TextAppearanceSpan(this, R.style.MenuTitleColor), 0, spannableString1.length(), 0);
        spannableString2 .setSpan(new TextAppearanceSpan(this, R.style.MenuTitleColor), 0, spannableString2.length(), 0);
        opt1.setTitle(spannableString1);
        opt2.setTitle(spannableString2);

        View navigationViewHeaderView =  navigationView.getHeaderView(0);
        userFullName = (TextView)navigationViewHeaderView.findViewById(R.id.userName);
        userEmail = (TextView)navigationViewHeaderView.findViewById(R.id.userEmail);

        SharedPreferences user = getSharedPreferences("MyInfo", MODE_PRIVATE);
        userFullName.setText(user.getString("first_name", "") + " " + user.getString("last_name", ""));
        userEmail.setText(user.getString("username", ""));

        StatisticWorker statisticWorker = new StatisticWorker(this);
        statisticWorker.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            LogoutWorker logoutWorker = new LogoutWorker(this);
            logoutWorker.execute();
        }else if (id == R.id.action_settings){
            startActivity(new Intent(this, AccountSettings.class));
        }else if (id == R.id.action_password){
            startActivity(new Intent(this, ChangePassword.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_products) {
            startActivity(new Intent(this, ListProduct.class));
        } else if (id == R.id.nav_explore) {
            startActivity(new Intent(this, Explore.class));
        } else if(id == R.id.nav_manage) {
            startActivity(new Intent(this, MyProduct.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, AccountSettings.class));
        } else if (id == R.id.nav_logout) {
            LogoutWorker logoutWorker = new LogoutWorker(this);
            logoutWorker.execute();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onPostLogout(){
        user = getSharedPreferences("MyInfo", MODE_PRIVATE);
        user.edit().clear().apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    protected void showNotification(String response){
        View view = findViewById(android.R.id.content);
        if (response == null){
            Snackbar snackbar = Snackbar.make(view, "Check your connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Dashboard.this.onPostLogout();
                        }
                    })
                    .setActionTextColor(Color.WHITE);

            View mySbView = snackbar.getView();
            mySbView.setBackgroundColor(getResources().getColor(R.color.ColorDanger));
            TextView textView = (TextView) mySbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }else if(response.equals("LOGOUT_FAILED")){
            Snackbar snackbar = Snackbar.make(view, "Internal Server Error", Snackbar.LENGTH_SHORT)
                    .setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView();
            mySbView.setBackgroundColor(getResources().getColor(R.color.ColorWarning));
            TextView textView = (TextView) mySbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    protected void setStatistics(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            TextView views = (TextView)findViewById(R.id.value1);
            views.setText(jsonObject.getString("totalViews"));

            TextView uploaded = (TextView)findViewById(R.id.value2);
            uploaded.setText(jsonObject.getString("totalUploadedProducts"));

            TextView refers = (TextView)findViewById(R.id.value3);
            refers.setText(jsonObject.getString("totalRefers"));

            TextView clicks = (TextView)findViewById(R.id.value4);
            clicks.setText(jsonObject.getString("totalClicks"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
