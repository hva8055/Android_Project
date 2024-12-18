package com.example.youtube.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.youtube.Models.ContentModel;
import com.example.youtube.R;
import com.example.youtube.VideoPlayerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

    Context context;
    ArrayList<ContentModel> list;
    DatabaseReference reference;
    FirebaseUser user;

    public ContentAdapter(Context context, ArrayList<ContentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_video, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContentModel model = list.get(position);
        if (model!=null){
            Glide.with(context).asBitmap().load(model.getVideo_url()).into(holder.thumbnail);
            holder.video_title.setText(model.getVideo_title());
            holder.date.setText(model.getDate());


            setData(model.getPublisher(), holder.channel_logo,holder.channel_name);

            holder.thumbnail.setOnClickListener(view -> {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("video_url", model.getVideo_url());
                intent.putExtra("video_title", model.getVideo_title());
                intent.putExtra("video_description", model.getVideo_description());
                // Pass other data as needed
                context.startActivity(intent);
            });

        }
    }

    private void setData(String publisher, CircleImageView logo, TextView channel_name) {
        if (publisher == null || publisher.isEmpty()) {
            Log.e("PublisherError", "Publisher ID is null or empty");
            return; // Prevent further processing
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("channels");
        reference.child(publisher).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String cname = snapshot.child("channel").getValue(String.class);
                    if (cname != null) {
                        channel_name.setText(cname);
                        String clogo = snapshot.child("channel_logo").getValue().toString();
                        Picasso.get().load(clogo).placeholder(R.drawable.baseline_account_circle_24).into(logo);
                    } else {
                        channel_name.setText("Unknown Channel");
                    }
                } else {
                    Log.e("FirebaseError", "Channel data not found for publisher: " + publisher);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching channel: " + error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView thumbnail;
        TextView video_title, channel_name, date;
        CircleImageView channel_logo;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            video_title = itemView.findViewById(R.id.video_title);
            channel_name = itemView.findViewById(R.id.channel_name);
            date = itemView.findViewById(R.id.date);
            channel_logo = itemView.findViewById(R.id.channel_logo);

        }
    }



}