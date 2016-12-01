package com.smyc.kaftanis.lookingfortable;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by kaftanis on 3/19/16.
 */
public class getSearchResults extends DialogFragment {


    ArrayList<String> listLabels;
    ArrayAdapter<String> adapter;

    ListView listView;

    RequestQueue requestQueue;
    String selectedName;
    String selectedTown;

    ProgressDialog progress;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle("Αποτελέσματα");
        View view = inflater.inflate(R.layout.list_gps_dialog, null);

        listLabels = new ArrayList<String>();
        listView = (ListView) view.findViewById(R.id.listView2);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                listView.setEnabled(false);

                selectedName = listLabels.get(position);

                progress = new ProgressDialog(getActivity());
                progress.setTitle("Φόρτωση");
                progress.setMessage("Λήψη πληροφοριών...");
                progress.show();

                try {
                    final String s = URLEncoder.encode(selectedName, "utf-8");
                    String tempLink;
                    if (s.contains(" "))
                        tempLink=s.replace(" ","%20");
                    else
                        tempLink=s;
                    JSONproccess2("--name=" + s);
                  

                } catch (UnsupportedEncodingException e ) {}


            }
        });


        // Toast.makeText(this, cit  , Toast.LENGTH_LONG).show();


        if (!searchCities.citySelected.equals("Όλες") && !searchKind.kindSelected.equals("Όλα"))
            JSONproccess("--);
        if ( searchCities.citySelected.equals("Όλες") && !searchKind.kindSelected.equals("Όλα") )
            JSONproccess("--");
        if ( !searchCities.citySelected.equals("Όλες") && searchKind.kindSelected.equals("Όλα"))
            JSONproccess("--");
        if ( searchCities.citySelected.equals("Όλες") && searchKind.kindSelected.equals("Όλα") )
            JSONproccess("--");



        return view;
    }

    private void JSONproccess ( String loginURL ) {



        requestQueue = Volley.newRequestQueue(getActivity());
        // output = (TextView) findViewById(R.id.jsonData);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray ja = response.getJSONArray("results");

                            for (int i = 0; i < ja.length(); i++) {

                                JSONObject jsonObject = ja.getJSONObject(i);

                                String name = jsonObject.getString("name");

                                if (name.equals("SPACE")) {
                                    getDialog().setTitle("oops");
                                    listLabels.add("Δεν υπάρχει κάποιο τέτοιο ανοιχτό κατάστημα");
                                }
                                else
                                    listLabels.add(name);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter= new ArrayAdapter<String>(getActivity(), R.layout.listitem,R.id.txtitem, listLabels);
                        listView.setAdapter(adapter);


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

    private void JSONproccess2 ( String loginURL ) {

        requestQueue = Volley.newRequestQueue(getActivity());
        // output = (TextView) findViewById(R.id.jsonData);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray ja = response.getJSONArray("results");
                            JSONObject jsonObject = ja.getJSONObject(0);

                            String location = jsonObject.getString("location");
                            String lat = jsonObject.getString("lat");
                            String lon = jsonObject.getString("lon");
                            String official_input = jsonObject.getString("official_input");
                            String user_input = jsonObject.getString("user_input");
                            String status = jsonObject.getString("status");
                            String link = jsonObject.getString("photo_link");
                            String id = jsonObject.getString("id");
                            String size = jsonObject.getString("size");
                            String rating = jsonObject.getString("rating");
                            String isOfficial = jsonObject.getString("isOfficial");
                            String kind = jsonObject.getString("kind");
                            selectedTown=jsonObject.getString("town");
                            String strRating = jsonObject.getString("strRating");
                            String forced = jsonObject.getString("forced");
                            String entrace = jsonObject.getString("entrance");



                            progress.dismiss();
                            dismiss();

                            Intent intent = new Intent(getActivity(),  InfosActivity.class);
                            intent.putExtra("name", selectedName);
                            intent.putExtra("town", selectedTown);
                            intent.putExtra("location", location);
                            intent.putExtra("lat", lat );
                            intent.putExtra("lon", lon );
                            intent.putExtra("official_input", official_input);
                            intent.putExtra("user_input", user_input);
                            intent.putExtra("status", status);
                            intent.putExtra("photo_link", link);
                            intent.putExtra("id", id);
                            intent.putExtra("size", size);
                            intent.putExtra("rating", rating);
                            intent.putExtra("isOfficial", isOfficial);
                            intent.putExtra("kind", kind);
                            intent.putExtra("strRating", strRating);
                            intent.putExtra("forced", forced);
                            intent.putExtra("entrance", entrace);
                            startActivity(intent);




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert);
        }
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Results");
        return dialog;
    }



}
