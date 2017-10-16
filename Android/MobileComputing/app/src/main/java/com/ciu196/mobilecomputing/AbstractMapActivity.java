package com.ciu196.mobilecomputing;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.geometry.Bounds;

/**
 * Created by alexanderalvmo on 2017-10-16.
 */

public class AbstractMapActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnCameraMoveListener {
    //CONSTANTS
    protected static final int FINE_LOCATION_REQUEST_CODE = 99;
    public static final double LIB_LNG = 11.979743;
    public static final double LIB_LAT = 57.697844;
    protected static final int NUMBER_OF_CIRCLES = 4;
    protected static final int CIRCLE_RADIUS_INC = 300;
    protected static final int CENTER_MAP_PADDING = 300;
    public static final String TAG = "MAP_ACTIVITY";
    public static final int PERFECT_ZOOM_LEVEL = 13;

    //variables
    protected int play_zone = 2;
    protected GoogleMap mMap;
    protected LatLng libLatLng;

    LocationManager locationManager;
    protected int[] circleColors = {
            Color.argb(255, 0,0,255),
            Color.argb(255, 0,255,0),
            Color.argb(255, 255,0,0),
            Color.argb(255,255,255,0)
    };

    GoogleMap.CancelableCallback mapCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Library
        libLatLng = new LatLng(LIB_LAT, LIB_LNG);
    }



    public void redrawMap(){
        if(mMap != null){
            //clear map
            mMap.clear();

            //draw circles
            for(int i = NUMBER_OF_CIRCLES; i > 0; i--){
                int radius = CIRCLE_RADIUS_INC*(i+1);

                if(i <= play_zone){
                    int color = circleColors[i-1];
                    drawCircleOnMap(libLatLng, color, radius);
                } else {
                    drawCircleOnMap(libLatLng, Color.argb(55, 0, 0, 0), radius);
                }

            }

            //add library marker
            mMap.addMarker(new MarkerOptions()
                    .position(libLatLng)
                    .title("Stadsbiblioteket, Göteborg")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_local_library_white_24dp)));


            //center map
            /*
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(libLatLng);
            if(userLatLng != null){
                builder.include(userLatLng);
            }
            LatLngBounds bounds = builder.build();
            */
            //centerMap(libLatLng);

        }
    }

    public void centerMap(LatLng center){
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(center, PERFECT_ZOOM_LEVEL);
        mMap.animateCamera(cu, 300, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onCancel() {

            }
        });
        /*
        int padding = CENTER_MAP_PADDING; // offset from edges of the map in pixels
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(b, width, height, padding);
        mMap.animateCamera(cu, 300, callback);*/
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
        Log.i(TAG, "Map ready");
        mMap = googleMap;
        mMap.setOnCameraMoveListener(this);
        //Change style of the map
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));
            if (success) {
                Toast.makeText(this, "Map styling successful", Toast.LENGTH_LONG).show();
            }
        } catch (Resources.NotFoundException e) {
            Toast.makeText(this, "Map styling failed", Toast.LENGTH_LONG).show();
        }

        // draw map with all the markers etc...
        redrawMap();
        centerMap(libLatLng);
    }


    public void drawCircleOnMap(LatLng center, int color, int radius){
        mMap.addCircle(new CircleOptions()
                .center(center)
                .radius(radius)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(color));
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCameraMove() {

    }


}
