package com.cbsjchen0601;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.cbsjchen0601.models.Yaks;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Random;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "LocationActivity";
    private SupportMapFragment supportMapFragment;
    private BottomNavigationView bottomNavigationView;
    private SearchView searchView;
    private GoogleMap googleMap;
    private Button btnLocate, btnRoam;
    private Location currentLocation;
    private LatLng currentLatLng;
    private FusedLocationProviderClient fusedLocateProviderCLient;
    private static final int REQUEST_CODE = 101;
    private final Random random = new Random();
    private final Handler handler = new Handler();
    private static final double radius = 0.12;
    private boolean roamStatus = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        btnLocate = findViewById(R.id.locate);
        btnRoam = findViewById(R.id.btnRoam);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        searchView = findViewById(R.id.sv_location);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        fusedLocateProviderCLient = LocationServices.getFusedLocationProviderClient(this);

        fetchLastLocation();

        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
        btnLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRandomMarkers(currentLatLng);
            }
        });

        btnRoam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
                roamStatus = true;
                moveEveryXseconds();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(rippleBackground.isRippleAnimationRunning()) {
                    rippleBackground.stopRippleAnimation();
                    handler.removeCallbacksAndMessages(null);
                }
                //GRPC ISSUE
                /*
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equalsIgnoreCase("")) {
                    Geocoder geocoder = new Geocoder(LocationActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }*/

                String queryLocation = searchView.getQuery().toString().toLowerCase();
                if (queryLocation.equalsIgnoreCase("New York")) {
                    roamStatus = true;
                    moveCameraPosition(40.7128, -74.0060, queryLocation);
                } else if (queryLocation.equalsIgnoreCase("California")) {
                    roamStatus = true;
                    moveCameraPosition(36.7783, -119.4179, queryLocation);
                } else {
                    Toast.makeText(LocationActivity.this, "Location not yet supported!", Toast.LENGTH_LONG).show();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }

    private void moveEveryXseconds() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                freeRoam();
                handler.postDelayed(this, 3000);
                Toast.makeText(LocationActivity.this,
                        "Updated: " + currentLatLng.latitude + " ," + currentLatLng.longitude,
                        Toast.LENGTH_SHORT).show();
            }
        }, 3000);

    }


    private void freeRoam() {

        Marker marker = googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("You're here!"));
        LatLng randLatLng = generateRandomLatLng(currentLatLng);
        animateMarker(marker, randLatLng, false);
        googleMap.clear();
        generateRandomMarkers(randLatLng);
        moveCameraPosition(randLatLng.latitude, randLatLng.longitude, "You're here!");

        /*
        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(randLatLng.latitude, randLatLng.longitude))
                .zoom(16)
                .bearing(0)
                .tilt(45)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);*/
    }

    private void moveCameraPosition(double lat, double lng, String locationName) {
        LatLng latLng = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(latLng).title(locationName));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(12000)
                .strokeWidth(3f)
                .strokeColor(Color.BLUE));
        currentLatLng = latLng;
    }

    private void fetchLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocateProviderCLient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    supportMapFragment.getMapAsync(LocationActivity.this);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
        currentLocation = null;
        currentLatLng = null;
        googleMap.clear();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.YakPost:
                            Intent yakIntent = new Intent(LocationActivity.this, MainActivity.class);
                            if(roamStatus)
                                yakIntent.putExtra("updatedData", true);
                            startActivity(yakIntent);
                            return true;
                        case R.id.GPS:
                            break;
                        case R.id.Notifcation:
                            Intent miscIntent = new Intent(LocationActivity.this, MiscActivity.class);
                            startActivity(miscIntent);
                            return true;
                    }
                    return false;
                }
            };

    private void createMarker(double latitude, double longitude) {

        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .anchor(0.5f, 0.5f)
                .title("Nearby Yakers!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        currentLatLng = latLng;
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

        googleMap.addMarker(new MarkerOptions().position(latLng).title("You're here!"));
        googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(12000)
                .strokeWidth(3f)
                .strokeColor(Color.BLUE));

        //Marker marker = googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("You're here!"));
        //animateMarker(marker);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
            default:
                break;
        }
    }

    private void generateRandomCoordinatesWithinRadius(LatLng currentLocation) {
        double a = currentLocation.longitude;
        double b = currentLocation.latitude;
        double r = radius;

        // x must be in (a-r, a + r) range
        double xMin = a - r;
        double xMax = a + r;
        double xRange = xMax - xMin;

        // get a random x within the range
        double x = xMin + random.nextDouble() * xRange;

        // circle equation is (y-b)^2 + (x-a)^2 = r^2
        // based on the above work out the range for y
        double yDelta = Math.sqrt(Math.pow(r, 2) - Math.pow((x - a), 2));
        double yMax = b + yDelta;
        double yMin = b - yDelta;
        double yRange = yMax - yMin;
        // Get a random y within its range
        double y = yMin + random.nextDouble() * yRange;

        createMarker(y, x);
    }

    private LatLng generateRandomLatLng(LatLng currentLocation) {
        double a = currentLocation.longitude;
        double b = currentLocation.latitude;
        double r = radius;

        // x must be in (a-r, a + r) range
        double xMin = a - r;
        double xMax = a + r;
        double xRange = xMax - xMin;

        // get a random x within the range
        double x = xMin + random.nextDouble() * xRange;

        // circle equation is (y-b)^2 + (x-a)^2 = r^2
        // based on the above work out the range for y
        double yDelta = Math.sqrt(Math.pow(r, 2) - Math.pow((x - a), 2));
        double yMax = b + yDelta;
        double yMin = b - yDelta;
        double yRange = yMax - yMin;
        // Get a random y within its range
        double y = yMin + random.nextDouble() * yRange;

        return new LatLng(y, x);
    }

    private void generateRandomMarkers(LatLng latLng) {

        for (int i = 0; i < 10; i++) {
                generateRandomCoordinatesWithinRadius(latLng);
        }
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
}