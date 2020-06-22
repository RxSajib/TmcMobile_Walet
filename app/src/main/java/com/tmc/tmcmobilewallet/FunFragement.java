package com.tmc.tmcmobilewallet;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FunFragement extends Fragment {

    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private DatabaseReference Muserdatabase;
    private RecyclerView recyclerView;

    private AdView adView;

    public FunFragement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fun_fragement, container, false);



        ConnectivityManager cm =(ConnectivityManager)getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);

            dialog.setContentView(R.layout.costomdioloag_cheackinternet);
            dialog.show();


            RelativeLayout button = dialog.findViewById(R.id.WifiOnbuttonID);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(getContext().WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                    dialog.dismiss();
                }
            });

        }





        adView = view.findViewById(R.id.adds);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        adView.loadAd(adRequest1);

        recyclerView = view.findViewById(R.id.ChatRecylearviewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();


        Muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Muserdatabase.keepSynced(true);


        return view;
    }


    @Override
    public void onStart() {

        FirebaseRecyclerAdapter<ChatModal, FunHolder> funHolderFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatModal, FunHolder>(
                ChatModal.class,
                R.layout.user_design,
                FunHolder.class,
                Muserdatabase
        ) {
            @Override
            protected void populateViewHolder(final FunHolder funHolder, ChatModal chatModal, int i) {
                final String UID = getRef(i).getKey();

                Muserdatabase.child(UID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        if(dataSnapshot.hasChild("name")){
                                            String nameget = dataSnapshot.child("name").getValue().toString();
                                            funHolder.setUsernameset(nameget);
                                        }
                                        if(dataSnapshot.hasChild("uri")){
                                            String uriget = dataSnapshot.child("uri").getValue().toString();
                                            funHolder.setProfileimageset(uriget);
                                        }
                                        if(dataSnapshot.hasChild("usercoin")){
                                            String usercoinget = dataSnapshot.child("usercoin").getValue().toString();
                                            funHolder.setUsernameset(usercoinget);
                                        }
                                    }
                                    else {

                                    }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };

        recyclerView.setAdapter(funHolderFirebaseRecyclerAdapter);
        super.onStart();
    }

    public static class FunHolder extends RecyclerView.ViewHolder{

        private Context context;
        private View Mview;
        private TextView username, coin;
        private CircleImageView profileimage;

        public FunHolder(@NonNull View itemView) {
            super(itemView);


            Mview = itemView;
            context = Mview.getContext();
            username = Mview.findViewById(R.id.Sfullname);
            coin = Mview.findViewById(R.id.UserCoinID);
            profileimage = Mview.findViewById(R.id.SprofileimageID) ;
        }

        public void setUsernameset(String nam){
            username.setText(nam);
        }
        public void setProfileimageset(String img){
            Picasso.with(context).load(img).placeholder(R.drawable.user_design).into(profileimage);
        }
    }
}
