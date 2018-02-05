package localhost.huskroot;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewProduct extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            ViewProductWorker viewProductWorker = new ViewProductWorker(this);
            viewProductWorker.execute(bundle.getString("product_id"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_explore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, Dashboard.class));
                return true;
            case R.id.refresh:
                finish(); startActivity(getIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setProduct(final String product){
        try {

            final JSONObject jsonObject = new JSONObject(product);

            final String productName = jsonObject.getString("name");
            getSupportActionBar().setTitle("HuskRoot/Products/" + productName);
            ((TextView)findViewById(R.id.productName)).setText(productName);

            final String productLocation = jsonObject.getString("location");
            ((TextView)findViewById(R.id.productLocation2)).setText(productLocation);

            final String productCurrency = jsonObject.getString("currency");
            ((TextView)findViewById(R.id.productCur2)).setText(productCurrency);

            final String productUnit = jsonObject.getString("unit");
            ((TextView)findViewById(R.id.productUnit2)).setText(productUnit);

            final String productPrice = jsonObject.getString("price");
            ((TextView)findViewById(R.id.productPrice1)).setText(productCurrency + " " + productPrice + " / " + productUnit);
            ((TextView)findViewById(R.id.productPrice3)).setText(productPrice);

            final String productFarmer = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
            ((TextView)findViewById(R.id.productFarmer1)).setText(productFarmer);

            final String productDesc = jsonObject.getString("description");
            ((TextView)findViewById(R.id.productDesc2)).setText(productDesc);

            final String productDate = jsonObject.getString("created_at");
            ((TextView)findViewById(R.id.productDate2)).setText(productDate);

            ImageView imageView = (ImageView)findViewById(R.id.productImage);
            String imageRemotePath = Config.remoteHost + "/assets/img/products/" + jsonObject.getString("peopleid") + "/" + jsonObject.getString("id") + ".jpg";
            if (remoteFileExists(imageRemotePath)){Glide.with(this).load(imageRemotePath).into(imageView);}

            String audioRemotePath = Config.remoteHost + "/assets/audio/products/" + jsonObject.getString("peopleid") + "/" + jsonObject.getString("id") + ".wav";
            if (remoteFileExists(audioRemotePath)){
                ImageView audioStart, audioPause;
                audioStart = (ImageView)findViewById(R.id.audioStart);
                audioPause = (ImageView)findViewById(R.id.audioPause);

                final MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(Config.remoteHost + "/assets/audio/products/" + jsonObject.getString("peopleid") + "/" + jsonObject.getString("id") + ".wav");
                mediaPlayer.prepare();
                audioStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mediaPlayer.start();
                    }
                });
                audioPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mediaPlayer.pause();
                    }
                });
            }

            final TextView showPhone, showSMS, showEmail;
            showPhone = (TextView)findViewById(R.id.productPhone);
            showEmail = (TextView)findViewById(R.id.productEmail);
            showSMS = (TextView)findViewById(R.id.productSMS);

            showPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + jsonObject.getString("phone")));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            showEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String mailto = "mailto:" + jsonObject.getString("email");
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse(mailto));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            showSMS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String number = jsonObject.getString("phone");  // The number on which you want to send SMS
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.fromParts("sms", number, null));
                        intent.putExtra("sms_body","Dear " + productFarmer + ", I am interested in your product ("
                                + productName + "). Please contact me. Thank you.");
                        startActivity(intent);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });

            Button btnVideo = (Button) findViewById(R.id.btnVideo);
            String videoRemotePath = Config.remoteHost + "/assets/video/products/" + jsonObject.getString("peopleid") + "/" + jsonObject.getString("id") + ".mp4";
            if (remoteFileExists(videoRemotePath)) {
                btnVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ViewProduct.this, ProductVideo.class);
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putString("videoLink", Config.remoteHost + "/assets/video/products/" + jsonObject.getString("peopleid") + "/" + jsonObject.getString("id") + ".mp4");
                            bundle.putString("videoTitle", "Crop Video: " + productName);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else{btnVideo.setText("Not Available");}

            Button btnMap = (Button)findViewById(R.id.btnMap);
            btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewProduct.this, ProductMapsActivity.class);
                    Bundle bundle = new Bundle();
                    try {
                        bundle.putString("lat", jsonObject.getString("lat"));
                        bundle.putString("lng", jsonObject.getString("lng"));
                        bundle.putString("productName", productName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } catch (JSONException e) {e.printStackTrace();}
                    }
                });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    protected static boolean remoteFileExists(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(URLName).openConnection();
            httpURLConnection.setRequestMethod("HEAD");
            httpURLConnection.setConnectTimeout(100000);
            httpURLConnection.connect();
            httpURLConnection.disconnect();
            return httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
