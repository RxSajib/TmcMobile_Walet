package com.tmc.tmcmobilewallet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button login;
    private DatabaseReference Madmindata;

    private String emialdata, passworddata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);



        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(AdminLoginActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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





        Madmindata = FirebaseDatabase.getInstance().getReference().child("Admin");

        email  = findViewById(R.id.aEmailEdittextID);
        password = findViewById(R.id.aInputPasswordID);
        login = findViewById(R.id.aLoginButton);

        Madmindata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("email")){
                        emialdata = dataSnapshot.child("email").getValue().toString();
                    }
                    if(dataSnapshot.hasChild("password")){
                        passworddata = dataSnapshot.child("password").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailtext = email.getText().toString();
                String passwordtext = password.getText().toString();
                if(emailtext.isEmpty()){
                    email.setError("Email require");
                }
                else if(passwordtext.isEmpty()){
                    password.setError("Password require");
                }
                else {

                    if(emailtext.equals(emialdata) && passwordtext.equals(passworddata)){
                        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                }
            }
        });
    }
}
