package com.example.youtube;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youtube.Adapter.ViewPagerAdapter;
import com.example.youtube.Dashboard.AboutDashboard;
import com.example.youtube.Dashboard.HomeDashboard;
import com.example.youtube.Dashboard.PlaylistDashboard;
import com.example.youtube.Dashboard.SubscriptionDashboard;
import com.example.youtube.Dashboard.VideosDashboard;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class channelDashboardFragment extends Fragment {

    TextView user_channel_name;

    ViewPagerAdapter adapters;
    ViewPager viewPager;
    TabLayout tabLayout;


    public channelDashboardFragment(){

}


    public static channelDashboardFragment newInstance() {
        channelDashboardFragment fragment = new channelDashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_channel_dashboard, container, false);

        user_channel_name = view.findViewById(R.id.user_channel_name);

        tabLayout  = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        initAdapter();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("channels");
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("channel").getValue().toString();
                    user_channel_name.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    private void initAdapter() {
        adapters = new ViewPagerAdapter(getChildFragmentManager());
        adapters.add(new HomeDashboard(),"Home");
        adapters.add(new VideosDashboard(),"Videos");
        adapters.add(new PlaylistDashboard(),"Playlists");
        adapters.add(new AboutDashboard(),"About");
        adapters.add(new SubscriptionDashboard(),"Subscription");

        viewPager.setAdapter(adapters);
        tabLayout.setupWithViewPager(viewPager);


    }
}