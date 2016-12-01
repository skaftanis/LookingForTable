package com.smyc.kaftanis.lookingfortable;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kaftanis on 3/19/16.
 */
public class Memo extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle("Σημείωση");
        View view = inflater.inflate(R.layout.memo, null);



        return view;

    }
}
