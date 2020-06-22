package com.tmc.tmcmobilewallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EarnMoneyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RelativeLayout earnbutton;
    private TextView countertext;
    private DatabaseReference Mcoindata;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private InterstitialAd interstitialAd;
    private DatabaseReference Mcoincoutdata;
    private Long countcoin;
    private TextView countercointext;
    private DatabaseReference Mcoindatabase;
    private int counter = 0;
    private String datacoin;
    private int bcount = 0;
    private TextView counttext;
    private RelativeLayout RewardsActivity ;
    private DatabaseReference MuserDatabase;

    ///notifaction
    private  final String CHANELID="coin_notifaction";
    private final int NOTFACTIONID = 001;
    ///notifaction

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earn_money);




        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(EarnMoneyActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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
        countercointext = findViewById(R.id.countercointext);

        counttext = findViewById(R.id.CounterTextID);
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

        Mcoindata = FirebaseDatabase.getInstance().getReference().child("Coin");
        Mcoindata.keepSynced(true);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.EarnMoneyToolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbutton);
        getSupportActionBar().setTitle("");

        earnbutton = findViewById(R.id.EarnButtonID);
        countertext = findViewById(R.id.CounterTextID);


        Mcoindatabase.child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            datacoin = dataSnapshot.child("value").getValue().toString();
                            countercointext.setText(datacoin+".0");
                        }
                        else {
                            counter = 0;
                            Mcoindatabase.child(CurrentUserID).child("value").setValue(counter);
                            countercointext.setText(datacoin+".0");
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        earnbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                bcount++;
                counttext.setText("You Click "+bcount+" Times");

                if(bcount == 60){


                    bcount = 0;


                    /*counter = 0;

                    Mcoindata.child(CurrentUserID)
                            .push().child("added").setValue("1");*/


                    if(interstitialAd.isLoaded()){

                        createnotifactionchannel();

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(EarnMoneyActivity.this, CHANELID);
                        builder.setSmallIcon(R.drawable.coincolour);
                        builder.setContentTitle("Winner");
                        builder.setContentText("You get 1 coin");
                        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(EarnMoneyActivity.this);
                        notificationManagerCompat.notify(NOTFACTIONID, builder.build());


                        interstitialAd.show();
                        interstitialAd.loadAd(new AdRequest.Builder().build());


                        counter = Integer.parseInt(datacoin);
                        counter = counter+1;
                        Mcoindatabase.child(CurrentUserID).child("value").setValue(counter);
                        countercointext.setText(datacoin+".0");


                        MuserDatabase.child(CurrentUserID).child("usercoin").setValue(String.valueOf(counter)+".0");
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        Mcoincoutdata = FirebaseDatabase.getInstance().getReference().child("Earn");
        Mcoincoutdata.keepSynced(true);
        Mcoincoutdata.child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String k = dataSnapshot.child("value").getValue().toString();
                            countercointext.setText(k+".0");
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

    public void createnotifactionchannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "coin notifaction";
            String descptrion = "coin des";

            int inportance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANELID, name, inportance);
            notificationChannel.setDescription(descptrion);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
