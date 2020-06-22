package com.tmc.tmcmobilewallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextView logintext;
    private FirebaseAuth Mauth;
    private EditText email, password;
    private Button loginbutton;
    private ProgressDialog Mprogrss;
    private TextView fpassword;

    private TextView admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        admin = findViewById(R.id.sLointextID);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(LoginActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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




        fpassword = findViewById(R.id.LfpasswordID);
        fpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        Mprogrss = new ProgressDialog(LoginActivity.this);
        email = findViewById(R.id.EmailEdittextID);
        password = findViewById(R.id.InputPasswordID);
        loginbutton = findViewById(R.id.LoginButton);
        Mauth = FirebaseAuth.getInstance();
        logintext = findViewById(R.id.LogintextID);
        logintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailtext = email.getText().toString();
                String passwordtext= password.getText().toString();

                if(emailtext.isEmpty()){
                    email.setError("Email require");
                }
                else if(passwordtext.isEmpty()){
                    password.setError("Password require");
                }
                else {
                    Mprogrss.setTitle("Please wait ...");
                    Mprogrss.setMessage("We are checking your email and password");
                    Mprogrss.setCanceledOnTouchOutside(false);
                    Mprogrss.show();
                    Mauth.signInWithEmailAndPassword(emailtext, passwordtext)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Mprogrss.dismiss();
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Mprogrss.dismiss();
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Mprogrss.dismiss();
                                    Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        FirebaseUser Muser = Mauth.getCurrentUser();
        if(Muser != null){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        super.onStart();
    }
}
