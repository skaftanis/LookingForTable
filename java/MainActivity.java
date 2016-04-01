package com.smyc.kaftanis.lookingfortable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.widget.LikeView;


public class MainActivity extends AppCompatActivity {


    Button signUpButton;
    Button loginButton;
    Button guestButton;


    public static String loginName;//the login name of the player
    public static String loginMail;
    public static String loginId;
    public static boolean isLoged=false;


    TextView output ;
    //String loginURL="https://lookingfortable-skaftanis.c9.io/getAllCustomers.php";
    String data = "";

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //setup buttons
        signUpButton = (Button) findViewById(R.id.newAccount);
        loginButton = (Button) findViewById(R.id.Singin);
        guestButton = (Button) findViewById(R.id.guest);

        //setup listeners
        signUpButton.setOnClickListener(SingUpListener);
        loginButton.setOnClickListener(LoginListener);
        guestButton.setOnClickListener(GuestListener);

        SharedPreferences sharedPreferences = getSharedPreferences("tutorial", Context.MODE_PRIVATE);
        String done = sharedPreferences.getString("done", "0");

        //first time
        if (done.equals("0")) {
            Intent intent = new Intent(this, FirstScreen.class);
            startActivity(intent);
            finish();
        }






       //output.setText(JSONproccess("https://lookingfortable-skaftanis.c9.io/getAllCustomers.php"));
      //  JSONproccess("https://lookingfortable-skaftanis.c9.io/getAllCustomers.php");

        //FacebookSdk.sdkInitialize(getApplicationContext());



    }


    private View.OnClickListener SingUpListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //finish();
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);

        }

        };


    private View.OnClickListener GuestListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //finish();
            MainActivity.isLoged=false;
            loginName = null;
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);

        }

    };


    private View.OnClickListener LoginListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
           // finish();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

        }

    };

        public String JSONproccess ( String loginURL ) {

        requestQueue = Volley.newRequestQueue(this);
       // output = (TextView) findViewById(R.id.jsonData);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            JSONArray ja = response.getJSONArray("results");

                            for(int i=0; i < ja.length(); i++){

                                JSONObject jsonObject = ja.getJSONObject(i);

                                // int id = Integer.parseInt(jsonObject.optString("id").toString());
                                String title = jsonObject.getString("nickname");
                                String url = jsonObject.getString("email");

                                data += "Blog Number "+(i+1)+" \n Blog Name= "+title  +" \n URL= "+ url +" \n\n\n\n ";
                            }

                            output.setText(data);
                        }catch(JSONException e){e.printStackTrace();}
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

        return data;

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    //#TODO override to every activity
    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }











}
