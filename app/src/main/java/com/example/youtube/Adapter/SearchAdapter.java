package com.example.youtube.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtube.Models.VideoModel;
import com.example.youtube.R;
import com.example.youtube.VideoPlayerActivity;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<VideoModel> videoList;

    public SearchAdapter(List<VideoModel> videoList) {
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoModel video = videoList.get(position);
        holder.titleTextView.setText(video.getTitle());
        holder.descriptionTextView.setText(video.getDescription());

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("videoUrl", video.getUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.video_title);
            descriptionTextView = itemView.findViewById(R.id.video_description);
        }
    }
}
