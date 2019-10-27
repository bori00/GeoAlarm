package com.example.mukodjvegre_location;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public class dialogSureErase<dialogSureEraseListener> extends AppCompatDialogFragment {

    private dialogSureErase.dialogSureEraseListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        //Log.d(TAG, "opening dialogue");
        AlertDialog.Builder dialogSEmail = new AlertDialog.Builder(getActivity());

        dialogSEmail.setTitle("Warning!");
        dialogSEmail.setMessage("Are you sure that you want to delete this destination?");
        dialogSEmail.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.applyValueSure(true);
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.applyValueSure(false);
            }
        });
        return dialogSEmail.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (dialogSureErase.dialogSureEraseListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement dialogSureEraseListener");
        }

    }

    public interface dialogSureEraseListener{
        void applyValueSure(boolean sure);
    }

}
