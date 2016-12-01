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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
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
    ///TextView statusLabel;
    TextView latestupdateLabel;
    TextView moneyLabel;

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
    float strRating;
    String official_input;
    String isOfficial;
    String rating;
    String forced;
    String entrance;

    String latest;

    int finalValue;

    ProgressDialog progress;
    int endOfCounter=0; // 2 when text and image are loaded

    Button more;
    Button storeRating;

    private Menu menu;

    boolean moreEnable=true;

    int probability;

    ProgressDialog progress2;


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
            official_input = extras.getString("official_input");
            user_input = extras.getString("user_input");
            status = extras.getString("status");
            String link = extras.getString("photo_link");
            storeID = extras.getString("id");
            size = extras.getString("size");
            rating=extras.getString("rating");
            isOfficial=extras.getString("isOfficial");
            kind=extras.getString("kind");
            strRating=Float.parseFloat(extras.getString("strRating"));
            forced = extras.getString("forced");
            entrance = extras.getString("entrance");




            if (isOfficial.equals("0"))
                finalValue=Integer.parseInt(user_input);
            else
                finalValue=Math.round(Float.parseFloat(rating)*Integer.parseInt(official_input)+(1-Float.parseFloat(rating))*Integer.parseInt(user_input));


            new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
                    .execute(link);

        //title with stars setup
            int noOfStars = Math.round((strRating*10)/2);

            String stars="";
            for (int i=0; i<noOfStars; i++)
                stars=stars+"\u2605";
            String printName;
            if (name.length() > 8 )
                printName=name.substring(0,7);
            else
                printName=name;
            setTitle(printName+ " " +stars);

        //more button staff
        more = (Button) findViewById(R.id.more);

        more.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                     if (moreEnable) {
                         Intent intent = new Intent(InfosActivity.this, storeLog.class);
                         intent.putExtra("official_input", official_input);
                         intent.putExtra("rating", rating);
                         startActivity(intent);
                     } else {
                         Toast.makeText(getApplicationContext(), "Δεν υπάρχουν διαθέσιμα δεδομένα", Toast.LENGTH_SHORT).show();
                     }
            }
        });



          //rating button staff
            Calendar c = Calendar.getInstance();
            final int cur_day_of_year = c.get(Calendar.DAY_OF_YEAR);
            final int cur_year = c.get(Calendar.YEAR);


            storeRating = (Button) findViewById(R.id.button11);
            storeRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences sharedPreferences = getSharedPreferences("strRating", Context.MODE_PRIVATE);
                    int day_of_year = sharedPreferences.getInt(name+"day",-1);
                    int year = sharedPreferences.getInt(name+"year",-1);

                    int flag=0; //0 -> not do it, 1 -> do it

                    if (day_of_year == -1 || year == -1)
                        flag=1;
                    else {
                        if (cur_year == year) {
                            if (cur_day_of_year - day_of_year > 7) {
                                flag = 1;
                            } else {
                                flag = 0;
                            }
                        }
                        else {
                            if ((365 - day_of_year) + cur_day_of_year > 7)
                                flag = 1;
                            else
                                flag = 0;
                        }
                    }

                    if (flag == 1 ) {
                        StoreRatingDialog ratingDialog = new StoreRatingDialog();
                        ratingDialog.show(getFragmentManager(), "ratingDialog");
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Μπορείτε να αξιολογήσετε το συγκεκριμένο κατάστημα μόνο μια φορά σε 7 ημέρες", Toast.LENGTH_LONG).show();


                }
            });




            // text = (TextView) findViewById(R.id.addr);
            townLabel = (TextView) findViewById(R.id.townLabel);
            addressLabel = (TextView) findViewById(R.id.AddressLabel);
           // statusLabel = (TextView) findViewById(R.id.StatusLabel);
            latestupdateLabel = (TextView) findViewById(R.id.LatestUpdateLabel);
            moneyLabel = (TextView) findViewById(R.id.moneyLabel);

        mapButton = (Button) findViewById(R.id.button);
        setSpotButton = (Button) findViewById( R.id.button2);
            range = (TextView) findViewById(R.id.range);

            int entranceInt  = Integer.parseInt(entrance);
            if (entranceInt > 0  && entranceInt <= 3)
                moneyLabel.setText("Πιθανόν με είσοδο");
            else if (entranceInt > 3 )
                moneyLabel.setText("Με είσοδο");
            else if (entranceInt < 0 && entranceInt >= -3 )
                moneyLabel.setText("Πιθανόν χωρίς είσοδο");
            else if (entranceInt < -3 )
                moneyLabel.setText("Χωρίς είσοδο");
            else if (entranceInt == 0 )
                moneyLabel.setText("Δεν υπάρχουν στοιχεία για είσοδο");


            setSpotButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN )
                      setSpotButton.setBackgroundResource(R.drawable.set_hovered);
                    else if (event.getAction() == MotionEvent.ACTION_UP )
                        setSpotButton.setBackgroundResource(R.drawable.set2);

                    return false;
                }
            });

            more.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN )
                        more.setBackgroundResource(R.drawable.list_hovered);
                    else if (event.getAction() == MotionEvent.ACTION_UP )
                        more.setBackgroundResource(R.drawable.list234);
                    return false;
                }
            });

            mapButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN )
                        mapButton.setBackgroundResource(R.drawable.map_hovered);
                    else if (event.getAction() == MotionEvent.ACTION_UP )
                        mapButton.setBackgroundResource(R.drawable.map2);
                    return false;
                }
            });

            storeRating.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN )
                        storeRating.setBackgroundResource(R.drawable.star_hovered);
                    else if (event.getAction() == MotionEvent.ACTION_UP )
                        storeRating.setBackgroundResource(R.drawable.star2);
                    return false;
                }
            });

        //no clicable in guest mode
        if (MainActivity.isLoged==false)
            setSpotButton.setClickable(false);

        mapButton.setOnClickListener(MapListener);
        setSpotButton.setOnClickListener(setSpotListener);

            progress = new ProgressDialog(this);
            progress.setTitle("Φόρτωση");
            progress.setMessage("Λήψη πληροφοριών...");
            progress.show();


            JSONproccess("--=" + storeID + "&user=" + MainActivity.loginId);






        mLevel = START_LEVEL;
            //ADS
        // Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).
        mInterstitialAd = newInterstitialAd();


        SharedPreferences sharedPreferences = getSharedPreferences("adsStaff", Context.MODE_PRIVATE);
        probability = sharedPreferences.getInt("prob", 30);


        //30-70% propability to show Interstitial add
        Random r = new Random();
        int randomAddChoice = r.nextInt(100-1) + 1;
        if (randomAddChoice <= probability ) {
            loadInterstitial();
            showInterstitial();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with_fav, menu);
        this.menu=menu;

        SharedPreferences sharedPreferences = getSharedPreferences("favouriteStores", Context.MODE_PRIVATE);
        if ( sharedPreferences.getString(name, "" ).equals(name) )
            menu.getItem(1).setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.starfilled48, null));

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
            finish();

            return true;
        }

        if ( id == R.id.add_store) {
            AddNewStore addNewStore = new AddNewStore();
            addNewStore.show(getFragmentManager(),"addnewStore");
        }

        if ( id == R.id.bug_report) {
            BugReport bugReport = new BugReport();
            bugReport.show(getFragmentManager(), "bugReport");
        }

        //toggle favourite
        if ( id == R.id.action_favourite) {
            if ( menu.getItem(1).getIcon().getConstantState().equals(ResourcesCompat.getDrawable(getResources(), R.drawable.star48, null).getConstantState())) {
                menu.getItem(1).setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.starfilled48, null));
                SharedPreferences shared = getSharedPreferences("favouriteStores", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString( name , name);
                editor.apply();
                Toast.makeText(this, "Προστέθηκε στα αγαπημένα", Toast.LENGTH_SHORT).show();
            }
            else {
                menu.getItem(1).setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.star48, null));
                SharedPreferences shared = getSharedPreferences("favouriteStores", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.remove(name);
                editor.apply();
                Toast.makeText(this, "Αφαιρέθηκε από τα αγαπημένα", Toast.LENGTH_SHORT).show();

            }

        }

        if ( id == R.id.store_owners) {

            AlertDialog alertDialog = new AlertDialog.Builder(InfosActivity.this).create();
            alertDialog.setTitle("Για καταστηματάρχες");
            alertDialog.setMessage("Εάν είστε ιδιοκτήτης κάποιου καταστήματος και επιθυμείτε να αποκτήσετε κωδικό για το LookingForTable for Stores για να " +
                    "διαχειρίζεστε τη κίνηση του καταστήματός επίσημα ή εάν θέλετε να αφαιρέσουμε το κατάστημά σας από το app,  στείλτε μας email στο lft@showmeyourcode.org");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Αποστολή Mail",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"lft@showmeyourcode.org"});
                            i.putExtra(Intent.EXTRA_SUBJECT, "ΑΙΤΗΣΗ ΓΙΑ LOOKINGFORTABLE FOR STORES");
                            i.putExtra(Intent.EXTRA_TEXT   , "");
                            try {
                                startActivity(Intent.createChooser(i, "Send mail..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(InfosActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                            }

                            dialog.dismiss();
                        }
                    });
            alertDialog.show();


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
                Toast.makeText(getApplicationContext(), "Δε μπορείτε να κάνετε καταχώρηση σαν επισκέπτης", Toast.LENGTH_SHORT).show();
            else if (forced.equals("-1")) {
                if (status.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Το κατάστημα είναι κλειστό", Toast.LENGTH_SHORT).show();
                }
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
            else {
                if (forced.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Το κατάστημα είναι κλειστό", Toast.LENGTH_SHORT).show();
                }
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

                            JSONprocessPAST("--?name=" + storeID); //delete_the_past - deletes old records if we are 1hour or more without a new record


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
                            String minutesToPrint;
                            if ( minutesBefore.equals("1"))
                                minutesToPrint = "Τελευταία ανανέωση: " + minutesBefore+ " λεπτό πριν";
                            else
                                minutesToPrint = "Τελευταία ανανέωση: " + minutesBefore+ " λεπτά πριν";




                            String spots="";
                            boolean isClub=false;

                            //αν το μαγαζί δεν είναι club
                            if ( !kind.equals("Club")) {


                                if (Integer.parseInt(minutesBefore) > 60) {
                                    spots = "Δεν υπάρχει πρόσφατη ενημέρωση";
                                    minutesToPrint = "Τελευταία ανανέωση: Πάνω από 1 ώρα";
                                   // Toast.makeText(getApplicationContext(), "Δεν υπάρχουν διαθέσιμα δεδομένα", Toast.LENGTH_SHORT).show();
                                    //more.setBackgroundResource(R.drawable.list234blur);
                                    moreEnable=false;
                                    //more.setEnabled(false);

                                } else if (Integer.parseInt(minutesBefore) == -1) {
                                    spots = "Δεν υπάρχει πρόσφατη ενημέρωση";
                                    minutesToPrint = "Τελευταία ανανέωση: Πάνω από 1 ώρα";
                                    moreEnable=false;
                                    //  Toast.makeText(getApplicationContext(), "Δεν υπάρχουν διαθέσιμα δεδομένα", Toast.LENGTH_SHORT).show();
                                    //more.setBackgroundResource(R.drawable.list234blur);
                                    //more.setEnabled(false);
                                } else {
                                    if (finalValue > 0)
                                        spots = Integer.toString(finalValue - 1) + "-" + Integer.toString(finalValue + 2);
                                    else
                                        spots = "0-2";
                                }

                            } else {

                                isClub=true;

                                if (Integer.parseInt(minutesBefore) > 60) {
                                    spots = "Δεν υπάρχει πρόσφατη ενημέρωση";
                                    minutesToPrint = "Τελευταία ανανέωση: Πάνω από 1 ώρα";
                                    moreEnable=false;
                                    range.setTextSize(20);
                                    range.setText(spots);
                                    // Toast.makeText(getApplicationContext(), "Δεν υπάρχουν διαθέσιμα δεδομένα", Toast.LENGTH_SHORT).show();
                                    //more.setBackgroundResource(R.drawable.list234blur);
                                    //more.setEnabled(false);

                                } else if (Integer.parseInt(minutesBefore) == -1) {
                                    spots = "Δεν υπάρχει πρόσφατη ενημέρωση";
                                    minutesToPrint = "Τελευταία ανανέωση: Πάνω από 1 ώρα";
                                    range.setText(spots);
                                    range.setTextSize(20);
                                    moreEnable=false;

                                    // Toast.makeText(getApplicationContext(), "Δεν υπάρχουν διαθέσιμα δεδομένα", Toast.LENGTH_SHORT).show();
                                   // more.setBackgroundResource(R.drawable.list234blur);
                                    //more.setEnabled(false);
                                }
                                else {
                                    double doubleSize = Double.parseDouble(size);
                                    if (finalValue > 0 ) {
                                        if ( finalValue >= doubleSize-0.05*doubleSize )
                                            range.setText("Άδειο");
                                        else if ( finalValue >= doubleSize-0.25*doubleSize)
                                            range.setText("Σχεδόν Άδειο");
                                        else if ( finalValue >= doubleSize-0.5*doubleSize)
                                            range.setText("Μισό γεμάτο");
                                        else if ( finalValue >= doubleSize-0.85*doubleSize)
                                            range.setText("Σχεδόν γεμάτο");
                                        else if (finalValue == doubleSize)
                                            range.setText("Γεμάτο");


                                    }
                                }

                            }


                            if ( isClub == false) {

                                if (!spots.equals("Δεν υπάρχει πρόσφατη ενημέρωση"))
                                    range.setText(spots + " τραπέζια");
                                else {
                                    range.setTextSize(20);
                                    range.setText("Δεν υπάρχει πρόσφατη ενημέρωση");
                                }

                            }

                            String realStatus;

                            if ( ( forced.equals("-1") && status.equals("0") )  || (forced.equals("0"))) {
                                moreEnable=false;

                                //more.setEnabled(false);
                                //more.setBackgroundResource(R.drawable.list234blur);
                                range.setTextSize(30);
                                range.setText("Κλειστό");
                                realStatus="Κλειστό";
                            }
                            else realStatus="Ανοιχτό";

                            townLabel.setText(town);
                            addressLabel.setText(location);
                            latestupdateLabel.setText(minutesToPrint);

                           // if (status.equals("null"))
                            //    statusLabel.setText("Άγνωστο");
                            //else
                             //   statusLabel.setText(realStatus);

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
        JSONproccess2("--id=" + storeID);


    }

    private Activity getActivity(){
        return this;
    }








}
