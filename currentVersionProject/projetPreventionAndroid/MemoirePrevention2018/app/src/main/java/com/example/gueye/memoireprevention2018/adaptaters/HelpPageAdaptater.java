package com.example.gueye.memoireprevention2018.adaptaters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class HelpPageAdaptater extends FragmentPagerAdapter{

    private List<Fragment> fragments ;

    public HelpPageAdaptater(FragmentManager fm) {
        super(fm);
    }

    public HelpPageAdaptater(FragmentManager fm , List<Fragment> fragments){

        this(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
