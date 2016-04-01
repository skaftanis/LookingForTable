package com.smyc.kaftanis.lookingfortable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
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
import java.util.List;

public class storeLog extends AppCompatActivity {

    public static String[] minutes;
    public static String[] positions;
    public static String[] names;
    public static String nameClicked;
    RequestQueue requestQueue;
    ProgressDialog progress;

    List<String> stockNameList;
    List<String> stockMinutesList;
    List<String> stockPosList;
    List<String> alreadyRate;



    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_log);


        setTitle("Latest on "+InfosActivity.name);

        //start the progrss dialog
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Gettings informations...");
        progress.show();

        //name List and array setup
        stockNameList = new ArrayList<String>();

        //minutes List and array setup
        stockMinutesList = new ArrayList<String>();

        //positions List and array setup
        stockPosList = new ArrayList<String>();

        alreadyRate = new ArrayList<String>();



        JSONproccess("PRIVATE?storeId="+InfosActivity.storeID);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(storeLog.this, com.smyc.kaftanis.lookingfortable.Settings.class);
            startActivity(intent);
        }

        if ( id == R.id.action_about) {

            AlertDialog alertDialog = new AlertDialog.Builder(storeLog.this).create();
            alertDialog.setTitle("About");
            alertDialog.setMessage("This is a demo for 2016 eestec android competition. Stores are real, but the available tables appearing are not accurate, because " +
                    "people testing this app right now. If you want to add any store send us a mail on kaftanis@showmeyourcode.org. After the demo period there will be an " +
                    "online platform for store owners. ");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            return true;
        }

        if ( id == R.id.action_memo) {
            Memo memo = new Memo();
            memo.show(getFragmentManager(), "memo");

            return true;
        }

        if ( id == R.id.action_search) {

            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private void JSONproccess ( String loginURL ) {

        requestQueue = Volley.newRequestQueue(this);
        // output = (TextView) findViewById(R.id.jsonData);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                                try {

                                    JSONArray ja = response.getJSONArray("results");

                                    for (int i = 0; i < ja.length(); i++) {

                                        JSONObject jsonObject = ja.getJSONObject(i);

                                        String name = jsonObject.getString("nickname");
                                        String input = jsonObject.getString("input");
                                        String minutes = jsonObject.getString("diff");
                                        String rating = jsonObject.getString("rating").substring(0,3);
                                        minutes=minutes+" minutes ago \nuser rating="+Double.parseDouble(rating)*10+"/10";


                                stockNameList.add(name);
                                stockMinutesList.add(minutes);
                                stockPosList.add(input);

                            }

                            //finalize setup

                            names = new String[stockNameList.size()];
                            names = stockNameList.toArray(names);

                            minutes = new String[stockMinutesList.size()];
                            minutes = stockMinutesList.toArray(minutes);

                            positions = new String[stockPosList.size()];
                            positions = stockPosList.toArray(positions);



                            //  ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.listitem, R.id.txtitem, test);
                            ListAdapter adapter = new CustomAdapter(getActivity(), names);
                            listView = (ListView) findViewById(R.id.listView);

                            listView.setAdapter(adapter);

                                    //click on an item on list
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                                        @Override
                                        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                                                 nameClicked=stockNameList.get(position);

                                            //    if (alreadyRate.contains(nameClicked+stockMinutesList.get(position)+stockPosList.get(position)) && !nameClicked.equals(MainActivity.loginName)){
                                            //        Toast.makeText(getActivity(), "You have already rate this entry", Toast.LENGTH_LONG).show();
                                           //     }
                                           // else {

                                                   // alreadyRate.add(nameClicked+stockMinutesList.get(position)+stockPosList.get(position));

                                                    if (nameClicked.equals(MainActivity.loginName)) {
                                                        Toast.makeText(getActivity(), "You can't rate yourself", Toast.LENGTH_LONG).show();

                                                    } else {

                                                        RatingDialog ratingDialog = new RatingDialog();
                                                        ratingDialog.show(getFragmentManager(), "ratingDialog");

                                                    }
                                               // }




                                        }
                                    });


                            progress.dismiss();


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

    private Activity getActivity() {
        return this;
    }


}
