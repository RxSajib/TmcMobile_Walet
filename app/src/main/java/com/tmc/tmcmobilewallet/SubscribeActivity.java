package com.tmc.tmcmobilewallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
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

import java.awt.font.TextAttribute;
import java.util.Locale;
import java.util.MissingResourceException;

public class SubscribeActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private Toolbar toolbar;

    private static final long START_TIME_IN_MILLIS = 600000;
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;

    private String timeLeftFormatted;

    private TextView subcribecoin;
    private DatabaseReference MearnDatabase;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private String totalearn;
    private RewardedVideoAd ads;
    private int counter = 0;

    private InterstitialAd interstitialAd;

    private final String NOTIFACTIONCHANNELNAME = "notifactionname";
    private final int NOTIFACTIONID = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);




        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(SubscribeActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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










        interstitialAd = new InterstitialAd(getApplicationContext());
        interstitialAd.setAdUnitId("ca-app-pub-3947412102662378/8007740230");
        interstitialAd.loadAd(new AdRequest.Builder().build());


        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
        });


        subcribecoin = findViewById(R.id.subsribecoinID);
        MearnDatabase = FirebaseDatabase.getInstance().getReference().child("Earn");
        MearnDatabase.keepSynced(true);

        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        MearnDatabase.child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                     totalearn = dataSnapshot.child("value").getValue().toString();
                     subcribecoin.setText(totalearn+".0");
                }
                else {

                     counter = 0;
                    MearnDatabase.child(CurrentUserID).child("value").setValue(counter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        toolbar = findViewById(R.id.SubscribeToolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbutton);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");



        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3947412102662378/4973903851");

        ads = MobileAds.getRewardedVideoAdInstance(this);
        ads.setRewardedVideoAdListener(this);

        loadadsvideo();

        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);
        mButtonStartPause.setOnClickListener(new View.OnClickListener() {

            String text = mTextViewCountDown.getText().toString();

            @Override
            public void onClick(View v) {


                if (mTimerRunning) {
                    //pauseTimer();
                } else {
                    startTimer();



                    if(timeLeftFormatted.equals("10:00")){
                        if(ads.isLoaded()){
                            ads.show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Please try 10 minute later", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                    }
                }
            }
        });
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }
    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateButtons();
            }
        }.start();
        mTimerRunning = true;
        updateButtons();
    }
    /* private void pauseTimer() {
         mCountDownTimer.cancel();
         mTimerRunning = false;
         updateButtons();
     }*/
    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        updateButtons();
    }
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }
    private void updateButtons() {
        if (mTimerRunning) {
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setText("Subcribe");

            interstitialAdiscoming();


        } else {
            mButtonStartPause.setText("Start earn 5+");
            if (mTimeLeftInMillis < 1000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);
            }
            if (mTimeLeftInMillis < START_TIME_IN_MILLIS) {
                mButtonReset.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);
        updateCountDownText();
        updateButtons();
        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateButtons();
            } else {
                startTimer();
            }
        }
    }





    private void loadadsvideo(){
        if(!ads.isLoaded()){
            ads.loadAd("ca-app-pub-3947412102662378/4973903851", new AdRequest.Builder().build());
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
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

        cheackandroidverions();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(SubscribeActivity.this, NOTIFACTIONCHANNELNAME);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setSmallIcon(R.drawable.coincolour);
        builder.setContentText("You got 5 coin");
        builder.setContentTitle("Winner");

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(SubscribeActivity.this);
        notificationManagerCompat.notify(NOTIFACTIONID, builder.build());

        counter = Integer.parseInt(totalearn) ;
        counter = counter+5;
        MearnDatabase.child(CurrentUserID).child("value").setValue(counter);
        subcribecoin.setText(totalearn+".0");
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

    private void interstitialAdiscoming(){
        if(interstitialAd.isLoaded()){

            interstitialAd.show();
            interstitialAd.loadAd(new AdRequest.Builder().build());

        }
        else {

        }
    }

    private void cheackandroidverions(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "notifactionname";
            String descptrion = "des";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;


            NotificationChannel notificationChannel = new NotificationChannel(NOTIFACTIONCHANNELNAME,name, importance);
            notificationChannel.setDescription(descptrion);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }



    }
}
