package com.example.mukodjvegre_location;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import static android.location.Location.distanceBetween;

public class targetPoint implements java.io.Serializable{
    String targetName;
    boolean markerExists;
    //private boolean arrived = false;
    Marker targetPositionMarker;
    LatLng targetCoordinates;
    int circleRadius; //in meter
    int prevCircleRadius = -1;
    CircleOptions circleOptions;
    Circle drawnCircle;
    private boolean saved;
    private boolean isClicked;
    GoogleMap myMap;
    String sendEmailToAddress;
    boolean sendEmail;
    boolean statusChanged;
    int volume;
    boolean radiusDialogOpen;
    boolean arrived;

    // boolean exists;


    targetPoint(String name, double tLatitude, double tLongtitude, GoogleMap mMap) {
        myMap = mMap;
        markerExists = false;
        circleRadius = 100;
        saved=false;
        targetName = name;
        targetCoordinates = new LatLng(tLatitude, tLongtitude);
        circleRadius = 500;
        saved=false;
        isClicked = false;
        sendEmail = false;
        sendEmailToAddress = "";
        statusChanged = true;
        //TODO consider adding a default value, that depends on the device settings
        volume = 7;
        radiusDialogOpen = false;
        arrived = false;
        //exists = false;
    }

   void actualize(String name, double tLatitude, double tLongtitude){
       saved=false;
       targetName = name;
       targetCoordinates = new LatLng(tLatitude, tLongtitude);
       circleRadius = 500;
       isClicked = false;
       sendEmail = false;
       sendEmailToAddress = "";
       volume = 7;
       radiusDialogOpen = false;
       arrived = false;
        //exists = true;
    }

    public void sync_target_readFromFile(targetPointInFile targetInFile)
    {
        circleRadius = targetInFile.circleRadius;
        saved = targetInFile.saved;
        targetName = targetInFile.targetName;
        targetCoordinates = new LatLng(targetInFile.myLatitude, targetInFile.myLongtitude);
        isClicked = false;
        sendEmail = targetInFile.sendEmail;
        sendEmailToAddress = targetInFile.sendEmailToAddress;
        statusChanged = true;
        volume = targetInFile.volume;
        isClicked = targetInFile.clicked;
        statusChanged = true;
        arrived = targetInFile.arrived;
    }

    public void showTitle(){
        targetPositionMarker.showInfoWindow();
    }

    public void eraseTitle(){
        targetPositionMarker.hideInfoWindow();
    }

    public targetPointInFile getTargetToFile()
    {
        targetPointInFile targetToFile = new targetPointInFile();
        targetToFile.targetName = targetName;
        targetToFile.circleRadius = circleRadius;
        targetToFile.myLatitude = targetCoordinates.latitude;
        targetToFile.myLongtitude = targetCoordinates.longitude;
        targetToFile.sendEmail =sendEmail;
        targetToFile.sendEmailToAddress = sendEmailToAddress;
        targetToFile.volume = volume;
        targetToFile.saved = saved;
        targetToFile.clicked = isClicked;
        targetToFile.arrived = arrived;
        return targetToFile;
    }

    public void Click()
    {
        isClicked = true;
        statusChanged = true;
    }

    public void unClick()
    {
        isClicked = false;
        statusChanged = true;
    }

    public boolean isClicked()
    {
        return isClicked;
    }

    void save(){
        saved=true;
        statusChanged = true;
    }

    void setRadius(int newRadius, boolean meters, boolean last){
        prevCircleRadius = circleRadius;
        if(meters) circleRadius = newRadius;
        else circleRadius = newRadius * 1000;

        statusChanged = true;

        if(last) radiusDialogOpen = false;
        else radiusDialogOpen = true;
    }

    void setVolume(int newvolume){
        volume = newvolume;
    }

    boolean isSaved(){
        if(saved) return true;
        return false;
    }

    void setEmailRecipient(String Address, boolean send){
        sendEmailToAddress = Address;
        sendEmail = send;
        Log.e("MapsActivity", "SetAddress" + sendEmailToAddress);
    }


    void eraseMarker(GoogleMap mMap){
        if(markerExists){
           // Log.e("MapsActivity", "eraseMarker started, marker exists");
            targetPositionMarker.remove();
            if(prevCircleRadius != circleRadius || statusChanged)
                drawnCircle.remove();
        }
    }

    void eraseFromMap()
    {
        if(markerExists) {
            targetPositionMarker.remove();
            drawnCircle.remove();
        }
    }

    void showOnMap(GoogleMap mMap, boolean movingCamera){
        //TODO consider erasing parameter mMap
        //myMap = mMap;
        //Log.e("MapsActivity", "ShowOnMap started");
        eraseMarker(myMap);
        //Log.e("MapsActivity", "eraseMarker finished");
        if(saved && !isClicked) targetPositionMarker = myMap.addMarker(new MarkerOptions().position(targetCoordinates).title(targetName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        else if(isClicked) targetPositionMarker = myMap.addMarker(new MarkerOptions().position(targetCoordinates).title(targetName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        else targetPositionMarker = myMap.addMarker(new MarkerOptions().position(targetCoordinates).title(targetName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        //Log.e("MapsActivity", "Marker added");
        if(movingCamera && !saved) myMap.moveCamera(CameraUpdateFactory.newLatLng(targetCoordinates));
        if(movingCamera && !saved) myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetCoordinates, 5f));
        markerExists = true;


        // Instantiating CircleOptions to draw a circle around the marker
        if(prevCircleRadius != circleRadius || statusChanged)
        {   circleOptions = new CircleOptions();
            // Specifying the center of the circle
            circleOptions.center(targetCoordinates);
            // Radius of the circle
            circleOptions.radius(circleRadius);
            // Border color of the circle
            //60 stands for transparency
            circleOptions.strokeColor(0x60ff6700);
            // Fill color of the circle
            /*if(!saved && !isClicked) circleOptions.fillColor(0x80ff6700); //orange
            else if(saved && !isClicked) circleOptions.fillColor(0x80fff400); //yellow
            else circleOptions.fillColor(0x80ff1a00); //red*/

            if(saved && !isClicked) circleOptions.fillColor(0x60fff400); // yellow
            else if(isClicked) circleOptions.fillColor(0x60ff1a00); //red
            else circleOptions.fillColor(0x60ff6700); //orange
            // Border width of the circle
            circleOptions.strokeWidth(3);
            // Adding the circle to the GoogleMap
            drawnCircle = myMap.addCircle(circleOptions);
            statusChanged = false;
        }
        /*if(isClicked){
            targetPositionMarker.showInfoWindow();
            targetPositionMarker.in;
        }
        //targetPositionMarker.setVisible(true);
        else if(targetPositionMarker.isInfoWindowShown()) targetPositionMarker.hideInfoWindow();*/
    }


    boolean arrived(double startLat, double startLong){

        float result[] = new float[1];
        distanceBetween(startLat, startLong, targetCoordinates.latitude, targetCoordinates.longitude, result);

       // Log.d(TAG, "distance calculated " + result[0]);
        if(result[0]<=circleRadius) return true;
        return false;
    }

    float distanceFromDevice(double startLat, double startLong){
        float result[] = new float[1];
        distanceBetween(startLat, startLong, targetCoordinates.latitude, targetCoordinates.longitude, result);

        return result[0];
    }
}
