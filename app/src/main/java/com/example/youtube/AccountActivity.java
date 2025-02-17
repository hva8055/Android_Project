package com.example.youtube;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {

    Toolbar toolbar;
    CircleImageView  user_profile_image;
    TextView username,email;

    TextView txt_your_channel,txt_setting,txt_help;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    String p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        init();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        getData();

        txt_your_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUserHaveChannel();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkUserHaveChannel() {
        reference.child("channels").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Intent intent = new Intent(AccountActivity.this,MainActivity.class);
                    intent.putExtra("type","channel");
                    startActivity(intent);

                }
                else{
                    showDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void showDialog(){
        Dialog dialog = new Dialog(AccountActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.channel_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        EditText input_Channel_name = dialog.findViewById(R.id.input_channel_name);
        EditText input_description = dialog.findViewById(R.id.input_description);
        TextView txt_create_channel = dialog.findViewById(R.id.txt_create_channel);

        txt_create_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = input_Channel_name.getText().toString();
                String description = input_description.getText().toString();

                if (name.isEmpty() || description.isEmpty()){
                    Toast.makeText(AccountActivity.this, "Fill Required fields", Toast.LENGTH_SHORT).show();
                }else{
                    createNewChannel(name,description,dialog);
                }
            }
        });

        dialog.show();
    }

    private void getData() {
        reference.child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String n = snapshot.child("username").getValue().toString();
                String e = snapshot.child("email").getValue().toString();
                p = snapshot.child("profile").getValue().toString();

                username.setText(n);
                email.setText(e);

                try {
                    Picasso.get().load(p).placeholder(R.drawable.baseline_account_circle_24).into(user_profile_image);
                }
                catch (Exception exception){
                    exception.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(AccountActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void createNewChannel(String name, String description, Dialog dialog){
        ProgressDialog progressDialog = new ProgressDialog(AccountActivity.this);
        progressDialog.setTitle("New Channel");
        progressDialog.setMessage("Creating...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Storing data in firebase

        String date = DateFormat.getDateInstance().format(new Date());

        HashMap<String, Object> map = new HashMap<>();
        map.put("channel",name);
        map.put("description",description);
        map.put("joined",date);
        map.put("uid",user.getUid());
        map.put("channel_logo",p);

        reference.child("channels").child(user.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(AccountActivity.this, name+" channel as been created", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(AccountActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("RestrictedApi")
    protected void init(){

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        user_profile_image = findViewById(R.id.user_profile_image);
        username = findViewById(R.id.user_channel_name);
        email = findViewById(R.id.email);
        txt_your_channel = findViewById(R.id.txt_channel_name);
        txt_setting = findViewById(R.id.txt_setting);
        txt_help = findViewById(R.id.txt_help);

    }

}