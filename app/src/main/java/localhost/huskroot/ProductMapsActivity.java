package localhost.huskroot;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ProductMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mMap = googleMap;
            LatLng productLocation = new LatLng(Double.parseDouble(bundle.getString("lat")), Double.parseDouble(bundle.getString("lng")));
            mMap.addMarker(new MarkerOptions().position(productLocation).title("Location of " + bundle.getString("productName")));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(productLocation, 14));
        }
    }
}
