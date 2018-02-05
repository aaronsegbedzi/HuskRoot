package localhost.huskroot;

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
 * Created by Aaron Segbedzi on 11/14/2017.
 */

class ForgotPasswordWorker extends AsyncTask<String, Void, String> {


    private ProgressDialog pDialog;
    private ForgotPassword forgotPassword;

    ForgotPasswordWorker(ForgotPassword forgotPassword){this.forgotPassword = forgotPassword;}

    @Override
    protected String doInBackground(String... params) {
        String response = null;
        String REMOTE_URL = Config.remoteHost + "/api/v2/user.php?action=forgot_password";
        String username = params[0];
        String securityQuestion = params[1];
        String securityAnswer = params[2];
        try {
            URL url = new URL(REMOTE_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String Body = URLEncoder.encode("rec_username", "UTF-8")+"="+URLEncoder.encode(username, "UTF-8")+"&"
                    +URLEncoder.encode("rec_question", "UTF-8")+"="+URLEncoder.encode(securityQuestion, "UTF-8")+"&"
                    +URLEncoder.encode("rec_answer", "UTF-8")+"="+URLEncoder.encode(securityAnswer, "UTF-8");
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
            }else{ response = "RECOVERY_FAILED";}
            httpURLConnection.disconnect();
        } catch (IOException e) { e.printStackTrace();}
    return response;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(forgotPassword, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Recovering password...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setInverseBackgroundForced(true);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(String response) {
        if(response == null || response.equals("RECOVERY_FAILED")) {forgotPassword.showNotification(response);}
        else{forgotPassword.postRecoverPassword(response);}
        pDialog.dismiss();
    }
}
