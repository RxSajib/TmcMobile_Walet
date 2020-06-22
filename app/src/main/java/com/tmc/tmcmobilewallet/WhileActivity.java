package com.tmc.tmcmobilewallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WhileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RelativeLayout youtue, facebook;
    private String youtubelink, facebooklink, twofacebooklink, twoyoutubelink;
    private DatabaseReference Mlinkdata, Earndatabase, Mcoindatabase, MuserDatabase;

    private TextView coin;
    private FirebaseAuth Mauth;
    private String CurrentuserID;

    private RelativeLayout facebooktwo;
    private RelativeLayout youtubetwo;

    private AdView adView;
    private InterstitialAd interstitialAd;
    private final int NOTFACTIONID = 001;
    private final String CHANELID = "mynottcation";
    private int counter = 0;
    private String datacoin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_while);




        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(WhileActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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








        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        MuserDatabase.keepSynced(true);
        Mcoindatabase = FirebaseDatabase.getInstance().getReference().child("Earn");
        Mcoindatabase.keepSynced(true);
        interstitialAd = new InterstitialAd(getApplicationContext());
        interstitialAd.setAdUnitId("ca-app-pub-3947412102662378/8007740230");
        interstitialAd.loadAd(new AdRequest.Builder().build());


        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
        });


        adView = findViewById(R.id.ads);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        adView.loadAd(adRequest1);

        youtubetwo = findViewById(R.id.youtubetwo);
        coin = findViewById(R.id.subscribe);
        Earndatabase = FirebaseDatabase.getInstance().getReference().child("Earn");
        Earndatabase.keepSynced(true);
        Mauth = FirebaseAuth.getInstance();
        CurrentuserID = Mauth.getCurrentUser().getUid();

        facebooktwo = findViewById(R.id.faceboktwo);

        Mcoindatabase.child(CurrentuserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            datacoin = dataSnapshot.child("value").getValue().toString();
                          //  countercointext.setText(datacoin+".0");
                        }
                        else {
                            counter = 0;
                            Mcoindatabase.child(CurrentuserID).child("value").setValue(counter);
                        //    countercointext.setText(datacoin+".0");
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




        Earndatabase.child(CurrentuserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String val = dataSnapshot.child("value").getValue().toString();
                            coin.setText(val+".0");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        Mlinkdata  = FirebaseDatabase.getInstance().getReference().child("link");
        Mlinkdata.keepSynced(true);
        Mlinkdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("facebook")){
                        facebooklink = dataSnapshot.child("facebook").getValue().toString();
                    }
                    if(dataSnapshot.hasChild("youtube")){
                        youtubelink = dataSnapshot.child("youtube").getValue().toString();
                    }

                    if(dataSnapshot.hasChild("twofacebook")){
                        twofacebooklink = dataSnapshot.child("twofacebook").getValue().toString();
                    }
                    if(dataSnapshot.hasChild("twoyoutube")){
                        twoyoutubelink = dataSnapshot.child("twoyoutube").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        youtue = findViewById(R.id.YoutubeButtonID);
        facebook = findViewById(R.id.jbd);

        toolbar= findViewById(R.id.SpinToolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbutton);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        youtue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(interstitialAd.isLoaded()){

                    createnotifactionchannel();

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(WhileActivity.this, CHANELID);
                    builder.setSmallIcon(R.drawable.coincolour);
                    builder.setContentTitle("Winner");
                    builder.setContentText("You get 2 coin");
                    builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(WhileActivity.this);
                    notificationManagerCompat.notify(NOTFACTIONID, builder.build());


                    interstitialAd.show();
                    interstitialAd.loadAd(new AdRequest.Builder().build());


                    counter = Integer.parseInt(datacoin);
                    counter = counter+2;
                    Mcoindatabase.child(CurrentuserID).child("value").setValue(counter);
               //     countercointext.setText(datacoin+".0");


                    MuserDatabase.child(CurrentuserID).child("usercoin").setValue(String.valueOf(counter)+".0");


                }
                else {
                  //  Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                }



                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(youtubelink));
                startActivity(i);
            }
        });

        youtubetwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(interstitialAd.isLoaded()){

                    createnotifactionchannel();

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(WhileActivity.this, CHANELID);
                    builder.setSmallIcon(R.drawable.coincolour);
                    builder.setContentTitle("Winner");
                    builder.setContentText("You get 2 coin");
                    builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(WhileActivity.this);
                    notificationManagerCompat.notify(NOTFACTIONID, builder.build());


                    interstitialAd.show();
                    interstitialAd.loadAd(new AdRequest.Builder().build());


                    counter = Integer.parseInt(datacoin);
                    counter = counter+2;
                    Mcoindatabase.child(CurrentuserID).child("value").setValue(counter);
                    //     countercointext.setText(datacoin+".0");


                    MuserDatabase.child(CurrentuserID).child("usercoin").setValue(String.valueOf(counter)+".0");


                }
                else {
                    //  Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(WhileActivity.this, CHANELID);
                builder.setSmallIcon(R.drawable.coincolour);
                builder.setContentTitle("Winner");
                builder.setContentText("You get 2 coin");
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(WhileActivity.this);
                notificationManagerCompat.notify(NOTFACTIONID, builder.build());


                interstitialAd.show();
                interstitialAd.loadAd(new AdRequest.Builder().build());


                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(twoyoutubelink));
                startActivity(i);
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(interstitialAd.isLoaded()){

                    createnotifactionchannel();

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(WhileActivity.this, CHANELID);
                    builder.setSmallIcon(R.drawable.coincolour);
                    builder.setContentTitle("Winner");
                    builder.setContentText("You get 2 coin");
                    builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(WhileActivity.this);
                    notificationManagerCompat.notify(NOTFACTIONID, builder.build());


                    interstitialAd.show();
                    interstitialAd.loadAd(new AdRequest.Builder().build());


                    counter = Integer.parseInt(datacoin);
                    counter = counter+2;
                    Mcoindatabase.child(CurrentuserID).child("value").setValue(counter);
                    //     countercointext.setText(datacoin+".0");


                    MuserDatabase.child(CurrentuserID).child("usercoin").setValue(String.valueOf(counter)+".0");


                }
                else {
                    //  Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(WhileActivity.this, CHANELID);
                builder.setSmallIcon(R.drawable.coincolour);
                builder.setContentTitle("Winner");
                builder.setContentText("You get 2 coin");
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(WhileActivity.this);
                notificationManagerCompat.notify(NOTFACTIONID, builder.build());


                interstitialAd.show();
                interstitialAd.loadAd(new AdRequest.Builder().build());


                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(facebooklink));
                startActivity(i);
            }
        });

        facebooktwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(interstitialAd.isLoaded()){

                    createnotifactionchannel();

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(WhileActivity.this, CHANELID);
                    builder.setSmallIcon(R.drawable.coincolour);
                    builder.setContentTitle("Winner");
                    builder.setContentText("You get 2 coin");
                    builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(WhileActivity.this);
                    notificationManagerCompat.notify(NOTFACTIONID, builder.build());


                    interstitialAd.show();
                    interstitialAd.loadAd(new AdRequest.Builder().build());


                    counter = Integer.parseInt(datacoin);
                    counter = counter+2;
                    Mcoindatabase.child(CurrentuserID).child("value").setValue(counter);
                    //     countercointext.setText(datacoin+".0");


                    MuserDatabase.child(CurrentuserID).child("usercoin").setValue(String.valueOf(counter)+".0");


                }
                else {
                    //  Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(WhileActivity.this, CHANELID);
                builder.setSmallIcon(R.drawable.coincolour);
                builder.setContentTitle("Winner");
                builder.setContentText("You get 2 coin");
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(WhileActivity.this);
                notificationManagerCompat.notify(NOTFACTIONID, builder.build());


                interstitialAd.show();
                interstitialAd.loadAd(new AdRequest.Builder().build());


                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(twofacebooklink));
                startActivity(i);
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

    private void createnotifactionchannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "notifactionname";
            int priparity = NotificationManager.IMPORTANCE_DEFAULT;
            String des = "des";

            NotificationChannel notificationChannel = new NotificationChannel(CHANELID, name, priparity);
            notificationChannel.setDescription(des);

            NotificationManager notificationManager1 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager1.createNotificationChannel(notificationChannel);
        }

    }
}
