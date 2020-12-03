package edu.uw.tcss450.groupchat.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;

/**
 * Fragment for Contact list tab of the Contact page.
 *
 * @version November 19, 2020
 */
public class ContactsFragment extends Fragment {

    private ContactListViewModel mModel;

    private UserInfoViewModel mUserModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(getActivity()).get(ContactListViewModel.class);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager2 viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new ContactsFragmentAdapter(this));

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 1) {
                tab.setText("Incoming");
            } else if (position == 2) {
                tab.setText("Outgoing");
            } else if (position == 3) {
                tab.setText("Search");
            } else {
                tab.setText("Contacts");
            }
            viewPager.setCurrentItem(tab.getPosition(), true);
        }).attach();

        mModel.addResponseObserver(getViewLifecycleOwner(), response -> {
            if (response.length() > 0) {
                if (response.has("code")) {
                    try {
                        Log.e("Web Service Error",
                                response.getJSONObject("data").getString("message"));
                    } catch (JSONException e) {
                        Log.e("JSON Parse Error", e.getMessage());
                    }
                } else {
                    mModel.connectContacts(mUserModel.getJwt());
                    mModel.connectIncoming(mUserModel.getJwt());
                    mModel.connectOutgoing(mUserModel.getJwt());
                    mModel.connectSearch(mUserModel.getJwt(), "");
                }
            } else {
                Log.d("JSON Response", "No Response");
            }
        });
    }
}