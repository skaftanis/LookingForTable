package com.smyc.kaftanis.lookingfortable;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.InputStream;

class CustomAdapter extends ArrayAdapter<String> {

    boolean gotError;
    ImageView personImage;

    CustomAdapter(Context context, String[] names) {
        super(context, R.layout.custom_row, names);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_row, parent, false);

        String singleNameItem = getItem(position);
        TextView personName = (TextView) customView.findViewById(R.id.name);
        TextView minuteAgo = (TextView) customView.findViewById(R.id.small);
        TextView positions = (TextView) customView.findViewById(R.id.positions);
        personImage = (ImageView) customView.findViewById(R.id.image);

        personName.setText(singleNameItem);
        minuteAgo.setText(storeLog.minutes[position]);
        positions.setText(storeLog.positions[position]);

        if (storeLog.officialPos != -1)
            if (storeLog.officialPos == position) {
                customView.setBackgroundColor(Color.parseColor("#BBDEFB"));
            }

            SharedPreferences prefs = getContext().getSharedPreferences("details", getContext().MODE_PRIVATE);
            if (prefs.getString("value", "No name").equals("true")) {
                String tempLink;
                if (storeLog.names[position].contains(" "))
                    tempLink = storeLog.names[position].replace(" ", "%20");
                else
                    tempLink = storeLog.names[position];

                new DownloadImageTask(personImage)
                        .execute("-- + tempLink + ".png");
            }




        return customView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                gotError=false;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                gotError=true;
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {


            //set default image if there is no image by the user
            if (gotError == true)
                personImage.setImageResource(R.drawable.person);
            else
                bmImage.setImageBitmap(result);

            if (storeLog.officialPos != -1)
                personImage.setImageResource(R.drawable.person);
        }
    }

}
