package com.example.gueye.memoireprevention2018.adaptaters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.activities.ChatMessageActivity;
import com.example.gueye.memoireprevention2018.activities.DiscussionsActivity;
import com.example.gueye.memoireprevention2018.modele.Chat;
import com.example.gueye.memoireprevention2018.modele.ChatMessage;
import com.example.gueye.memoireprevention2018.modele.Users;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.facebook.login.widget.ProfilePictureView.TAG;

public class ChatMessageAdaptater  extends  RecyclerView.Adapter<ChatMessageAdaptater.ChatViewHolder> {

    private List<Users> usersList ;
    private List<List<Chat>> listDiscussions = new ArrayList<>();
    private Context context ;

    List<Chat> chatList;

    private List <String> userIdList;

    public static  final String TAG = "ChatMessageAdaptater";
    private FirebaseDatabase mDatabase;
    FirebaseFirestore mFirestore;
    private String currentUserId ;

    private String statusUser;


    public ChatMessageAdaptater(List<Chat> chatList , List<Users> usersList, Context context,String currentUserId) {

        this.currentUserId = currentUserId;
        this .chatList = chatList;
        this.usersList= usersList;
        this .context = context;
        mDatabase = FirebaseDatabase.getInstance();
        mFirestore  = FirebaseFirestore.getInstance();

    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_discussions_item_layout,parent,false);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, final int position) {

        if(usersList.size()!=0){

            String usersName = usersList.get(position).getName();
            String imageProfile = usersList.get(position).getImage();

            statusUser = usersList.get(position).status;

            holder.ivStatus.setImageResource(R.drawable.ic_off_line);

            if(statusUser.equals("online")){

                holder.ivStatus.setImageResource(R.drawable.ic_one_line);
            }

            holder.setUpDiscussion(usersName,imageProfile);

            holder.getLastMessage(usersList.get(position).getUser_id(),chatList);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String userId = usersList.get(position).usersId;
                    //String tokenId = userIdList.get( position )
                    Log.d(TAG, "onClick: user Id"+ userId);
                    String userNameDest = usersList.get(position).getName();
                    String tokenId = usersList.get( position ).getToken_id();
                    Log.d(TAG, "onClick: token id"+ tokenId);

                    Log.d(TAG, "onClick: username "+ userNameDest);
                    String profilImageDest = usersList.get(position).getImage();

                    Log.d(TAG, "onClick: profile image dest "+ profilImageDest);

                    Intent discussionIntent =new Intent(context, DiscussionsActivity.class);
                    discussionIntent.putExtra(Const.TOKEN_ID, tokenId);
                    discussionIntent.putExtra(Const.USER_ID_DEST,userId);
                    discussionIntent.putExtra(Const.USER_PROFILE_DEST,profilImageDest);
                    discussionIntent.putExtra(Const.USERNAME_DEST,userNameDest);

                    context.startActivity(discussionIntent.setFlags(FLAG_ACTIVITY_NEW_TASK));
                }
            });


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
        return usersList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {


        private TextView tvUsername;
        private CircleImageView civUserImage;
        private TextView tvMessage;
        private TextView tvDate;
        private ImageView ivStatus;



        public ChatViewHolder(View itemView) {
            super(itemView);


            tvMessage = itemView.findViewById(R.id.tv_message_sender);
            tvDate = itemView.findViewById(R.id.tv_message_date_sender);
            civUserImage = itemView.findViewById(R.id.iv_sender_user);
            ivStatus = itemView.findViewById(R.id.iv_status_user);


        }

        public void getLastMessage(String userId , List<Chat> chatLists ){

            String theLastMessage = "Pas de message encore ";
            String theLastDate = "null";

                String idChatSender = currentUserId+"-"+userId;
                String idChatReceiver = userId+"-"+currentUserId;

            for (Chat chat : chatLists ){

                if(chat.getId().equals(idChatReceiver) || chat.getId().equals(idChatSender)){

                    theLastMessage = chat.getMessage();
                    theLastDate = getIntervalleTime(chat.getDate()/1000,System.currentTimeMillis()/1000);
                }
            }

            tvMessage.setText(theLastMessage);
            tvDate.setText(theLastDate);

        }

        public void setUpDiscussion(String username, String imageProfile) {

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.send_alerte);
            tvUsername = itemView.findViewById(R.id.tv_username_sender);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(imageProfile).into(civUserImage);

            tvUsername.setText(username);
        }
    }
}
