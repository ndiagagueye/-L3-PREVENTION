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
import com.example.gueye.memoireprevention2018.modele.Comments;
import com.example.gueye.memoireprevention2018.modele.Like;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by gueye on 14/08/18.
 */

public class LikesRecyclerAdapter extends RecyclerView.Adapter<LikesRecyclerAdapter.ViewHolder> {

    public List<Like> likesList;
    public Context context;
    public FirebaseFirestore firebaseFirestore;
    public FirebaseAuth firebaseAuth;

    public LikesRecyclerAdapter(List<Like> likesList) {

        this.likesList = likesList;

    }

    @Override
    public LikesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.likes_item_layout, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        return new LikesRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LikesRecyclerAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        String usename = likesList.get( position ).getUsername();
        String image = likesList.get( position ).getImage();
        holder.setuserData(usename,image);
    }


    @Override
    public int getItemCount() {

        if (likesList != null) {

            return likesList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView likeUserName;
        private ImageView likeUserImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        // set usermane and image profile

        public  void setuserData(String name, String image){
            likeUserName = mView.findViewById(R.id.tv_like_username);
            likeUserImage = mView.findViewById( R.id.iv_like_user);
            likeUserName.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.send_alerte);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(likeUserImage);
        }


    }

}