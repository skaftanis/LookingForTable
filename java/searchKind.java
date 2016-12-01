package com.smyc.kaftanis.lookingfortable;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;

/**
 * Created by kaftanis on 3/19/16.
 */
public class searchKind extends DialogFragment{


    ArrayList<String> listLabels;
    ArrayAdapter<String> adapter;

    ListView listView;

    RequestQueue requestQueue;

    public static String kindSelected;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        getDialog().setTitle("Επιλέξτε είδος");

        View view = inflater.inflate(R.layout.list_gps_dialog, null);

        listLabels = new ArrayList<String>();
        listLabels.add("Όλα");
        listView = (ListView) view.findViewById(R.id.listView2);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                kindSelected = listLabels.get(position);
                if (kindSelected.equals("Εστιατόριο"))
                    kindSelected="Restaurant";
                else if (kindSelected.equals("Μπυραρία"))
                    kindSelected="BeerBar";
                else if (kindSelected.equals("Κρασάδικο"))
                    kindSelected="Wine";
                else if (kindSelected.equals("Ουζερί"))
                    kindSelected="Ouzeri";
                else if (kindSelected.equals("Άλλο"))
                    kindSelected="Other";
                else if (kindSelected.equals("Μαγειρίο"))
                    kindSelected="Mageirio";
                //Toast.makeText(getActivity(), kindSelected + " selected", Toast.LENGTH_SHORT).show();
                String shownKind;
                if (kindSelected.equals("BeerBar"))
                    shownKind="Μπυραρία";
                else if ( kindSelected.equals("Wine"))
                    shownKind="Κρασάδικο";
                else if (kindSelected.equals("Ouzeri"))
                    shownKind="Oυζερί";
                else if (kindSelected.equals("Restaurant"))
                    shownKind="Εστιατόριο";
                else if (kindSelected.equals("Other"))
                    shownKind="Άλλο";
                else if (kindSelected.equals("Mageirio"))
                    shownKind="Μαγειρίο";
                else
                    shownKind=kindSelected;
                AdvancedSearch.kind.setText("ΕΠΕΛΕΞΕ ΕΙΔΟΣ (" + shownKind + ")");
                dismiss();


            }
        });



        JSONproccess("..getKind.php");



        return  view;


    }


    private void JSONproccess ( String loginURL ) {



        requestQueue = Volley.newRequestQueue(getActivity());
        // output = (TextView) findViewById(R.id.jsonData);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray ja = response.getJSONArray("results");

                            String label;

                            for (int i = 0; i < ja.length(); i++) {

                                JSONObject jsonObject = ja.getJSONObject(i);

                                String kind = jsonObject.getString("kind");

                                if ( kind.equals("Restaurant"))
                                    listLabels.add("Εστιατόριο");
                                else if (kind.equals("BeerBar"))
                                    listLabels.add("Μπυραρία");
                                else if (kind.equals("Wine"))
                                    listLabels.add("Κρασάδικο");
                                else if (kind.equals("Ouzeri"))
                                    listLabels.add("Ουζερί");
                                else if (kind.equals("Cafe"))
                                    listLabels.add("Cafe");
                                else if (kind.equals("Club"))
                                    listLabels.add("Club");
                                else if (kind.equals("Bar"))
                                    listLabels.add("Bar");
                                else if (kind.equals("Other"))
                                    listLabels.add("Άλλο");
                                else if (kind.equals("Creperie"))
                                    listLabels.add("Creperie");
                                else if (kind.equals("Burgers"))
                                    listLabels.add("Burgers");
                                else if (kind.equals("Bookstore"))
                                    listLabels.add("Bookstore");
                                else if (kind.equals("Souvlaki"))
                                    listLabels.add("Souvlaki");
                                else if (kind.equals("Mageirio"))
                                    listLabels.add("Μαγειρίο");
                                else if (kind.equals("CafeBar"))
                                    listLabels.add("CafeBar");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter= new ArrayAdapter<String>(getActivity(), R.layout.listitem,R.id.txtitem, listLabels);
                        listView.setAdapter(adapter);


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
        dialog.setTitle("Επιλέξτε είδος");
        return dialog;
    }
}
