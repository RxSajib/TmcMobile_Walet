package com.tmc.tmcmobilewallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {

    private EditText facebook, youtube;
    private Button submit;
    private DatabaseReference linkdata;
    private Toolbar toolbar;

    private EditText twofacebook, twoyoutube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);



        twofacebook =findViewById(R.id.twoFacebooklink);
        twoyoutube = findViewById(R.id.twoyoutubelink);


        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(AdminActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

            dialog.setContentView(R.layout.costomdioloag_cheackinternet);
            dialog.show();


            RelativeLayout button = dialog.findViewById(R.id.WifiOnbuttonID);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                    dialog.dismiss();
                }
            });

        }





        toolbar = findViewById(R.id.Admin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbutton);
        getSupportActionBar().setTitle("Put your link");

        linkdata= FirebaseDatabase.getInstance().getReference().child("link");
        facebook = findViewById(R.id.Facebooklink);
        youtube = findViewById(R.id.youtubelink);
        submit= findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String facebooklink = facebook.getText().toString();
                String youtubelink = youtube.getText().toString();


                String twofacebooklink = twofacebook.getText().toString();
                String twoyoutubelink = twoyoutube.getText().toString();

                if(facebooklink.isEmpty()){
                    facebook.setError("error");
                }
                if(youtubelink.isEmpty()){
                    youtube.setError("error");
                }
                if(twofacebooklink.isEmpty()){
                    twofacebook.setError("error");
                }
                if(twoyoutubelink.isEmpty()){
                    twoyoutube.setError("error");
                }

                else {

                    facebook.setText("");
                    youtube.setText("");
                    linkdata.child("facebook").setValue("https://"+facebooklink+"/");
                    linkdata.child("youtube").setValue("https://"+youtubelink+"/");
                    linkdata.child("twofacebook").setValue("https://"+twofacebooklink+"/");
                    linkdata.child("twoyoutube").setValue("https://"+twoyoutubelink+"/");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
