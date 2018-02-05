package localhost.huskroot;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Aaron Segbedzi on 11/14/2017.
 */

class LogoutWorker extends AsyncTask<String, Void, String> {

    private ProgressDialog pDialog;
    private Dashboard dashboard;
    private SharedPreferences user;

    LogoutWorker(Dashboard dashboard){this.dashboard = dashboard;}

    @Override
    protected String doInBackground(String... params) {
        String response = null;
        user = dashboard.getSharedPreferences("MyInfo", Dashboard.MODE_PRIVATE);
        String access_key = user.getString("token", null);
        String REMOTE_URL = Config.remoteHost + "/api/v2/auth.php?token=" + access_key;
        try {
            URL url = new URL(REMOTE_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("DELETE");
            httpURLConnection.setRequestProperty("Content-length", "0");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setConnectTimeout(100000);
            httpURLConnection.setReadTimeout(100000);
            httpURLConnection.connect();
            int HTTP_RESPONSE_CODE = httpURLConnection.getResponseCode();
            if (HTTP_RESPONSE_CODE == HttpURLConnection.HTTP_OK){ response = "";}
            else{ response = "LOGOUT_FAILED"; }
            httpURLConnection.disconnect();
        } catch (IOException e) {e.printStackTrace();}
        return response;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(dashboard, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Logging out...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setInverseBackgroundForced(true);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(String response) {
        if(response == null || response.equals("LOGOUT_FAILED")) {dashboard.showNotification(response);}
        else{dashboard.onPostLogout();}
        pDialog.dismiss();
    }
}
