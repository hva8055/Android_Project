package com.example.youtube.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.FileObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.youtube.Adapter.ContentAdapter;
import com.example.youtube.Models.ContentModel;
import com.example.youtube.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class    HomeFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ContentModel> list;
    ContentAdapter adapter;
    ImageView thumbnail;

    DatabaseReference reference;

    String url;

    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        reference = FirebaseDatabase.getInstance().getReference().child("Content");

        getAllVideos();


        return view;
    }

    private void getAllVideos() {
        list = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ContentModel model = dataSnapshot.getValue(ContentModel.class);
                        if (model != null) {
                            if (model.getPublisher() == null || model.getPublisher().isEmpty()) {
                                Log.e("FirebaseError", "Publisher ID is null or empty for content: " + model.getId());
                            } else {
                                list.add(model);
                            }
                        } else {
                            Log.e("FirebaseError", "ContentModel is null for snapshot: " + dataSnapshot.toString());
                        }
                    }

                    Collections.shuffle(list);

                    adapter = new ContentAdapter(getActivity(), list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}