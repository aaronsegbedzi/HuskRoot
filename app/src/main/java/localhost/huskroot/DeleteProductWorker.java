package localhost.huskroot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Aaron Segbedzi on 11/30/2017.
 */

class DeleteProductWorker extends AsyncTask<String, Void, Integer>{

    private ProgressDialog pDialog;
    private Context mContext;

    DeleteProductWorker(Context mContext){this.mContext = mContext;}

    @Override
    protected Integer doInBackground(String... params) {
        int HTTP_RESPONSE_CODE = 500;
        String product_id = params[0];
        SharedPreferences user = mContext.getSharedPreferences("MyInfo", Context.MODE_PRIVATE);
        String REMOTE_URL = Config.remoteHost + "/api/v2/product.php?id=" + product_id + "&farmer_id=" + user.getString("person_id", null) + "&token=" + user.getString("token", null);
        try {
            URL url = new URL(REMOTE_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("DELETE");
            httpURLConnection.setRequestProperty("Content-length", "0");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setConnectTimeout(100000);
            httpURLConnection.setReadTimeout(100000);
            httpURLConnection.connect();
            HTTP_RESPONSE_CODE = httpURLConnection.getResponseCode();
            httpURLConnection.disconnect();
        } catch (IOException e) {e.printStackTrace();}
        return HTTP_RESPONSE_CODE;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(mContext, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pDialog.setMessage("Deleting product...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setInverseBackgroundForced(true);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(Integer HTTP_RESPONSE_CODE) {
        pDialog.dismiss();
        if (HTTP_RESPONSE_CODE != 200){ Toast.makeText(mContext, "Failed to delete product. Try again.", Toast.LENGTH_SHORT).show();}
        else{ Toast.makeText(mContext, "Product has been deleted.", Toast.LENGTH_SHORT).show(); mContext.startActivity(new Intent(mContext, MyProduct.class)); }
    }
}

