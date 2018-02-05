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

class ProductListWorker extends AsyncTask<String[][], Void, String[][]> {

    private ProgressDialog pDialog;
    private ListProduct listProduct;
    private MyProduct myProduct;

    ProductListWorker(ListProduct listProduct) {
        this.listProduct = listProduct;
    }
    ProductListWorker(MyProduct myProduct) {
        this.myProduct = myProduct;
    }

    @Override
    protected String[][] doInBackground(String[][]... params) {
        String[][] products = null;
        String getBy = params[0][0][0];
        String REMOTE_URL = null;
        if(getBy.equals("all")) {
            SharedPreferences user = listProduct.getSharedPreferences("MyInfo", ExploreSubCategory.MODE_PRIVATE);
            String access_key = user.getString("token", null);
             REMOTE_URL = Config.remoteHost + "/api/v2/product.php?token=" + access_key;
        }else if (getBy.equals("subSubCategory")){
            SharedPreferences user = listProduct.getSharedPreferences("MyInfo", ExploreSubCategory.MODE_PRIVATE);
            String access_key = user.getString("token", null);
            String subSubcategory_id = params[0][0][1];
            REMOTE_URL = Config.remoteHost + "/api/v2/product.php?subsubcategory_id=" + subSubcategory_id + "&token=" + access_key;
        }else if(getBy.equals("myProduct")){
            SharedPreferences user = myProduct.getSharedPreferences("MyInfo", MyProduct.MODE_PRIVATE);
            String access_key = user.getString("token", null);
            String farmer_id = user.getString("person_id", null);
            REMOTE_URL = Config.remoteHost + "/api/v2/product.php?farmer_id=" + farmer_id + "&token=" + access_key;
        }
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
                products = new String[jsonArray.length()][7];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    products[i][0] = jsonObject.getString("id");
                    products[i][1] = jsonObject.getString("name");
                    products[i][2] = jsonObject.getString("currency") + " " + jsonObject.getString("price") + " / " + jsonObject.getString("unit");
                    products[i][3] = jsonObject.getString("location");
                    products[i][4] = jsonObject.getString("created_at");
                    products[i][5] = jsonObject.getString("peopleid");
                    products[i][6] = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
                }
                bufferedReader.close();
                inputStream.close();
            }
            httpURLConnection.disconnect();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return products;
    }

    @Override
    protected void onPreExecute() {
        if (myProduct == null){
            pDialog = new ProgressDialog(listProduct, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        }else{
            pDialog = new ProgressDialog(myProduct, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        }
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading products...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setInverseBackgroundForced(true);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(String[][] products) {
        if (myProduct == null){
            listProduct.setProducts(products);
        }else{
            myProduct.setProducts(products);
        }
        pDialog.dismiss();
    }
}
