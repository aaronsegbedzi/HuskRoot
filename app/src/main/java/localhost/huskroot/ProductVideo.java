package localhost.huskroot;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.widget.MediaController;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

/**
 * Created by Aaron Segbedzi on 11/26/2017.
 */

public class ProductVideo extends AppCompatActivity {

    private VideoView videoView;
    private MediaController mediaController;
    private ProgressDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_video);
        Bundle bundle = getIntent().getExtras();
        pDialog = new ProgressDialog(ProductVideo.this);
        pDialog.setMessage("Buffering");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        if (bundle != null){
            try {
                getSupportActionBar().setTitle(bundle.getString("videoTitle"));
                videoView = (VideoView)findViewById(R.id.videoView);
                if (mediaController == null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mediaController = new MediaController(ProductVideo.this);
                        mediaController.setAnchorView(videoView);

                    }
                }
                videoView.setMediaController(mediaController);
                videoView.setVideoPath(bundle.getString("videoLink"));
                videoView.requestFocus();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        pDialog.dismiss();
                        videoView.start();
                    }
                });
            }catch (Exception e){e.printStackTrace();}
        }
    }
}
