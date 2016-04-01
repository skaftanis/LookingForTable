package com.smyc.kaftanis.lookingfortable;

import android.app.DialogFragment;
import android.app.ProgressDialog;
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

import java.util.ArrayList;

/**
 * Created by kaftanis on 3/19/16.
 */
public class searchCities extends DialogFragment {

    ArrayList<String> listLabels;
    ArrayAdapter<String> adapter;

    ListView listView;

    RequestQueue requestQueue;

    public static String citySelected;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle("Select your city");
        View view = inflater.inflate(R.layout.list_gps_dialog, null);

        listLabels = new ArrayList<String>();
        listLabels.add("All");
        listView = (ListView) view.findViewById(R.id.listView2);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                citySelected = listLabels.get(position);
               // Toast.makeText( getActivity(), citySelected + " selected", Toast.LENGTH_SHORT).show();
                AdvancedSearch.city.setText("SELECT CITY ("+citySelected+" selected)");
                dismiss();


            }
        });



        JSONproccess("PRIVATE");


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

                            String label;

                            for (int i = 0; i < ja.length(); i++) {

                                JSONObject jsonObject = ja.getJSONObject(i);

                                String town = jsonObject.getString("town");

                                listLabels.add(town);

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



}
