package com.example.mukodjvegre_location;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;

import java.io.File;

public class allTargetsBackground
{

    int maxTragetsNr;
    targetPointBackground targets[] = new targetPointBackground[51];
    int targetsNr;
    allTargetsInFile myTargetsInFile;
    Context context;
    int arrivedToVolume = 0;

    allTargetsBackground(Context appContext){
        maxTragetsNr =50;
        targetsNr = 0;
        myTargetsInFile = new allTargetsInFile();
        context = appContext;
        arrivedToVolume = 7;

        //todo errod = appContext is null
        File file = new File(appContext.getFilesDir(),"allTargetsFile.txt");
        if(file.exists()){ //ha mar vannak lementve adatok
            //Do something
            //Log.e("allTargetsBackground", "your file exists");
            myTargetsInFile = allTargetsInFile.readFromFile(context);
            //Log.e("allTargetsBackground", "the nr. of targets saved to file = " + myTargetsInFile.targetsNr);
            sync_targets_readFromFile();
        }
    }

    public void sync_targets_readFromFile()
    {
        //Log.e("allTargetsBackground", "Synchronising started");
        //TODO solve this issue: nullPointerException
        targetsNr = myTargetsInFile.targetsNr;
        //Log.e("allTargetsBackground", "Synchronising works");
        for(int i=1; i<=targetsNr; i++)
        {
            targets[i] = new targetPointBackground("", 0, 0);
            targets[i].sync_target_readFromFile(myTargetsInFile.targets[i]);
        }
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

    public void eraseReachedTarget(int id){
        /*for(int i=id; i<=targetsNr-1; i++) targets[i] = targets[i+1];
        targetsNr--;*/
        targets[id].arrived = true;
        onChangeActualiseFile();
    }

    public String arrivedCheck(double myLat, double myLong){ //returns the name of the reached destination

        for(int i=1; i<=targetsNr; i++){
            if(targets[i].arrived(myLat,myLong) && targets[i].saved && !targets[i].arrived) { //todo check if dialog is open.
                arrivedToVolume = targets[i].volume;
                eraseReachedTarget(i); //todo you should mark this target as "arrived" and erase it in mapsactivity
               return targets[i].targetName;
            }
        }
        return "";
    }

    public int getLastVolume(){
        return arrivedToVolume;
    }
}
