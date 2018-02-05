package localhost.huskroot;

import android.app.AlertDialog;
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

class LoginWorker extends AsyncTask<String, Void, String> {


    private ProgressDialog pDialog;
    private MainActivity mainActivity;

    LoginWorker(MainActivity mainActivity){this.mainActivity = mainActivity;}

    @Override
    protected String doInBackground(String... params) {
        String response = null;
        String REMOTE_URL = Config.remoteHost + "/api/v2/auth.php";
        String username = params[0];
        String password = params[1];
        try {
            URL url = new URL(REMOTE_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                            String Body = URLEncoder.encode("username", "UTF-8")+"="+URLEncoder.encode(username, "UTF-8")+"&"
                                    +URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password, "UTF-8");
                        bufferedWriter.write(Body);
                        bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            int HTTP_RESPONSE_CODE = httpURLConnection.getResponseCode();
            if (HTTP_RESPONSE_CODE == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                        response = bufferedReader.readLine();
                    bufferedReader.close();
                inputStream.close();
            }else{ response = "LOGIN_FAILED";}
            httpURLConnection.disconnect();
        } catch (IOException e) {e.printStackTrace();}
        return response;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(mainActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Authenticating...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.setInverseBackgroundForced(true);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(String response) {
        if (response == null || response.equals("LOGIN_FAILED") ){mainActivity.showNotification(response);}
        else{mainActivity.onPostLogin(response);}
        pDialog.dismiss();
    }
}
