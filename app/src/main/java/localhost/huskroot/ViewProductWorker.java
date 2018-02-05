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
 * Created by Aaron Segbedzi on 11/23/2017.
 */

 class ViewProductWorker extends AsyncTask<String, Void, String> {

    private ViewProduct viewProduct;
    private EditProduct editProduct;
    private ProgressDialog pDialog;
    SharedPreferences user;

    ViewProductWorker(ViewProduct viewProduct){
        this.viewProduct = viewProduct;
    }
    ViewProductWorker(EditProduct editProduct) { this. editProduct = editProduct; }

    @Override
    protected String doInBackground(String... params) {
        String product_id = params[0];
        String product = null;

        if (editProduct == null){  user = viewProduct.getSharedPreferences("MyInfo", ViewProduct.MODE_PRIVATE);
        }else if (viewProduct == null){  user = editProduct.getSharedPreferences("MyInfo", EditProduct.MODE_PRIVATE);}

        String access_key = user.getString("token", null);
        String REMOTE_URL = Config.remoteHost + "/api/v2/product.php?id=" + product_id + "&token=" + access_key;

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
                product = bufferedReader.readLine();
                bufferedReader.close();
                inputStream.close();
            }
            httpURLConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return product;
    }

    @Override
    protected void onPreExecute() {
        if (editProduct == null){pDialog = new ProgressDialog(viewProduct, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);}
        else if(viewProduct == null){pDialog = new ProgressDialog(editProduct, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);}
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setInverseBackgroundForced(true);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(String product) {
        if (editProduct == null){viewProduct.setProduct(product);}
        else if(viewProduct == null){editProduct.setProduct(product);}
        pDialog.dismiss();
    }
}
