package localhost.huskroot;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Aaron Segbedzi on 11/27/2017.
 */

public class AddProduct extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerDragListener {

    final int CAMERA_REQUEST = 6789, GALLERY_REQUEST = 9876, VIDEO_REQUEST = 6978;
    private GoogleMap mMap;
    ImageButton btnCamera, btnGallery;
    ImageView productImage;
    Spinner SubSubCategory;
    double latitude, longitude;
    EditText Name, Price, Unit, Description, Location, Currency;
    Button audioRecordStart, audioRecordStop, audioPlay, audioPause, btnVideo, btnLocation;
    String VideoSavePathInDevice = null, ImageSavePathInDevice = null, AudioSavePathInDevice = null, RandomAudioFileName = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    VideoView videoView;
    MediaController mediaController;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;
    ProgressDialog pDialog;
    HashMap<Integer,String> spinnerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//      START ACTIVITY.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_form);

        String[][] action = {{"all"}};
        ExploreSubSubWorker exploreSubSubWorker = new ExploreSubSubWorker(this);
        exploreSubSubWorker.execute(action);

//      SET POLICY FOR THIS ACTIVITY.
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

//      SET ACTIVITY ACTIONBAR FEATURES.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Husk Root - Add a Crop");


//      SET ACTIVITY GOOGLE MAP FEATURE.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//      BEGIN CAMERA FEATURE FOR PRODUCT IMAGE.
        cameraPhoto = new CameraPhoto(this);
        btnCamera = (ImageButton) findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                    cameraPhoto.addToGallery();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

//      BEGIN GALLERY FEATURE FOR PRODUCT IMAGE.
        galleryPhoto = new GalleryPhoto(this);
        btnGallery = (ImageButton) findViewById(R.id.btnGallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
            }
        });


//      BEGIN FORM FEATURE FOR PRODUCT INFORMATION.
        Name = (EditText) findViewById(R.id.name);
        Price = (EditText) findViewById(R.id.price);
        Currency = (EditText) findViewById(R.id.currency);
        Unit = (EditText) findViewById(R.id.unit);
        Description = (EditText) findViewById(R.id.description);
        Location = (EditText) findViewById(R.id.location);
        SubSubCategory = (Spinner) findViewById(R.id.subSubCategory);
        videoView = (VideoView) findViewById(R.id.videoView);
        productImage = (ImageView) findViewById(R.id.productImage);


//      BEGIN AUDIO RECORDING FEATURE FOR PRODUCT AUDIO.
        audioRecordStart = (Button) findViewById(R.id.audioRecordStart);
        audioRecordStart.setEnabled(true);
        audioRecordStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File directory = new File(getFilesDir().getAbsolutePath() + "/HuskRootMedia/");
                if (!directory.exists()){ directory.mkdir(); }
                AudioSavePathInDevice =
                        getFilesDir().getAbsolutePath() + "/HuskRootMedia/" +
                                CreateRandomFileName(5) + "AudioRecording.wav";
                AudioMediaRecorderReady();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();

                    audioRecordStart.setEnabled(false);
                    audioPlay.setEnabled(false);
                    audioPause.setEnabled(false);

                    audioRecordStop.setEnabled(true);

                    Snackbar snackbar = Snackbar.make(view, "Recording has started.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
                    View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorSuccess));
                    snackbar.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        audioRecordStop = (Button) findViewById(R.id.audioRecordStop);
        audioRecordStop.setEnabled(false);
        audioRecordStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();

                audioRecordStart.setEnabled(true);
                audioPlay.setEnabled(true);

                audioRecordStop.setEnabled(false);
                audioPause.setEnabled(false);

                Snackbar snackbar = Snackbar.make(view, "Recording completed.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
                View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorSuccess));
                snackbar.show();
            }
        });

        audioPlay = (Button) findViewById(R.id.audioPlay);
        audioPlay.setEnabled(false);
        audioPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioRecordStart.setEnabled(false);
                audioRecordStop.setEnabled(false);

                audioPlay.setEnabled(true);
                audioPause.setEnabled(true);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Snackbar snackbar = Snackbar.make(view, "Playing the recorded audio file.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
                    View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorInfo));
                    snackbar.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        audioPause = (Button) findViewById(R.id.audioPause);
        audioPause.setEnabled(false);
        audioPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioRecordStart.setEnabled(true);
                audioPlay.setEnabled(true);

                audioPause.setEnabled(false);
                audioRecordStop.setEnabled(false);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    AudioMediaRecorderReady();
                }
            }
        });

//      BEGIN VIDEO RECORDING FEATURE FOR PRODUCT VIDEO.
        btnVideo = (Button) findViewById(R.id.btnVideo);
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(intent, VIDEO_REQUEST);
            }
        });

//      BEGIN USER CURRENT LOCATION FEATURE FOR PRODUCT LOCATION.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }

        Button btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddProduct.this.uploadMultipart(view);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                ImageSavePathInDevice = cameraPhoto.getPhotoPath();
                try {
                    Bitmap bitmap = ImageLoader.init().from(ImageSavePathInDevice).requestSize(200, 200).getBitmap();
                    productImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == GALLERY_REQUEST) {
                Uri uri = data.getData();
                galleryPhoto.setPhotoUri(uri);
                ImageSavePathInDevice = galleryPhoto.getPath();
                try {
                    Bitmap bitmap = ImageLoader.init().from(ImageSavePathInDevice).requestSize(200, 200).getBitmap();
                    productImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == VIDEO_REQUEST) {
                Uri uri = data.getData();
                VideoSavePathInDevice = uri.toString();
                if (mediaController == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mediaController = new MediaController(AddProduct.this);
                        mediaController.setAnchorView(videoView);
                        videoView.setMediaController(mediaController);
                        videoView.setVideoPath(VideoSavePathInDevice);
                        videoView.requestFocus();
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            public void onPrepared(MediaPlayer mp) {
                                videoView.start();
                            }
                        });
                    }
                }

            }
        }
    }

    protected void AudioMediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomFileName(int string) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));
            i++;
        }
        return stringBuilder.toString();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng marker = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(marker).title("Your Marked Crop Location.").draggable(true));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 10));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerDragListener(this);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {}

    @Override
    public void onMarkerDrag(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {}

    public void uploadMultipart(View view) {
        SharedPreferences user = getSharedPreferences("MyInfo", MODE_PRIVATE);
        String access_key = user.getString("token", null);
        String productName = Name.getText().toString();
        String productPrice = Price.getText().toString();
        String productCurrency = Currency.getText().toString();
        String productLocation = Location.getText().toString();
        String productUnit = Unit.getText().toString();
        String productSubSubCategory = spinnerMap.get(SubSubCategory.getSelectedItemPosition());
        String productDescription = Description.getText().toString();
        String productLatitude = Double.toString(latitude);
        String productLongitude = Double.toString(longitude);
        if (!productName.matches("")
                && !productPrice.matches("")
                && !productCurrency.matches("")
                && !productUnit.matches("")
                && !productSubSubCategory.matches("")) {
            pDialog = new ProgressDialog(AddProduct.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setTitle("Uploading Product");
            pDialog.setMax(100);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setInverseBackgroundForced(true);
            pDialog.show();
            try {
                MultipartUploadRequest multipartUploadRequest = new MultipartUploadRequest(this, Config.remoteHost + "/api/v2/product.php?token=" + access_key);
                if (AudioSavePathInDevice != null) {multipartUploadRequest.addFileToUpload(AudioSavePathInDevice, "audio");}
                if (VideoSavePathInDevice != null) {multipartUploadRequest.addFileToUpload(getRealPathFromUri(this ,Uri.parse(VideoSavePathInDevice)), "video");}
                if (ImageSavePathInDevice != null) {multipartUploadRequest.addFileToUpload(ImageSavePathInDevice, "img");}
                multipartUploadRequest.addParameter("name", productName)
                        .addParameter("price", productPrice)
                        .addParameter("currency", productCurrency)
                        .addParameter("unit", productUnit)
                        .addParameter("description", productDescription)
                        .addParameter("location", productLocation)
                        .addParameter("subsubcategory_id", productSubSubCategory)
                        .addParameter("lat", productLatitude)
                        .addParameter("lng", productLongitude);
                String uploadId = multipartUploadRequest
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .setDelegate(new UploadStatusDelegate() {
                            @Override
                            public void onProgress(Context context, UploadInfo uploadInfo) {
                                pDialog.setProgress(uploadInfo.getProgressPercent());
                            }

                            @Override
                            public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                                pDialog.dismiss();
                            }

                            @Override
                            public void onCompleted(Context context, UploadInfo uploadInfo, final ServerResponse serverResponse) {
                                pDialog.dismiss();
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Crop uploaded successfully.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
                                View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorSuccess));
                                snackbar.setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        Intent intent = new Intent(AddProduct.this, ViewProduct.class);
                                        Bundle bundle = new Bundle();
                                        try {
                                            JSONObject jsonObject  = new JSONObject(serverResponse.getBodyAsString());
                                            bundle.putString("product_id", jsonObject.getString("inserted_id"));
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                snackbar.show();
                            }

                            @Override
                            public void onCancelled(Context context,UploadInfo uploadInfo) {
                                pDialog.dismiss();
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Failed to upload crop. Try again.", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE);
                                View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorDanger));
                                snackbar.show();
                            }
                        }).startUpload();
            } catch (Exception exc) {
                Log.e("AndroidUploadService", exc.getMessage(), exc);
                Toast.makeText(AddProduct.this, exc.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else {
            Snackbar snackbar = Snackbar.make(view, "All fields required. Pleas try again.", Snackbar.LENGTH_SHORT)
                    .setActionTextColor(Color.WHITE);
            View mySbView = snackbar.getView(); mySbView.setBackgroundColor(getResources().getColor(R.color.ColorDanger));
            snackbar.show();
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Video.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    protected void setSubSubCategories(String[][] string){
        String[] spinnerArray = new String[string.length];
         spinnerMap = new HashMap<Integer, String>();
        for (int i = 0; i < string.length; i++) {
            spinnerMap.put(i,string[i][0]);
            spinnerArray[i] = string[i][1];
        }
        ArrayAdapter<String> adapter =new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SubSubCategory.setAdapter(adapter);
    }
}
