package edu.uw.tcss450.groupchat.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.util.ArrayList;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentContactsIncomingBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsIncomingFragment extends Fragment {

    private ContactListViewModel mModel;

    private UserInfoViewModel mUserModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(getActivity()).get(ContactListViewModel.class);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);

        mModel.connectIncoming(mUserModel.getJwt());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts_incoming, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentContactsIncomingBinding binding = FragmentContactsIncomingBinding.bind(getView());

        binding.incomingSwipeContainer.setRefreshing(true);

        final RecyclerView recyclerView = binding.incomingListRoot;
        recyclerView.setAdapter(new ContactsRecyclerViewAdapter(new ArrayList<>(), mModel, mUserModel));

        binding.incomingSwipeContainer.setOnRefreshListener(() ->
                mModel.connectIncoming(mUserModel.getJwt()));

        mModel.addIncomingListObserver(getViewLifecycleOwner(), incomingList -> {
            ((ContactsRecyclerViewAdapter) recyclerView.getAdapter()).setList(incomingList);
            binding.incomingSwipeContainer.setRefreshing(false);
        });
    }
}