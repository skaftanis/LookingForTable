package com.smyc.kaftanis.lookingfortable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    public static String minutesClicked;
    public static String positionsClicked;
    RequestQueue requestQueue;
    ProgressDialog progress;

    List<String> stockNameList;
    List<String> stockMinutesList;
    List<String> stockPosList;
    List<String> alreadyRate;

    String official_input;

    public static boolean isOfficialPublic;
    public static String StoreRating;
    public static  int officialPos=-1; //η θέση που βρίσκεται το official input (εάν υπάρχει)

    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_log);

        Bundle extras = getIntent().getExtras();
        official_input = extras.getString("official_input");

        StoreRating = extras.getString("rating"); //το rating που έχει το μαγαζί για τις τιμές που βάζει


        setTitle("Πρόσφατα στο "+InfosActivity.name);

        //start the progrss dialog
        progress = new ProgressDialog(this);
        progress.setTitle("Φόρτωση");
        progress.setMessage("Λήψη πληροφοριών...");
        progress.show();

        //name List and array setup
        stockNameList = new ArrayList<String>();

        //minutes List and array setup
        stockMinutesList = new ArrayList<String>();

        //positions List and array setup
        stockPosList = new ArrayList<String>();

        alreadyRate = new ArrayList<String>();

     //   if ( isOfficial.equals("1")) {
     //       stockNameList.add("Official Input");
     //       stockMinutesList.add("trust");
      //      alreadyRate.add("rating");
      //      stockPosList.add("pos");
      //  }


        JSONproccess("--?storeId="+InfosActivity.storeID);



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
            alertDialog.setTitle("Σχετικά");
            alertDialog.setMessage("Δημιουργήθηκε από τον Σπύρο Καφτάνη. Επισκεφτείτε το showmeyourcode.org για περισσότερα apps και tutorials. " +
                    "Για οποιοδήποτε feedback, σχόλια και παρατηρήσεις μπορείτε να επικοινωνήσετε μαζί μας στο: kaftanis@showmeyourcode.org ");
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
                                    int starti=100000;
                                    for (int i = 0; i < ja.length(); i++) {

                                        JSONObject jsonObject = ja.getJSONObject(i);

                                        String name = jsonObject.getString("nickname");
                                        String input = jsonObject.getString("input");
                                        String minutes = jsonObject.getString("diff");
                                        String rating = jsonObject.getString("rating").substring(0,3);
                                        String isOfficial = jsonObject.getString("isOfficial");


                                        if (isOfficial.equals("0")) {
                                    stockNameList.add(name);
                                    minutes=minutes+" λεπτά πριν \nΒαθμολογία χρήστη:"+Double.parseDouble(rating)*10+"/10";
                                    stockMinutesList.add(minutes);
                                    stockPosList.add(input);
                                } else {
                                    //if there are many official inputs in the row keep (show with color) only the first
                                    if (i<starti) {
                                        officialPos = i;
                                        starti=i;
                                    }
                                    stockNameList.add(InfosActivity.name + " Official");
                                    minutes=minutes+" λεπτά πριν \nΒαθμολογία χρήστη="+Double.parseDouble(StoreRating)*10+"/10";
                                    stockMinutesList.add(minutes);
                                    stockPosList.add(input);
                                }

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
                                                 positionsClicked=stockPosList.get(position);
                                                 if (position == officialPos)
                                                     isOfficialPublic=true;
                                                 else
                                                     isOfficialPublic=false;



                                            if (nameClicked.equals(MainActivity.loginName)) {
                                                Toast.makeText(getActivity(), "Δε μπορείτε να αξιολογίσετε τον εαυτό σας", Toast.LENGTH_LONG).show();

                                            } else {


                                                //GET
                                                SharedPreferences sharedPreferences3 = getSharedPreferences("alreadyRate", Context.MODE_PRIVATE);
                                                int noOfRates = sharedPreferences3.getInt("Rates", -1);
                                                for (int i=0; i<noOfRates; i++) {
                                                    alreadyRate.add(sharedPreferences3.getString("Rate"+Integer.toString(i),"null" ));
                                                }

                                                if (!alreadyRate.contains(stockNameList.get(position)+InfosActivity.name+stockPosList.get(position))) {
                                                    RatingDialog ratingDialog = new RatingDialog();
                                                    ratingDialog.show(getFragmentManager(), "ratingDialog");

                                                }
                                                else {
                                                    Toast.makeText(getActivity(), "Έχετε ήδη αξιολογήσει αυτή τη καταχώρηση", Toast.LENGTH_LONG).show();

                                                }



                                            }



                                            // String last = sharedPreferences2.getString("lastSet", "");
                                           // int setsToday = sharedPreferences2.getInt("setsToday", 0);
                                           // int prob = sharedPreferences2.getInt("prob", 0);
                                           // String lastDown = sharedPreferences2.getString("lastDown", "");

                                            //    if (alreadyRate.contains(nameClicked+stockMinutesList.get(position)+stockPosList.get(position)) && !nameClicked.equals(MainActivity.loginName)){
                                            //        Toast.makeText(getActivity(), "You have already rate this entry", Toast.LENGTH_LONG).show();
                                           //     }
                                           // else {

                                                   // alreadyRate.add(nameClicked+stockMinutesList.get(position)+stockPosList.get(position));


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
