package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String latitude2;
    String longitude2;

    Double latitude3;
    Double longitude3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationset();
        backset();
    }
    public void locationset(){
        Intent intent = getIntent();
        //System.out.println(intent);
        latitude2 = intent.getStringExtra("latitude1");//string型
        longitude2 = intent.getStringExtra("longitude1");//string型

        latitude3 = Double.parseDouble(latitude2);//double型
        longitude3 = Double.parseDouble(longitude2);//double型
    }
    public void backset(){
        Button mainactivity = findViewById(R.id.back);
        mainactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //db.close();
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
            }
        });
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
        mMap = googleMap;

        //System.out.println(latitude3);
        //System.out.println(longitude3);
        // Add a marker in Sydney and move the camera
        LatLng maps = new LatLng(latitude3,longitude3);
        mMap.addMarker(new MarkerOptions().position(maps).title("Google Maps"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(maps));
    }
}
