package com.example.youtube.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.youtube.Dashboard.AboutDashboard;
import com.example.youtube.Dashboard.HomeDashboard;
import com.example.youtube.Dashboard.PlaylistDashboard;
import com.example.youtube.Dashboard.SubscriptionDashboard;
import com.example.youtube.Dashboard.VideosDashboard;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> strings = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm){
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeDashboard();
            case 1:
                return new VideosDashboard();
            case 2:
                return new PlaylistDashboard();
            case 3:
                return new AboutDashboard();
            case 4:
                return new SubscriptionDashboard();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
    public void add(Fragment fr, String sr){
        fragments.add(fr);
        strings.add(sr);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return strings.get(position);
    }
}
