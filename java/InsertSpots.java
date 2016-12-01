package com.smyc.kaftanis.lookingfortable;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Switch;
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
import org.w3c.dom.Text;

public class InsertSpots extends AppCompatActivity {


    TextView topTitle;

    public static String storeID;
    String storeName;
    String size;
    String kind;
    public static Switch entrance;



    //(public to use on ListItemDialo too)
    public static String minutesToOtherStore; //must be greater than 15 to allow a new check in (in this store)
    public static String postsInLastTenMinutes;

    at.markushi.ui.CircleButton apprButoon;
    at.markushi.ui.CircleButton exactButton;


    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_spots);



        Bundle extras = getIntent().getExtras();
        storeID = extras.getString("id");
        storeName = extras.getString("name");
        size = extras.getString("size");
        kind=extras.getString("kind");


        JSONproccess("-- + storeID + "&user=" + MainActivity.loginId);
        JSONproccess2(" --+ storeID + "&user=" + MainActivity.loginId);

        setTitle(storeName);

        apprButoon = (at.markushi.ui.CircleButton) findViewById(R.id.view);
        exactButton = (at.markushi.ui.CircleButton) findViewById(R.id.view2);
        topTitle = (TextView) findViewById (R.id.topTitle);
        entrance = (Switch) findViewById(R.id.eisodos);

        entrance.setTextOn("Ναι");
        entrance.setTextOff("Όχι");

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Roboto_Light.ttf");
        topTitle.setTypeface(type);

        topTitle.setText("Πόσα ελέυθερα τραπέζια βλέπετε στο " + storeName + " ?");

        apprButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ListItemDialog listItemDialog = new ListItemDialog();
                listItemDialog.show(getFragmentManager(), "listItemDialog");

            }
        });

        exactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kind.equals("Club")){
                    Toast.makeText(getApplicationContext(), "Μπορείτε να βάλετε μόνο εκτίμηση στα clubs", Toast.LENGTH_LONG).show();
                }
                else {
                    NumberChoiceDialog numberChoiceDialog = new NumberChoiceDialog();
                    numberChoiceDialog.show(getFragmentManager(), "numberChoiceDialog");
                }
            }
        });


    }


    private void JSONproccessInsert ( String loginURL ) {

        requestQueue = Volley.newRequestQueue(this);
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

        Toast.makeText(getApplicationContext(), "Ευχαριστούμε για τη καταχώρηση", Toast.LENGTH_LONG).show();
        //Intent intent = new Intent(InsertSpots.this, SearchActivity.class);
        //startActivity(intent);
        //finish();


    }

    // to find the minutes before the latest check in on an other store. allow new store every 15 minutes. So it must be
    //bigger that 15 to allow
    private void JSONproccess ( String loginURL ) {

        requestQueue = Volley.newRequestQueue(this);
        // output = (TextView) findViewById(R.id.jsonData);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray ja = response.getJSONArray("results");
                            JSONObject jsonObject = ja.getJSONObject(0);

                            minutesToOtherStore = jsonObject.getString("diff");


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

    //to find the checks in of the user in the selected store. Allow 4 every 10 minutes.
    private void JSONproccess2 ( String loginURL ) {

        requestQueue = Volley.newRequestQueue(this);
        // output = (TextView) findViewById(R.id.jsonData);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray ja = response.getJSONArray("results");
                            JSONObject jsonObject = ja.getJSONObject(0);

                            postsInLastTenMinutes = jsonObject.getString("times");


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





}


