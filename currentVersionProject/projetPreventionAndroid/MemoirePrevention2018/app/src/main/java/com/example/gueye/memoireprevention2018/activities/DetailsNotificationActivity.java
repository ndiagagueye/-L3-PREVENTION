package com.example.gueye.memoireprevention2018.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.services.GoogleMapsServices;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsNotificationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Detail";
    private String notif_id ;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private String typeAlert = null ;

    private GoogleMapsServices mapsServices;
    private boolean isPermissionsGranted =false;
    private GoogleMap mMap;

    private TextView descView;
    private ImageView blogImageView;
    private TextView blogDate;
    private TextView blogUserName;
    private CircleImageView blogUserImage;
    private TextView blogType;
    private TextView time;
    private String mDate = null;
    private String mTime = null;
    private TextView like, comment;

    String description = null;
    String imagePub = null;
    String user_id = null;
    Double longitude ;
    Double latitude ;
    long position = 0;

    private Location currentLocationDevice;

    public LatLng currentLatLng;
    public LatLng destLatLng ;
    //private FirebaseFirestore mFirestore;

    private android.support.v7.widget.Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_details_notification );

        notif_id = getIntent().getStringExtra("notif_id");

        mFirestore = FirebaseFirestore.getInstance();

        mapsServices = new GoogleMapsServices(this, TAG);

        latitude = getIntent().getDoubleExtra(Const.LAT ,0.d);
        longitude = getIntent().getDoubleExtra(Const.LONG,0.d);

        DisplyCountLikeAndComment();


        initialize();

        init();
        //Toast.makeText( this, notif_id , Toast.LENGTH_SHORT ).show();
        getDetailsNotification();


    }

    private void initialize() {

        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById( R.id.detail_notif_toolbar );

        setSupportActionBar( mToolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        mToolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                finish();
            }
        } );



        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById( R.id.detail_toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById( R.id.detail_app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(" Details Notification");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });


        descView = findViewById( R.id.detail_desc);
        blogImageView = findViewById( R.id.detail_image);
        blogDate = findViewById( R.id.detail_date);
        blogUserName = findViewById( R.id.detail_user_name);
        blogUserImage = findViewById( R.id.detail_user_image);
        blogType = findViewById( R.id.detail_tv_title );
        time = findViewById( R.id.detail_time_date );
        like = findViewById(R.id.detail_like_count);
        comment = findViewById(R.id.detail_comment_count);

        mDate = getIntent().getStringExtra("date");
        mTime = getIntent().getStringExtra("time");
        if (mDate != null && mTime != null){
            time.setText(mTime);
            blogDate.setText(mDate);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void getDetailsNotification(){

        mFirestore.collection( "Notifications" ).document(notif_id).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    description = documentSnapshot.getString( "description" );
                    imagePub = documentSnapshot.getString( "image" );
                    position = documentSnapshot.getLong("type") ;
                    user_id =documentSnapshot.getString( "from" );

                    typeAlert = Const.DEFAULT_TYPES[(int) position];
                    if (description != null && user_id != null && imagePub != null && position != 0 && typeAlert != null ){

                        descView.setText( description );
                        blogType.setText( typeAlert );

                        if (imagePub.equals("")){

                            blogImageView.setImageResource( Const.DEFAULT_RESOURCE_IMAGES[(int) position]);
                        }else{

                            RequestOptions placeholderOption = new RequestOptions();
                            placeholderOption.placeholder( R.drawable.profile_placeholder );

                            Glide.with( DetailsNotificationActivity.this ).applyDefaultRequestOptions( placeholderOption ).load( imagePub ).into( blogImageView );
                        }


                        mFirestore.collection( "Users" ).document(user_id).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String userImage = documentSnapshot.getString( "image" );
                                String userName = documentSnapshot.getString("name" );

                                blogUserName.setText( userName );

                                RequestOptions placeholderOption = new RequestOptions();
                                placeholderOption.placeholder( R.drawable.profile_placeholder );

                                Glide.with( DetailsNotificationActivity.this ).applyDefaultRequestOptions( placeholderOption ).load( userImage ).into( blogUserImage );

                            }
                        } );

                    }

                }



            }
        } );
    }

    private void init() {

        Toast.makeText(this, " latitude "+ latitude +"  longitude "+ longitude , Toast.LENGTH_SHORT).show();


        isPermissionsGranted = mapsServices.getLocationPermission(this);

        if(isPermissionsGranted){

            Toast.makeText(this, "permissions granted", Toast.LENGTH_SHORT).show();
        }

        initmMap();

    }

    private void DisplyCountLikeAndComment(){
        if (notif_id != null){

            mFirestore.collection("Posts").document(notif_id).collection("Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (documentSnapshots == null) return;
                    if(!documentSnapshots.isEmpty()){
                        int count = documentSnapshots.size();
                        like.setText(""+count);
                        Toast.makeText( DetailsNotificationActivity.this, "likes "+count, Toast.LENGTH_SHORT ).show();

                    } else {
                        like.setText(""+00);
                    }
                }
            });

            //COUNT COMMENT

            mFirestore.collection("Posts").document(notif_id).collection("Comments").addSnapshotListener( new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if(documentSnapshots == null) return;

                    if (!documentSnapshots.isEmpty()){
                        int countComment = documentSnapshots.size();
                        comment.setText(""+countComment);
                        Toast.makeText( DetailsNotificationActivity.this, "likes "+countComment, Toast.LENGTH_SHORT ).show();

                    }else{
                        comment.setText(""+00);
                    }

                }
            } );
        }else
        {
            Toast.makeText( this, "Null id post", Toast.LENGTH_SHORT ).show();
        }

    }


    private void verifyServices() {

        boolean serviceOk = mapsServices.isServicesOk(DetailsNotificationActivity.this);

        Toast.makeText(this, "services "+serviceOk, Toast.LENGTH_SHORT).show();

        if (serviceOk) {

            Toast.makeText(this, "Services are OK !!!", Toast.LENGTH_SHORT).show();

            // get the location post

            getPostLocation(latitude,longitude );

        }

    }

    private void initmMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.notif_map);
        mapFragment.getMapAsync(this);
        Toast.makeText(this, "map try to initialise it ", Toast.LENGTH_SHORT).show();


    }

    public void getPostLocation (double latitude , double longitude ) {


        Double latitudeDevice =0.d;
        Double longitudeDevice = 0.d;

        Log.d(TAG, "getPostLocation: permissions "+ isPermissionsGranted);

        Toast.makeText(this, "Permissions "+isPermissionsGranted, Toast.LENGTH_SHORT).show();

        if (isPermissionsGranted) {

            Toast.makeText(this, "lat "+latitude, Toast.LENGTH_SHORT).show();



            destLatLng = new LatLng(latitude,longitude);

            moveCameraTo(destLatLng, Const.DEFAULT_ZOOM_MAP,"Lieu ou s'est produit l'evenement"+'\n'+"Latitude: "+latitude+" Longitude: "+longitude);


            Log.d(TAG, "getDeviceLocation: getting devices location");



        } else {
            Log.d(TAG, "getCurrentLocation: asking permissions ");
            mapsServices.askPermissions();
        }

    }


    private void moveCameraTo(LatLng latLng, float zoom , String title) {

        Log.d(TAG, "mooveCamera:  mooving the camera to latitude : "+ latLng.latitude +" and longitude "+latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        mMap.clear();

        Toast.makeText(this, "Trying to moove the camera ...  ", Toast.LENGTH_SHORT).show();

        if(!title.equals("My location")){

            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            mMap.addMarker(options);
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady: loading map ");
        mMap = googleMap;
        Toast.makeText(this, "Map's is ready !!!", Toast.LENGTH_SHORT).show();



        if (isPermissionsGranted) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            verifyServices();

        }

    }




}
