package com.smyc.kaftanis.lookingfortable;

import android.app.Activity;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by kaftanis on 2/26/16.
 */
public class SearchListAdapter<String> extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemname;
    private final ArrayList<Integer> pictures;



    public SearchListAdapter(Activity context, ArrayList<String> itemname, ArrayList<Integer> pictures) {

        super(context, R.layout.listitem, itemname);

        this.context=context;
        this.itemname=itemname;
        this.pictures=pictures;

    }

    public View getView(int position,View view,ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listitem, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtitem);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.dots);

        String temp = itemname.get(position);
        txtTitle.setText((CharSequence) temp);

        imageView.setImageResource(pictures.get(position));

        return rowView;


    };
}

