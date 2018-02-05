package localhost.huskroot;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Aaron Segbedzi on 11/21/2017.
 */

class ExploreSubWorker extends AsyncTask<String[][], Void, String[][]> {
    private ProgressDialog pDialog;
    private ExploreSubCategory exploreSubCategory;
    ExploreSubWorker(ExploreSubCategory exploreSubCategory) {
        this.exploreSubCategory = exploreSubCategory;
    }

    @Override
    protected String[][] doInBackground(String[][]... params) {
        String[][] subcategories = null;
        String category_id = params[0][0][0];
        SharedPreferences user = exploreSubCategory.getSharedPreferences("MyInfo", ExploreSubCategory.MODE_PRIVATE);
        String access_key = user.getString("token", null);
        String REMOTE_URL = Config.remoteHost + "/api/v2/subcategory.php?category_id=" + category_id + "&token=" + access_key;
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
                String result = bufferedReader.readLine();
                JSONArray jsonArray = new JSONArray(result);
                subcategories = new String[jsonArray.length()][3];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    subcategories[i][0] = jsonObject.getString("id");
                    subcategories[i][1] = jsonObject.getString("name");
                    subcategories[i][2] = jsonObject.getString("totalProducts");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return subcategories;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(exploreSubCategory, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading more categories...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setInverseBackgroundForced(true);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(String[][] subcategories) {
        exploreSubCategory.setSubCategories(subcategories);
        pDialog.dismiss();
    }

}
