package com.example.mukodjvegre_location;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

public class dialogSetEmail<dialogSetEmailListener> extends AppCompatDialogFragment {

    private dialogSetEmail.dialogSetEmailListener listener;

    private boolean switchEmailChecked(final View view) //meter vagy kilometer?
    {
        final Switch switchSendEmail = (Switch) view.findViewById(R.id.switchSendEmail);
        return switchSendEmail.isChecked();
    }

    private String getEmailAddress(final View view)
    {
        final EditText editEmail = (EditText) view.findViewById(R.id.editTextEmail);
        return editEmail.getText().toString();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    void setPreviousValues(String address, boolean send, final View view){
        final Switch switchSendEmail = (Switch) view.findViewById(R.id.switchSendEmail);

        if(send) switchSendEmail.setChecked(true);
        else switchSendEmail.setChecked(false);

        final EditText editEmail = (EditText) view.findViewById(R.id.editTextEmail);
        editEmail.setText(address);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        //Log.d(TAG, "opening dialogue");
        AlertDialog.Builder dialogSEmail = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layaout_dialog_setemail, null);

        dialogSEmail.setView(view);

        String setEmail = getArguments().getString("setEmailAddress");
        boolean setSendEmail = getArguments().getBoolean("setSendEmail");

        setPreviousValues(setEmail, setSendEmail, view);

        dialogSEmail.setTitle("Set the E-Mail Address!");
        dialogSEmail.setMessage("You can send someone an e-mail about having arrived safely to your destination");
        dialogSEmail.setPositiveButton("apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.applyValueEmail(switchEmailChecked(view), getEmailAddress(view), isEmailValid(getEmailAddress(view)));
            }
        });
        return dialogSEmail.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (dialogSetEmail.dialogSetEmailListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement dialogSetEmailListener");
        }

    }

    public interface dialogSetEmailListener{
        void applyValueEmail(boolean sendEmailChecked, String emailAddress, boolean validAddress);
    }
}
