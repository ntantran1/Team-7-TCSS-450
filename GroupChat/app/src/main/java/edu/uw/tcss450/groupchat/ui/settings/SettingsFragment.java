package edu.uw.tcss450.groupchat.ui.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentSettingsBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass
 */
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    private UserInfoViewModel mUserViewModel;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserViewModel = (new ViewModelProvider(getActivity())).get(UserInfoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        switch (mUserViewModel.getTheme()) {
            case R.style.Theme_PurpleGold:
                binding.settingsColorPg.setChecked(true);
                break;
            case R.style.Theme_IndigoGreen:
                binding.settingsColorIg.setChecked(true);
                break;
            case R.style.Theme_GreyOrange:
                binding.settingsColorGo.setChecked(true);
                break;
        }

        return binding.getRoot();
    }
}