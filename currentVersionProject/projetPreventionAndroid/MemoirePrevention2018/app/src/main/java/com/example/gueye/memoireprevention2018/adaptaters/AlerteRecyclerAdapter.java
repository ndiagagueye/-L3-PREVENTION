package com.example.gueye.memoireprevention2018.adaptaters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.activities.CommentsActivity;
import com.example.gueye.memoireprevention2018.activities.PostAlerteDetails;
import com.example.gueye.memoireprevention2018.modele.BlogPost;
import com.example.gueye.memoireprevention2018.modele.Users;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gueye on 13/08/18.
 */

public class AlerteRecyclerAdapter extends RecyclerView.Adapter<AlerteRecyclerAdapter.ViewHolder> {


    public List<BlogPost> blog_list;
    public List<Users> user_list;
    public Context context;
    public FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    public AlerteRecyclerAdapter(List<BlogPost> blog_list, List<Users> user_list, FirebaseFirestore firebaseFirestore,FirebaseAuth firebaseAuth){
        this.blog_list = blog_list;
        this.user_list = user_list;
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseAuth = firebaseAuth ;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_layout, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    // load data
    @Override
    public void onBindViewHolder(@NonNull final AlerteRecyclerAdapter.ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);
        // Get Id for target post
        final String blogPostId =  blog_list.get(position).blogPostId;
        //get Current User id who is login
        SharedPreferences preferences = context.getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        final String currentUserId = preferences.getString("user_id", null);
        // Put description
        final String desc_data = blog_list.get(position).getDescription();
        holder.setDescText(holder.extract(desc_data));

        // Put image post
        // Put type

        final int postionType = blog_list.get(position).getType();
        holder.setTitle(Const.DEFAULT_TYPES[postionType]);

        final String image_url = blog_list.get(position).getImage_url();
        String thumbUri = blog_list.get(position).getImage_thumb();

        if (image_url.equals("") && thumbUri.equals("")){

            holder.blogImageView.setImageResource(Const.DEFAULT_RESOURCE_IMAGES[postionType]);

        }else{
            holder.setBlogImage(image_url, thumbUri);
        }

        // get id username for post
        final String blog_user_id = blog_list.get(position).getUser_id();

        if (blog_user_id.equals(currentUserId)){

         //   holder.deletePostBtn.setEnabled(true);
           // holder.deletePostBtn.setVisibility(View.VISIBLE);
        }

        // get username and profile image
        final String userName = user_list.get(position).getName();
        final String userImage = user_list.get(position).getImage();
        holder.setuserData(userName,userImage);


        // Put date post
        String timestamp = blog_list.get( position ).getTimestamp();

        String[] dateFormat = timestamp.split(" ");

        final String time = dateFormat[1];

        String[] dateArray = dateFormat[0].split("-");

        final String date = dateArray[0]+" "+dateArray[1].substring(0,3)+" "+dateArray[2];


        holder.setDate(date,time);

        // COUNT COMMENTS

        firebaseFirestore.collection("Posts").document(blogPostId).collection("Comments").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(documentSnapshots == null) return;

                if (!documentSnapshots.isEmpty()){
                    int count = documentSnapshots.size();
                    holder.updateCommentCount(count);

                }



            }
        } );

        // LIKES COUNT

        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (documentSnapshots == null) return;

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.updateLikesCount(count);

                } else {

                    holder.updateLikesCount(0);

                }

            }
        });


        //Get Likes
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (documentSnapshot == null) return;
                if(documentSnapshot.exists()){

                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_like_red));

                }  else {

                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_like_holo_light));

                }

            }
        });


        //Likes Feature
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(! holder.isConnected(context)) return;

                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (holder.isConnected( context )){

                            if(!task.getResult().exists()){

                                Map<String, Object> likesMap = new HashMap<>();
                                likesMap.put("timestamp",holder.saveDate());
                                likesMap.put("user_id", blog_user_id);
                                likesMap.put("username", userName);
                                likesMap.put("image", userImage);

                                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);

                            } else {

                                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();

                            }
                        }


                    }
                });
            }
        });
        // Clike on comment btn
        holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);
            }
        });

        holder.post_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double latitude = blog_list.get(position).getLatitude();
                Log.d("AlerteRecycleAdapter", "onClick: latitude "+ latitude);
                double longitude = blog_list.get(position).getLongitude();



                Toast.makeText(context, "On a recupéré la position du post  post ", Toast.LENGTH_SHORT).show();
                
                Intent postAlertDetailIntent = new Intent(context, PostAlerteDetails.class);
                postAlertDetailIntent.putExtra(Const.LONG,longitude);
                postAlertDetailIntent.putExtra( Const.LAT,latitude);
                postAlertDetailIntent.putExtra(Const.USERNAME_IMAGE,userImage);
                postAlertDetailIntent.putExtra(Const.USERNAME, userName);
                postAlertDetailIntent.putExtra(Const.USER_ID, currentUserId);
                postAlertDetailIntent.putExtra(Const.IMAGE_POST,image_url);
                postAlertDetailIntent.putExtra(Const.TYPE,postionType);
                postAlertDetailIntent.putExtra(Const.DESC_DATA, desc_data);
                postAlertDetailIntent.putExtra(Const.DATE,date+" à "+time);
                postAlertDetailIntent.putExtra( Const.POST_ID, blogPostId );
                context.startActivity(postAlertDetailIntent);
            }
        });

        // DELETE POST

       /* holder.deletePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Posts").document(blogPostId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        blog_list.remove(position);
                        user_list.remove(position);
                    }
                });
            }
        }); */



    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // ATTRIBUTES
        private View mView;
        private TextView descView;
        private TextView post_tv_title ;
        private ImageView blogImageView;
        private TextView blogDate;
        private TextView blogDateTime;
        private TextView blogUserName;
        private TextView comment_count;
        private CircleImageView blogUserImage;
        private TextView tvMoreAboutPost ;
        private LinearLayout post_more;

        private ImageView blogLikeBtn;
        private TextView blogLikeCount;
        private ImageView blogCommentBtn;
       // private Button deletePostBtn;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView ;
            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogCommentBtn = mView.findViewById(R.id.blog_comment_btn);
            //deletePostBtn = mView.findViewById(R.id.blog_delete_post_btn);
            blogImageView = mView.findViewById(R.id.blog_image);
            tvMoreAboutPost = mView.findViewById(R.id.post_tv_more);
            post_more = mView.findViewById(R.id.post_more);
            
            //tvMoreAboutPost.setOnClickListener(this);
            
        }


        public boolean isConnected(Context context){
            NetworkInfo info = getNetworkInfo(context);
            return (info != null && info.isConnected());
        }

        public  NetworkInfo getNetworkInfo(Context context){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo();
        }


        // Set description
        public void setDescText(String descText){
            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);
        }

        // Set title
        public void setTitle(String title){
            post_tv_title = mView.findViewById(R.id.post_tv_title);
            post_tv_title.setText(title);
        }

        // set image blog
        public void setBlogImage(String downloadUri, String thumbUri){

            blogImageView = mView.findViewById(R.id.blog_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);

        }
        // set date
        public void setDate(String date, String time){
            blogDate = mView.findViewById(R.id.blog_date);
            blogDateTime = mView.findViewById(R.id.blog_time_date);

            blogDateTime.setText(time);
            blogDate.setText(date);
        }

        public String saveDate(){

            String saveDate;
            String saveCurrentTime;
            Calendar calendar  = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");

            saveDate = currentDate.format(calendar.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat time = new SimpleDateFormat("H:mm");
            saveCurrentTime = time.format(calForTime.getTime());

            return  saveDate+" "+saveCurrentTime;
        }

        // set usermane and image profile

        public  void setuserData(String name, String image){
            blogUserName = mView.findViewById(R.id.blog_user_name);
            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);
        }

        public void updateLikesCount(int count){
            blogLikeCount = mView.findViewById(R.id.blog_lke_count);
            blogLikeCount.setText(count + "");
        }

        public void updateCommentCount(int count){
            comment_count = mView.findViewById(R.id.comment_count);
            comment_count.setText(count +"");
        }

        public String extract(String texte){
            if (texte.length() > 40){

              return   texte.substring(0,35)+"...";
            }
                return texte ;
        }

        @Override
        public void onClick(View view) {

            Toast.makeText(context, "On m'a clické hahahahah ", Toast.LENGTH_SHORT).show();
            
        }
    }
}
