package com.smyc.kaftanis.lookingfortable;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by kaftanis on 3/19/16.
 */
public class AdvancedSearch extends DialogFragment implements View.OnClickListener {

    public static Button city;
    public static Button kind;
    Button done;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.two_buttons, null);
        getDialog().setTitle("Advanced Search");

        city = (Button) view.findViewById(R.id.city);
        kind = (Button) view.findViewById(R.id.kind);
        done = (Button) view.findViewById(R.id.button6);

        city.setOnClickListener(this);
        kind.setOnClickListener(this);
        done.setOnClickListener(this);

        searchCities.citySelected="All";
        searchKind.kindSelected="All";


        return view;
    }


    @Override
    public void onClick(View v) {

        if ( v.getId() == R.id.city ) {
            searchCities search = new searchCities();
            search.show(getFragmentManager(), "cities");

        }
        if ( v.getId() == R.id.kind) {
            searchKind kind = new searchKind();
            kind.show(getFragmentManager(),"kind");

        }

        if ( v.getId() == R.id.button6) {
            getSearchResults results = new getSearchResults();
            results.show(getFragmentManager(), "results");

        }

    }

}

