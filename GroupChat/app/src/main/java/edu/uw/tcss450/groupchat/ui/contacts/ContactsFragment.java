package edu.uw.tcss450.groupchat.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import edu.uw.tcss450.groupchat.R;

/**
 * Fragment for Contact list tab of the Contact page.
 *
 * @version November 19, 2020
 */
public class ContactsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager2 vp = view.findViewById(R.id.view_pager);
        vp.setAdapter(new ContactsFragmentAdapter(this));

        TabLayout tl = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tl, vp, (tab, position) -> {
            if (position == 1) {
                tab.setText("Requests");
            } else {
                tab.setText("Contacts");
            }
            vp.setCurrentItem(tab.getPosition(), true);
        }).attach();
    }
}