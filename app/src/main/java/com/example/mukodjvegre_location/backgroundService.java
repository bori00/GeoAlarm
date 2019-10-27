package com.example.mukodjvegre_location;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Timer;
import java.util.TimerTask;

public class backgroundService extends Service {
    public int counter=0;
    boolean providerEnabled = false;
    private LatLng myPosition;
    LocationManager locationManager;
    Context context;
    allTargetsBackground targetsInBack;

    public backgroundService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!"); //reached
    }

    public backgroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        targetsInBack = new allTargetsBackground(getApplicationContext()); //todo context = null, solve this problem!!!
        myLocation();
        Log.e("backgroundService", "onStartCommand started");

        return START_STICKY;
    }



    public void myLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 500);
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //todo consider deleting this context
                    context = getApplicationContext();
                    myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.e("backgroundService", "LocationChanged " + location.getLatitude() + " " + location.getLongitude());
                    if(!targetsInBack.arrivedCheck(myPosition.latitude, myPosition.longitude).equals("")){
                        //todo consider waking up the phone and opening a new dialogArrived
                        Log.e("backgroundService", "arrived to " + targetsInBack.arrivedCheck(myPosition.latitude, myPosition.longitude));
                        targetsInBack = new allTargetsBackground(getApplicationContext());
                    };
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
                    myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.e("backgroundService", "LocationChanged " + location.getLatitude() + " " + location.getLongitude());
                    providerEnabled = true;
                    if(!targetsInBack.arrivedCheck(myPosition.latitude, myPosition.longitude).equals("")){
                        //todo consider waking up the phone and opening a new dialogArrived
                        Log.e("backgroundService", "arrived to " + targetsInBack.arrivedCheck(myPosition.latitude, myPosition.longitude));
                        targetsInBack = new allTargetsBackground(getApplicationContext());
                    };
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(this, backgroundServiceRestarterBroadcastReceiver.class);

        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
