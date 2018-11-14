package com.example.gueye.memoireprevention2018.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.adaptaters.TypeUserAdaptater;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.HashMap;

public class ProfilActivity extends AppCompatActivity {

    private CheckBox benevole;
    private static final String TAG = "PROFILE";
    private TextView mUsername, mTelephone, mTelephoneEmergency;
    private EditText mEditUsername, mEditTelephone, mEditTelephoneEmergency;

    private ImageView mProfileImage;
    private ImageView mEditBtn;
    private  ImageView mSaveInfos;
    private FloatingActionButton mchangeProfileImage;
    private Uri mainImageURI = null;
    private boolean isChanged = false;
    private ProgressDialog loadinBar;
    private int positionSelect = 0;
    private Spinner typeSpinner;
    private static final int REQUEST_CALL =1;
    private TypeUserAdaptater customAdaptater;
    private String name = null;
    private String image = null;
    private String telephone = null;
    private String telephoneEmergency = null;

    private Button callUrgence;
    private Button inviteFriend;


    private StorageReference storageReference;
    //Firebase

    private FirebaseFirestore mFirestore;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profil );

        init();


        loadInfo();

        typeSpinner = (Spinner) findViewById(R.id.spinner_type_user);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                positionSelect = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        customAdaptater = new TypeUserAdaptater(ProfilActivity.this, Const.DEFAULT_RESOURCE_ICONES_USER, Const.DEFAULT_TYPES_USER);

        typeSpinner.setAdapter(customAdaptater);

        // Click on CircleImageView
        mchangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Allow or Deny
                permissionGaranted();

            }
        });

        benevole.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                if (compoundButton.isChecked()) {
                    typeSpinner.setVisibility( View.VISIBLE );

                } else {
                    typeSpinner.setVisibility( View.GONE );
                }
            }

        } );




    }


    public void init(){
        mFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        loadinBar = new ProgressDialog(this);
        callUrgence = findViewById(R.id.urgence_call_btn);
        inviteFriend = findViewById(R.id.invite_friend_btn);

        benevole = findViewById(R.id.be_benevole);



        SharedPreferences preferences = getApplicationContext().getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        mUserId = preferences.getString("user_id", null);

        mUsername = findViewById(R.id.profile_username);
        mTelephone = findViewById(R.id.profile_telephone);
        mTelephoneEmergency = findViewById(R.id.profile_user_contact_here);

        mEditUsername = findViewById(R.id.edit_profile_username);
        mEditTelephone = findViewById(R.id.edit_profile_telephone);
        mEditTelephoneEmergency = findViewById(R.id.edit_profile_user_contact_here);

        mProfileImage = findViewById( R.id.profile_image_circle);
        mEditBtn = findViewById(R.id.btn_edit_profile);
        mSaveInfos = findViewById(R.id.btn_save_info);
        mchangeProfileImage = findViewById(R.id.take_a_picture);

        mEditBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSaveInfos.setVisibility(View.VISIBLE);
                mEditBtn.setVisibility( View.GONE );
                enableEditText();
            }
        } );

        saveInfo();

        callUrgence.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        } );

        inviteFriend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareIt();
                Toast.makeText( ProfilActivity.this, "S'invite mes proche", Toast.LENGTH_SHORT ).show();
            }
        } );

    }


    private void saveInfo() {

        mSaveInfos.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadinBar.setTitle( "" );
                loadinBar.setMessage( "Chargement..." );
                loadinBar.setCanceledOnTouchOutside( false );
                loadinBar.show();

                disableEditText();

                final String name = mEditUsername.getText().toString();
                final String telephone = mEditTelephone.getText().toString();
                final String urgenceTel = mEditTelephoneEmergency.getText().toString();

                if (isChanged){

                    StorageReference image_path = storageReference.child("profile_image").child(mUserId + ".jpg");
                    image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {
                                loadinBar.dismiss();
                                updateProfilUser(task, name, telephone, urgenceTel);

                            } else {
                                loadinBar.dismiss();

                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(ProfilActivity.this, "Image error " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    updateProfilUser(null,name, telephone, urgenceTel);

                }
            }

        } );
    }

    private void permissionGaranted() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (ContextCompat.checkSelfPermission(ProfilActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                Toast.makeText(ProfilActivity.this, "Permission denied!", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(ProfilActivity.this, new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            else{

                BringImagePicker();
            }
        }else{
            BringImagePicker();
        }
    }


    private void enableEditText() {

        mchangeProfileImage.setVisibility(View.VISIBLE);
        //typeSpinner.setVisibility(View.VISIBLE);

        mEditTelephone.setVisibility( View.VISIBLE );
        mEditUsername.setVisibility( View.VISIBLE );
        mEditTelephoneEmergency.setVisibility( View.VISIBLE );

        mTelephone.setVisibility( View.GONE );
        mUsername.setVisibility( View.GONE );
        mTelephoneEmergency.setVisibility( View.GONE );


    }



    private void disableEditText() {

        hideSoftKeyboard();

        mSaveInfos.setVisibility(View.GONE);
        mEditBtn.setVisibility( View.VISIBLE );

        mUsername.setVisibility( View.VISIBLE );
        mTelephone.setVisibility( View.VISIBLE );
        mTelephoneEmergency.setVisibility( View.VISIBLE );

        typeSpinner.setVisibility(View.GONE);
        mchangeProfileImage.setVisibility(View.GONE);
        mSaveInfos.setVisibility(View.GONE);
        mEditUsername.setVisibility( View.GONE );

        mEditTelephone.setVisibility( View.GONE );

        mEditTelephoneEmergency.setVisibility( View.GONE );


    }

    public void hideSoftKeyboard() {

        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(
                        ProfilActivity.INPUT_METHOD_SERVICE);
        if(this.getCurrentFocus() != null){
            inputMethodManager.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(), 0);
        }

    }

    public  void  loadInfo(){

        if (mUserId != null){

            mFirestore.collection("Benevol").document(mUserId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){

                        if(documentSnapshot.getLong("profession") != null){

                            long position = documentSnapshot.getLong("profession");
                            typeSpinner.setSelection( (int) position );
                        }else {
                            Toast.makeText( ProfilActivity.this, "null postion selected", Toast.LENGTH_SHORT ).show();
                        }

                    }
                }
            } );

            mFirestore.collection("Users").document(mUserId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    if (documentSnapshot == null) return;

                    if (documentSnapshot.exists()){
                         name = documentSnapshot.getString( "name" );
                         telephone = documentSnapshot.getString( "telephone" );
                         telephoneEmergency = documentSnapshot.getString( "telephoneEmergency");
                         image = documentSnapshot.getString( "image" );

                        if (name != null && telephone != null && telephoneEmergency != null && image != null){

                            mUsername.setText( name );
                            mTelephone.setText(telephone);
                            mTelephoneEmergency.setText(telephoneEmergency);

                            Log.d( TAG, "onSuccess: "+ name );
                            Log.d( TAG, "onSuccess: "+ telephone );
                            Log.d( TAG, "onSuccess: "+ telephoneEmergency );
                            Log.d( TAG, "onSuccess: "+ image );



                            mEditUsername.setText( name );
                            mEditTelephone.setText(telephone);
                            mEditTelephoneEmergency.setText(telephoneEmergency);

                            RequestOptions plasholderOption = new RequestOptions();
                            plasholderOption.placeholder( R.drawable.back );

                            Glide.with(ProfilActivity.this ).setDefaultRequestOptions( plasholderOption ).load( image ).into( mProfileImage );

                        }

                    }

                }
            } );
        }


    }

    // Dialling phone
    private void makePhoneCall() {
        if(mUserId != null){

            mFirestore.collection( "Users" ).document(mUserId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    if (documentSnapshot.exists()) {

                        String number = documentSnapshot.getString( "telephoneEmergency" );

                        if (number.trim().length() > 0) {

                            if (ContextCompat.checkSelfPermission(ProfilActivity.this,
                                    android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(ProfilActivity.this,
                                        new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                            } else {
                                String dial = "tel:" + number;
                                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                            }

                        }else {
                            Toast.makeText(ProfilActivity.this, "Veuillez completer votre profile", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            } );
        }


    }


    // invite Friend
    private void shareIt() {
        //sharing implementation here
        if (mUserId != null){

            final Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Invitation");
            mFirestore.collection("Users").document(mUserId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    if (documentSnapshot == null) return;

                    if (documentSnapshot.exists()){
                        String name = documentSnapshot.getString( "name" );
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, name+" Vous invite à utiliser l'application");
                        startActivity(Intent.createChooser(sharingIntent, "inviter via"));
                    }

                }
            } );
            //sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "  ");

        }

    }


    //Stock infos
    private void updateProfilUser(@NonNull Task<UploadTask.TaskSnapshot> task, String username, String telephone, String telephoneEmergency) {

        if (mUserId != null){

            Uri download_uri;
            if (task != null){
                download_uri = task.getResult().getDownloadUrl();

            }else{
                download_uri = mainImageURI;
            }


            final HashMap<String,Object> userMap = new HashMap<>();



            if(task != null){
                userMap.put("image", download_uri.toString());
            }else {
                userMap.put("name", username);
                userMap.put("telephone", telephone);
                userMap.put("telephoneEmergency", telephoneEmergency);
            }

            if (benevole.isChecked()){

                final HashMap<String,Object> benevolMap = new HashMap<>();
                benevolMap.put("profession", positionSelect);
                benevolMap.put("user_id", mUserId);
                mFirestore.collection("Benevol").document(mUserId).update(benevolMap);

            }

            mFirestore.collection("Users").document(mUserId).update(userMap).addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        loadInfo();
                        loadinBar.dismiss();
                    }
                }
            } );
        }


    }


    private void BringImagePicker(){
        CropImage.activity()
                .setGuidelines( CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(ProfilActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                mProfileImage.setImageURI(mainImageURI);
                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
