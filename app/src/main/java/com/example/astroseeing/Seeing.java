package com.example.astroseeing;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.location.LocationManager.NETWORK_PROVIDER;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Looper;
import android.provider.Settings;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Seeing#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Seeing extends Fragment {
    FusedLocationProviderClient mFusedLocationClient;
    TextView latitudeTextView, longitTextView;
    int PERMISSION_ID = 44;
    private Button updateButton;

    private Getting parser;
    private Table table;

    private MyViewModel model;

    public Seeing() {
        // Required empty public constructor
    }

    public static Seeing newInstance() {
        Seeing fragment = new Seeing();
        return fragment;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            table = (Table)getArguments().getSerializable("table");
        };
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getLastLocation();
        model = ViewModelProviders.of(getActivity()).get(MyViewModel.class);
        View view = inflater.inflate(R.layout.fragment_seeing, container, false);
        updateButton = view.findViewById(R.id.update_button);

        MutableLiveData<String> location = model.getLocation();
        location.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String value) {
                TextView text = view.findViewById(R.id.textView2);
                String loc = "location: " + model.getLocation().getValue();
                text.setText(loc);
            }
        });

        TextView text = view.findViewById(R.id.textView2);
        String loc = "location: " + model.getLocation().getValue();
        text.setText(loc);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
                updateButton.setText(getResources().getString(R.string.loading_text));
                updateButton.setBackgroundColor(getResources().getColor(R.color.loadingColor));
                model = ViewModelProviders.of(getActivity()).get(MyViewModel.class);
                parser = new Getting(model);
                parser.execute();
            }
        });

        model.getSelected().observe(getViewLifecycleOwner(), item -> {
            setTable(item);
            Spinner spinner = view.findViewById(R.id.spinner);
            TableLayout tableLayout = getActivity().findViewById(R.id.tableLayout1);
            tableLayout.removeAllViews();
            int size = 15;
            int [] arr;
            int [] arr1 = {0, 1, 2, 3, 4, 5, 6};
            int [] arr2 = {0, 5, 6};
            int [] arr3 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
            if(spinner.getSelectedItemPosition() == 1) {
                arr = arr1;
            }
            else if(spinner.getSelectedItemPosition() == 2){
                arr = arr2;
            }
            else{
                arr = arr3;
                size = 11;
            }
            TextView column;
            TableRow row = new TableRow(getContext());
            String[] tableHeader = getResources().getStringArray(R.array.table_array);
            for (int i : arr) {
                column = new TextView(getContext());
                column.setText(tableHeader[i]);
                column.setTextSize(size);
                row.addView(column);
            }
            tableLayout.addView(row);
            for (int j = 0; j < table.data.size(); j++) {
                row = new TableRow(getContext());
                column = new TextView(getContext());
                for (int i : arr) {
                    int color1;
                    try {
                        column = new TextView(getContext());
                        column.setText(table.data.get(j).get(i));
                        column.setTextSize(size);
                        String color = table.colors.get(j).get(i);
                        color1 = Color.parseColor(color);
                    } catch (Exception e) {
                        color1 = 0;
                    }
                    column.setBackgroundColor(color1);
                    row.addView(column);
                }
                tableLayout.addView(row);
                updateButton.setBackgroundColor(getResources().getColor(R.color.down_choser));
                updateButton.setText(getResources().getString(R.string.update_data));
            }
        });
        return view;
    }
    private void getLastLocation() {
        if (isLocationEnabled()) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
                return;
            }
            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    requestNewLocationData();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String latit, longit;
                    latit = String.format("%.3f", latitude) + "N";
                    longit = String.format("%.3f", longitude) + "E";
                    model.setLocation(latit + longit);
                }
            });
        } else {
            Toast.makeText(getActivity(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
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
        return ActivityCompat.checkSelfPermission(getActivity(), ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(NETWORK_PROVIDER);
    }
}