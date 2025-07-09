package com.example.showmethemoney;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class GrafikPagerAdapter extends FragmentStateAdapter {

    public GrafikPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LineChartFragment();
        } else {
            return new PieChartFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // dua tab
    }
}

