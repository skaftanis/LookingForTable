package com.smyc.kaftanis.lookingfortable;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Favoutites extends AppCompatActivity {

    String[] items;
    ProgressDialog progress2;
    RequestQueue requestQueue;

    String selectedName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoutites);

        setTitle("Αγαπημένα καταστήματα");

        ArrayList<String> listItems;
        listItems = new ArrayList<String>();

        SharedPreferences sharedPreferences = getSharedPreferences("favouriteStores", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            listItems.add(entry.getValue().toString());
        }


        if ( listItems.size() == 0 )
            Toast.makeText(getApplicationContext(), "Δεν έχετε κάποιo αγαπημένο κατάστημα ακόμα" , Toast.LENGTH_LONG).show();
        else {
            items = new String[listItems.size()];
            items=listItems.toArray(items);
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
            ListView listView = (ListView) findViewById(R.id.listView4);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                    progress2 = new ProgressDialog(Favoutites.this);
                    progress2.setTitle("Φόρτωση");
                    progress2.setMessage("Λήψη πληροφοριών...");
                    progress2.show();

                    selectedName = items[i];

                    //String nameForLink=items[i].replace(" ","%20"); //fixes the bug with spaces in name
                    try {
                        final String s = URLEncoder.encode(items[i], "utf-8");
                        String tempLink;
                        if (s.contains(" "))
                            tempLink=s.replace(" ","%20");
                        else
                            tempLink=s;
                        JSONproccess2("h--name=" + s);
          

                    } catch (UnsupportedEncodingException e ) {}






                    //Toast.makeText(getApplicationContext(),"pos= " +i + " text= " +items[i] , Toast.LENGTH_LONG).show();


                }
            });


        }



       // String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X"};


    }

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
                            String town = jsonObject.getString("town");
                            String strRating = jsonObject.getString("strRating");
                            String forced = jsonObject.getString("forced");
                            String entrace = jsonObject.getString("entrance");




                            progress2.dismiss();

                            Intent intent = new Intent(Favoutites.this, InfosActivity.class);
                            intent.putExtra("name", selectedName);
                            intent.putExtra("town", town);
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

}
