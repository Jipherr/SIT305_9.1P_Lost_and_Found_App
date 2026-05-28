package com.s224776282.a91lostandfoundapp;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;

    private Location currentUserLocation;
    private List<Item> allItems;
    private int currentRadiusKm = 10;

    private TextView txtRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        dbHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        txtRadius = findViewById(R.id.txtRadius);
        SeekBar seekBarRadius = findViewById(R.id.seekBarRadius);

        allItems = dbHelper.getItems("All");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentRadiusKm = Math.max(1, progress);
                txtRadius.setText("Search Radius: " + currentRadiusKm + " km");

                updateMapMarkers();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentUserLocation = location;

                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f));

                    updateMapMarkers();
                } else {
                    Toast.makeText(this, "Could not find your location.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Location permission is required for radius search.", Toast.LENGTH_LONG).show();
        }
    }

    private void updateMapMarkers() {
        if (mMap == null || currentUserLocation == null) return;

        mMap.clear();

        for (Item item : allItems) {
            if (item.getLatitude() == 0.0 && item.getLongitude() == 0.0) continue;

            Location itemLocation = new Location("");
            itemLocation.setLatitude(item.getLatitude());
            itemLocation.setLongitude(item.getLongitude());

            float distanceInMeters = currentUserLocation.distanceTo(itemLocation);
            float distanceInKm = distanceInMeters / 1000f;

            if (distanceInKm <= currentRadiusKm) {
                LatLng position = new LatLng(item.getLatitude(), item.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(item.getPostType() + ": " + item.getDescription())
                        .snippet(item.getCategory() + " (approx " + String.format("%.1f", distanceInKm) + " km away)"));
            }
        }
    }
}