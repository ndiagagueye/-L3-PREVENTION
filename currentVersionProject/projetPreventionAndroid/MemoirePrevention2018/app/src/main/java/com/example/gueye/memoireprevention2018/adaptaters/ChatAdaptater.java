package com.example.gueye.memoireprevention2018.adaptaters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gueye.memoireprevention2018.R;

import java.util.List;

public class ChatAdaptater extends RecyclerView.Adapter <ChatAdaptater.ChatViewHolder> {

    private Context context;
    private List users ;

    public ChatAdaptater(Context context, List users){

        this.context = context;
        this.users = users;


    }

    @NonNull
    @Override
    public ChatAdaptater.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_discussions_item_layout,parent,false);


        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdaptater.ChatViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public ChatViewHolder(View itemView) {
            super(itemView);
        }
    }
}
