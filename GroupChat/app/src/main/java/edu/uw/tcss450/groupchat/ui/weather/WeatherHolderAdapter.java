package edu.uw.tcss450.groupchat.ui.weather;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class WeatherHolderAdapter extends FragmentStateAdapter {

    private List<Fragment> mPages;

    public WeatherHolderAdapter(Fragment fragment) {
        super(fragment);
        mPages = new ArrayList<>();
        mPages.add(new WeatherMainFragment());
        mPages.add(new WeatherSearchFragment());
        mPages.add(new WeatherMapFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mPages.get(position);
    }

    @Override
    public int getItemCount() {
        return mPages.size();
    }
}
