package edu.uw.tcss450.groupchat.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.android.jwt.JWT;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentContactsHomeBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;

/**
 * Fragment for default page of Contacts.
 *
 * @version November 5
 */
public class ContactsHomeFragment extends Fragment {

    private ContactListViewModel mModel;

    private UserInfoViewModel mUserModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(getActivity()).get(ContactListViewModel.class);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);

        mModel.connectContacts(mUserModel.getJwt());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentContactsHomeBinding binding = FragmentContactsHomeBinding.bind(getView());

        binding.swipeContainer.setRefreshing(true);

        final RecyclerView recyclerView = binding.listRoot;
        recyclerView.setAdapter(new ContactsRecyclerViewAdapter(new ArrayList<>(), mModel, mUserModel));

        binding.swipeContainer.setOnRefreshListener(() ->
                mModel.connectContacts(mUserModel.getJwt()));

        mModel.addContactListObserver(getViewLifecycleOwner(), contactList -> {
            ((ContactsRecyclerViewAdapter) recyclerView.getAdapter()).setList(contactList);
            binding.swipeContainer.setRefreshing(false);
        });
    }
}