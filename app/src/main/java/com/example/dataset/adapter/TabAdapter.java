package com.example.dataset.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.dataset.tab.AvailableFragment;
import com.example.dataset.tab.HelpFragment;
import com.example.dataset.tab.RequestFragment;


public class TabAdapter extends FragmentStatePagerAdapter {

    int tabCount;

    public TabAdapter(@NonNull FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0:{
                return new AvailableFragment();
            }
            case 1:{
                return new RequestFragment();
            }
            case 2:{
                return new HelpFragment();
            }
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return tabCount;
    }
}
