package com.smyc.kaftanis.lookingfortable;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by kaftanis on 11/13/16.
 */

public class BugReport  extends DialogFragment implements View.OnClickListener {

    EditText editText;
    Button button;
    RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.set_radius, null);
        getDialog().setTitle("Αναφορά σφάλματος");

        editText = (EditText) view.findViewById(R.id.editText);

        editText.setHint("Περιγράψτε μας το σφάλμα που εντοπίσατε");

        button = (Button) view.findViewById(R.id.button4);

        button.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button4) {

            if (editText.getText().toString().equals("")) {
                Toast.makeText( getActivity() , "Πληκτρολογίστε κάτι" , Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    String  s = URLEncoder.encode(editText.getText().toString(), "utf-8");
                    // String p = URLEncoder.encode(insertPhone, "utf-8");
                    if (s.contains(" "))
                        s=s.replace(" ", "%20");
                    JSONproccess("--"BUG:"+s);
                } catch (UnsupportedEncodingException e) {}

            }

        }
    }

    private void JSONproccess ( String loginURL ) {

        requestQueue = Volley.newRequestQueue(getActivity());
        // output = (TextView) findViewById(R.id.jsonData);
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
        dismiss();
        Toast.makeText( getActivity() , "Ευχαριστούμε για τη συνεισφορά σας! " , Toast.LENGTH_SHORT).show();

    }



}

