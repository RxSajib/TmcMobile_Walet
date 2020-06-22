package com.tmc.tmcmobilewallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RewardsActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private Toolbar toolbar;
    private RelativeLayout FullscreenAds, VideoAds;
    private FirebaseAuth mauth;
    private String CurrentUserID;
    private String CurrentDate;
    private DatabaseReference Mdailyfullscreenads;
    private int fullscreenadscounter = 0;
    private InterstitialAd interstitialAd;
    private DatabaseReference Mcoindatabase;
    private int counter = 0;
    private String datacoin;
    private TextView Cointext;
    private DatabaseReference DailyVideoTask;
    private int videocointer=0;
    private RewardedVideoAd ads;
    private DatabaseReference MuserDatabase;

    private final String NOTICATIONCHANNELNAME = "coin name";
    private final int NOTIFACTIONID = 001;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);



        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(RewardsActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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





        adView = findViewById(R.id.ads);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        adView.loadAd(adRequest1);


        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        MuserDatabase.keepSynced(true);
        DailyVideoTask = FirebaseDatabase.getInstance().getReference().child("DailyVideoWatch");
        DailyVideoTask.keepSynced(true);
        Cointext = findViewById(R.id.bankcoins);
        Mcoindatabase = FirebaseDatabase.getInstance().getReference().child("Earn");
        Mcoindatabase.keepSynced(true);
        Mdailyfullscreenads = FirebaseDatabase.getInstance().getReference().child("DailyFullscreenAds");
        Mdailyfullscreenads.keepSynced(true);
        mauth = FirebaseAuth.getInstance();
        CurrentUserID = mauth.getCurrentUser().getUid();
        toolbar = findViewById(R.id.RewardsToolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbutton);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        FullscreenAds = findViewById(R.id.jbd);
        VideoAds = findViewById(R.id.VideoID);


        interstitialAd = new InterstitialAd(getApplicationContext());
        interstitialAd.setAdUnitId("ca-app-pub-3947412102662378/8007740230");
        interstitialAd.loadAd(new AdRequest.Builder().build());


        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3947412102662378/4973903851");
        ads = MobileAds.getRewardedVideoAdInstance(this);
        ads.setRewardedVideoAdListener(this);
        loadadsvideo();

        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
        });

        Calendar calendardate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
        CurrentDate = simpleDateFormatdate.format(calendardate.getTime());

       // Mdailyfullscreenads.child(CurrentUserID).child(CurrentDate).setValue("start");
        Mdailyfullscreenads.child(CurrentUserID).child(CurrentDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(dataSnapshot.exists()) {
                    fullscreenadscounter = (int) dataSnapshot.getChildrenCount();
                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DailyVideoTask.child(CurrentUserID).child(CurrentDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                videocointer = (int)dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        Mcoindatabase.child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            datacoin = dataSnapshot.child("value").getValue().toString();
                            Cointext.setText(datacoin+".0");
                        }
                        else {
                            counter = 0;
                            Mcoindatabase.child(CurrentUserID).child("value").setValue(counter);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        VideoAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videocointer <= 10){



                    if(ads.isLoaded()){
                        ads.show();




                    }

                }
                else {
                    Toast.makeText(getApplicationContext(),"The daily task is finish please try leter", Toast.LENGTH_LONG ).show();
                }
            }
        });

        FullscreenAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              if(fullscreenadscounter <= 16){

                  if(interstitialAd.isLoaded()){


                      interstitialAd.show();
                      interstitialAd.loadAd(new AdRequest.Builder().build());


                      Map<String, Object> map = new HashMap<String , Object>();
                      map.put("value", true);
                      Mdailyfullscreenads.child(CurrentUserID).child(CurrentDate).push().setValue(true);


                      counter = Integer.parseInt(datacoin);
                      counter = counter+1;
                      Mcoindatabase.child(CurrentUserID).child("value").setValue(counter);
                      Cointext.setText(datacoin+".0");

                      MuserDatabase.child(CurrentUserID).child("usercoin").setValue(counter+".0");
                  }
                  else {

                  }

              }
              else {
                  Toast.makeText(getApplicationContext(), "The daily task is finish please try leter", Toast.LENGTH_LONG).show();
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

    private void loadadsvideo(){
        if(!ads.isLoaded()){
            ads.loadAd("ca-app-pub-3947412102662378/4973903851", new AdRequest.Builder().build());
        }
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

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

        cheackcurrentandroid_version();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(RewardsActivity.this, NOTICATIONCHANNELNAME);
        builder.setSmallIcon(R.drawable.coincolour);
        builder.setContentTitle("Winner");
        builder.setContentText("You got 1 coin");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(RewardsActivity.this);
        notificationManagerCompat.notify(NOTIFACTIONID, builder.build());


        counter = Integer.parseInt(datacoin);
        counter = counter+1;
        Mcoindatabase.child(CurrentUserID).child("value").setValue(counter);
        Cointext.setText(datacoin+".0");

        MuserDatabase.child(CurrentUserID).child("usercoin").setValue(counter+".0");

        Map<String, Object> map = new HashMap<String , Object>();
        map.put("value", true);
        DailyVideoTask.child(CurrentUserID).child(CurrentDate).push().setValue(true);

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

    private void cheackcurrentandroid_version(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "notifactionname";
            String descptrion = "notfactiondes";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(NOTICATIONCHANNELNAME, name, importance);
            notificationChannel.setDescription(descptrion);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
