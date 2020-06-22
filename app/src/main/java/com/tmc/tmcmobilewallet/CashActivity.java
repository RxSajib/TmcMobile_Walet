package com.tmc.tmcmobilewallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class CashActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private Toolbar toolbar;
    private DatabaseReference Mcahdatabase;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private Long coincounter;
    private TextView cashcounttext;
    private Button paymentbutton;
    private RewardedVideoAd ads;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash);



        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(CashActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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





        paymentbutton = findViewById(R.id.PaymentButtonID);
        Mcahdatabase = FirebaseDatabase.getInstance().getReference().child("Earn");
        Mcahdatabase.keepSynced(true);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();
        cashcounttext = findViewById(R.id.cxountercointext);


        toolbar = findViewById(R.id.CashToolbarID);
        setSupportActionBar(toolbar);
     //   getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbutton);
        getSupportActionBar().setTitle("");

        Mcahdatabase.child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String v = dataSnapshot.child("value").getValue().toString();
                            cashcounttext.setText(v+".0");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3947412102662378/4973903851");



        ads = MobileAds.getRewardedVideoAdInstance(this);
        ads.setRewardedVideoAdListener(this);
        loadadsvideo();

        paymentbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                if(ads.isLoaded()){
                    ads.show();
                }




                AlertDialog.Builder Mbuilder = new AlertDialog.Builder(CashActivity.this);

                CharSequence[] paymentoptions = new CharSequence[]{
                        "Paytm",
                        "Mobile Recharge",
                        "GooglePay",
                        "Paypal",
                        "Rocket",
                        "easypaisa",
                        "bKas",
                        "Wiretransfer"
                };

                Mbuilder.setTitle("Select Your Payment Getway");
                Mbuilder.setItems(paymentoptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            Intent intent = new Intent(getApplicationContext(), PaytmActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        if(which == 1){
                            Intent intent = new Intent(getApplicationContext(), MobileRechargeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        if(which == 2){
                            Intent intent = new Intent(getApplicationContext(), GooglePayActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        if(which == 3){
                            Intent intent = new Intent(getApplicationContext(), PaypalActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        if(which == 4){
                            Intent intent = new Intent(getApplicationContext(), RoketActiity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        if(which == 5){
                            Intent intent = new Intent(getApplicationContext(), EasypisaActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        if (which == 6){
                            Intent intent = new Intent(getApplicationContext(), BkasActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        if(which == 7){
                            Intent intent = new Intent(getApplicationContext(), BankTransfarActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                });

                AlertDialog alertDialog = Mbuilder.create();
                alertDialog.show();
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

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

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

}
