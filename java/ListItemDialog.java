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
 * Created by kaftanis on 2/19/16.
 */
public class ListItemDialog extends DialogFragment implements View.OnClickListener{

    Button empty;
    Button aempty;
    Button medium;
    Button afull;
    Button full;

    RequestQueue requestQueue;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle("How full is the store?");

        View view = inflater.inflate(R.layout.list_item_dialog, null);
        empty = (Button) view.findViewById(R.id.empty);
        aempty = (Button) view.findViewById(R.id.aempty);
        medium = (Button) view.findViewById(R.id.medium);
        afull = (Button) view.findViewById(R.id.afull);
        full = (Button) view.findViewById(R.id.full);

        empty.setOnClickListener(this);
        aempty.setOnClickListener(this);
        medium.setOnClickListener(this);
        afull.setOnClickListener(this);
        full.setOnClickListener(this);



        return view;
    }

    @Override
    public void onClick(View v) {

        double approximationValue=0; //approvimation of empty tables


        if (v.getId() == R.id.empty ) {
            approximationValue=Double.parseDouble(InfosActivity.size) - 0.05*Double.parseDouble(InfosActivity.size);
            dismiss();
        }
        else if (v.getId() == R.id.aempty) {
            approximationValue=Double.parseDouble(InfosActivity.size) - 0.25*Double.parseDouble(InfosActivity.size);
            dismiss();
        }
        else if (v.getId() == R.id.medium) {
            approximationValue=Double.parseDouble(InfosActivity.size) - 0.5*Double.parseDouble(InfosActivity.size);
            dismiss();
        }
        else if (v.getId() == R.id.afull) {
            approximationValue=Double.parseDouble(InfosActivity.size) - 0.85*Double.parseDouble(InfosActivity.size);
            dismiss();
        }
        else if (v.getId() == R.id.full) {
            approximationValue=0;
            dismiss();
        }


        if (Integer.parseInt(InsertSpots.minutesToOtherStore) > 2)
            if (Integer.parseInt(InsertSpots.postsInLastTenMinutes) >= 2 ) {
                Toast.makeText(getActivity(), "You can't make more than 2 updates in 10 minutes. Please try later.", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
            else {
                    JSONproccessInsert("PRIVATE?userID=" + MainActivity.loginId + "&storeID=" + InsertSpots.storeID + "&input=" + Double.toString(approximationValue));
            }
        else {
            Toast.makeText(getActivity(), "You can't set spots in different store in time less than 2 minutes. Please try later.", Toast.LENGTH_LONG).show();
            //Intent intent = new Intent(getActivity(), SearchActivity.class);
            //startActivity(intent);
            getActivity().finish();
        }

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
