package localhost.huskroot;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;

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
 * Created by Aaron Segbedzi on 11/24/2017.
 */

public class RegisterWorker extends AsyncTask<String, Void, String> {
    private ProgressDialog pDialog;
    private AlertDialog alertDialog;
    private Register register;

    RegisterWorker(Register register){this.register = register;}

    @Override
    protected String doInBackground(String... params) {
        String response = null;
        String REMOTE_URL = Config.remoteHost + "/api/v2/user.php?action=register";
        String email = params[0];
        String phone = params[1];
        String password = params[2];
        String firstName = params[3];
        String lastName = params[4];
        String securityQuestion = params[5];
        String securityAnswer = params[6];
        String userType = params[7];
        try {
            URL url = new URL(REMOTE_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String Body = URLEncoder.encode("reg_first_name", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8") + "&"
                    + URLEncoder.encode("reg_last_name", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8") + "&"
                    + URLEncoder.encode("reg_username", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"
                    + URLEncoder.encode("reg_phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&"
                    + URLEncoder.encode("reg_type", "UTF-8") + "=" + URLEncoder.encode(userType, "UTF-8") + "&"
                    + URLEncoder.encode("reg_password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&"
                    + URLEncoder.encode("reg_question", "UTF-8") + "=" + URLEncoder.encode(securityQuestion, "UTF-8") + "&"
                    + URLEncoder.encode("reg_answer", "UTF-8") + "=" + URLEncoder.encode(securityAnswer, "UTF-8");
            bufferedWriter.write(Body);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            int HTTP_RESPONSE_CODE = httpURLConnection.getResponseCode();

            if (HTTP_RESPONSE_CODE == HttpURLConnection.HTTP_CREATED) {
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                response = "REGISTER_TRUE";
                bufferedReader.close();
                inputStream.close();
            } else {
                response = "REGISTER_FALSE";
            }
            httpURLConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(register, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Registering...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setInverseBackgroundForced(true);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(String response) {
        if (response == null || response.equals("REGISTER_FALSE")){register.showNotification(response);}
        else{register.postRegister();}
        pDialog.dismiss();
    }
}
