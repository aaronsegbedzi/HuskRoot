package localhost.huskroot;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Aaron Segbedzi on 11/30/2017.
 */

public class StatisticWorker extends AsyncTask<String, Void, String>{

    private Dashboard dashboard;
    private ProgressDialog pDialog;

    StatisticWorker(Dashboard dashboard){this.dashboard = dashboard;}

    @Override
    protected String doInBackground(String... params) {
        String response = null;
        SharedPreferences user = dashboard.getSharedPreferences("MyInfo", Dashboard.MODE_PRIVATE);
        String access_key = user.getString("token", null);
        String REMOTE_URL = Config.remoteHost + "/api/v2/statistics.php?token=" + access_key;
        try {
            URL url = new URL(REMOTE_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-length", "0");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setConnectTimeout(100000);
            httpURLConnection.setReadTimeout(100000);
            httpURLConnection.connect();
            int HTTP_RESPONSE_CODE = httpURLConnection.getResponseCode();
            if (HTTP_RESPONSE_CODE == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                response = bufferedReader.readLine();
                bufferedReader.close();
                inputStream.close();
            }
            httpURLConnection.disconnect();
        } catch (IOException e) {e.printStackTrace();}

        return response;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(dashboard, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setInverseBackgroundForced(true);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(String response) {
        if (response != null){dashboard.setStatistics(response);}
        pDialog.dismiss();
    }
}
