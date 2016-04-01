package com.smyc.kaftanis.lookingfortable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {


    private EditText mailE;
    private EditText passE;

    private String signInMail;
    private String signInName;
    private String signInPass;

    Button singupButton;

    RequestQueue requestQueue;


    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("LookingForTable");

        mailE = (EditText) findViewById(R.id.mainField);

        passE = (EditText) findViewById(R.id.passwordField);

        mailE.setHint("Enter your email");
        passE.setHint("Enter Your Password");

        singupButton = (Button) findViewById(R.id.button1);
        singupButton.setClickable(true);

        singupButton.setOnClickListener(SingUpListener);

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String prmail = sharedPreferences.getString("email", "");
        String prpass = sharedPreferences.getString("pass", "");
        if (!prmail.equals("")) {
            mailE.setText(prmail);
            passE.setText(prpass);
        }


    }




    private View.OnClickListener SingUpListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {


            //singupButton.setClickable(false);

            //if there is an empty field
            if (mailE.getText().toString().equals("") || passE.getText().toString().equals("") )  {
                new AlertDialog.Builder(getSinginActivity())
                        .setTitle("Error")
                        .setMessage("You need to complete all the fields")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            } //if a mail isn't in the right format
            else if ( !isEmailValid(mailE.getText().toString())) {
                new AlertDialog.Builder(getSinginActivity())
                        .setTitle("Error")
                        .setMessage("You need a valid email account")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                mailE.setText("");
            }
            else { //NO input error

                signInMail=mailE.getText().toString();
                signInPass=passE.getText().toString();

                progress = new ProgressDialog(LoginActivity.this);
                progress.setTitle("Loading");
                progress.setMessage("Loggin in...");
                progress.show();


                JSONproccess("PRIVATE?mail=" + mailE.getText().toString() + "&pass=" + passE.getText().toString());

            }

        }

    };

    private static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private Activity getSinginActivity() {
        return this;
    }

    public void JSONproccess ( String loginURL ) {

        requestQueue = Volley.newRequestQueue(this);
        // output = (TextView) findViewById(R.id.jsonData);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                               JSONArray ja= null;
                               ja = response.getJSONArray("results");

                                JSONObject jsonObject = null;
                                 jsonObject = ja.getJSONObject(0);

                                String name = jsonObject.getString("nickname");
                                String mail = jsonObject.getString("email");
                                String pass = jsonObject.getString("password");
                                String id= jsonObject.getString("id");

                                //if we are ok (login succesfully)
                                if ( mail.equals(signInMail)  && pass.equals(signInPass) ) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

                                    //check the auto login
                                    String prmail = sharedPreferences.getString("email", "");
                                    String prpass = sharedPreferences.getString("pass", "");

                                    if (  prmail.isEmpty() || !prmail.equals(signInMail)) {

                                        //question for auto login
                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        SharedPreferences shared = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = shared.edit();
                                                        editor.putString("email", signInMail);
                                                        editor.putString("pass", signInPass);
                                                        editor.apply();
                                                        //intent to the search activity
                                                        progress.dismiss();
                                                        Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                                                        startActivity(intent);
                                                        Toast.makeText(getApplicationContext(), "Login Completed!", Toast.LENGTH_LONG).show();
                                                        break;

                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        progress.dismiss();
                                                        Intent intent2 = new Intent(LoginActivity.this, SearchActivity.class);
                                                        startActivity(intent2);
                                                        Toast.makeText(getApplicationContext(), "Login Completed!", Toast.LENGTH_LONG).show();
                                                        break;
                                                }
                                            }
                                        };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                        builder.setMessage("Do you want to save these info?").setPositiveButton("Yes", dialogClickListener)
                                                .setNegativeButton("No", dialogClickListener).show();
                                    } else {
                                        progress.dismiss();
                                        Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                                        startActivity(intent);
                                        //singupButton.setClickable(true);
                                        Toast.makeText(getApplicationContext(), "Login Completed!", Toast.LENGTH_LONG).show();
                                    }


                                    MainActivity.loginName=name;
                                    MainActivity.loginMail=mail;
                                    MainActivity.loginId=id;
                                    MainActivity.isLoged=true;


                                }
                                else
                                    Toast.makeText(getApplicationContext(), "Wrong email or password!", Toast.LENGTH_LONG).show();



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


    }

}
