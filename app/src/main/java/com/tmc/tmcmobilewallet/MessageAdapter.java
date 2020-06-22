package com.tmc.tmcmobilewallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<MessageHolder> messageHolderList = new ArrayList<>();
    private DatabaseReference MmessageDatabase;
    private FirebaseAuth Mauth;
    private String CurrentUserID;

    public MessageAdapter(List<MessageHolder> messageHolderList) {
        this.messageHolderList = messageHolderList;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_templated, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {

        Toast.makeText(holder.context, "call", Toast.LENGTH_LONG).show();
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();
        MmessageDatabase = FirebaseDatabase.getInstance().getReference().child("Message");
        MmessageDatabase.keepSynced(true);

        final MessageHolder messageHolder = messageHolderList.get(position);
        String MessageSenderID = messageHolder.getFrom();
        String MessageType = messageHolder.getType();

        MmessageDatabase.child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        if(MessageType.equals("text")){
            Toast.makeText(holder.context, "call", Toast.LENGTH_LONG).show();
            if(MessageSenderID.equals(CurrentUserID)){
                holder.sendmessage.setText(messageHolder.getMessage());
                Toast.makeText(holder.context, messageHolder.getMessage(), Toast.LENGTH_LONG).show();
            }
            else {
                holder.recivemessage.setText(messageHolder.getMessage());
                Toast.makeText(holder.context, messageHolder.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public int getItemCount() {
        return messageHolderList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        private Context context;
        private TextView sendmessage, recivemessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();
            sendmessage = itemView.findViewById(R.id.SendMessageID);
            recivemessage = itemView.findViewById(R.id.ReciveMessageID);

        }
    }
}
