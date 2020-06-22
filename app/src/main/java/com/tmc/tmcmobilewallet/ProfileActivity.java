package com.tmc.tmcmobilewallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText username, phnenumber, locaion;
    private Button submitbutton;
    private ProgressDialog Mprogress;
    private CircleImageView profielimage;
    private DatabaseReference MuserDatabase;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private StorageReference MprofileData;
    private String ImageDownloadUri;
    private DatabaseReference Mrefer_database;
    private DatabaseReference Mreferral_database;


    /// refer info
    private String refer_id, _refername;
    /// refer info

    /// short data
    private int short_positive;
    private int short_negative;
    /// short data


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        Mreferral_database = FirebaseDatabase.getInstance().getReference().child("Referral_data");


        Mreferral_database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    short_positive = (int) dataSnapshot.getChildrenCount();
                    short_negative  = (~(short_positive - 1));
                }
                else {
                    short_negative = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Mrefer_database = FirebaseDatabase.getInstance().getReference().child("Users");

        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(ProfileActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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



        MprofileData = FirebaseStorage.getInstance().getReference();
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();
        Mprogress = new ProgressDialog(ProfileActivity.this);
        toolbar = findViewById(R.id.ProfileToolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setup Your Profile First");

        username = findViewById(R.id.FullnameID);
        phnenumber = findViewById(R.id.MobileNumber);
        locaion = findViewById(R.id.LocationID);
        submitbutton = findViewById(R.id.SubmitButtonID);
        profielimage = findViewById(R.id.ProfileImageID);

        profielimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfileActivity.this);
            }
        });

        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nametext = username.getText().toString();
                String phonetext = phnenumber.getText().toString();
                String locationtext = locaion.getText().toString();

                if(nametext.isEmpty()){
                    username.setError("Name require");
                }
                else if(phonetext.isEmpty()){
                    phnenumber.setError("Number require");
                }
                else if(locationtext.isEmpty()){
                    locaion.setError("Location require");
                }

                else {
                    Mprogress.setTitle("Please wait ...");
                    Mprogress.setMessage("Saving your info");
                    Mprogress.setCanceledOnTouchOutside(false);
                    Mprogress.show();
                    Map<String, Object> profilemap = new HashMap<String, Object>();
                    profilemap.put("name", nametext);
                    profilemap.put("phone", phonetext);
                    profilemap.put("location", locationtext);
                    profilemap.put("uri", ImageDownloadUri);
                    profilemap.put("UID", Mauth.getCurrentUser().getUid());
                    profilemap.put("usercoin", "0.0");

                    MuserDatabase.child(CurrentUserID)
                            .updateChildren(profilemap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){


                                        Mrefer_database.child(CurrentUserID)
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if(dataSnapshot.exists()) {
                                                            if (dataSnapshot.hasChild("referral_id")) {
                                                                refer_id = dataSnapshot.child("referral_id").getValue().toString();
                                                            }
                                                            if (dataSnapshot.hasChild("email_address")) {
                                                                _refername = dataSnapshot.child("email_address").getValue().toString();
                                                            }

                                                                if(!refer_id.isEmpty()){

                                                                    Mprogress.dismiss();

                                                            Map<String, Object> refer_map = new HashMap<String, Object>();
                                                            refer_map.put("_id", refer_id);
                                                            refer_map.put("_refer_name", _refername);
                                                            refer_map.put("short_data", short_negative);


                                                            Mreferral_database.push()
                                                                    .updateChildren(refer_map)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Mprogress.dismiss();
                                                                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                startActivity(intent);
                                                                                finish();
                                                                            }
                                                                        }
                                                                    });

                                                        }

                                                        }
                                                        else {
                                                            Mprogress.dismiss();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        Mprogress.dismiss();
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
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Mprogress.setTitle("Please wait ...");
                Mprogress.setMessage("Saving your photo");
                Mprogress.setCanceledOnTouchOutside(false);
                Mprogress.show();

                Uri imageuri = result.getUri();
                profielimage.setImageURI(imageuri);

                StorageReference filepath = MprofileData.child("image").child(imageuri.getLastPathSegment());
                filepath.putFile(imageuri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    ImageDownloadUri = task.getResult().getDownloadUrl().toString();
                                    Mprogress.dismiss();
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

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Mprogress.dismiss();

                Exception error = result.getError();
            }
        }
    }
}