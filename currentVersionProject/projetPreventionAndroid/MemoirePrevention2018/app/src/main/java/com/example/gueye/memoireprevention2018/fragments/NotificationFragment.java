package com.example.gueye.memoireprevention2018.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.adaptaters.NotificationsAdapter;

import com.example.gueye.memoireprevention2018.modele.Notifications;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private RecyclerView mNotificationList;
    private NotificationsAdapter notificationsAdapter;

    private List<Notifications> mNotifList;
    private FirebaseFirestore mFirestore;
    private String currentUserId;
    private TextView tv_no_notification_get;
    private AVLoadingIndicatorView loading_spinner;




    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate( R.layout.fragment_notification, container, false);


        mNotifList = new ArrayList<>();

        mNotificationList = (RecyclerView) view.findViewById(R.id.notification_list);
        notificationsAdapter = new NotificationsAdapter(getContext(), mNotifList);
        loading_spinner = view.findViewById(R.id.linear_scale_progress_loader_notif);

        lineScaleLoader();

        mNotificationList.setHasFixedSize(true);
        mNotificationList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mNotificationList.setAdapter(notificationsAdapter);

        mFirestore = FirebaseFirestore.getInstance();

        SharedPreferences preferences = container.getContext().getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        currentUserId = preferences.getString("user_id", null);


        Query firstQuery = mFirestore.collection("Notifications").orderBy("timestamp", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(documentSnapshots == null){
                    return;
                }


                for(DocumentChange doc: documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String notif_id = doc.getDocument().getId();

                        Notifications notifications = doc.getDocument().toObject(Notifications.class).withId(notif_id);

                        if (!currentUserId.equals(notifications.getFromNotif())){
                            mNotifList.add( notifications );
                            if (mNotifList.size()>0){
                                hideLineScaleLoader();
                                Toast.makeText( container.getContext(), "hide loader", Toast.LENGTH_SHORT ).show();
                            }
                            notificationsAdapter.notifyDataSetChanged();

                        }


                    }

                }

            }
        });

        return view;
    }

    void lineScaleLoader() {
        loading_spinner.setVisibility(View.VISIBLE);
    }
    void hideLineScaleLoader() {
        loading_spinner.setVisibility(View.GONE);
    }


}
