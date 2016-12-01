package com.smyc.kaftanis.lookingfortable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FirstScreen extends AppCompatActivity {

    Button next;
    Calendar calander;
    String Date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        next = (Button) findViewById(R.id.button7);


        //ΣΗΜΕΡΙΝΗ ΗΜΕΡΑ
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
      //  Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();

        /*
       // String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        Date strDate = null;
           try{
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
         strDate = sdf.parse("10/11/2016");
        }catch (ParseException e1){
            e1.printStackTrace();
        }

        //ΣΥΓΚΡΙΣΗ ΤΟΥ ΣΗΜΕΡΑ ΜΕ ΤΗΝ STRDATE
        //εάν σήμερα είναι μετά το strDate
        if (new Date().after(strDate)) {
            Toast.makeText(getApplicationContext(), "xaxa", Toast.LENGTH_SHORT).show();
        }
        */


         SharedPreferences shared = getSharedPreferences("adsStaff", Context.MODE_PRIVATE);
         SharedPreferences.Editor editor = shared.edit();
         editor.putInt("prob", 70);
         editor.putString("lastSet", formattedDate);
         editor.putInt("setsToday", 0);
         editor.putString("lastDown", "0");
         editor.apply();


        //Get Settings
       // SharedPreferences sharedPreferences = getSharedPreferences("adsStaff", Context.MODE_PRIVATE);
       // String prob = sharedPreferences.getString("prob", "");
       // String last = sharedPreferences.getString("lastSet", "");
       // Toast.makeText(this, prob + " " + last, Toast.LENGTH_SHORT).show();



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FirstScreen.this, SecondScreen.class);
                startActivity(intent);
                finish();

            }
        });

    }
}
