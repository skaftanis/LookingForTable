package com.smyc.kaftanis.lookingfortable;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.smyc.kaftanis.lookingfortable.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaftanis on 2/22/16.
 */
public class RatingDialog extends DialogFragment implements View.OnClickListener{

    RatingBar ratingBar;
    Button submit;

    Float rating;
    RequestQueue requestQueue;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_rating_dialog, null);
        getDialog().setTitle("Rate this input");

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        submit = (Button) view.findViewById(R.id.submit);

        ratingBar.setOnClickListener(this);
        submit.setOnClickListener(this);


        return view;
    }

    //generally implemented. show with if's what widget is clicked.
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.submit) {

                rating  = ratingBar.getRating();
                String tempLink;
                if (storeLog.nameClicked.contains(" "))
                    tempLink=storeLog.nameClicked.replace(" ","%20");
                else
                    tempLink=storeLog.nameClicked;

                JSONproccess("PRIVATE?name="+tempLink );

        }

    }


    private void JSONproccess ( String loginURL ) {

        requestQueue = Volley.newRequestQueue(getActivity());
        // output = (TextView) findViewById(R.id.jsonData);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            JSONArray ja= null;
                            ja = response.getJSONArray("results");

                            JSONObject jsonObject = null;
                            jsonObject = ja.getJSONObject(0); //there is on;y ore responce

                            String oldRating = jsonObject.getString("rating");

                            //normalize newRating and create the next value
                            double newValue = ((0.2 * rating )+Double.parseDouble(oldRating))/2;

                            String tempLink;
                            if (storeLog.nameClicked.contains(" "))
                                tempLink=storeLog.nameClicked.replace(" ","%20");
                            else
                                tempLink=storeLog.nameClicked;

                            JSONproccessUpdate("PRIVATE?name=" + tempLink + "&rating=" + Double.toString(newValue));



                        }catch(JSONException e){e.printStackTrace();}
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


    }


    private void JSONproccessUpdate ( String loginURL ) {

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
        Toast.makeText(getActivity(), "Your rating has submitted successfully", Toast.LENGTH_LONG).show();
        dismiss();


    }

}
