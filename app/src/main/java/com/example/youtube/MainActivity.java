package com.example.youtube;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.assist.AssistStructure;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.LocaleDisplayNames;
import android.media.MediaCas;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.youtube.fragment.ExplorFragment;
import com.example.youtube.fragment.HomeFragment;
import com.example.youtube.fragment.LibraryFragment;
import com.example.youtube.fragment.SubscriptionFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TotpMultiFactorAssertion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.concurrent.Flow;


public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    AppBarLayout appBarLayout;

    ImageView userprofile_image;

    FirebaseAuth auth;
    FirebaseUser user;
    GoogleSignInClient googleSignInClient;

    Uri videouri;


    Fragment fragment;
    private static final int RC_SIGN_IN = 100;
    private static final int PERMISSION = 101;
    private static final int PICK_VIDEO = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        frameLayout = findViewById(R.id.frame_layout);
        appBarLayout = findViewById(R.id.appBar);

        userprofile_image = findViewById(R.id.user_profile_image);

        checkPermission();

        getProfileImage();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                                .build();
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this,googleSignInOptions);

        showFragment();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        userprofile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user!=null){
                    startActivity(new Intent(MainActivity.this,AccountActivity.class));
                    getProfileImage();
                }
                else {
                    userprofile_image.setImageResource(R.drawable.baseline_account_circle_24);
                    showDialogue();

                }
            }
        });



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.home:
                        HomeFragment homeFragment = new HomeFragment();
                        selectedFragment(homeFragment);
                        break;

                    case R.id.explore:
                        ExplorFragment explorFragment = new ExplorFragment();
                        selectedFragment(explorFragment);
                        break;

                    case R.id.publish:
                        showPublishContentDialogue();
                        break;

                    case R.id.subscription:
                        SubscriptionFragment subscriptionFragment = new SubscriptionFragment();
                        selectedFragment(subscriptionFragment);
                        break;

                    case R.id.library:
                        LibraryFragment libraryFragment = new LibraryFragment();
                        selectedFragment(libraryFragment);
                        break;

                }

                return true;
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.search:
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(intent);
                    break;
                    default:
                        return false;
                }
                return true;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void SearchFromFirebase() {
    }

    private void showPublishContentDialogue() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.upload_dialogue);
        dialog.setCanceledOnTouchOutside(true);

        TextView txt_upload_video = dialog.findViewById(R.id.txt_upload_video);
        TextView txt_make_post = dialog.findViewById(R.id.txt_publish_post);
        TextView txt_poll = dialog.findViewById(R.id.txt_release_poll);

        txt_upload_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(Intent.createChooser(intent,"select video"),PICK_VIDEO);
            }
        });

        dialog.show();
    }

    private void showDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_signin_dialogue,viewGroup,false);
        builder.setView(view);

        TextView txt_google_signin = view.findViewById(R.id.txt_google_signin);
        txt_google_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        builder.create().show();
    }

    private void signIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                if (RC_SIGN_IN == requestCode && data != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);

                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    user = auth.getCurrentUser(); // Ensure `user` is updated
                                    if (user != null) {
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("username", user.getDisplayName());
                                        map.put("email", user.getEmail());
                                        map.put("profile", String.valueOf(user.getPhotoUrl()));
                                        map.put("uid", user.getUid());
                                        map.put("Search", user.getDisplayName().toLowerCase());

                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                                        reference.setValue(map).addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(MainActivity.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Failed to save data: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (ApiException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

            case PERMISSION:

            case PICK_VIDEO:
                if (requestCode==PICK_VIDEO && data!=null){
                    videouri = data.getData();
                    Intent intent = new Intent(MainActivity.this, PublishContentActivity.class);
                    intent.putExtra("Type","video");
                    intent.setData(videouri);
                    startActivity(intent);
                }
        }
    }

    private void selectedFragment(Fragment fragment) {
        setStatusBarColor("#FFFFFF");
        appBarLayout.setVisibility(View.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout,fragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.notification:
                Toast.makeText(this, "notification", Toast.LENGTH_SHORT).show();
                break;

            case R.id.search:
                Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }


    // to fetch profile picture
    private void getProfileImage(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(MainActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    String p = snapshot.child("profile").getValue().toString();

                    // picasso for to load profile picture
                    Picasso.get().load(p).placeholder(R.drawable.baseline_account_circle_24)
                            .into(userprofile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFragment(){
        String type = getIntent().getStringExtra("type");
        if(type!=null){
            switch (type){
                case "channel":
                    setStatusBarColor("#99FF0000");
                    appBarLayout.setVisibility(View.GONE);
                    fragment = channelDashboardFragment.newInstance();
                    break;
            }
            if (fragment!=null){
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.frame_layout,fragment).commit();
            }else{
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setStatusBarColor(String color){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(color));
    }

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION);
        }else{
            Log.d("tag", "checkPermission: Permission granted");
        }
    }

}