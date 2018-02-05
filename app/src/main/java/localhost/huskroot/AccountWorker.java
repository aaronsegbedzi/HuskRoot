package localhost.huskroot;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Aaron Segbedzi on 11/26/2017.
 */

public class AccountWorker extends AsyncTask<String, Void, String> {

    private AccountSettings accountSettings;
    private ChangePassword changePassword;
    private ProgressDialog pDialog;

    AccountWorker(AccountSettings accountSettings){this.accountSettings = accountSettings;}
    AccountWorker(ChangePassword changePassword){this.changePassword = changePassword;}

    @Override
    protected String doInBackground(String... params) {
        String response = null;
        String action = params[0];

        if (action.equals("ACTION_DETAILS")) {
            SharedPreferences user = accountSettings.getSharedPreferences("MyInfo", Dashboard.MODE_PRIVATE);
            String access_key = user.getString("token", null);
            String firstName = params[1];
            String lastName = params[2];
            String phone = params[3];
            String email = params[4];
            String REMOTE_URL = Config.remoteHost + "/api/v2/user.php?token=" + access_key;
            try {
                URL url = new URL(REMOTE_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setConnectTimeout(100000);
                httpURLConnection.setReadTimeout(100000);
                httpURLConnection.connect();
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String Body = URLEncoder.encode("first_name", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8") + "&"
                        + URLEncoder.encode("last_name", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8") + "&"
                        + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&"
                        + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                bufferedWriter.write(Body);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                int HTTP_RESPONSE_CODE = httpURLConnection.getResponseCode();
                if (HTTP_RESPONSE_CODE == HttpURLConnection.HTTP_OK) {
                    SharedPreferences.Editor editor = user.edit();
                        editor.putString("first_name", firstName);
                        editor.putString("last_name", lastName);
                        editor.putString("phone", phone);
                        editor.putString("email", email);
                    editor.apply();
                    response = "";
                }else {response = "ACCOUNT_FAILED";}
                httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(action.equals("ACTION_PASSWORD")){
            SharedPreferences user = changePassword.getSharedPreferences("MyInfo", Dashboard.MODE_PRIVATE);
            String access_key = user.getString("token", null);
            String password = params[1];
            String REMOTE_URL = Config.remoteHost + "/api/v2/user.php?action=password&token=" + access_key;
            try {
                URL url = new URL(REMOTE_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setConnectTimeout(100000);
                httpURLConnection.setReadTimeout(100000);
                httpURLConnection.connect();
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String Body = URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                bufferedWriter.write(Body);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                int HTTP_RESPONSE_CODE = httpURLConnection.getResponseCode();
                if (HTTP_RESPONSE_CODE == HttpURLConnection.HTTP_OK) {response = "";}
                else {response = "PASSWORD_FAILED";}
                httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    @Override
    protected void onPreExecute() {
        if (changePassword == null) {
            pDialog = new ProgressDialog(accountSettings, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Updating User Information...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.setInverseBackgroundForced(true);
            pDialog.show();
        }else if(accountSettings == null){
            pDialog = new ProgressDialog(changePassword, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Changing your password...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.setInverseBackgroundForced(true);
            pDialog.show();
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (changePassword == null){
            accountSettings.showNotification(response);
        }else if(accountSettings == null){
            changePassword.showNotification(response);
        }
        pDialog.dismiss();
    }
}
