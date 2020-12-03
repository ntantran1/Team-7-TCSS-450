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
import android.widget.SearchView;

import java.util.ArrayList;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentContactsSearchBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsSearchFragment extends Fragment {

    private FragmentContactsSearchBinding binding;

    private ContactListViewModel mModel;

    private UserInfoViewModel mUserModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(getActivity()).get(ContactListViewModel.class);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);

        mModel.connectSearch(mUserModel.getJwt(), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContactsSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.searchSwipeContainer.setRefreshing(true);

        final RecyclerView recyclerView = binding.searchListRoot;
        recyclerView.setAdapter(new ContactsRecyclerViewAdapter(new ArrayList<>(), mModel, mUserModel));

        binding.searchSwipeContainer.setOnRefreshListener(() ->
                mModel.connectSearch(mUserModel.getJwt(),
                        binding.searchUsers.getQuery().toString()));

        binding.searchUsers.setSubmitButtonEnabled(true);
        binding.searchUsers.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mModel.connectSearch(mUserModel.getJwt(), query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) mModel.connectSearch(mUserModel.getJwt(), newText);
                return false;
            }
        });

        mModel.addSearchedListObserver(getViewLifecycleOwner(), userList -> {
            ((ContactsRecyclerViewAdapter) recyclerView.getAdapter()).setList(userList);
            binding.searchSwipeContainer.setRefreshing(false);
        });
    }
}