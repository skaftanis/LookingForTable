package com.smyc.kaftanis.lookingfortable;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.smyc.kaftanis.lookingfortable.R;

import org.json.JSONObject;

/**
 * Created by kaftanis on 2/20/16.
 */
public class NumberChoiceDialog extends DialogFragment {

    NumberPicker numberPicker;
    Button submit;
    RequestQueue requestQueue;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle("How many free tables?");
        View view = inflater.inflate(R.layout.number_choice_dialog, null);

        //setup widgets
        numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
        submit = (Button) view.findViewById(R.id.submit);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(Integer.parseInt(InfosActivity.size));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(InsertSpots.minutesToOtherStore) > 2)
                    if (Integer.parseInt(InsertSpots.postsInLastTenMinutes) >= 2 ) {
                        Toast.makeText(getActivity(), "You can't make more than 2 updates in 10 minutes. Please try later.", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                    else {
                        JSONproccessInsert("PRIVATE?userID=" + MainActivity.loginId + "&storeID=" + InsertSpots.storeID + "&input=" + numberPicker.getValue());
                    }
                else {
                    Toast.makeText(getActivity(), "You can't set spots in different store in time less than 2 minutes. Please try later.", Toast.LENGTH_LONG).show();
                    //Intent intent = new Intent(getActivity(), SearchActivity.class);
                    //startActivity(intent);
                   getActivity().finish();
                }

                dismiss();
            }
        });

        return view;
    }


    private void JSONproccessInsert ( String loginURL ) {

        requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");

                    }
                }
        );
        requestQueue.add(jor);

        Toast.makeText(getActivity(), "Thanks for your commit", Toast.LENGTH_LONG).show();

        //Intent intent = new Intent(getActivity(), SearchActivity.class);
        //startActivity(intent);
        getActivity().finish();

    }
}
