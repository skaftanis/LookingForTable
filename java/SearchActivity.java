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
import java.lang.reflect.Array;
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
    protected void onResume() {
        super.onResume();

         listView.setEnabled(true);

          if (MainActivity.loginName == null)
            setTitle("Welcome Guest");
          else
            setTitle("Welcome " + MainActivity.loginName);

    }


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
        locationManager.requestLocationUpdates("gps", 300000, 15, locationListener);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Gettings the stores...");
        progress.show();

        if (MainActivity.loginName == null)
            setTitle("Welcome Guest");
        else
            setTitle("Welcome " + MainActivity.loginName);

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




        //map button
         fab = (FloatingActionButton) findViewById(R.id.fab);


         fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progress3 = new ProgressDialog(SearchActivity.this);
                progress3.setTitle("Loading");
                progress3.setMessage("Getting your location...");
                progress3.show();



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
                progress2.setTitle("Loading");
                progress2.setMessage("Getting informations...");
                progress2.show();

                String nameForLink=selectedName.replace(" ","%20"); //fix the bug with spaces in name
                JSONproccess2("PRIVATE?name=" + nameForLink);

                //listView.getChildAt(position).setText
                // equivalentList contains the right name on $position
                // equivalentList contains the right name on $position
                // Toast.makeText(SearchActivity.this, "" +  equivalentList.get(position), Toast.LENGTH_SHORT).show();
                //Toast.makeText(SearchActivity.this, "" +  equivalentList.get(position), Toast.LENGTH_SHORT).show();


            }


        });



        editText = (EditText) findViewById(R.id.txtsearch);


        JSONproccess("PRIVATE");


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
                        if (!items[i].toLowerCase().contains(s.toString().toLowerCase())) {
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
            if (!items[i].toLowerCase().contains(txtToSearch.toString().toLowerCase())) {
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

                                String label = name + ", " + town +"\n"+location;

                                int user_input_int = Integer.parseInt(user_input);

                                listLabels.add(label);


                                if ( status.equals("1")) {
                                    if (user_input_int <= 2)
                                        imageLabels.add(R.drawable.orange);
                                    else if (user_input_int <= 5)
                                        imageLabels.add(R.drawable.yellow);
                                    else
                                        imageLabels.add(R.drawable.green);
                                }
                                else imageLabels.add(R.drawable.gray);




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

        JSONproccessSTATUS("PRIVATE");


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


    }

}
