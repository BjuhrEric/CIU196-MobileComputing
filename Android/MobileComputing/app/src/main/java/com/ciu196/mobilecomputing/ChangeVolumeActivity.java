package com.ciu196.mobilecomputing;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class ChangeVolumeActivity extends AbstractMapActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton fabPlus;
    private FloatingActionButton fabMinus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_volume);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Toolbar init
        Toolbar toolbar = (Toolbar) findViewById(R.id.map_volume_toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Broadcast Volume");
        //getSupportActionBar().setSubtitle("This is a subtitle");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Fab init
        fabMinus = (FloatingActionButton) findViewById(R.id.volume_minus_fab);
        fabMinus.setOnClickListener(this);
        fabPlus = (FloatingActionButton) findViewById(R.id.volume_plus_fab);
        fabPlus.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.volume_minus_fab:
                play_zone--;
                if(play_zone < 1){
                    play_zone = 1;
                }
                redrawMap();
                centerMap(libLatLng);
                break;
            case R.id.volume_plus_fab:
                play_zone++;
                if(play_zone > NUMBER_OF_CIRCLES){
                    play_zone = NUMBER_OF_CIRCLES;

                }
                redrawMap();
                centerMap(libLatLng);
            default:
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
    }

    /*
    @Override
    public void centerMap(LatLngBounds b, GoogleMap.CancelableCallback callback){
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(libLatLng, 15);
        mMap.animateCamera(cu, 300, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onCancel() {

            }
        });
    }*/
}
