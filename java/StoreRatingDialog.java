package com.smyc.kaftanis.lookingfortable;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by skaff on 8/6/2016.
 */
public class StoreRatingDialog extends DialogFragment implements View.OnClickListener {


    RatingBar ratingBar;
    Button submit;

    Float rating;
    RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_rating_dialog, null);
        getDialog().setTitle("Αξιολόγησε το(ν): "+InfosActivity.name);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        submit = (Button) view.findViewById(R.id.submit);

        ratingBar.setOnClickListener(this);
        submit.setOnClickListener(this);


        return view;


    }

    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.submit) {

            Calendar c = Calendar.getInstance();
            final int cur_day_of_year = c.get(Calendar.DAY_OF_YEAR);
            final int cur_year = c.get(Calendar.YEAR);


            SharedPreferences shared = getActivity().getSharedPreferences("strRating", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putInt(InfosActivity.name+"day", cur_day_of_year);
            editor.putInt(InfosActivity.name+"year", cur_year);
            editor.apply();


            rating  = ratingBar.getRating();
            String tempLink;
            if (InfosActivity.name.contains(" "))
                tempLink=InfosActivity.name.replace(" ","%20");
            else
                tempLink=InfosActivity.name;

            JSONproccess("--name="+tempLink );

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

                            String oldRating = jsonObject.getString("strRating");

                            //normalize newRating and create the next value
                            double newValue = ((0.2 * rating )+Double.parseDouble(oldRating))/2;

                            String tempLink;
                            if (InfosActivity.name.contains(" "))
                                tempLink=InfosActivity.name.replace(" ","%20");
                            else
                                tempLink=InfosActivity.name;

                            JSONproccessUpdate("--?name=" + tempLink + "&rating=" + Double.toString(newValue));



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
        Toast.makeText(getActivity(), "Η κριτική σας καταχωρήθηκε με επιτυχία", Toast.LENGTH_LONG).show();
        dismiss();


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert);
        }
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Αξιολόγησε τον: "+InfosActivity.name);
        return dialog;
    }

}
