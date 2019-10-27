package com.example.mukodjvegre_location;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.location.Location.distanceBetween;
import static android.location.LocationManager.NETWORK_PROVIDER;

//without job

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, dialogSetRadius.dialogSetRadiusListener, dialogSetEmail.dialogSetEmailListener, dialogSetVolume.dialogSetVolumeListener, dialogSureErase.dialogSureEraseListener{

    GoogleMap mMap;
    Geocoder geocoder;
    boolean search_found = false;
    private static final String TAG = "MapsActivity";
    String searchString = "111";
    myPos myPosition = new myPos();
    LocationListener locationListener;
    private static Context context;
    allTargets theTargets;
    boolean providerEnabled = false;
    boolean targetsExist = false;

    //az engedelyek lekerdezesehez
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    LocationManager locationManager;

    //private backgroundService myBackgroundService;
    //Intent myServiceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        initSearch();
        //onScheduleJob(getWindow().getDecorView().getRootView());



        //permissionGranted=checkLocationPermission();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 500);
            return;
        }

        //myLocationSearchDisplay(); //previously
    }

    public void onScheduleJob(View v){
        ComponentName componentName = new ComponentName(this, checkArrivedJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setPeriodic(60 * 15 * 1000)
                .setPersisted(true)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);
        int resultCode = scheduler.schedule(info);
        if(resultCode == JobScheduler.RESULT_SUCCESS){
            Log.e("MainActivity", "job scheduled");
        }
        else Log.e("MainActivity", "job scheduling failed");

    }

    public void onCancelJob(View v){
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.e("MapsActivity", "job cancelled" );
    }

   /* private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }*/


    /*@Override
    protected void onDestroy() {
        stopService(myServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }*/


    @Override
    public void applyValueRadius(int newRadius, boolean meters, boolean last) { //dialogSetRadiusbol
        theTargets.setRadiusForClickedTarget(newRadius, meters, last,  mMap);
    }

    @Override
    public void applyValueEmail(boolean sendEmail, String address, boolean validAddress) { //dialogSetEmailbol
        theTargets.setEmailAddressForClickedTarget(address, sendEmail, validAddress);
        if(!validAddress)  Snackbar.make(getWindow().getDecorView().getRootView(), "This doesn't seem to be a valid e-mail address. Please retry!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        else Toast.makeText(getApplicationContext(), "E-mail address saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void applyValueVolume(int volume) { //dialogSetVolumebol
        theTargets.setVolumeForClickedTarget(volume);
    }

    @Override
    public void applyValueSure(boolean erase) { //dialogSureErasebol
        if(erase) theTargets.eraseClickedTarget();
    }


    void myLocationSearchDisplay() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 500);
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    context = getApplicationContext();
                    myPosition.newMyPos(location.getLatitude(), location.getLongitude(), mMap, context);
                    if(targetsExist) theTargets.arrivedCheck(myPosition);
                    providerEnabled = true;
                   // Log.e(TAG, "new myPosition");
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }

            });
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    context = getApplicationContext();

                    myPosition.newMyPos(location.getLatitude(), location.getLongitude(), mMap, context);
                    if(targetsExist) theTargets.arrivedCheck(myPosition);
                    providerEnabled = true;
                   // Log.e(TAG, "new myPosition");
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        else{
            if(providerEnabled){
                Toast.makeText(context, "Please enable device location!", Toast.LENGTH_SHORT).show();
                providerEnabled = false;
            }
        }
    }



    private void initSearch() {

        EditText mSearchText = (EditText) findViewById(R.id.input_search);
        Log.d(TAG, "initialising started");

        final ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);


        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            //Log.d(TAG, "OnEditorActionListener entered!");
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                {
                    Log.d(TAG, "action!");
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN
                            || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                        //execute our method for searching
                        geoLocate();
                    }
                    return false;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please enable intenet access", Toast.LENGTH_LONG).show();
                    return false;
                }

            }
        });


    }

    private void geoLocate() {
        EditText mSearchText = (EditText) findViewById(R.id.input_search);
        String searchString =  mSearchText.getText().toString();
        Log.e(TAG, "the strings " + searchString);
        if (!theTargets.existsTargetWithThisName(searchString)) {
            Log.d(TAG, "geolocating started");

            Geocoder geocoder = new Geocoder(MapsActivity.this);
            String searchString2;
            List<Address> list = new ArrayList<>();
            try {
                list = geocoder.getFromLocationName(searchString, 1);
                if(list.size() ==  0) {
                    searchString2 = searchString.substring(0, searchString.length()-2);
                    list = geocoder.getFromLocationName(searchString2, 1);
                }
                Log.e(TAG, "this is a location!");
                /*while (list.size()==0) {
                    list = geocoder.getFromLocationName(searchString, 1);
                }*/

            } catch (IOException e) {
                if(search_found) Toast.makeText(getApplicationContext(), "I can't find this place", Toast.LENGTH_LONG).show();
                else  Toast.makeText(getApplicationContext(), "Please wait a few seconds and try again!", Toast.LENGTH_LONG ).show();
                Log.e(TAG, "geolocate: IOException: " + e.getMessage());
            }

            if (list.size() > 0) {

                Address address = list.get(0);

                Log.d(TAG, "geolocate: found a location: " + address.toString());
                theTargets.addNewTarget(searchString, address.getLatitude(), address.getLongitude(), mMap, myPosition);
                search_found = true;
            }
            else {
                Log.e("Geolocating", "list size = 0");
                if(search_found) Toast.makeText(getApplicationContext(), "I can't find this place", Toast.LENGTH_LONG).show();
                else Toast.makeText(getApplicationContext(), "Please wait a few seconds and try again!", Toast.LENGTH_LONG ).show();
            }
        }
        else Toast.makeText(getApplicationContext(), "You have already set an alarm for this destination", Toast.LENGTH_LONG).show();

    }




    public void initialiseMapButtons(GoogleMap mMap){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 500);
            return;
        }
        mMap.setPadding(10, 155, 10, 10 );
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(true);
       // Toast.makeText(getApplicationContext(), "buttons initialised", Toast.LENGTH_SHORT). show();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add  we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initialiseMapButtons(mMap);
        targetsExist = false;
        myLocationSearchDisplay();
        if(getSupportFragmentManager() != null) theTargets = new allTargets(myPosition, mMap, getWindow().getDecorView().getRootView(), getApplicationContext(), getSupportFragmentManager());
        else Log.e("MapsActivity", "supportFragmentManager is null");
        targetsExist = true;
        //myLocationSearchDisplay();


        //background service> not working for API 26 or higher
        /*myBackgroundService = new backgroundService(getApplicationContext());
        myServiceIntent = new Intent(getApplicationContext(), myBackgroundService.getClass());
        if (!isMyServiceRunning(myBackgroundService.getClass())) {
            startService(myServiceIntent);
            Log.e("MapsActivity", "background service started"); //reached
        }*/

    }
}
