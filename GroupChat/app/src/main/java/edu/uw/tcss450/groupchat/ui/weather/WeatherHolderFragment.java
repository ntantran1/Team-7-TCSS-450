package edu.uw.tcss450.groupchat.ui.weather;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayoutMediator;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentWeatherHolderBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherHolderFragment extends Fragment {

    private FragmentWeatherHolderBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWeatherHolderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.viewPager.setAdapter(new WeatherHolderAdapter(this));
        binding.viewPager.setUserInputEnabled(false);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 1:
                    tab.setText("Searched");
                    break;
                case 2:
                    tab.setText("Map");
                    break;
                default:
                    tab.setText("Current");
                    break;
            }
            binding.viewPager.setCurrentItem(tab.getPosition(), true);
        }).attach();
    }
}