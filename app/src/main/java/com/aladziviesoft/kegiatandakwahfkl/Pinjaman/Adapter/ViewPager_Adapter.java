package com.aladziviesoft.kegiatandakwahfkl.Pinjaman.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPager_Adapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> tittlelist = new ArrayList<>();

    public ViewPager_Adapter(FragmentManager fm) {
        super(fm);
    }

    public void add(Fragment fragment, String tittle) {
        fragmentList.add(fragment);
        tittlelist.add(tittle);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public CharSequence getPageTitle(int i) {
        return tittlelist.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
