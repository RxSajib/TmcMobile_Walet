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
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VideoActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private Toolbar toolbar;
    private RelativeLayout videoone, videotwo, videothree, videofour;
    private RewardedVideoAd ads;
    private DatabaseReference Mcoindatabase;
    private FirebaseAuth Mauth;
    private String CurrentUsrID;
    private int counter = 0;
    private String datacoin;
    private TextView cointext;
    private DatabaseReference MuserDatabase;

    private final String NOTIFACTIONCHANNELID = "coinnotifaction";
    private final int NOTIFACTIONID = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);





        ConnectivityManager cm =(ConnectivityManager)VideoActivity.this.getSystemService(VideoActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(VideoActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

            dialog.setContentView(R.layout.costomdioloag_cheackinternet);
            dialog.show();


            RelativeLayout button = dialog.findViewById(R.id.WifiOnbuttonID);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WifiManager wifiManager = (WifiManager) VideoActivity.this.getApplicationContext().getSystemService(VideoActivity.this.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                    dialog.dismiss();
                }
            });

        }








        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        MuserDatabase.keepSynced(true);
        cointext = findViewById(R.id.cxountercointext);
        Mcoindatabase = FirebaseDatabase.getInstance().getReference().child("Earn");
        Mcoindatabase.keepSynced(true);
        Mauth = FirebaseAuth.getInstance();
        CurrentUsrID = Mauth.getCurrentUser().getUid();
        toolbar = findViewById(R.id.VideoToolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbutton);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        videoone = findViewById(R.id.VideoOne);
        videotwo = findViewById(R.id.VideoTwo);
        videothree = findViewById(R.id.VideoThree);
        videofour = findViewById(R.id.VideoFoue);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3947412102662378/4973903851");

        ads = MobileAds.getRewardedVideoAdInstance(this);
        ads.setRewardedVideoAdListener(this);

        loadadsvideo();

        videoone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ads.isLoaded()){
                    ads.show();
                }
            }
        });

        videotwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ads.isLoaded()){
                    ads.show();
                }
            }
        });  videothree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ads.isLoaded()){
                    ads.show();
                }
            }
        });
         videofour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ads.isLoaded()){
                    ads.show();
                }
            }
        });




        Mcoindatabase.child(CurrentUsrID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            datacoin = dataSnapshot.child("value").getValue().toString();
                            cointext.setText(datacoin+".0");
                        }
                        else {
                            counter = 0;
                            Mcoindatabase.child(CurrentUsrID).child("value").setValue(counter);
                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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

    private void loadadsvideo(){
        if(!ads.isLoaded()){
            ads.loadAd("ca-app-pub-3947412102662378/4973903851", new AdRequest.Builder().build());
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
            loadadsvideo();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        ///value changes


        notifactionover_oriodversion();

        NotificationCompat.Builder notifactionbuilder = new NotificationCompat.Builder(VideoActivity.this, NOTIFACTIONCHANNELID);
        notifactionbuilder.setSmallIcon(R.drawable.coincolour);
        notifactionbuilder.setContentTitle("Winner");
        notifactionbuilder.setContentText("You get 3 coin");
        notifactionbuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(VideoActivity.this);
        notificationManagerCompat.notify(NOTIFACTIONID, notifactionbuilder.build());


        counter = Integer.parseInt(datacoin) ;
        counter = counter+3;
        Mcoindatabase.child(CurrentUsrID).child("value").setValue(counter);
        cointext.setText(datacoin+".0");


        MuserDatabase.child(CurrentUsrID).child("usercoin").setValue(String.valueOf(counter)+".0");
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    @Override
    protected void onPause() {
        ads.pause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        ads.resume(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        ads.destroy(this);
        super.onDestroy();
    }


    private void notifactionover_oriodversion(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "coindnME";
            String descptrion = "coin des";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFACTIONCHANNELID, name, importance);
            notificationChannel.setDescription(descptrion);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }
}
