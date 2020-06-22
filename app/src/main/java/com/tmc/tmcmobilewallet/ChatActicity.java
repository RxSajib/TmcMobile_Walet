package com.tmc.tmcmobilewallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActicity extends AppCompatActivity {

    private String ReciverID;
    private Toolbar toolbar;
    private TextView username;
    private CircleImageView profileimage;
    private DatabaseReference MuserDatabase;
    private FirebaseAuth Mauth;
    private String SenderID;
    private EditText messgeinput;
    private RelativeLayout sendbutton;
    private DatabaseReference MessageDatabase;
    private String CurrentTime,CurrentDate;
    private RecyclerView chatview;
    private DatabaseReference Messageref;

    private List<MessageHolder> messageHolderList = new ArrayList<>();
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_acticity);




        MessageDatabase = FirebaseDatabase.getInstance().getReference();
        MessageDatabase.keepSynced(true);
        messgeinput = findViewById(R.id.MessageInputID);
        sendbutton = findViewById(R.id.SendButtonID);
        profileimage = findViewById(R.id.ProfileChatImageID);
        username = findViewById(R.id.ChatUsernameID);
        ReciverID = getIntent().getStringExtra("KEY");


        toolbar = findViewById(R.id.ChatToolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbutton);

        Mauth = FirebaseAuth.getInstance();
        SenderID = Mauth.getCurrentUser().getUid();
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        MuserDatabase.keepSynced(true);

        chatview = findViewById(R.id.ChatViewID);
        chatview.setHasFixedSize(true);
        chatview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        messageAdapter = new MessageAdapter(messageHolderList);
        chatview.setAdapter(messageAdapter);


        MuserDatabase.child(ReciverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild("uri")){
                                String uriget = dataSnapshot.child("uri").getValue().toString();
                                Picasso.with(getApplicationContext()).load(uriget).into(profileimage);
                            }
                            if(dataSnapshot.hasChild("name")){
                                String nameget = dataSnapshot.child("name").getValue().toString();
                                username.setText(nameget);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messgeinput.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(getApplicationContext(), "input your message", Toast.LENGTH_LONG).show();
                }
                else {

                    messgeinput.setText("");
                    String message_sender_ref = "Message/"+SenderID+"Message"+"/"+ReciverID;
                    String message_reciver_ref = "Message/"+ReciverID+"Message"+"/"+SenderID;

                    DatabaseReference usermessageke = MessageDatabase.child("Message").child(SenderID).child(ReciverID).push();

                    String messagepush_id = usermessageke.getKey();

                    Calendar calendartime = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormattime = new SimpleDateFormat("HH:mm");
                    CurrentTime = simpleDateFormattime.format(calendartime.getTime());

                    Calendar calendardate = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormatdate = new SimpleDateFormat("yyyy-MM-dd");
                    CurrentDate = simpleDateFormatdate.format(calendardate.getTime());


                    Map messagemap = new HashMap();
                    messagemap.put("message", message);
                    messagemap.put("time", CurrentTime);
                    messagemap.put("date", CurrentDate);
                    messagemap.put("from", SenderID);
                    messagemap.put("type", "text");

                    Map messagebody = new HashMap();
                    messagebody.put(message_sender_ref+"/"+messagepush_id, messagemap);
                    messagebody.put(message_reciver_ref+"/"+messagepush_id, messagemap);

                    MessageDatabase.updateChildren(messagebody)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){

                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                }
                            });

                }
            }
        });




        Messageref = FirebaseDatabase.getInstance().getReference();
        Messageref.keepSynced(true);
        readingMessage();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    protected void readingMessage(){
        Messageref.child("Message").child(SenderID).child(ReciverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                   /*     UserChatMdal message = dataSnapshot.getValue(UserChatMdal.class);
                        messageHolderList.add(message);
                        chatAdapter.notifyDataSetChanged();
                        chatview.smoothScrollToPosition(chatAdapter.getItemCount());*/
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
