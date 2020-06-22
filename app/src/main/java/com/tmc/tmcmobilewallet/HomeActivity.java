package com.tmc.tmcmobilewallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private Toolbar hometoobar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private HomeScreeHaper homeScreeHaper;
    private FirebaseAuth Mauth;
    private TextView tmcaccount;
    private DatabaseReference MuserCoinDatabase;
    private FirebaseAuth mauth;
    private String CurrentuserID;
    private DatabaseReference Muserdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);




        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(HomeActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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






        tmcaccount = findViewById(R.id.myaccount);
        MuserCoinDatabase = FirebaseDatabase.getInstance().getReference().child("Earn");
        MuserCoinDatabase.keepSynced(true);
        mauth = FirebaseAuth.getInstance();
        CurrentuserID = mauth.getCurrentUser().getUid();

        Muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentuserID);

        MuserCoinDatabase.child(CurrentuserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String val = dataSnapshot.child("value").getValue().toString();
                            tmcaccount.setText(val+".0");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        Mauth = FirebaseAuth.getInstance();
        viewPager = findViewById(R.id.ViewPagrID);
        tabLayout = findViewById(R.id.TablayoutID);
        homeScreeHaper = new HomeScreeHaper(getSupportFragmentManager());
        viewPager.setAdapter(homeScreeHaper);
        tabLayout.setupWithViewPager(viewPager);

        hometoobar = findViewById(R.id.HomeToolbarID);
        setSupportActionBar(hometoobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_ico);

        navigationView = findViewById(R.id.navagationID);
        drawerLayout = findViewById(R.id.DrawerID);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.LogoutID){
                    Mauth.signOut();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

                if(menuItem.getItemId() == R.id.CashID){
                    menuItem.setCheckable(true);
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    Intent intent = new Intent(getApplicationContext(), CashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                if(menuItem.getItemId() == R.id.RewardID){
                    menuItem.setCheckable(true);
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    Intent intent = new Intent(getApplicationContext(), RewardsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }



                if(menuItem.getItemId() == R.id.ReferID){
                    menuItem.setCheckable(true);
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawer(Gravity.LEFT);


                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");

                    String shareMessage = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    String spancer_id = "Referral id:\n"+CurrentuserID;
                    String sharebody = shareMessage;
                    String sharesubject = "Daily Real Earning 5.00$ to 15.00$\n" +
                            "Paypal,paytm,Easypaisa,Bkash Etc..\n" +
                            "Just Download TMC Mobile Wallet" + "\n"+sharebody+"\n"+spancer_id;
                    intent.putExtra(Intent.EXTRA_TEXT, sharesubject);
                    //  intent.putExtra(Intent.EXTRA_SUBJECT, sharebody);
                    startActivity(Intent.createChooser(intent, "share with"));

                }

                return true;
            }
        });

        cheack_user();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            drawerLayout.openDrawer(Gravity.LEFT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        FirebaseUser Muser = Mauth.getCurrentUser();
        if(Muser == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        super.onStart();
    }

    private void cheack_user(){
        Muserdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild("name")){
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
