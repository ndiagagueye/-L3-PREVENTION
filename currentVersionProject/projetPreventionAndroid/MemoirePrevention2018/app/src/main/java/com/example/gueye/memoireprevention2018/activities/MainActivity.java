package com.example.gueye.memoireprevention2018.activities;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.graphics.drawable.Drawable;
        import android.location.Location;
        import android.net.ConnectivityManager;
        import android.net.Uri;
        import android.speech.RecognizerIntent;
        import android.support.annotation.NonNull;
        import android.support.design.widget.BottomNavigationView;
        import android.support.design.widget.FloatingActionButton;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentTransaction;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.gueye.memoireprevention2018.ModeleService.Common;
        import com.example.gueye.memoireprevention2018.ModeleService.MyResponse;
        import com.example.gueye.memoireprevention2018.ModeleService.Notification;
        import com.example.gueye.memoireprevention2018.ModeleService.Sender;
        import com.example.gueye.memoireprevention2018.R;
        import com.example.gueye.memoireprevention2018.Remote.APIService;
        import com.example.gueye.memoireprevention2018.appServices.ShakeService;
        import com.example.gueye.memoireprevention2018.fragments.HomeFragment;
        import com.example.gueye.memoireprevention2018.fragments.NotificationFragment;
        import com.example.gueye.memoireprevention2018.modele.Notifications;
        import com.example.gueye.memoireprevention2018.modele.Users;
        import com.example.gueye.memoireprevention2018.services.GoogleMapsServices;
        import com.example.gueye.memoireprevention2018.utils.Const;
        import com.facebook.login.LoginManager;
        import com.google.android.gms.location.FusedLocationProviderClient;
        import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.auth.UserInfo;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.firestore.DocumentChange;
        import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.EventListener;
        import com.google.firebase.firestore.FieldValue;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.FirebaseFirestoreException;
        import com.google.firebase.firestore.QuerySnapshot;
        import com.google.firebase.iid.FirebaseInstanceId;
        import com.google.firebase.messaging.FirebaseMessaging;
        import com.mikepenz.materialdrawer.AccountHeader;
        import com.mikepenz.materialdrawer.AccountHeaderBuilder;
        import com.mikepenz.materialdrawer.Drawer;
        import com.mikepenz.materialdrawer.DrawerBuilder;
        import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
        import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
        import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
        import com.mikepenz.materialdrawer.model.interfaces.IProfile;
        import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
        import com.mikepenz.materialdrawer.util.DrawerImageLoader;
        import com.squareup.picasso.Picasso;

        import java.lang.reflect.InvocationTargetException;
        import java.lang.reflect.Method;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Locale;
        import java.util.Map;

        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private BottomNavigationView mainBottomNav;
    private HomeFragment homeFragment;
    private FirebaseUser fUser;
    private List<Notifications> mNotifList;
    private String current_user_id;
    private Toolbar mToolbar;
    private static final int REQUEST_CALL = 1;
    private String userIdFace;
    private String name = null;
    private String telephone = null;
    private String image = null;

    private static final String TAG = "MainActivity";

    private String saveDate;
    private String saveCurrentTime;
    private String currentTime;

    GoogleMapsServices mapsServices;
    private boolean isLocationFound = false;
    private boolean isPermissionsGranted = false;
    public FusedLocationProviderClient mFusedLocationProviderClient;
    private Location currentLocationDevice;
    private double latitude;
    private double longitude;
    APIService mService;


    //private AccountFragment accountFragment;
    private NotificationFragment notificationFragment;
    private FloatingActionButton flabAddNewAlerte;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        verifyServices();


        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById( R.id.main_page_toolbar );
        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "Accueil" );

        subscribeToTopic();


        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPrefMain", MODE_PRIVATE);
        userIdFace = preferences.getString("user_id", null);
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        //user_is_logged_in_via_Facebook_in_Firebase_Auth();

        FirebaseMessaging.getInstance().subscribeToTopic("alerte");
        //Log.d( "Message Token", Common.currentToken );

        firebaseFirestore = FirebaseFirestore.getInstance();

        navigationDrawer( savedInstanceState, Const.AVATAR, "", "" );
        current_user_id = getCurrentUserId();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("ShareIdUser", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_id",current_user_id);
        editor.commit();

        Log.d( "MainActivity", "USER_ID "+current_user_id);

        if (current_user_id != null){

            firebaseFirestore.collection("Users").document(current_user_id).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        String name = documentSnapshot.getString("name");
                        String telephone = documentSnapshot.getString("telephone");
                        String image = documentSnapshot.getString("image");
                        if (name != null && telephone != null && image != null) navigationDrawer( savedInstanceState, image, name, telephone );
                    }
                }
            } );

        }else {
            sendTologin();
        }




        flabAddNewAlerte = (FloatingActionButton) findViewById( R.id.fl_add_new_alerte );

        flabAddNewAlerte = (FloatingActionButton) findViewById( R.id.fl_add_new_alerte );

        flabAddNewAlerte.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendUserToSendAlertActivity();
            }
        } );

        mainBottomNav = findViewById( R.id.mainBottomNav );

        getNotification();


        // FRAGMENT
        notificationFragment = new NotificationFragment();
        //accountFragment = new AccountFragment();
        homeFragment = new HomeFragment();


        initializeFragment();

        //replaceFragment(homeFragment);

        //MainNavigationView
        mainBottomNav.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment currentFragment = getSupportFragmentManager().findFragmentById( R.id.main_container );

                switch (item.getItemId()) {

                    case R.id.bottom_action_home:

                        replaceFragment( homeFragment, currentFragment );
                        return true;

                    case R.id.bottom_action_notif:
                        replaceFragment( notificationFragment, currentFragment );
                        return true;

                    case  R.id.bottom_action_help :
                        startActivity(new Intent(MainActivity.this, HelpPageActivity.class));

                        return true;

                    default:
                        return false;
                }
            }
        } );



        flabAddNewAlerte.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendUserToSendAlertActivity();
            }
        } );


    }

    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("alerte");
        //Common.currentToken = FirebaseInstanceId.getInstance().getToken();
       // Log.d( "Message Token", Common.currentToken );
        mService = Common.getFCMClient();
    }

    private void unSubscribeToTopic() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("alerte");
    }

    public void getSpeechInput() {

        Intent intent = new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH );
        intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM );
        intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault() );

        if (intent.resolveActivity( getPackageManager() ) != null) {
            startActivityForResult( intent, 10 );
        } else {
            Toast.makeText( this, "Votre smartphone ne supporte pas l'enregistrement vocal de google", Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );
                    if (result.get( 0 ).equals( Const.AU_SECOURS )) {
                        sendAlerte();
                    } else {
                        Toast.makeText( this, "Dites au secours", Toast.LENGTH_SHORT ).show();
                    }

                }
                break;
        }
    }

    public void sendAlerte() {

        if (isLocationFound) {
            LatLng latLng = new LatLng( currentLocationDevice.getLatitude(), currentLocationDevice.getLongitude() );
            latitude = latLng.latitude;
            longitude = latLng.longitude;

            Toast.makeText( MainActivity.this, "latitude " + latitude + " longitude " + longitude, Toast.LENGTH_SHORT ).show();

            storeFirebaseInstance( Const.DESCRIPTION_ALERT, "", "", current_user_id, latitude, longitude );

        } else {
            Toast.makeText( this, "Location not found", Toast.LENGTH_SHORT ).show();
            storeFirebaseInstance( Const.DESCRIPTION_ALERT, "", "", current_user_id, 0d, 0d );

        }
    }

    public void storeFirebaseInstance(final String description, String downloadUri, final String downloadthumbUri, final String current_user_id, final Double latitude, final Double longitude) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat( "dd-MMMM-yyyy" );

        saveDate = currentDate.format( calendar.getTime() );

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat( "H:mm" );
        saveCurrentTime = time.format( calForTime.getTime() );

        currentTime = saveDate + " " + saveCurrentTime;

        Map<String, Object> postMap = new HashMap<>();
        postMap.put( "description", description );
        postMap.put( "type", 0 );
        postMap.put( "latitude", latitude );
        postMap.put( "longitude", longitude );
        postMap.put( "image_url", downloadUri );
        postMap.put( "image_thumb", downloadthumbUri );
        postMap.put( "user_id", current_user_id );
        postMap.put( "timestamp", currentTime );
        postMap.put( "date", FieldValue.serverTimestamp());

        firebaseFirestore.collection( "Posts" ).add( postMap ).addOnCompleteListener( new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if (task.isSuccessful()) {

                    Toast.makeText( MainActivity.this, "Alerte a été envoyé avec success.", Toast.LENGTH_LONG ).show();

                    // SEND NOTIFICATION

                    sendNotification( 0, current_user_id, Const.DESCRIPTION_ALERT, "", latitude, longitude, currentTime );

                    sendNotificationPush( Const.DESCRIPTION_ALERT, Const.DEFAULT_TYPES[0] );


                } else {

                    Toast.makeText( MainActivity.this, "Erroor while trying to send the alert", Toast.LENGTH_SHORT ).show();
                }
                //newPostProgress.setVisibility(View.INVISIBLE);
            }
        } );

    }

    // Notification Push
    private void sendNotificationPush(String description, String typeAlerte) {

        unSubscribeToTopic();

        Notification notification = new Notification( description, typeAlerte );

        Sender sender = new Sender( "/topics/alerte", notification );
        mService.sendNotification( sender ).enqueue( new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                if (response.body().success == 1){
                    subscribeToTopic();
                    Toast.makeText( MainActivity.this, "Success", Toast.LENGTH_SHORT ).show();
                }

                else
                    Toast.makeText( MainActivity.this, "Failed", Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

                Log.e( "Error ", t.getMessage() );

            }
        } );

    }


    public void sendNotification(final int typeAlert, final String user_id, String description, String image, Double latitude, Double longitude, String date) {

        Map<String, Object> notificationMessage = new HashMap<>();
        notificationMessage.put( "type", typeAlert );
        notificationMessage.put( "from", user_id );
        notificationMessage.put( "description", description );
        notificationMessage.put( "latitude", latitude );
        notificationMessage.put( "longitude", longitude );
        notificationMessage.put( "image", image );
        notificationMessage.put( "date", date );
        notificationMessage.put( "status", "0" );
        notificationMessage.put( "timestamp", FieldValue.serverTimestamp());

        firebaseFirestore.collection( "Notifications" ).document().set( notificationMessage ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Toast.makeText( MainActivity.this, "alerte envoyé avec success. ", Toast.LENGTH_SHORT ).show();
                    Intent mainIntent = new Intent( MainActivity.this, MainActivity.class );
                    startActivity( mainIntent );
                    finish();
                } else {
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText( MainActivity.this, "Une erreur s'est produite. " + errorMsg, Toast.LENGTH_SHORT ).show();

                }
            }
        } );
    }


    public void getCurrentLocation() {

        isPermissionsGranted = mapsServices.getLocationPermission( this );

        if (isPermissionsGranted) {


            Log.d( TAG, "getDeviceLocation: getting devices location" );

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this );

            try {

                Log.d( TAG, "getDeviceLocation: permission is granted , we try now to get the current location" );

                Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener( new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        Log.d( TAG, "onComplete: we are trying to found the current location " );

                        Toast.makeText( MainActivity.this, "Trying to found the current location ", Toast.LENGTH_SHORT ).show();

                        if (task.isSuccessful() && task.getResult() != null) {

                            Toast.makeText( MainActivity.this, "C'est cool ", Toast.LENGTH_SHORT ).show();

                            Log.d( TAG, "onComplete: we have found the location " + task.getResult().toString() );

                            currentLocationDevice = (Location) task.getResult();

                            isLocationFound = true;

                            Toast.makeText( MainActivity.this, "Location found ", Toast.LENGTH_SHORT ).show();

                        } else {

                            Toast.makeText( MainActivity.this, "location not found ", Toast.LENGTH_SHORT ).show();

                        }

                        Log.d( TAG, "onComplete: currentLocation " + currentLocationDevice );
                    }
                } );

            } catch (SecurityException e) {

                Log.d( TAG, "getDeviceLocation: SecurityException : " + e.getMessage() );
            }


        } else {
            Log.d( TAG, "getCurrentLocation: asking permissions " );
            mapsServices.askPermissions();
        }

    }

    private void verifyServices() {

        mapsServices = new GoogleMapsServices( this, TAG );

        boolean serviceOk = mapsServices.isServicesOk( MainActivity.this );

        if (serviceOk) {

            getCurrentLocation();
        }
    }


    // Dialling phone
    private void makePhoneCall() {
        if (current_user_id != null){
            firebaseFirestore.collection( "Users" ).document(current_user_id).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    if (documentSnapshot.exists()) {

                        String number = documentSnapshot.getString( "telephoneEmergency" );

                        if (number.trim().length() > 0) {

                            if (ContextCompat.checkSelfPermission(MainActivity.this,
                                    android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                            } else {
                                String dial = "tel:" + number;
                                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                                finish();
                            }

                        }

                    }else{
                        Toast.makeText(MainActivity.this, "Veuillez completer votre profile", Toast.LENGTH_SHORT).show();

                    }
                }
            } );
        }
    }

    private boolean user_is_logged_in_via_Facebook_in_Firebase_Auth(){

        if (fUser != null) {
            for (UserInfo userInfo : fUser.getProviderData()) {
                if (userInfo.getProviderId().equals("facebook.com")) {
                    Toast.makeText( this, "Using facebook", Toast.LENGTH_SHORT ).show();
                    return true;
                }
            }
            Toast.makeText( this, "Using Firebase", Toast.LENGTH_SHORT ).show();

            return false;
        }
        return false;
    }



    private void navigationDrawer(final Bundle savedInstanceState,String image, String name,String telephone) {


        //initialize and create the image loader logic
        DrawerImageLoader.init( new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri image, Drawable placeholder) {
                Picasso.with( imageView.getContext() ).load( image ).placeholder( placeholder ).into( imageView );
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with( imageView.getContext() ).cancelRequest( imageView );
            }


        } );

        final IProfile profile = new ProfileDrawerItem().withName( name ).withEmail( telephone ).withIcon( image ).withIdentifier( 101 );

        //Navigation drawer
        new DrawerBuilder().withActivity( MainActivity.this ).build();

        AccountHeader accountHeader = new AccountHeaderBuilder().withActivity( MainActivity.this ).withHeaderBackgroundScaleType( ImageView.ScaleType.FIT_CENTER ).withHeightDp( 200 ).addProfiles( profile ).withHeaderBackground( R.color.colorPrimary ).build();

        //secondary items
        SecondaryDrawerItem home = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 1 ).withName( "Accueil" ).withIcon( R.drawable.home1 );
        SecondaryDrawerItem my_profile = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 2 ).withName( "Mon Profile" ).withIcon( R.drawable.man );
        SecondaryDrawerItem add_alert = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 3 ).withName( "Alertés" ).withIcon( R.drawable.clock );

        //settings, help, urgences items
        SecondaryDrawerItem help = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 4 ).withName( "Aide" ).withIcon( R.drawable.ic_help );
        SecondaryDrawerItem location = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 5 ).withName( "Ma position" ).withIcon( R.drawable.marker_map );
        SecondaryDrawerItem urgences = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 6 ).withName( "Urgences" ).withIcon( R.drawable.urge );
        SecondaryDrawerItem apropos = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 7 ).withName( "A propos" ).withIcon( R.drawable.about );
        SecondaryDrawerItem membres = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 8 ).withName( "membres" ).withIcon( R.drawable.usersgroup );
        SecondaryDrawerItem benevol = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 9 ).withName( "Bénévoles" ).withIcon( R.drawable.help );
        SecondaryDrawerItem contact = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 11 ).withName( "appel d'urgence" ).withIcon( R.drawable.call );
        SecondaryDrawerItem logout = (SecondaryDrawerItem) new SecondaryDrawerItem().withIdentifier( 10 ).withName( "Deconnexion" ).withIcon( R.drawable.logoutt );

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById( R.id.main_page_toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeButtonEnabled( false );


        new DrawerBuilder().withHeaderDivider( true ).withActivity( MainActivity.this ).withAccountHeader( accountHeader ).withToolbar( toolbar ).withActionBarDrawerToggleAnimated( true ).withTranslucentStatusBar( false ).withFullscreen( true ).withSavedInstance( savedInstanceState ).addDrawerItems(

                home, my_profile,
                //new DividerDrawerItem(),
                add_alert, location, urgences, contact, membres, benevol, help, apropos, logout

        ).withOnDrawerItemClickListener( new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if (drawerItem != null) {
                    Intent intent = null;
                    if (drawerItem.getIdentifier() == 1) {
                        intent = new Intent( MainActivity.this, MainActivity.class );
                    } else if (drawerItem.getIdentifier() == 2) {
                        intent = new Intent( MainActivity.this, ProfilActivity.class );
                        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

                    } else if (drawerItem.getIdentifier() == 3) {

                        intent = new Intent(MainActivity.this, SendAlerteActivity.class);
                        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
                        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

                    } else if (drawerItem.getIdentifier() == 4) {
                        intent = new Intent(MainActivity.this, HelpPageActivity.class);
                    } else if (drawerItem.getIdentifier() == 5) {
                        intent = new Intent(MainActivity.this, MyPositionActivity.class);
                        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

                    } else if (drawerItem.getIdentifier() == 6) {

                        //makePhoneCall();

                        intent = new Intent(MainActivity.this, ListSecoursActivity.class);
                        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

                    } else if (drawerItem.getIdentifier() == 7) {

                        intent = new Intent(MainActivity.this, AproposActivity.class);
                        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

                    } else if (drawerItem.getIdentifier() == 8) {

                        intent = new Intent(MainActivity.this, ChatActivity.class);
                        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

                    } else if (drawerItem.getIdentifier() == 9) {

                        intent = new Intent(MainActivity.this, ListBenevolActivity.class);
                        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);


                    } else if (drawerItem.getIdentifier() == 11) {

                        makePhoneCall();

                    } else if (drawerItem.getIdentifier() == 10) {

                        logout();

                    }
                    if (intent != null) {
                        MainActivity.this.startActivity( intent );
                    }
                }

                return false;
            }
        } ).build();
        //End of Navigation drawer
    }

    private void logout() {

        statusUser("offline");
        new AlertDialog.Builder(this)
                //.setTitle("Deconnection")
                .setMessage("Voulez-vous vraiment vous déconnecter ?")
                .setPositiveButton("Déconnection", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mAuth.signOut();
                        LoginManager.getInstance().logOut();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity( intent );
                        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
                        finish();

                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        statusUser("online");
                    }
                })
                .show();
    }

    ////Get Notification


    public void getNotification(){
        countNotif();
    }

    public String getCurrentUserId(){
        String userId = null;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth != null)
            userId = firebaseAuth.getUid();

        if (user_is_logged_in_via_Facebook_in_Firebase_Auth() == true){
            return  userIdFace;
        }
        return userId;
    }

    public void countNotif(){

        final TextView countNotif = findViewById( R.id.count_notif );
        firebaseFirestore.collection("Notifications").whereEqualTo( "status", "0").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots == null) return;
                if(documentSnapshots.isEmpty()){
                    countNotif.setVisibility( View.GONE );
                }else{
                    int count = ((int) documentSnapshots.size());

                     mNotifList = new ArrayList<>();
                    for(DocumentChange doc: documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            Notifications notifications = doc.getDocument().toObject( Notifications.class );

                            if (notifications.getFromNotif() != null){

                                if (current_user_id != null){
                                    if (!current_user_id.equals( notifications.getFromNotif() )) {
                                        mNotifList.add( notifications );

                                        countNotif.setVisibility( View.VISIBLE );
                                        countNotif.setText(""+ count );

                                    }
                                }
                            }

                        }
                    }

                }
            }
        } );
    }


    private void sendUserToSendAlertActivity() {

        Intent sendAlertIntent = new Intent(MainActivity.this, SendAlerteActivity.class);

        startActivity(sendAlertIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_left, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_apropos:
                // TODO Something when menu item selected
                startActivity(new Intent( MainActivity.this, AproposActivity.class ));
                overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

                return true;

            case R.id.menu_help:
                // TODO Something when menu item selected
                startActivity(new Intent( MainActivity.this, HelpPageActivity.class ));
                overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);


                return true;

            case R.id.menu_call:
                // TODO Something when menu item selected
                startActivity(new Intent( MainActivity.this, EmergencyCallActivity.class ));
                overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);


                return true;
            case R.id.menu_alerte:
                // TODO Something when menu item selected
                getSpeechInput();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onStart(){

        //launch the service for emmergency alerte
        startServiceApp();
        subscribeToTopic();



        super.onStart();
        // Get instance for current user
        FirebaseUser currentUser =  FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null && user_is_logged_in_via_Facebook_in_Firebase_Auth() == false){

            sendTologin();

        }else{

            current_user_id = getCurrentUserId();
            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (!task.getResult().exists()){
                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(setupIntent);
                            finish();
                        }
                    }
                    else{
                        String errorMsg = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error accured", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void startServiceApp() {

        Intent shakeIntent = new Intent(MainActivity.this, ShakeService.class);
        shakeIntent.setAction("com.example.gueye.memoireprevention2018.appServices.ShakeService");
        startService(shakeIntent);

        Log.d("Main Activity", "onStart:  shake service is running  ");
    }

    private void sendTologin() {

        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == homeFragment){

            fragmentTransaction.hide(notificationFragment);

        }

        if(fragment == notificationFragment){

            fragmentTransaction.hide(homeFragment);
            //fragmentTransaction.hide(accountFragment);

        }

        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }



    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, notificationFragment);
        //fragmentTransaction.add(R.id.main_container, accountFragment);

        fragmentTransaction.hide(notificationFragment);
        //fragmentTransaction.hide(accountFragment);

        fragmentTransaction.commit();

    }


    private  void statusUser(String statutus){

        HashMap<String, Object> statusHashMap = new HashMap<>() ;

        statusHashMap.put("status",statutus);

        if (current_user_id != null){
            firebaseFirestore.collection("Users").document(current_user_id).update(statusHashMap);
        }

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                //.setTitle("Deconnection")
                .setMessage("Voulez-vous vraiment quitter ?")
                .setPositiveButton("Quitter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                        dialog.dismiss();
                        statusUser("offline");


                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        statusUser("online");
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusUser("online");
    }




}
