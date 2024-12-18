package com.example.youtube;

import android.app.appsearch.SearchResult;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtube.Adapter.SearchAdapter;
import com.example.youtube.Models.VideoModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchField;
    private RecyclerView searchResultsRecyclerView;
    private SearchAdapter searchAdapter;
    private DatabaseReference databaseReference;
    private List<VideoModel> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchField = findViewById(R.id.search_field);
        searchResultsRecyclerView = findViewById(R.id.recycler_view123);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        videoList = new ArrayList<>();
        searchAdapter = new SearchAdapter(videoList);
        searchResultsRecyclerView.setAdapter(searchAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("content");

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchVideos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchVideos(String query) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("content");
        Query searchQuery = databaseReference.orderByChild("video_title").startAt(query).endAt(query + "\uf8ff");

        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<SearchResult> searchResults = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve video details
                    String videoTitle = snapshot.child("video_title").getValue(String.class);
                    String videoDescription = snapshot.child("video_description").getValue(String.class);
                    String videoUrl = snapshot.child("video_url").getValue(String.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchActivity.this, "Search failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

