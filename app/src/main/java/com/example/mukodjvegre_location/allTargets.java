package com.example.mukodjvegre_location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.lang.annotation.Target;
import java.util.List;

public class allTargets implements java.io.Serializable{
    int maxTragetsNr;
    targetPoint targets[] = new targetPoint[51];
    int targetsNr;
    View myview;
    Context context;
    manageSettingsButtons manageButtons;
    private int theClickedMarker;
    GoogleMap myMap;
    allTargetsInFile myTargetsInFile;
    FragmentManager myFragmentManager;
    myPos devicePosition;
    targetPoint notSavedTarget;
    boolean existsNotSavedTarget;


    allTargets(myPos myPosition, GoogleMap mMap, View thisview, Context appContext, FragmentManager thisFragmentManager){
        devicePosition = new myPos();
        devicePosition = myPosition;
        myMap = mMap;
        myFragmentManager = thisFragmentManager;
        markerClickCheck(mMap);
        //infoWindowClickCheck(mMap);
        myview = thisview;
        context = appContext;
        maxTragetsNr =50;
        targetsNr = 0;
        manageButtons = new manageSettingsButtons(thisview, appContext,this, thisFragmentManager);
        theClickedMarker = -1;
        myTargetsInFile = new allTargetsInFile();
        existsNotSavedTarget = false;


        File file = new File(context.getFilesDir(),"allTargetsFile.txt");
        if(file.exists()){ //ha mar vannak lementve adatok
            //Do something
            //Log.e("MapsActivity", "your file exists");
            myTargetsInFile = allTargetsInFile.readFromFile(context);
            //Log.e("MapsActivity", "the nr. of targets saved to file = " + myTargetsInFile.targetsNr);
            sync_targets_readFromFile();
        }
        //showOnMap(mMap, false);
    }

    public void sync_targets_readFromFile()
    {
        //Log.e("MapsActivity", "Synchronising started");
        //TODO solve this issue: nullPointerException
        targetsNr = myTargetsInFile.targetsNr;
        //Log.e("MapsActivity", "Synchronising failed");
        for(int i=1; i<=targetsNr; i++)
        {
            targets[i] = new targetPoint("", 0, 0, myMap);
            targets[i].sync_target_readFromFile(myTargetsInFile.targets[i]);
        }
        for(int i=1; i<=targetsNr; i++)
        {
            if(targets[i].arrived) eraseReachedTarget(i);
        }
        showOnMap(myMap, false);
    }

    public void onChangeActualiseFile()
    {
        if(targetsNr >0) {
            myTargetsInFile.targetsNr = targetsNr;
            for (int i = 1; i <= targetsNr; i++) {
                myTargetsInFile.targets[i] = targets[i].getTargetToFile();
            }
            myTargetsInFile.saveToFile(context);
        }
        else {
            myTargetsInFile.targetsNr=0;
            myTargetsInFile.saveToFile(context);
        }
    }

    public void addNewTarget(String name, double theLatitude, double theLongtitude, GoogleMap mMap, myPos myPosition) //ujonnan beelolt celpont hozzaadasa
    {
        if(targetsNr > 0 ) Log.e("MapsActivity", "adding new target to allTargets" + targetsNr + " saved = " + targets[targetsNr].isSaved() + "clicked = " + targets[targetsNr].isClicked());
        else Log.e("MapsActivity", "adding new target to allTargets" + targetsNr );
        if(targetsNr == 0 || (targetsNr<maxTragetsNr && targets[targetsNr].isSaved())) //meg van szabad hely egy uj celpontnak, es eddig minden celpont el volt mentve
        {
            targetsNr ++;
            existsNotSavedTarget = true;
            targets[targetsNr] = new targetPoint(name, theLatitude, theLongtitude, myMap);
            notSavedTarget = targets[targetsNr];
            if(targetsNr == 1) markerClickCheck(mMap);
            showOnMap(mMap, true);
            onChangeActualiseFile();
            arrivedCheck(myPosition);
        }
        else if(targetsNr<=maxTragetsNr  && !targets[targetsNr].isSaved()) //meg van hely az uj celpontnak, de az elozo kereses eredmenye nem volt elmentve, igy azt helyettesitjuk
        {
            if(targets[targetsNr].isClicked()){
                targets[targetsNr].unClick();
                theClickedMarker = -1;
                if(manageButtons.areButtonsOpen()){
                    Log.e("MapsActivity", "Buttons are open");
                    manageButtons.hideAllButtons();
                }
            }
            targets[targetsNr].actualize(name, theLatitude, theLongtitude);
            existsNotSavedTarget = true;
            notSavedTarget = targets[targetsNr];
            showOnMap(mMap, true);
            onChangeActualiseFile();
            arrivedCheck(myPosition);
        }
        else
        {
            //TODO consider sending an error message
            Snackbar.make(myview, "You reached the maximum number of targets. Please erase some of them! ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void saveClickedMarker()
    {
        if(!targets[theClickedMarker].isSaved()) {
            Log.e("MapsActivity", "saving target nr. " + theClickedMarker);
            targets[theClickedMarker].save();
            Toast.makeText(context, "Destination succesfully saved", Toast.LENGTH_SHORT).show();
            onChangeActualiseFile();
            arrivedCheck(devicePosition);
            existsNotSavedTarget = false;
        }
        else Toast.makeText(context, "The selected destination is already saved", Toast.LENGTH_LONG).show();
    }

    public void showOnMap(GoogleMap newMap, boolean movingCamera)
    {
        Log.e("MapsActivity", "targetsNr=" + targetsNr);
        for(int i=1; i<=targetsNr; i++) targets[i].showOnMap(newMap, movingCamera);
    }

    public void markerClickCheck(final GoogleMap mMap)
    {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker clickedMarker) {
                /*Snackbar.make(view, "One marker clicked ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                //clickedMarker.hideInfoWindow();
                Log.e("MapsActivity", "marker clicked, targetsNr = " + targetsNr);
                for(int i=1; i<=targetsNr; i++) //megkeresem, hogy hanyadik szamu(i) target markerje klikkelt a felhasznalo
                {
                    if(clickedMarker.getTitle().equals(targets[i].targetName)) {
                        clickedMarker(i, mMap);
                    }
                }
                return true;
            }
        });
    }

    public void infoWindowClickCheck(final GoogleMap mMap){ //in case that an infowindow is shown for a marker, I should check whteher it is clicked instead of the marker itself
        /*mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker clickedMarker) {
                Log.e("MapsActivity", "infoWindow clicked");
                for(int i=1; i<=targetsNr; i++) //megkeresem, hogy hanyadik szamu(i) target markerje klikkelt a felhasznalo
                {
                    if(clickedMarker.equals(targets[i].targetPositionMarker)) {
                        clickedMarker(i, mMap);
                    }
                }
            }
        });*/
    }

    public void clickedMarker(int id, GoogleMap mMap)
    {
        Log.e("clickedMarker", "Marker clicked previously = " + targets[id].isClicked());
        if(targets[id].isClicked()) {
            Log.e("clickedMarker", "I will unclick this marker");
            targets[id].unClick();
            theClickedMarker = -1;
            manageButtons.hideAllButtons();
        }
        else {
            if(!manageButtons.areButtonsOpen()) manageButtons.expandAllButtons();
            for (int i = 1; i <= targetsNr; i++) targets[i].unClick();
            targets[id].Click();
            theClickedMarker = id;
            Log.e("allTargets", "clickedTarget title = " + targets[theClickedMarker].targetPositionMarker.getTitle());
            //targets[id].targetPositionMarker.showInfoWindow();
        }
        showOnMap(mMap, false);
        /*if(targets[id].targetPositionMarker.isInfoWindowShown()) targets[id].targetPositionMarker.hideInfoWindow();
        else targets[id].targetPositionMarker.showInfoWindow();*/
        onChangeActualiseFile();
       Log.e("MapsActivity", "clickedMarker called");
    }

    public int clickedMarkerId()
    {
        return theClickedMarker;
    }

    public void setRadiusForClickedTarget(int newRadius, boolean meters, boolean last, GoogleMap newMap) {
        Log.e("MapsActivity", "SetRadiusForClickedTarget called, target =  " + theClickedMarker);
        if (theClickedMarker > 0){
            targets[theClickedMarker].setRadius(newRadius, meters, last);
            showOnMap(newMap, false);
            if(last) onChangeActualiseFile();
         }
    }

    public void setVolumeForClickedTarget(int newVolume) {
        Log.e("MapsActivity", "SetVolumeForClickedTarget called, target =  " + theClickedMarker);
        if (theClickedMarker > 0){
            targets[theClickedMarker].setVolume(newVolume);
            //showOnMap(newMap, false);
            onChangeActualiseFile();
        }
    }

    public void setEmailAddressForClickedTarget(String Address, boolean send, boolean valid){
        if(!valid || !send) targets[theClickedMarker].setEmailRecipient(Address, false);
        else targets[theClickedMarker].setEmailRecipient(Address, true);
        Log.e("MapsActivity", "SetEmailAddressForClickedTarget called, target =  " + theClickedMarker);
        onChangeActualiseFile();
    }

    public void eraseClickedTarget()
    {
        targets[theClickedMarker].eraseFromMap();
        for(int i=theClickedMarker; i<=targetsNr-1; i++) targets[i] = targets[i+1];
        theClickedMarker = -1;
        if(manageButtons.areButtonsOpen()){
            Log.e("MapsActivity", "Buttons are open");
            manageButtons.hideAllButtons();
        }
        targetsNr--;
        Toast.makeText(context, "Destination successfully erased", Toast.LENGTH_SHORT).show();
        onChangeActualiseFile();
        showOnMap(myMap, false);
    }

    public void eraseReachedTarget(int id){
        Log.d("allTargets", "erasing reached Target");
        if(id == theClickedMarker && manageButtons.areButtonsOpen()){
            Log.e("MapsActivity", "Buttons are open");
            manageButtons.hideAllButtons();
        }
        targets[id].eraseFromMap();
        for(int i=id; i<=targetsNr-1; i++) targets[i] = targets[i+1];
        targetsNr--;
        onChangeActualiseFile();
        showOnMap(myMap, false);
    }

    /*private void addNotSavedTarget(myPos myPosition){
        if(existsNotSavedTarget) addNewTarget(notSavedTarget.targetName, notSavedTarget.targetCoordinates.latitude, notSavedTarget.targetCoordinates.longitude, myMap, myPosition);
    }*/

    public void erasePrevTargets(){
        for(int i=1; i<=targetsNr; i++){
            targets[i].eraseFromMap();
        }
    }


    public void arrivedCheck(myPos myPosition){
        double myLat = myPosition.getLatitude();
        double myLong = myPosition.getLongtitude();

        //ujraolvasom, hiszen a jobservice miatt valtozhatott
        /*File file = new File(context.getFilesDir(),"allTargetsFile.txt");
        if(file.exists()){ //ha mar vannak lementve adatok
            //Do something
            //Log.e("MapsActivity", "your file exists");
            myTargetsInFile = allTargetsInFile.readFromFile(context);
            Log.e("MapsActivity", "the nr. of targets saved to file = " + myTargetsInFile.targetsNr);
            //if(myTargetsInFile.targetsNr < targetsNr) {
                erasePrevTargets();
                sync_targets_readFromFile();
            //}
        }*/
        //addNotSavedTarget(myPosition);

        for(int i=1; i<=targetsNr; i++){
            if(targets[i].isSaved() && targets[i].arrived(myLat,myLong) && !targets[i].radiusDialogOpen) {

                Bundle args = new Bundle(); // to set arguents for the dialog
                args.putString("targetName", targets[i].targetName);
                args.putInt("volume", targets[i].volume);

                dialogArrived d_arrived = new dialogArrived();
                d_arrived.setCancelable(false);
                d_arrived.setArguments(args);
                d_arrived.show(myFragmentManager, " ");
                eraseReachedTarget(i);

                Log.e("allTargets", "arriveeeeeeeeed to " + targets[i].targetName);
                //eraseReachedTarget(i);
            }
        }
    }

    public String getEmailAddressForClickedTarget(){
        return targets[theClickedMarker].sendEmailToAddress;
    }

    public boolean getSendEmailForClickedTarget(){
        return targets[theClickedMarker].sendEmail;
    }

    public int getRadiusForClickedTarget(){
        if(targets[theClickedMarker].circleRadius<=1000) return targets[theClickedMarker].circleRadius;
        else return targets[theClickedMarker].circleRadius/1000;
    }

    public boolean getIsMetersForClickedTarget(){
        return targets[theClickedMarker].circleRadius<=1000;
    }

    public int getVolumeForClickedTarget(){
        return targets[theClickedMarker].volume;
    }

    public boolean existsTargetWithThisName(String name){
        for(int i=1; i<=targetsNr; i++){
            if(targets[i].targetName.equals(name)) return true;
        }
        return false;
    }

    public int minDistanceMeters(myPos myPosition){
        if(targetsNr == 0) return -1;
        float minDist = targets[1].distanceFromDevice(myPosition.getLatitude(), myPosition.getLongtitude());
        float thisDist;
        for(int i=2; i<=targetsNr; i++){
            thisDist = targets[i].distanceFromDevice(myPosition.getLatitude(), myPosition.getLongtitude());
            if(thisDist < minDist){
                minDist = thisDist;
            }
        }
        return (int)minDist;
    }

}
