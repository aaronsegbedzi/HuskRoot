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

class ExploreSubSubWorker extends AsyncTask<String[][], Void, String[][]> {

    private ProgressDialog pDialog;
    private ExploreSubSubCategory exploreSubSubCategory;
    private AddProduct addProduct;
    private EditProduct editProduct;
    SharedPreferences user;

    ExploreSubSubWorker(ExploreSubSubCategory exploreSubSubCategory) {this.exploreSubSubCategory = exploreSubSubCategory;}
    ExploreSubSubWorker(AddProduct addProduct) {this.addProduct = addProduct;}
    ExploreSubSubWorker(EditProduct editProduct) {this.editProduct = editProduct;}


    @Override
    protected String[][] doInBackground(String[][]... params) {
        String REMOTE_URL;
        String[][] subSubCategories = null;
        String subcategory_id = params[0][0][0];
        if (addProduct == null && editProduct == null){user = exploreSubSubCategory.getSharedPreferences("MyInfo", ExploreSubCategory.MODE_PRIVATE);}
        else if (editProduct == null && exploreSubSubCategory == null){user = addProduct.getSharedPreferences("MyInfo", AddProduct.MODE_PRIVATE);}
        else if (exploreSubSubCategory == null && addProduct == null){user = editProduct.getSharedPreferences("MyInfo", EditProduct.MODE_PRIVATE);}
        String access_key = user.getString("token", null);
        if (subcategory_id.equals("all")){REMOTE_URL = Config.remoteHost + "/api/v2/subsubcategory.php?token=" + access_key;}
        else{REMOTE_URL = Config.remoteHost + "/api/v2/subsubcategory.php?subcategory_id=" + subcategory_id + "&token=" + access_key;}
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
                subSubCategories = new String[jsonArray.length()][3];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    subSubCategories[i][0] = jsonObject.getString("id");
                    subSubCategories[i][1] = jsonObject.getString("name");
                    subSubCategories[i][2] = jsonObject.getString("totalProducts");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return subSubCategories;
    }

    @Override
    protected void onPreExecute() {
        if (addProduct == null && editProduct == null){pDialog = new ProgressDialog(exploreSubSubCategory, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Loading...");
        }
        else if (editProduct == null && exploreSubSubCategory == null){pDialog = new ProgressDialog(addProduct, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Loading...");
        }
        else if (exploreSubSubCategory == null && addProduct == null){
            pDialog = new ProgressDialog(editProduct, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setMessage("Loading even more categories...");
        }
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setInverseBackgroundForced(true);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(String[][] subSubCategories) {
        if (addProduct == null && editProduct == null){exploreSubSubCategory.setSubSubCategories(subSubCategories);}
        else if (editProduct == null && exploreSubSubCategory == null){addProduct.setSubSubCategories(subSubCategories);}
        else if (exploreSubSubCategory == null && addProduct == null){editProduct.setSubSubCategories(subSubCategories);}
        pDialog.dismiss();
    }
}
