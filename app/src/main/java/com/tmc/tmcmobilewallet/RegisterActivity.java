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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextView registertext;
    private EditText email, rpassword, referralinput;
    private Button registerbutton;
    private FirebaseAuth Mauth;
    private ProgressDialog Mprogress;
    private TextView rfpasswordID;
    private TextView adminbutton;
    private String CurrentuserID;
    private DatabaseReference Muser_database;
    private String emailtext, reffer_id, rpasswordtext;

    private DatabaseReference Mreferral_database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);





        Muser_database = FirebaseDatabase.getInstance().getReference().child("Users");
        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(RegisterActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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







        rfpasswordID = findViewById(R.id.RForgetPasswordID);
        rfpasswordID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        Mprogress = new ProgressDialog(RegisterActivity.this);
        Mauth = FirebaseAuth.getInstance();
        registertext = findViewById(R.id.Registerext);
        registertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        email = findViewById(R.id.REmailEdittextID);
        rpassword = findViewById(R.id.RInputPasswordID);
        referralinput = findViewById(R.id.RefferlID);
        registerbutton = findViewById(R.id.ReisterButtonID);

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  emailtext = email.getText().toString();
                reffer_id = referralinput.getText().toString();
                 rpasswordtext = rpassword.getText().toString();

                if(emailtext.isEmpty()){
                    email.setError("Email require");
                }

                else if(rpasswordtext.isEmpty()){
                    rpassword.setError("Password require");
                }
                else if(!rpasswordtext.equals(rpasswordtext)){
                    rpassword.setError("Password not match");
                }
                else if(rpasswordtext.length() <= 8){
                    Toast.makeText(getApplicationContext(), "Password need 8 char", Toast.LENGTH_LONG).show();
                }
                else {
                    Mprogress.setTitle("Please wait ...");
                    Mprogress.setMessage("We are creating your account");
                    Mprogress.setCanceledOnTouchOutside(false);
                    Mprogress.show();
                    Mauth.createUserWithEmailAndPassword(emailtext, rpasswordtext)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){

                                       CurrentuserID = Mauth.getCurrentUser().getUid();

                                       Map<String, Object> usermap = new HashMap<String, Object>();
                                       usermap.put("email_address", emailtext);
                                       usermap.put("referral_id", reffer_id);

                                        Muser_database.child(CurrentuserID).updateChildren(usermap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){

                                                            Mprogress.dismiss();
                                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                        else {
                                                            Mprogress.dismiss();
                                                            Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
