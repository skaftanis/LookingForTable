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
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    String mail = null;
    String pass = null;

    TextView signup;
    TextView guest;


    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("LookingForTable");

        SharedPreferences sharedPreferences0 = getSharedPreferences("tutorial", Context.MODE_PRIVATE);
        String done = sharedPreferences0.getString("done", "0");

        //first time
        if (done.equals("0")) {
            Intent intent = new Intent(this, FirstScreen.class);
            startActivity(intent);
            finish();
        }


        mailE = (EditText) findViewById(R.id.mainField);

        passE = (EditText) findViewById(R.id.passwordField);

        signup = (TextView) findViewById(R.id.registerbtn);
        guest =  (TextView) findViewById(R.id.guestbtn);

        mailE.setHint("Email");
        passE.setHint("Κωδικός");

        passE.setTransformationMethod(new PasswordTransformationMethod());


        singupButton = (Button) findViewById(R.id.button1);
        singupButton.setClickable(true);

        singupButton.setOnClickListener(SingUpListener);


        //έλεγχος για αυτόματη εισαγωγή του ονόματος και του κωδικού
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String prmail = sharedPreferences.getString("email", "");
        String prpass = sharedPreferences.getString("pass", "");
        if (!prmail.equals("")) {
            mailE.setText(prmail);
            passE.setText(prpass);
        }

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isLoged=false;
                MainActivity.loginName = null;
                //finish();
                Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });


        //το if αυτό για να μην εκτελείται όταν έχει γίνει intent στο firstScreen και δεν έχουν προλάβει να σεταριστούν τα preferences
        if (!done.equals("0")) {

            //έλεγχος για το αν πρέπει να αυξηθεί η πιθανότητα των διαφημίσεων. Αυξάνεται εάν το lastDay είναι πριν από τη σημερινή ημέρα
            //Σημερινή ημερομηνία στο κατάλληλο format

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String todayDate = df.format(c.getTime());


            SharedPreferences sharedPreferences2 = getSharedPreferences("adsStaff", Context.MODE_PRIVATE);
            String last = sharedPreferences2.getString("lastSet", "");
            int setsToday = sharedPreferences2.getInt("setsToday", 0);
            int prob = sharedPreferences2.getInt("prob", 0);
            String lastDown = sharedPreferences2.getString("lastDown", "");

           // Toast.makeText(getApplicationContext(), "TODAY DATE: " + todayDate, Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "LAST: " + last, Toast.LENGTH_LONG).show();
          //  Toast.makeText(getApplicationContext(), "PROB: " + prob, Toast.LENGTH_LONG).show();

            //εάν η τελευταία μέρα που έχει γίνει DOWN η πιθανότητα (lastDown), δεν είναι η σημερινή τότε move on για τα σετ
            if (!lastDown.equals(todayDate)) {
                //αν η τελευταία ημερομηνία που έγινε set δεν είναι η σημερινή (δλδ είναι κάποια παλαιότερη), τότε αύξησε τη πιθανότητα DAYS_GAP*10 (με όριο το 70)
                if (!todayDate.equals(last)) {
                    int gap = 1;
                    int newProb;
                    //αν είναι στην αλλαγή του μήνα γλιτώνει το penalty
                    int lastDay = Integer.parseInt(last.substring(0, 2));
                    int nowDay = Integer.parseInt(todayDate.substring(0, 2));
                    int lastMonth = Integer.parseInt(last.substring(3, 5));
                    int nowMonth = Integer.parseInt(todayDate.substring(3, 5));

                    if (lastMonth == nowMonth) {
                        gap = (nowDay - lastDay) * 10;
                    }
                    //μην είναι πάνω από ένα μήνα διαφορά και δεν είμαστε στην αλλαγή του χρόνου
                    else if (nowMonth - lastMonth < 2 && nowMonth > lastMonth) {
                        gap = ((31 - lastDay) + nowDay) * 10;
                    }
                    //αν είμαστε στην αλλαγή του χρόνου
                    else if (lastMonth > nowMonth && lastMonth == 12) {
                        gap = (31 - lastDay + nowDay) * 10;
                    } else  //αυθαίρετα μεγάλη για να πάει 70
                        gap = 100;

                    if (prob + gap > 70)
                        newProb = 70;
                    else
                        newProb = prob + gap;

                    SharedPreferences shared = getSharedPreferences("adsStaff", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putInt("prob", newProb);
                    //βάλε τη σημερινή ημέρα ως την ημέρα που έγιναν οι τελευταίες μειώσεις
                    editor.putString("lastDown",todayDate);
                    editor.apply();

                }
            }

        }


    }




    private View.OnClickListener SingUpListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {


            //singupButton.setClickable(false);

            //if there is an empty field
            if (mailE.getText().toString().equals("") || passE.getText().toString().equals("") )  {
                new AlertDialog.Builder(getSinginActivity())
                        .setTitle("Σφάλμα")
                        .setMessage("Πρέπει να συμπληρώσετε όλα τα πεδία")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            } //if a mail isn't in the right format
            else if ( !isEmailValid(mailE.getText().toString())) {
                new AlertDialog.Builder(getSinginActivity())
                        .setTitle("Σφάλμα")
                        .setMessage("Πληκτρολογίστε έναν έγκυρο λογαριασμό email")
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
                progress.setTitle("Φόρτωση");
                progress.setMessage("Σύνδεση...");
                progress.show();


                JSONproccess("--mail=" + mailE.getText().toString() + "&pass=" + passE.getText().toString());

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
                                mail = jsonObject.getString("email");
                                pass = jsonObject.getString("password");
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
                                                        finish();
                                                        Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                                                        startActivity(intent);
                                                        Toast.makeText(getApplicationContext(), "Συνδεθήκατε με επιτυχία!", Toast.LENGTH_SHORT).show();
                                                        break;

                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        progress.dismiss();
                                                        finish();
                                                        Intent intent2 = new Intent(LoginActivity.this, SearchActivity.class);
                                                        startActivity(intent2);
                                                        Toast.makeText(getApplicationContext(), "Συνδεθήκατε με επιτυχία!", Toast.LENGTH_LONG).show();
                                                        break;
                                                }
                                            }
                                        };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                        builder.setMessage("Θέλετε να αποθηκεύσετε τα στοιχεία αυτά στη συσκευή (για αυτόματη σύνδεση) ").setPositiveButton("Ναι", dialogClickListener)
                                                .setNegativeButton("Όχι", dialogClickListener).show();
                                    } else {
                                        progress.dismiss();
                                        finish();
                                        Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                                        startActivity(intent);
                                        //singupButton.setClickable(true);
                                    }


                                    MainActivity.loginName=name;
                                    MainActivity.loginMail=mail;
                                    MainActivity.loginId=id;
                                    MainActivity.isLoged=true;


                                }
                                //doen't work for some reason
                                else {
                                    progress.dismiss();
                                    Toast.makeText(getApplicationContext(), "Λάθος email ή κωδικός!", Toast.LENGTH_LONG).show();
                                }



                        }catch(JSONException e){e.printStackTrace();}
                        //to fix the upper bug
                        if ( !mail.equals(signInMail)  ||  !pass.equals(signInPass) ) {
                            progress.dismiss();
                            Toast.makeText(getApplicationContext(), "Λάθος email ή κωδικός!", Toast.LENGTH_LONG).show();
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
