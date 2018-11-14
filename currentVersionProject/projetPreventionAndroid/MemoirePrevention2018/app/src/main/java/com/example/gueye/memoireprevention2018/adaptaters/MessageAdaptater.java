package com.example.gueye.memoireprevention2018.adaptaters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.modele.Chat;
import com.example.gueye.memoireprevention2018.modele.ChatMessage;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class MessageAdaptater extends RecyclerView.Adapter<MessageAdaptater.MyViewHolder> {

    private List<Chat> chatsList ;
    private Context mContext;
    private String imageProfile;


    String currentUserId;


    private boolean isseen = false;


    private FirebaseAuth mFirebaseAuth;


    public MessageAdaptater(List <Chat> chatsList, Context mContext,String imageProfile) {
        this.chatsList = chatsList;
        this.mContext = mContext;
        this.imageProfile = imageProfile;

        mFirebaseAuth = FirebaseAuth.getInstance();

        SharedPreferences preferences = mContext.getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        currentUserId = preferences.getString("user_id", null);    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;


        if(viewType == Const.MSG_TYPE_RECEIVE){

            view = LayoutInflater.from(mContext).inflate(R.layout.message_receive,parent,false);

        }else{

            view = LayoutInflater.from(mContext).inflate(R.layout.message_send,parent,false);
        }

        MyViewHolder viewHolder = new MyViewHolder(view);

        viewHolder.setTypeOf(viewType);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final String tvContentMessage = chatsList.get(position).getMessage();
        long date = chatsList.get(position).getDate();

        String dateString = getIntervalleTime(date/1000,System.currentTimeMillis()/1000);

        isseen = chatsList.get(position).isIsseen();

        if(position == chatsList.size()-1){

            if(isseen && holder.ivIsChatSeen != null){

                holder.ivMessageSend.setVisibility(View.VISIBLE);
                holder.ivIsChatSeen.setVisibility(View.VISIBLE);
            }else{

                if(holder.ivMessageSend != null)

                    holder.ivMessageSend.setVisibility(View.VISIBLE);
            }

        }

        holder.tvMessage.setText(tvContentMessage);
        holder.tvDate.setText(dateString);


        RequestOptions placeholderOption = new RequestOptions();
        placeholderOption.placeholder(R.drawable.send_alerte);
        if (holder.imageProfil != null){
            Glide.with(mContext).applyDefaultRequestOptions(placeholderOption).load(imageProfile).into(holder.imageProfil);

        }


    }

    private String getIntervalleTime(long date, long currentTime) {

        String startWord  = "Il y'a ";


        long intervalleTime = currentTime - date;

        if(intervalleTime <60){

            return    startWord+intervalleTime+ " s";
        }

        if(intervalleTime == 60){

            return  "Il y'a 1 mn " ;
        }

        if(intervalleTime > 60 && intervalleTime<3600){

            int partieEntier = (int) (intervalleTime/60);

            return  startWord + partieEntier + " mn";

        }if(intervalleTime< 86400 && intervalleTime > 3600){

            int nmbreHeures  = (int) intervalleTime/3600;

            float nmbreMinutes = (intervalleTime%3600)*60;

            if(nmbreMinutes < 60){

                return  startWord + nmbreHeures + "h"+nmbreMinutes+" s" ;
            }else{

                nmbreMinutes = (nmbreMinutes%60)*60;

                return startWord + nmbreHeures + "h "+ nmbreMinutes + "mn";
            }
        }

        if(intervalleTime >86400){

            int nombreJours = (int) intervalleTime/86400;

            if(nombreJours <5){

                return startWord + nombreJours+ "jr";
            }else{

                Calendar calendar = Calendar.getInstance();

                calendar.setTimeInMillis(intervalleTime);

                return String.valueOf(calendar);

            }
        }





        return null;
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMessage;
        private CircleImageView imageProfil;
        private TextView tvDate;
        private ImageView ivIsChatSeen;
        private ImageView ivMessageSend;

        private int typeOf = 0;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvMessage = (TextView) itemView.findViewById(R.id.tv_content_message);
            imageProfil = (CircleImageView)itemView.findViewById(R.id.civ_profile_image_author);
            ivIsChatSeen = (ImageView) itemView.findViewById(R.id.iv_seen_message);
            ivMessageSend = (ImageView) itemView.findViewById(R.id.iv_message_sent);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date_message);

        }



        public int getTypeOf() {
            return typeOf;
        }

        public void setTypeOf(int typeOf) {
            this.typeOf = typeOf;
        }
    }

    @Override
    public int getItemViewType(int position) {

        String idSender =  chatsList.get(position).getId().split("-")[0];

        if(!currentUserId.equals(idSender)){

            return Const.MSG_TYPE_RECEIVE;

        }else{

            return Const.MSG_TYPE_SEND;
        }




    }
}
