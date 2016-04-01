package com.smyc.kaftanis.lookingfortable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class SignUpActivity extends AppCompatActivity {


    private EditText mailE;
    private EditText passE;
    private EditText nameE;
    private EditText passE2;

    private String singupMail;
    private String singupName;
    private String singupPass;

    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
     //  fab.setOnClickListener(new View.OnClickListener() {
     //       @Override
     //       public void onClick(View view) {
      //          Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
       //                 .setAction("Action", null).show();
      //      }
      //  });

        mailE = (EditText) findViewById(R.id.mainField);
        passE = (EditText) findViewById(R.id.passwordField);
        nameE = (EditText) findViewById(R.id.nameField);
        passE2 = (EditText) findViewById(R.id.passwordField2);

        mailE.setHint("Set your email");
        passE.setHint("Set your password");
        nameE.setHint("Set your username");
        passE2.setHint("Retype your password");

        Button singupButton = (Button) findViewById(R.id.button1);

        singupButton.setOnClickListener(SingUpListener);


    }


    private View.OnClickListener SingUpListener = new View.OnClickListener() {
        //When SingUp Clicked

        @Override
        public void onClick(View v) {
            //  mailE.setText(mailE.getText().toString().replace(" ","_"));
            if (mailE.getText().toString().equals("") || passE.getText().toString().equals("") || nameE.getText().toString().equals("")  )  {
                new AlertDialog.Builder(getSingupActivity())
                        .setTitle("Error")
                        .setMessage("You need to complete all the fields")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
            else if ( !isEmailValid(mailE.getText().toString())) {
                new AlertDialog.Builder(getSingupActivity())
                        .setTitle("Error")
                        .setMessage("You need a valid email account")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                mailE.setText("");
            }
            else if ( !passE.getText().toString().equals(passE2.getText().toString())) { //if retype is wrong
                new AlertDialog.Builder(getSingupActivity())
                        .setTitle("Error")
                        .setMessage("Password doesn't match")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                passE.setText("");
                passE2.setText("");
            }
            else { //NO inut error (same email error (email is unique in database) below)

                //set the variables
                singupMail=mailE.getText().toString();
                singupName=nameE.getText().toString();
                singupPass=passE.getText().toString();

                String tempLink;
                if (nameE.getText().toString().contains(" "))
                    tempLink=nameE.getText().toString().replace(" ","%20");
                else
                    tempLink=nameE.getText().toString();

                JSONproccess("PRIVATE?name="+singupMail+"&nickname="+tempLink);

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

    private Activity getSingupActivity() {
        return this;
    }

    private void JSONproccess ( String loginURL ) {

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
                                jsonObject = ja.getJSONObject(0); //there is on;y ore responce

                                String mail = jsonObject.getString("email");


                                if ( mail.equals("SPACE")) { //the name does not exists (send values to database)
                                    //insert the new user
                                    if (singupName.contains(" "))
                                        singupName=singupName.replace(" ", "%20");
                                    JSONproccessInsert("PRIVATE?name=" + singupName + "&email=" + singupMail + "&pass=" + singupPass + "&rating=0.8&photo=NULL");
                                    Toast.makeText(getApplicationContext(), "Account Created! Login to continue", Toast.LENGTH_LONG).show();
                                    //autoLogin
                                    MainActivity.loginName=singupName;
                                    MainActivity.isLoged=true;
                                    //Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    //startActivity(intent);
                                    finish();

                                }
                                else { //the name exists already

                                    new AlertDialog.Builder(getSingupActivity())
                                            .setTitle("Error")
                                            .setMessage("Email or nickname already exists on database. Please use another!")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //mailE.setText("");
                                                    //mailE.setHint("Set a new email");
                                                }
                                            })
                                            .show();
                                }


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



    }


}
