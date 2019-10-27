package com.example.mukodjvegre_location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class checkArrivedJobService extends JobService {
    private static final String TAG = "ExampleobService";
    private boolean jobCancelled = false;

    //deleted from the Manifests!!!
    /*        <service android:name=".checkArrivedJobService"
    android:permission="android.permission.BIND_JOB_SERVICE" ></service>

        <receiver android:name=".notificationBroadcastReceiver">
            <intent-filter>
                <action android:name="notification_cancelled"/>
            </intent-filter>
        </receiver>

       <service
    android:name="com.example.mukodjvegre_location.backgroundService"
    android:enabled="true" >
        </service>

        <receiver
    android:name="com.example.mukodjvegre_location.backgroundServiceRestarterBroadcastReceiver"
    android:enabled="true"
    android:exported="true"
    android:label="RestartServiceWhenStopped">
        </receiver>*/



    //copied
    public int counter = 0;
    boolean providerEnabled = false;
    private LatLng myPosition;
    LocationManager locationManager;
    Context context;
    allTargetsBackground targetsInBack;
    String alertChannelId = "100";
    String alertChannelName = "alert";
    NotificationChannel myChannel;
    int notifyID = 1;
    Ringtone r;
    Uri alert;
    AudioAttributes audioAttributes;
    MediaPlayer player;

    @SuppressLint("MissingPermission")
    @Override
    public boolean onStartJob(JobParameters params) {
       /* Log.e(TAG, "Job started");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        targetsInBack = new allTargetsBackground(getApplicationContext());

        createNotificationChannel(); //initialising values
        //prepareMusic(); //initalising values
        doBackgroundWork(params); //check for arrivel*/
        return true; //because I will do some background operations
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prepareMusic();
            int importance = NotificationManager.IMPORTANCE_HIGH;
            myChannel = new NotificationChannel(alertChannelId, alertChannelName, importance);
            myChannel.setLockscreenVisibility(2);
            myChannel.enableVibration(true);
            //myChannel.setSound(alert, audioAttributes);
            //channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(myChannel);
        }
    }

    protected PendingIntent getDeleteIntent()
    {
        notificationBroadcastReceiver.init(player);
        Intent intent = new Intent(getBaseContext(), notificationBroadcastReceiver.class);
        intent.setAction("notification_cancelled");
        //pauseMusic();
        return PendingIntent.getBroadcast(getBaseContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void prepareMusic(){
        audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .build();

        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if(alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        r = RingtoneManager.getRingtone(getBaseContext(), alert);
        r.setStreamType(AudioManager.STREAM_ALARM);
        r.setAudioAttributes(audioAttributes);

    }

    public void playMusic(){
        prepareMusic();
        int volume = targetsInBack.getLastVolume();
        Log.e("jobService", "playing music with volume = " + volume);
        /*final AudioManager audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .build();

        r.setAudioAttributes(audioAttributes);
        r.play();*/
        player = MediaPlayer.create(this, alert);
        player.setAudioAttributes(audioAttributes);
        player.setLooping(true);
        player.setVolume(volume, volume);
        player.start();
        //if(r.isPlaying()) Log.e("jobService", "music playing started");
    }

    public void pauseMusic(){
        player.stop();
    }

    public void alertArrived(String s){
        Log.e("jobService", "opening notification");
        //audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        long pattern[] = new long[2];
        pattern[0] = 500;
        pattern[1] = 1000;
        playMusic();
        //if(r.isPlaying()) Log.e("jobService", "music playing started");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, alertChannelId)
                .setSmallIcon(R.drawable.geoalarm_logo)
                .setContentTitle("Alarm")
                .setContentText("You arrived to " + s)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setVibrate(pattern)
                .setDeleteIntent(getDeleteIntent())
                .setChannelId(alertChannelId);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notifyID, builder.build());
        //notifyID ++;
    }


    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(getApplication());
                final LocationRequest locationRequest = new LocationRequest();
                locationRequest.setMaxWaitTime(30 * 1000);
                locationRequest.setFastestInterval(2 * 1000);

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Looper.prepare();

                //without a foregroundService I can't request location updates more than a few times per hour
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);
                Notification notification = notificationBuilder.setOngoing(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(0)
                        .setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .build();

                startForeground(123, notification);


                //az eszkoz helyzetenek lekerdezese
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        List<Location> locationList = locationResult.getLocations();
                        if (locationList.size() > 0) {
                            //The last location in the list is the newest
                            Location location = locationList.get(locationList.size() - 1);
                            Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                            myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                            targetsInBack = new allTargetsBackground(getApplicationContext()); //todo context = null, solve this problem!!!
                            Log.e("backgroundService", "LocationChanged " + location.getLatitude() + " " + location.getLongitude());
                            //Toast.makeText(context, "LocationChanged " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                            String arrivedToName = targetsInBack.arrivedCheck(myPosition.latitude, myPosition.longitude);
                            if (!arrivedToName.equals("")) {
                                //todo consider waking up the phone and opening a new dialogArrived
                                Log.e("backgroundService", "arrived to " + targetsInBack.arrivedCheck(myPosition.latitude, myPosition.longitude));
                                Log.e("jobService", "opening notification");
                                //audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

                                alertArrived(arrivedToName);
                                targetsInBack = new allTargetsBackground(getApplicationContext());
                            }

                        }
                    }
                }, Looper.myLooper());

                Looper.loop();



                jobFinished(params, false);
            }
        }).start();
    }

    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }



   /* public void myLocation() {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //todo consider deleting this context
                    context = getApplicationContext();
                    myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.e("backgroundService", "LocationChanged " + location.getLatitude() + " " + location.getLongitude());
                    //Toast.makeText(context, "LocationChanged " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    if (!targetsInBack.arrivedCheck(myPosition.latitude, myPosition.longitude).equals("")) {
                        //todo consider waking up the phone and opening a new dialogArrived
                        Log.e("backgroundService", "arrived to " + targetsInBack.arrivedCheck(myPosition.latitude, myPosition.longitude));
                        targetsInBack = new allTargetsBackground(getApplicationContext());
                    }
                    ;
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
                    //Toast.makeText(context, "LocationChanged " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(context, "Please enable device location!", Toast.LENGTH_SHORT).show();
                providerEnabled = false;
            }
        }
    }*/

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(TAG, "job cancelled before completion");
        jobCancelled = true;
        return false;
    }
}

