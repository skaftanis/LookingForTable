package com.smyc.kaftanis.lookingfortable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.Locale;
import java.util.Random;

public class InfosActivity extends AppCompatActivity {

    private static final int START_LEVEL = 1;
    private int mLevel;
    private InterstitialAd mInterstitialAd;
    int tries = 2;

    TextView text;
    TextView townLabel;
    TextView addressLabel;
    TextView statusLabel;
    TextView latestupdateLabel;

    TextView range;
    Button mapButton;
    Button setSpotButton;
    String lat;
    String lon;
    public static String size;


    public static String storeID;
    public static String name;
    String user_input;
    String status;
    String location;
    String town;
    String kind;


    String latest;

    int finalValue;

    ProgressDialog progress;
    int endOfCounter=0; // 2 when text and image are loaded

    Button more;


    RequestQueue requestQueue;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_infos);



            //get staff from preview activty (all info)
            Bundle extras = getIntent().getExtras();
            name = extras.getString("name");
            town = extras.getString("town");
            location = extras.getString("location");
            lat = extras.getString("lat");
            lon = extras.getString("lon");
            String official_input = extras.getString("official_input");
            user_input = extras.getString("user_input");
            status = extras.getString("status");
            String link = extras.getString("photo_link");
            storeID = extras.getString("id");
            size = extras.getString("size");
            String rating=extras.getString("rating");
            String isOfficial=extras.getString("isOfficial");
            kind=extras.getString("kind");

            if (isOfficial.equals("0"))
                finalValue=Integer.parseInt(user_input);
            else
                finalValue=Math.round(Float.parseFloat(rating)*Integer.parseInt(official_input)+(1-Float.parseFloat(rating))*Integer.parseInt(user_input));



            new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
                    .execute(link);


        setTitle(name);

        //more button staff
        more = (Button) findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(InfosActivity.this, storeLog.class);
                    startActivity(intent);
            }
        });


           // text = (TextView) findViewById(R.id.addr);
            townLabel = (TextView) findViewById(R.id.townLabel);
            addressLabel = (TextView) findViewById(R.id.AddressLabel);
            statusLabel = (TextView) findViewById(R.id.StatusLabel);
            latestupdateLabel = (TextView) findViewById(R.id.LatestUpdateLabel);

        mapButton = (Button) findViewById(R.id.button);
        setSpotButton = (Button) findViewById( R.id.button2);
            range = (TextView) findViewById(R.id.range);

        //no clicable in guest mode
        if (MainActivity.isLoged==false)
            setSpotButton.setClickable(false);

        mapButton.setOnClickListener(MapListener);
        setSpotButton.setOnClickListener(setSpotListener);

            progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Getting informations...");
            progress.show();


            JSONproccess("PRIVATE?id=" + storeID + "&user=" + MainActivity.loginId);



        mLevel = START_LEVEL;
            //ADS
        // Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).
       // mInterstitialAd = newInterstitialAd();


        //30% propability to show Interstitial add
       // Random r = new Random();
       // int randomAddChoice = r.nextInt(100-1) + 1;
        //if (randomAddChoice <= 30 ) {
       //     loadInterstitial();
       //     showInterstitial();
       // }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(InfosActivity.this, com.smyc.kaftanis.lookingfortable.Settings.class);
            startActivity(intent);
            return true;
        }

        if ( id == R.id.action_about) {

            AlertDialog alertDialog = new AlertDialog.Builder(InfosActivity.this).create();
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
            finish();

            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (tries > 0 ) {
                    showInterstitial();
                    tries--;
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                goToNextLevel();
            }
        });
        return interstitialAd;
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
           // Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            goToNextLevel();
        };
    }

    private void loadInterstitial() {
        // Disable the next level button and load the ad.
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void goToNextLevel() {
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            endOfCounter++;
            if (endOfCounter >= 2)
                progress.dismiss();
        }
    }

    private View.OnClickListener MapListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 38.250545, 22.081094);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?q=" + lat+','+lon));
            startActivity(intent);
        }
    };

    private View.OnClickListener setSpotListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if ( MainActivity.isLoged == false)
                Toast.makeText(getApplicationContext(), "You can't do this in guest mode", Toast.LENGTH_SHORT).show();
            else if (status.equals("0"))
                Toast.makeText(getApplicationContext(), "The store is closed right now", Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(InfosActivity.this, InsertSpots.class);
                intent.putExtra("id", storeID);
                intent.putExtra("name",name );
                intent.putExtra("size", size);
                intent.putExtra("kind", kind);
                startActivity(intent);
                finish();
            }

        }
    };

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

                            latest = jsonObject.getString("diff");

                            JSONprocessPAST("PRIVATE?name=" + storeID); //delete_the_past - deletes old records if we are 1hour or more without a new record


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

    //to get the minites before the latest update in this store
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

                            String minutesBefore = jsonObject.getString("diff");
                            String minutesToPrint = minutesBefore+ " minutes ago";


                            String spots;
                            if ( Integer.parseInt(minutesBefore) > 60) {
                                spots = "No recent update";
                                more.setEnabled(false);

                            }
                            else if (Integer.parseInt(minutesBefore) == -1) {
                                spots = "No recent update";
                                minutesToPrint="More that 1 hour";
                                more.setEnabled(false);
                            }
                            else {
                                if ( finalValue > 0)
                            spots = Integer.toString(finalValue - 1) + "-" + Integer.toString(finalValue + 2);
                            else
                            spots = "0-2";
                        }

                        if (!spots.equals("No recent update"))
                            range.setText(spots+" tables");
                        else
                            range.setText("No recent update");

                            String realStatus;
                            if (status.equals("0"))
                                realStatus="closed";
                            else
                                realStatus="open";

                            townLabel.setText(town);
                            addressLabel.setText(location);
                            latestupdateLabel.setText(minutesToPrint);

                        if (status.equals("null"))
                            statusLabel.setText("Unknown");
                        else
                            statusLabel.setText(realStatus);

                          //  text.setText("Town: " + town + "\nAddress: " + location + "\nStatus: " + realStatus + "\nLatest update: " + minutesToPrint);

                            endOfCounter++;
                            if (endOfCounter >= 2)
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

    //1h check
    private void JSONprocessPAST ( String loginURL ) {

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

        //continue to getLatestUpdate
        JSONproccess2("PRIVATE?id=" + storeID);


    }

    private Activity getActivity(){
        return this;
    }







}
