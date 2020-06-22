package com.tmc.tmcmobilewallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class PaytmActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView paytmtext;


    private Long counter;
    private DatabaseReference MuserCoinDatabase;
    private FirebaseAuth Mauth;
    private String CurrentUserID;

    private EditText id, coin;
    private Button submitbutton;
    private int coincounter = 0;
    private DatabaseReference Mcoindatabase;
    private String coinget;
    private DatabaseReference MwinCoinDatabase;
    private ProgressDialog Mprogress;
    private int coinconvertint = 0;
    private DatabaseReference EarnDatabase;
    private DatabaseReference MuserDatabase;

    private String userlocation;
    private String username;
    private String usercoin;
    private String emailget, uri;
    private int ShortMessage, negativeValShort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm);



        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(PaytmActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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

        EarnDatabase = FirebaseDatabase.getInstance().getReference().child("Earn");
        EarnDatabase.keepSynced(true);
        Mprogress = new ProgressDialog(PaytmActivity.this);
        MwinCoinDatabase = FirebaseDatabase.getInstance().getReference().child("WineCoin");
        MwinCoinDatabase.keepSynced(true);
        Mcoindatabase = FirebaseDatabase.getInstance().getReference().child("Earn");
        Mcoindatabase.keepSynced(true);
        id = findViewById(R.id.PaytmID);
        coin = findViewById(R.id.paytmcoin);
        submitbutton = findViewById(R.id.Paytmsubmit);

        ///short data
        MwinCoinDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ShortMessage = (int) dataSnapshot.getChildrenCount();
                    negativeValShort = (~(ShortMessage - 1));
                }
                else {
                    negativeValShort = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ///short data

        toolbar = findViewById(R.id.PaytmToolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbutton);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        paytmtext = findViewById(R.id.paytmcoint);


        MuserCoinDatabase = FirebaseDatabase.getInstance().getReference().child("Earn");
        MuserCoinDatabase.keepSynced(true);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();


        ///get user info
        MuserDatabase.child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild("location")){
                                userlocation = dataSnapshot.child("location").getValue().toString();
                            }
                            if(dataSnapshot.hasChild("name")){
                                username = dataSnapshot.child("name").getValue().toString();
                            }
                            if(dataSnapshot.hasChild("usercoin")){
                                usercoin = dataSnapshot.child("usercoin").getValue().toString();
                            }
                            if(dataSnapshot.hasChild("email_address")){
                                emailget = dataSnapshot.child("email_address").getValue().toString();
                            }
                            if(dataSnapshot.hasChild("uri")){
                                uri = dataSnapshot.child("uri").getValue().toString();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        ///get user info


        MuserCoinDatabase.child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String k = dataSnapshot.child("value").getValue().toString();
                            paytmtext.setText(k+".0");
                        }
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
                            coinget = dataSnapshot.child("value").getValue().toString();

                            coincounter = Integer.parseInt(coinget);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String idget = id.getText().toString();
               final String coingettext = coin.getText().toString();





                if(coincounter <= 5000){
                    Toast.makeText(getApplicationContext(), "Please earn more coin", Toast.LENGTH_LONG).show();

                }
                else {


                    if (idget.isEmpty()) {
                        id.setError("ID require");
                    } else if (coingettext.isEmpty()) {

                        coin.setError("Coin require");
                    }
                    else   if(Integer.parseInt(coingettext) <= 4999){
                        Toast.makeText(getApplicationContext(), "Must selected 5000 coin", Toast.LENGTH_LONG).show();
                    }

                    else {



                        Mprogress.setTitle("Please wait ...");
                        Mprogress.setMessage("we are check your earn");
                        Mprogress.setCanceledOnTouchOutside(false);
                        Mprogress.dismiss();
                      Map<String, Object> winmap = new HashMap<String, Object>();
                      winmap.put("Paytm_ID", idget);
                      winmap.put("coinget", coingettext);
                        winmap.put("username", username);
                        winmap.put("location", userlocation);
                        winmap.put("coin", usercoin);
                        winmap.put("email", emailget);
                        winmap.put("uri", uri);
                        winmap.put("short", negativeValShort);

                      MwinCoinDatabase.push().updateChildren(winmap)
                              .addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                      if(task.isSuccessful()){
                                          id.setText("");
                                          coin.setText("");
                                          Mprogress.dismiss();
                                          Toast.makeText(getApplicationContext(), "Please earn more coin please wait one working day and thanks for using our application", Toast.LENGTH_LONG).show();

                                          int updatecoin = coincounter - Integer.parseInt(coingettext);
                                          Log.d("TAG", String.valueOf(updatecoin));
                                          EarnDatabase.child(CurrentUserID).child("value").setValue(String.valueOf(updatecoin));



                                          MuserDatabase.child(CurrentUserID).child("usercoin").setValue(String.valueOf(updatecoin)+".0");

                                      }
                                      else {
                                          Mprogress.dismiss();
                                          Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                      }
                                  }
                              })
                              .addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                      Mprogress.dismiss();
                                      Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                  }
                              });


                      ///
                    }

                }

                ////

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

    private void update_coin(){



    }
}

