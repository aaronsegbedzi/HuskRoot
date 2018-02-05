package localhost.huskroot;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Aaron Segbedzi on 12/4/2017.
 */

class TokenWorker extends AsyncTask<Void, Void, Boolean> {
    private MainActivity mainActivity;
    SharedPreferences user;
    TokenWorker(MainActivity mainActivity){this.mainActivity = mainActivity;}

    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean response = false;
        user = mainActivity.getSharedPreferences("MyInfo", MainActivity.MODE_PRIVATE);
        String access_key = user.getString("token", null);
        String REMOTE_URL = Config.remoteHost + "/api/v2/auth.php?token=" + access_key;
        try {
            URL url = new URL(REMOTE_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-length", "0");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setConnectTimeout(100000);
            httpURLConnection.setReadTimeout(100000);
            httpURLConnection.connect();
            int HTTP_RESPONSE_CODE = httpURLConnection.getResponseCode();
            if (HTTP_RESPONSE_CODE == HttpURLConnection.HTTP_ACCEPTED) {response = true; }
            httpURLConnection.disconnect();
        } catch (IOException e) {e.printStackTrace();}
        return response;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean){mainActivity.startActivity(new Intent(mainActivity, Dashboard.class));}
    }
}
