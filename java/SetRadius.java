package com.smyc.kaftanis.lookingfortable;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by kaftanis on 3/18/16.
 */
public class SetRadius extends DialogFragment implements View.OnClickListener {

    EditText editText;
    Button button;
    public static double radius;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.set_radius, null);
        getDialog().setTitle("Set the radius (in km)");

        editText = (EditText) view.findViewById(R.id.editText);
        editText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));

        SharedPreferences prefs = getActivity().getBaseContext().getSharedPreferences("radius", getActivity().getBaseContext().MODE_PRIVATE);
        String rert = prefs.getString("value", "empty");
        if (!rert.equals("empty")) {
            Double km = Double.parseDouble(rert);
            km=km/0.621371;
            editText.setHint("Current value is "+ String.format("%.2f", km ) +" "+"km");
        }
        else
            editText.setHint("Current value is 1 km");


        button = (Button) view.findViewById(R.id.button4);

        button.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button4) {

            if (editText.getText().toString().equals("")) {
                Toast.makeText( getActivity() , "Please set something" , Toast.LENGTH_SHORT).show();
            }
            else {
                radius=Integer.parseInt(editText.getText().toString())*0.621371; //convert to miles
                SharedPreferences.Editor editor = getActivity().getBaseContext().getSharedPreferences("radius", getActivity().getBaseContext().MODE_PRIVATE).edit();
                editor.putString("value", Double.toString(radius));
                editor.commit();
                dismiss();
                Toast.makeText( getActivity() , "Done" , Toast.LENGTH_SHORT).show();
            }





        }
    }
}
