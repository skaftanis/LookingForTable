package com.smyc.kaftanis.lookingfortable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class Settings extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> adapter;
    String[] items;
    ArrayList<String> listItems;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("Ρυθμίσεις");

        listView = (ListView) findViewById(R.id.listView3);
        listView.setEnabled(true);

        items = new String[]{"Καθορίστε την ακτίνα αναζήτησης (για το ροζ κουμπί)", "Εμφάνιση εικόνων", "Αλλαγή ονόματος", "Αλλαγή εικόνας"};

        listItems=new ArrayList<>(Arrays.asList(items));

        adapter=new ArrayAdapter<String>(this,R.layout.listitem, R.id.txtitem, listItems);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (listItems.get(position).equals("Καθορίστε την ακτίνα αναζήτησης (για το ροζ κουμπί)")) {
                    SetRadius setRadius = new SetRadius();
                    setRadius.show(getFragmentManager(), "radius");
                }
                else if (listItems.get(position).equals("Εμφάνιση εικόνων")) {
                    VisiblePhotos visiblePhotos = new VisiblePhotos();
                    visiblePhotos.show(getFragmentManager(),"details");
                }
                else if (listItems.get(position).equals("Αλλαγή ονόματος")) {
                    if (MainActivity.isLoged) {
                        ChangeNickName changeNickName = new ChangeNickName();
                        changeNickName.show(getFragmentManager(),"nickname");
                    }
                    else
                        Toast.makeText( Settings.this , "Δεν μπορείτε να το κάνετε αυτό ως επισκέπτης" , Toast.LENGTH_SHORT).show();
                }
                else if (listItems.get(position).equals("Αλλαγή εικόνας")) {
                    if (MainActivity.isLoged) {
                        Intent intent = new Intent( Settings.this, AccountSettings.class);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText( Settings.this , "Δεν μπορείτε να το κάνετε αυτό ως επισκέπτης" , Toast.LENGTH_SHORT).show();

                }


            }
        }
        );

    }
}
