package com.smyc.kaftanis.lookingfortable;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

public class AccountSettings extends AppCompatActivity {


    RequestQueue requestQueue;
    private Button buttonUpload;
    private ImageView imageView;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private String UPLOAD_URL ="--;
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private boolean wasHere=false;

    private EditText changeEmail;
    private EditText changePass;
    private Button setPassword;
    private Button setEmail;

    ProgressDialog progress;

    //keep the Name in the first open of Activity (way to keep the photo after nickname change)
    private String onCreateName;

    boolean gotError;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        onCreateName=MainActivity.loginName;

        setTitle("Αλλαγή εικόνας προφίλ");
        imageView = (ImageView) findViewById(R.id.imageView2);
        //buttonChoose = (Button) findViewById(R.id.button3);
        buttonUpload = (Button) findViewById(R.id.button3);

        progress = new ProgressDialog(this);
        progress.setTitle("Φόρτωση");
        progress.setMessage("Λήψη της φωτογραφίας σας...");
        progress.show();
        //download the right image and set it to the imageView

        String tempLink;
        if ( MainActivity.loginName.contains(" ")  )
            tempLink=MainActivity.loginName.replace(" ","%20");
        else
            tempLink=MainActivity.loginName;


        new DownloadImageTask((ImageView) findViewById(R.id.imageView2))
                .execute("--");



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wasHere=true;
                showFileChooser();

            }
        });
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wasHere) {
                    uploadImage();
                    wasHere=false;
                }
                else
                    Toast.makeText(AccountSettings.this, "Κάντε κλικ την τωρινή εικόνα σας για να επιλέξετε την επόμενη" , Toast.LENGTH_LONG).show();
            }
        });

    }



    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Επιλέξτε φωτογραφία"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Παρακαλώ περιμένετε...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(AccountSettings.this, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(AccountSettings.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                String name=MainActivity.loginName.trim();

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, name);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
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
            if (gotError == true)
                imageView.setImageResource(R.drawable.person);
            else
                bmImage.setImageBitmap(result);
            progress.dismiss();
        }
    }

    private void JSONproccessUpdate ( String loginURL ) {

        requestQueue = Volley.newRequestQueue(this);
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


    }






}
