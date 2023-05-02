package com.example.astroseeing;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentOnAttachListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Getting parser;
    private MyViewModel viewModel;
    public boolean offline;
    public String request;
    private int show_hours = 1;
    FusedLocationProviderClient mFusedLocationClient;
    TextView latitudeTextView, longitTextView;
    int PERMISSION_ID = 44;

    private ArrayList<ArrayList<String>> table = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.viewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        this.viewModel.setDb(getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null));

        this.viewModel.getDb().execSQL("CREATE TABLE IF NOT EXISTS places (city TEXT, url TEXT)");

        MutableLiveData<String> place = this.viewModel.getPlace();
        place.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String value) {
                parser = new Getting(viewModel);
                parser.execute();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bot_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        openFragment(Home.newInstance("", ""));
                        return true;
                    case R.id.navigation_seeing:
                        openFragment(Seeing.newInstance());
                        return true;
                    case R.id.navigation_light_pollution:
                        openFragment(Light_pollution.newInstance("", ""));
                        return true;
                }
                return false;
            }
        });
    }

    private void openFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        Log.d(TAG, "openFragment: ");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView2, fragment);
        transaction.addToBackStack(null); //if you add fragments it will be added to the backStack. If you replace the fragment it will add only the last fragment
        transaction.commit(); // commit() performs the action
    }

    private void getLastLocation() {
        if (isLocationEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
                return;
            }
            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        String latit, longit;
                        if(latitude > 0){
                            latit = String.format("%.3f", latitude) + "E";
                        }
                        else{
                            latit = String.format("%.3f", -latitude) + "W";
                        }
                        if(longitude > 0){
                            longit = String.format("%.3f", longitude) + "S";
                        }
                        else{
                            longit = String.format("%.3f", 180 + longitude) + "N";
                        }
                        viewModel.setLocation(longit + latit);
                    }
                }
            });
        } else {
            Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    /*@Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }*/

    /*@Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }*/
}