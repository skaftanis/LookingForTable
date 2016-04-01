package com.smyc.kaftanis.lookingfortable;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by kaftanis on 3/18/16.
 */
public class VisiblePhotos extends DialogFragment implements View.OnClickListener  {

    Switch aSwitch;
    Button button;
    public static boolean yesno;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.photos_on_details, null);
        getDialog().setTitle("Do you want to see them?");

        button = (Button) view.findViewById(R.id.button5);

        aSwitch = (Switch) view.findViewById(R.id.switch1);

        SharedPreferences prefs = getActivity().getBaseContext().getSharedPreferences("details", getActivity().getBaseContext().MODE_PRIVATE);
        String retr = prefs.getString("value", "empty");
        if (retr.equals("true")) {
            aSwitch.setChecked(true);
        }
        else if (retr.equals("false")) {
            aSwitch.setChecked(false);
        }



        aSwitch.setTextOn("Yes");
        aSwitch.setTextOff("No");

        button.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button5) {
            yesno=aSwitch.isChecked();
            SharedPreferences.Editor editor = getActivity().getBaseContext().getSharedPreferences("details", getActivity().getBaseContext().MODE_PRIVATE).edit();
            editor.putString("value", Boolean.toString(yesno));
            editor.commit();
            dismiss();
            Toast.makeText( getActivity() , "Done" , Toast.LENGTH_SHORT).show();
        }

    }
}
