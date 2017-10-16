package com.ciu196.mobilecomputing;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;

public class MapsActivity extends AbstractMapActivity implements View.OnClickListener, GoogleMap.OnCameraMoveListener, LocationListener {

    FloatingActionButton fab;
    FloatingActionButton fabLib;
    protected LatLng userLatLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set layout of the activity
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Toolbar init
        Toolbar toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);

        //getSupportActionBar().setSubtitle("This is a subtitle");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updateTitle();

        //Fab init
        fab = (FloatingActionButton) findViewById(R.id.map_fab_center_my_location);
        fab.setOnClickListener(this);
        fabLib = (FloatingActionButton) findViewById(R.id.map_fab_center_library);
        fabLib.setOnClickListener(this);

    }



    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        initLocationTracking();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(locationManager != null){
            locationManager.removeUpdates(this);
        }
    }

    public void initLocationTracking(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_REQUEST_CODE);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "returning...", Toast.LENGTH_LONG).show();
            return;
        }

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        Location userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(userLocation != null){
            userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            updateTitle();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_fab_center_my_location:
                //do something
                //Toast.makeText(this, "FAAAB", Toast.LENGTH_SHORT).show();
                redrawMap();
                if(userLatLng != null){
                    centerMap(userLatLng);
                } else {
                    centerMap(libLatLng);
                    Toast.makeText(this, "Can't find user location...", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.map_fab_center_library:
                redrawMap();
                centerMap(libLatLng);
                break;
            default:
        }
    }


    @Override
    public void onCameraMove() {
        fab.show();
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
    @Override
    public void redrawMap(){
        super.redrawMap();
        //add user marker
        if(userLatLng != null){
            mMap.addMarker(new MarkerOptions()
                    .position(userLatLng)
                    .title("My Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location_pin_red)));
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Toast.makeText(this, location.toString(), Toast.LENGTH_LONG).show();
            redrawMap();
            centerMap(userLatLng);
            updateTitle();
        }
    }

    public void updateTitle(){
        getSupportActionBar().setTitle("Volume Range");

        if(userLatLng != null){
            double distance = SphericalUtil.computeDistanceBetween(libLatLng, userLatLng);
            DecimalFormat numberFormat = new DecimalFormat("#.00");
            double distanceKM = distance / 1000;
            getSupportActionBar().setSubtitle(numberFormat.format(distanceKM) + "km to the piano");
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case FINE_LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Granted", Toast.LENGTH_LONG).show();
                    initLocationTracking();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "Denied", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

}


