package com.example.mukodjvegre_location;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import com.google.android.gms.maps.SupportMapFragment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

import java.lang.annotation.Target;

public class manageSettingsButtons  {
    private View myview;
    private Context context;
    private Animation FabOpen, FabClose, FabRClockwise, FabRAnticlockwise;
    private FloatingActionButton settingsBtn, setRadiusBtn, setVolumeBtn, saveTargetBtn, eraseTargetBtn;
    private boolean buttonsOpen;
    private allTargets theTargets;
    FragmentManager theFragmentManager;



    manageSettingsButtons(View thisview, Context thisContext, allTargets myTargets, FragmentManager myFragmentManager)
    {
        theFragmentManager = myFragmentManager;
        theTargets = myTargets;
        myview = thisview;
        context = thisContext;

        FabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close);
        FabRClockwise = AnimationUtils.loadAnimation(context, R.anim.rotate_clockwise);
        FabRAnticlockwise = AnimationUtils.loadAnimation(context, R.anim.rotate_anticlockwise);

        settingsBtn = (FloatingActionButton) myview.findViewById(R.id.SettingsBtn);
        setRadiusBtn = (FloatingActionButton) myview.findViewById(R.id.setRadiusBtn);
        setVolumeBtn = (FloatingActionButton) myview.findViewById(R.id.otherSettingsBtn);
        saveTargetBtn = (FloatingActionButton) myview.findViewById(R.id.saveTargetBtn);
        eraseTargetBtn = (FloatingActionButton) myview.findViewById(R.id.eraseTargetBtn);

        buttonsOpen = false;

        settingsButtonClicked();
        setSetRadiusBtn();
        setSetVolumeBtn();
        setSaveTargetBtn();
        setEraseTargetBtn();
    }

    public void expandAllButtons() {
        //gombok megjelenitese, animaciok
        setRadiusBtn.startAnimation(FabOpen);
        setVolumeBtn.startAnimation(FabOpen);
        saveTargetBtn.startAnimation(FabOpen);
        eraseTargetBtn.startAnimation(FabOpen);
        settingsBtn.startAnimation(FabRClockwise);
        buttonsOpen = true;
    }

    public void hideAllButtons()
    {
        //gombok eltuntetese, animaciok
        buttonsOpen = false;
        setRadiusBtn.startAnimation(FabClose);
        setVolumeBtn.startAnimation(FabClose);
        saveTargetBtn.startAnimation(FabClose);
        eraseTargetBtn.startAnimation(FabClose);
        settingsBtn.startAnimation(FabRAnticlockwise);
    }

    public boolean areButtonsOpen()
    {
        return buttonsOpen;
    }

    public void settingsButtonClicked()
    {
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Please select the destination that you want to customise by clicking on it!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


   public void setSetRadiusBtn()
    {
         setRadiusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(buttonsOpen) {
                        Log.e("MapsAtivity", "setRadiusButton clicked");

                        Bundle args = new Bundle(); // to set arguents for the dialog
                        args.putInt("radius", theTargets.getRadiusForClickedTarget());
                        args.putBoolean("isMeters", theTargets.getIsMetersForClickedTarget());

                        dialogSetRadius d_radius = new dialogSetRadius();
                        d_radius.setCancelable(false);
                        d_radius.setArguments(args);
                        d_radius.show(theFragmentManager, " ");
                    }
                }
        });

    }

    public void setSetVolumeBtn()
    {
        setVolumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonsOpen) {
                    Log.e("MapsAtivity", "setVolumeButton clicked");

                    Bundle args = new Bundle(); // to set arguents for the dialog
                    args.putInt("prevVolume", theTargets.getVolumeForClickedTarget());

                    dialogSetVolume d_volume = new dialogSetVolume();
                    d_volume.setCancelable(false);
                    d_volume.setArguments(args);
                    d_volume.show(theFragmentManager, " ");
                }
            }
        });

    }

    public void setSaveTargetBtn()
    {
        saveTargetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonsOpen) {
                    Log.e("MapsAtivity", "saveButton clicked");
                    theTargets.saveClickedMarker();
                }
            }
        });

    }

    public void setEraseTargetBtn()
    {
        eraseTargetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonsOpen) {
                    Log.e("MapsAtivity", "eraseButton clicked");
                    dialogSureErase d_sure = new dialogSureErase();
                    d_sure.setCancelable(false);
                    d_sure.show(theFragmentManager, " ");
                }
            }
        });
    }


}
