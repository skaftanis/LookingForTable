package com.smyc.kaftanis.lookingfortable;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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

/**
 * Created by kaftanis on 3/18/16.
 */
public class ChangeNickName extends DialogFragment implements View.OnClickListener {


    EditText editText;
    Button button;

    RequestQueue requestQueue;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.set_radius, null);
        getDialog().setTitle("Νέο όνομα χρήστη");

        editText = (EditText) view.findViewById(R.id.editText);
        editText.setHint("Το τωρινό είναι "+MainActivity.loginName);

        button = (Button) view.findViewById(R.id.button4);

        button.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button4) {
            if (editText.getText().toString().equals("")){
                Toast.makeText( getActivity() , "Παρακαλούμε πληκτρολογίστε το νέο σας όνομα χρήστη πρώτα" , Toast.LENGTH_SHORT).show();
            }
            else {
                JSONproccess("--oldname=" + MainActivity.loginName + "&newname=" + editText.getText().toString());

            }

        }

    }


    private void JSONproccess ( String loginURL ) {

        requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray ja = response.getJSONArray("results");
                            JSONObject jsonObject = ja.getJSONObject(0);

                            String error = jsonObject.getString("error");

                            if (error.equals("1")) {
                                Toast.makeText(getActivity(), "Το όνομα αυτό υπάρχει ήδη", Toast.LENGTH_SHORT).show();
                                editText.setText("");
                            }
                            else {
                                MainActivity.loginName = editText.getText().toString();
                                Toast.makeText(getActivity(), "Το όνομα χρήστη άλλαξε με επιτυχία", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }


                        }
                     catch (JSONException e) {
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert);
        }
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Νέο όνομα χρήστη");
        return dialog;
    }
}
