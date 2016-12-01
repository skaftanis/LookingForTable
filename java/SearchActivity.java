package com.smyc.kaftanis.lookingfortable;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.os.Handler;

public class SearchActivity extends AppCompatActivity {


    String[] items;
    Integer[] pictures;

    ArrayList<String> listItems;
    ArrayList<Integer> listPicture;

    //ArrayAdapter<String> adapter;
    SearchListAdapter adapter;

    ListView listView;
    EditText editText;
    ArrayList<String> listLabels;
    ArrayList<Integer> imageLabels;
    ArrayList<String> equivalentList; //this arraylist follows the changes in the listView

    int length = 0;
    RequestQueue requestQueue;

    private static final int PROGRESS = 0x1;
    private ProgressBar mProgress;
    private int mProgressStatus = 0;

    private Handler mHandler = new Handler();

    String selectedName;
    String selectedTown;

    boolean clicked = false;

    ProgressDialog progress;
    ProgressDialog progress2;
    public static ProgressDialog progress3;


    private LocationManager locationManager;
    private LocationListener locationListener;

    public static double lat;
    public static double lon;

    FloatingActionButton fab;


    @Override
    protected void onRestart() {
        super.onRestart();


        progress = new ProgressDialog(this);
        progress.setTitle("Φόρτωση");
        progress.setMessage("Λήψη των καταστημάτων...");
        progress.show();

        listLabels.clear();
        equivalentList.clear();
        listView.setAdapter(null);
        listItems.clear();
        imageLabels.clear();


        JSONproccess("--");


    }

    @Override
    protected void onResume() {
        super.onResume();

         listView.setEnabled(true);



             if (MainActivity.loginName == null)
            setTitle("Welcome Guest");
          else
            setTitle("Welcome " + MainActivity.loginName);




    }

/*
    private void configureButton() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 1000, 15, locationListener);
       // locationManager.getLastKnownLocation("gps");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 10:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }

    }

*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress = new ProgressDialog(this);
        progress.setTitle("Φόρτωση");
        progress.setMessage("Λήψη των καταστημάτων...");
        progress.show();

        if (MainActivity.loginName == null)
            setTitle("Welcome Guest");
        else
             setTitle("Welcome " + MainActivity.loginName);

        /*
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                lat=location.getLatitude();
                lon=location.getLongitude();

                ListGPSDialog listGPSDialog = new ListGPSDialog();
                listGPSDialog.show(getFragmentManager(), "listGpsLialog");


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }

        };
*/
      //  Toast.makeText(SearchActivity.this, Double.toString(getGPS()[0]) +  "," + Double.toString(getGPS()[1])  , Toast.LENGTH_LONG).show();


        //map button
         fab = (FloatingActionButton) findViewById(R.id.fab);


         fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progress3 = new ProgressDialog(SearchActivity.this);
                progress3.setTitle("Φόρτωση");
                progress3.setMessage("Λήψη τοποθεσίας...");
                progress3.show();

                double[] latlon = new double[2];
                latlon=getGPS();
                lat=latlon[0];
                lon=latlon[1];

                //if gps is not enable
                if (lat == 0.0 && lon==0.0) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    progress3.dismiss();
                    latlon = new double[2];
                    latlon=getGPS();
                    lat=latlon[0];
                    lon=latlon[1];
                }
                else {
                    ListGPSDialog listGPSDialog = new ListGPSDialog();
                    listGPSDialog.show(getFragmentManager(), "listGpsLialog");
                }

                /*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                        }, 10);
                        return;
                    }
                }
                else {
                    locationManager.requestLocationUpdates("gps", 300000, 15, locationListener);
                }
                 */
            }
        });
      //  fab.setOnClickListener(new View.OnClickListener() {
      //      @Override
      //      public void onClick(final View view) {


      //          ListGPSDialog listGPSDialog = new ListGPSDialog();
      //          listGPSDialog.show(getFragmentManager(), "listGpsLialog");



              //  locationManager.requestLocationUpdates("gps", 5000, 1000, locationListener);
               // Toast.makeText(SearchActivity.this, "-->" + lat , Toast.LENGTH_SHORT).show();
                //clicked=true;
                //configureButton();
                //100000 -> nsec to update
                //1000 -> meters to update
                //Maybe change them in the future when we want to know nearby stores #TODO

      //      }
    //    });




        listLabels = new ArrayList<String>();
        imageLabels = new ArrayList<Integer>();

        equivalentList = new ArrayList<String>();

        listView = (ListView) findViewById(R.id.listview);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                listView.setEnabled(false);


                selectedName = equivalentList.get(position);
                String[] separated = selectedName.split(",");
                selectedName = separated[0];
                selectedTown = separated[1];
                String[] separared2=selectedTown.split("\n");
                selectedTown=separared2[0];
                //load other infos

                progress2 = new ProgressDialog(SearchActivity.this);
                progress2.setTitle("Φόρτωση");
                progress2.setMessage("Λήψη πληροφοριών...");
                progress2.show();


                try {
                    final String s = URLEncoder.encode(selectedName, "utf-8");
                    String tempLink;
                    if (s.contains(" "))
                        tempLink=s.replace(" ","%20");
                    else
                        tempLink=s;
                    JSONproccess2("--?name=" + s);
                

                } catch (UnsupportedEncodingException e ) {}

             //  String nameForLink=selectedName.replace(" ","%20"); //fixes the bug with spaces in name




                //listView.getChildAt(position).setText
                // equivalentList contains the right name on $position
                // equivalentList contains the right name on $position
                // Toast.makeText(SearchActivity.this, "" +  equivalentList.get(position), Toast.LENGTH_SHORT).show();
                //Toast.makeText(SearchActivity.this, "" +  equivalentList.get(position), Toast.LENGTH_SHORT).show();


            }


        });



        editText = (EditText) findViewById(R.id.txtsearch);

        JSONproccessSTATUS("...call_status_update.php");



        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                length = s.toString().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    initList();
                } else {
                    searchItem(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < length) {
                    initList();

                    for (int i=0; i<items.length; i++) {
                        if (!items[i].toLowerCase().contains(s.toString().toLowerCase())  &&  !removeTones(items[i]).contains(removeTones(s.toString()))  &&  !removeTones(items[i]).toLowerCase().contains(removeTones(s.toString()).toLowerCase())  ) {
                            listItems.remove(items[i]);
                            equivalentList.remove(items[i]);
                            //listPicture.remove(pictures[i]);
                            for (int j=i; j<listPicture.size(); j++) {
                                if (listPicture.get(j) == pictures[i]) {
                                    listPicture.remove(pictures[i]);
                                    break;
                                }
                            }


                        }
                    }

                }

            }
        });


    } //on create end


    public void initList() {
        //items = new String[]{"Canada", "China", "Japan", "USA"};
        items=listLabels.toArray(items);
        pictures=imageLabels.toArray(pictures);


        listItems=new ArrayList<>(Arrays.asList(items));
        listPicture = new ArrayList<>(Arrays.asList(pictures));

        equivalentList=listItems;
        //adapter=new ArrayAdapter<String>(this,R.layout.listitem, R.id.txtitem, listItems);
        adapter = new SearchListAdapter<String>(this,listItems, listPicture);
         listView.setAdapter(adapter);

    }

    public void searchItem (String txtToSearch) {
        for (int i=0; i<items.length;i++) {
            if (!items[i].toLowerCase().contains(txtToSearch.toString().toLowerCase())  &&  !removeTones(items[i]).contains(removeTones(txtToSearch.toString()))  &&  !removeTones(items[i]).toLowerCase().contains(removeTones(txtToSearch.toString()).toLowerCase())  ) {
                listItems.remove(items[i]);
                equivalentList.remove(items[i]);
                for (int j=i; j<listPicture.size(); j++) {
                    if (listPicture.get(j) == pictures[i]) {
                        listPicture.remove(pictures[i]);
                        break;
                    }
                }

            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_fav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(SearchActivity.this, com.smyc.kaftanis.lookingfortable.Settings.class);
            startActivity(intent);
            return true;
        }
        if ( id == R.id.action_about) {

            AlertDialog alertDialog = new AlertDialog.Builder(SearchActivity.this).create();
            alertDialog.setTitle("Σχετικά");
            alertDialog.setMessage("Δημιουργήθηκε από τον Σπύρο Καφτάνη. Επισκεφτείτε το showmeyourcode.org για περισσότερα apps και tutorials. " +
                    "Για οποιοδήποτε feedback, σχόλια και παρατηρήσεις μπορείτε να επικοινωνήσετε μαζί μας στο: kaftanis@showmeyourcode.org " +
                    "Η εφαρμογή κέδισε το βραβείο καινοτομίας στον 5o διαγωνισμό της eestec! ");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            return true;
        }

        if ( id == R.id.action_search) {
            AdvancedSearch advancedSearch = new AdvancedSearch();
            advancedSearch.show(getFragmentManager(), "advanced");

            return true;
        }

        if ( id == R.id.action_memo) {
            Memo memo = new Memo();
            memo.show(getFragmentManager(), "memo");

            return true;
        }

        if ( id == R.id.action_favourite) {
            Intent intent = new Intent(SearchActivity.this, Favoutites.class);
            startActivity(intent);
        }

        if ( id == R.id.add_store) {
            AddNewStore addNewStore = new AddNewStore();
            addNewStore.show(getFragmentManager(),"addnewStore");
        }

        if ( id == R.id.bug_report) {
            BugReport bugReport = new BugReport();
            bugReport.show(getFragmentManager(), "bugReport");
        }

        if ( id == R.id.store_owners) {

            AlertDialog alertDialog = new AlertDialog.Builder(SearchActivity.this).create();
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
                                Toast.makeText(SearchActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                            }

                            dialog.dismiss();
                        }
                    });
            alertDialog.show();


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

                                String name = jsonObject.getString("name");
                                String town = jsonObject.getString("town");
                                String location = jsonObject.getString("location");
                                String user_input = jsonObject.getString("user_input");
                                String status = jsonObject.getString("status");
                                String forced = jsonObject.getString("forced");
                                float rating = Float.parseFloat(jsonObject.getString("rating") );
                                int official_input_int = Integer.parseInt(jsonObject.getString("official_input"));

                                String label = name + ", " + town +"\n"+location;

                                int user_input_int = Integer.parseInt(user_input);

                                //η τιμή με βάση την οποία επιλέγονται τα χρώματα (όπως και το php)
                              //  int mean = (user_input_int+official_input_int)/2;
                                float mean = rating*(float) official_input_int + (1-rating)*(float)user_input_int+1;

                                listLabels.add(label);

                                //εάν δεν έχει γίνει forced κάποια τιμή από τον καταστηματάρχη κοιτάμε το status
                                if (forced.equals("-1")) {
                                    if (status.equals("1")) {
                                        if (mean <= 2)
                                            imageLabels.add(R.drawable.orange);
                                        else if (mean <= 5)
                                            imageLabels.add(R.drawable.yellow);
                                        else
                                            imageLabels.add(R.drawable.green);
                                    } else imageLabels.add(R.drawable.gray);
                                }
                                //εάν έχει γίνει forced κάποια τιμή κοιτάμε αυτή
                                else {
                                    if (forced.equals("1")) {
                                        if (mean <= 2)
                                            imageLabels.add(R.drawable.orange);
                                        else if (mean <= 5)
                                            imageLabels.add(R.drawable.yellow);
                                        else
                                            imageLabels.add(R.drawable.green);
                                    } else imageLabels.add(R.drawable.gray);
                                }




                            }

                            items = new String[listLabels.size()];
                            pictures = new Integer[imageLabels.size()];
                            initList();
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
                            String strRating = jsonObject.getString("strRating");
                            String forced = jsonObject.getString("forced");
                            String entrace = jsonObject.getString("entrance");


                            progress2.dismiss();

                            Intent intent = new Intent(SearchActivity.this, InfosActivity.class);
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

                            editText.setText("");

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


    private Activity getSearchActivity() {
        return this;
    }






    //update statuses
    private void JSONproccessSTATUS ( String loginURL ) {

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

        JSONproccess("--");



    }

    private double[] getGPS() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);


        /* Loop over the array backwards, and if you get an accurate location, then break                 out the loop*/
        Location l = null;

        for (int i=providers.size()-1; i>=0; i--) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                    }, 10);
                }
            }
            else {
                l = lm.getLastKnownLocation(providers.get(i));
            }


            if (l != null) break;
        }

        double[] gps = new double[2];
        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        return gps;
    }

    private  String removeTones( String input) {

        int[][] cross = new int[7][2];

        cross[0][0] = 945;
        cross[0][1] = 940;

        cross[1][0] = 949;
        cross[1][1] = 941;

        cross[2][0] = 951;
        cross[2][1] = 942;

        cross[3][0] = 953;
        cross[3][1] = 943;

        cross[4][0] = 959;
        cross[4][1] = 972;

        cross[5][0] = 965;
        cross[5][1] = 973;

        cross[6][0]= 969;
        cross[6][1] = 974;

        String test = input;

        System.out.println("StartString: " + test);
        int flag=0;


        String finalString="";
        for (int i=0; i<test.length(); i++) {
            flag=0;
            //System.out.print(test.charAt(i));
            int ascii_test = (int) test.charAt(i);
            for (int j=0; j<7; j++) {
                if ( ascii_test == cross[j][1]) {
                    ascii_test = cross[j][0];
                    finalString+=Character.toString( (char) ascii_test ) ;
                    flag=1;
                }
            }
            if (flag == 0) {
                finalString+=Character.toString( (char) ascii_test ) ;
            }

        }

        return finalString;


    }


}


//ΕΠΙΤΕΛΟΥΣ ΤΕΛΕΙΩΣΕ !!!!!!
