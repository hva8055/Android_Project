package com.example.youtube.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtube.Models.PLaylistModel;
import com.example.youtube.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewMolder> {

    Context context;
    ArrayList<PLaylistModel> list;
    OnitemClickListener listener;

    public PlaylistAdapter(Context context, ArrayList<PLaylistModel> list, OnitemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public PlaylistAdapter() {
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewMolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewMolder(LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewMolder holder, int position) {
        holder.blind(list.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewMolder extends RecyclerView.ViewHolder {
        TextView txt_playlist_name,txt_videos_count;

        public ViewMolder(@NonNull View itemView) {
            super(itemView);

            txt_playlist_name = itemView.findViewById(R.id.txt_playlist_name);
            txt_videos_count = itemView.findViewById(R.id.txt_videos_count);


        }

        public void blind(final PLaylistModel model, final OnitemClickListener listener){
            txt_playlist_name.setText(model.getPlaylist_name());
            txt_videos_count.setText("Videos "+model.getVideos());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onitemClick(model);
                }
            });
        }
    }

    public interface OnitemClickListener{
        void onitemClick(PLaylistModel model);
    }
}
