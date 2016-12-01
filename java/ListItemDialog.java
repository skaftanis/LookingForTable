package com.smyc.kaftanis.lookingfortable;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.smyc.kaftanis.lookingfortable.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by kaftanis on 2/19/16.
 */
public class ListItemDialog extends DialogFragment implements View.OnClickListener{

    Button empty;
    Button aempty;
    Button medium;
    Button afull;
    Button full;

    RequestQueue requestQueue;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle("Πόσο γεμάτο είναι;");

        View view = inflater.inflate(R.layout.list_item_dialog, null);
        empty = (Button) view.findViewById(R.id.empty);
        aempty = (Button) view.findViewById(R.id.aempty);
        medium = (Button) view.findViewById(R.id.medium);
        afull = (Button) view.findViewById(R.id.afull);
        full = (Button) view.findViewById(R.id.full);

        empty.setOnClickListener(this);
        aempty.setOnClickListener(this);
        medium.setOnClickListener(this);
        afull.setOnClickListener(this);
        full.setOnClickListener(this);



        return view;
    }

    @Override
    public void onClick(View v) {

        double approximationValue=0; //approvimation of empty tables


        if (v.getId() == R.id.empty ) {
            approximationValue=Double.parseDouble(InfosActivity.size) - 0.05*Double.parseDouble(InfosActivity.size);
            dismiss();
        }
        else if (v.getId() == R.id.aempty) {
            approximationValue=Double.parseDouble(InfosActivity.size) - 0.25*Double.parseDouble(InfosActivity.size);
            dismiss();
        }
        else if (v.getId() == R.id.medium) {
            approximationValue=Double.parseDouble(InfosActivity.size) - 0.5*Double.parseDouble(InfosActivity.size);
            dismiss();
        }
        else if (v.getId() == R.id.afull) {
            approximationValue=Double.parseDouble(InfosActivity.size) - 0.85*Double.parseDouble(InfosActivity.size);
            dismiss();
        }
        else if (v.getId() == R.id.full) {
            approximationValue=0;
            dismiss();
        }


        if (Integer.parseInt(InsertSpots.minutesToOtherStore) > 2)
            if (Integer.parseInt(InsertSpots.postsInLastTenMinutes) >= 2 ) {
                Toast.makeText(getActivity(), "Δεν μπορείτε να κάντετε περισότερες από 2 καταχωρήσεις μέσα σε 10 λεπτά. Παρακαλώ δοκιμάστε αργότερα.", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
            else {
                //βάζει τις θέσεις και αυξάνει το entrance εάν είναι yes, αλλιώς το μειώνει
                if (InsertSpots.entrance.isChecked())
                        JSONproccessInsert("--?userID=" + MainActivity.loginId + "&storeID=" + InsertSpots.storeID + "&input=" + Double.toString(approximationValue));
                    else
                        JSONproccessInsert("--?userID=" + MainActivity.loginId + "&storeID=" + InsertSpots.storeID + "&input=" + Double.toString(approximationValue));

                //ανανέωση των στοιχείων για τις διαφημίσεις

                //διάβασμα του lastSet, του setsToday και του prob από τις προτιμήσεις
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("adsStaff", Context.MODE_PRIVATE);
                String last = sharedPreferences.getString("lastSet", "");
                int setsToday = sharedPreferences.getInt("setsToday", 0);
                int prob = sharedPreferences.getInt("prob", 0);

                //Σημερινή ημερομηνία στο κατάλληλο format
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String todayDate = df.format(c.getTime());

                //Εάν έχει γίνει και άλλο update σήμερα τότε +1
                if (todayDate.equals(last)){
                    SharedPreferences shared = getActivity().getSharedPreferences("adsStaff", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putInt("setsToday", setsToday+1);
                    setsToday++;
                    editor.apply();
                }
                //εάν δεν έχει γίνει άλλο τότε το setsToday γίνεται 1 (πριν μπορεί να έχει μία άκυρη τιμή (σκουπίδι) από τη προηγούμενη ημέρα
                //και η τελευταία ημερομηνια γίνεται η σημερινή
                else {
                    SharedPreferences shared = getActivity().getSharedPreferences("adsStaff", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putInt("setsToday", 1);
                    editor.putString("lastSet", todayDate);
                    setsToday=1;
                    editor.apply();
                }

                //ΑΛΛΑΓΗ ΠΙΘΑΝΟΤΗΤΑΣ με βάση το setsToday ( η πιθανότητα κρατιέται. μεγαλώνει ξανά κατά 10% εάν υπάρχει "κενή" ημέρα-------
                SharedPreferences shared = getActivity().getSharedPreferences("adsStaff", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();

                int newProb=30;

                if (setsToday == 1 )
                    newProb=prob-10;
                else if (setsToday >=2 && setsToday <=5)
                    newProb=prob-20;
                else if (setsToday > 5 )
                    newProb=prob-10;

                if ( newProb < 30)
                    newProb=30;

                editor.putInt("prob", newProb);
                editor.apply();

                //TYXAIA EMΦΑΝΙΣΗ αστείου μηνύματος επειδή μειώθηκε η πιθανότητα (εάν είναι πάνω από 1 οι καταχωρήσεις
                if (setsToday >= 2 ) {
                    Random r = new Random();
                    int randomAddChoice = r.nextInt(100 - 1) + 1;
                    if (randomAddChoice <= 20) {
                        Toast.makeText(getActivity(), "Συγχαρητήρια! Οι ενοχλητικές διαφημίσεις μειώθηκαν αισθητά χάρη στη συνεισφορά σας! Ευχαριστούμε", Toast.LENGTH_LONG).show();
                    } else if (randomAddChoice <= 40) {
                        Toast.makeText(getActivity(), "Μετά από αυτό θα σας σπαμάρουμε με λιγότερες διαφημίσεις. Το ενδιαφέρον σας μας συγκίνησε :( ", Toast.LENGTH_LONG).show();
                    } else if (randomAddChoice <= 60) {
                        Toast.makeText(getActivity(), "Οι διαφημίσεις του efood πλέον μόνο στον Μαλιάτση και τον Μάκιους. Κέρδισες μείωση διαφημίσεων λόγω της συνεισφοράς σου. Σε ευχαριστούμε!", Toast.LENGTH_LONG).show();
                    } else if (randomAddChoice <= 80) {
                        Toast.makeText(getActivity(), "", Toast.LENGTH_LONG).show();
                    }
                }
                else if (setsToday >= 13) {
                    Toast.makeText(getActivity(), "Σα να το παράκανες σήμερα! :) ", Toast.LENGTH_LONG).show();
                }
                else if (setsToday >=20)
                    Toast.makeText(getActivity(), "Μας τρολάρεις έτσι?", Toast.LENGTH_LONG).show();


            }
        else {
            Toast.makeText(getActivity(), "Δε μπορείτε να κάνετε καταχώρηση σε διαφορετικό κατάστημα σε λιγότερο από 2 λεπτά. Παρακαλώ δοκιμάστε αργότερα", Toast.LENGTH_LONG).show();
            //Intent intent = new Intent(getActivity(), SearchActivity.class);
            //startActivity(intent);
            getActivity().finish();
        }

    }

    private void JSONproccessInsert ( String loginURL ) {

        requestQueue = Volley.newRequestQueue(getActivity());
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

        Toast.makeText(getActivity(), "Ευχαριστούμε για τη καταχώρηση", Toast.LENGTH_LONG).show();
        //Intent intent = new Intent(getActivity(), SearchActivity.class);
        //startActivity(intent);
        getActivity().finish();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert);
        }
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Πόσο γεμάτο είναι;");
        return dialog;
    }

}
