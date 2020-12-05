package edu.uw.tcss450.groupchat.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import edu.uw.tcss450.groupchat.model.contacts.ContactsIncomingViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsMainViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsOutgoingViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsSearchViewModel;

/**
 * Fragment for Contact list tab of the Contact page.
 *
 * @version November 19, 2020
 */
public class ContactsHolderFragment extends Fragment {

    private ContactsMainViewModel mContactsModel;

    private ContactsIncomingViewModel mIncomingModel;

    private ContactsOutgoingViewModel mOutgoingModel;

    private ContactsSearchViewModel mSearchModel;

    private UserInfoViewModel mUserModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mContactsModel = provider.get(ContactsMainViewModel.class);
        mIncomingModel = provider.get(ContactsIncomingViewModel.class);
        mOutgoingModel = provider.get(ContactsOutgoingViewModel.class);
        mSearchModel = provider.get(ContactsSearchViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts_holder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager2 viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new ContactsHolderAdapter(this));

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 1:
                    tab.setText("Incoming");
                    break;
                case 2:
                    tab.setText("Outgoing");
                    break;
                case 3:
                    tab.setText("Search");
                    break;
                default:
                    tab.setText("Contacts");
                    break;
            }
            viewPager.setCurrentItem(tab.getPosition(), true);
        }).attach();

        mContactsModel.addResponseObserver(getViewLifecycleOwner(), response -> {
            if (response.length() > 0) {
                if (response.has("code")) {
                    try {
                        Log.e("Web Service Error",
                                response.getJSONObject("data").getString("message"));
                    } catch (JSONException e) {
                        Log.e("JSON Parse Error", e.getMessage());
                    }
                } else {
                    mContactsModel.connect(mUserModel.getJwt());
                    mSearchModel.connect(mUserModel.getJwt());
                }
            } else {
                Log.d("JSON Response", "No Response");
            }
        });

        mIncomingModel.addResponseObserver(getViewLifecycleOwner(), response -> {
            if (response.length() > 0) {
                if (response.has("code")) {
                    try {
                        Log.e("Web Service Error",
                                response.getJSONObject("data").getString("message"));
                    } catch (JSONException e) {
                        Log.e("JSON Parse Error", e.getMessage());
                    }
                } else {
                    mContactsModel.connect(mUserModel.getJwt());
                    mIncomingModel.connect(mUserModel.getJwt());
                    mSearchModel.connect(mUserModel.getJwt());
                }
            } else {
                Log.d("JSON Response", "No Response");
            }
        });

        mOutgoingModel.addResponseObserver(getViewLifecycleOwner(), response -> {
            if (response.length() > 0) {
                if (response.has("code")) {
                    try {
                        Log.e("Web Service Error",
                                response.getJSONObject("data").getString("message"));
                    } catch (JSONException e) {
                        Log.e("JSON Parse Error", e.getMessage());
                    }
                } else {
                    mOutgoingModel.connect(mUserModel.getJwt());
                    mSearchModel.connect(mUserModel.getJwt());
                }
            } else {
                Log.d("JSON Response", "No Response");
            }
        });

        mSearchModel.addResponseObserver(getViewLifecycleOwner(), response -> {
            if (response.length() > 0) {
                if (response.has("code")) {
                    try {
                        Log.e("Web Service Error",
                                response.getJSONObject("data").getString("message"));
                    } catch (JSONException e) {
                        Log.e("JSON Parse Error", e.getMessage());
                    }
                } else {
                    mOutgoingModel.connect(mUserModel.getJwt());
                    mSearchModel.connect(mUserModel.getJwt());
                }
            } else {
                Log.d("JSON Response", "No Response");
            }
        });
    }
}