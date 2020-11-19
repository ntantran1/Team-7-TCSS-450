package edu.uw.tcss450.groupchat.ui.chats;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentChatsDisplayBinding;
import edu.uw.tcss450.groupchat.databinding.FragmentContactsHomeBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;
import edu.uw.tcss450.groupchat.ui.contacts.ContactListViewModel;
import edu.uw.tcss450.groupchat.ui.contacts.ContactsRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsDisplayFragment extends Fragment {

    private ChatViewModel mChatModel;
    private ContactListViewModel mContacts;
    private UserInfoViewModel mUser;



    public ChatsDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mModel.connectGet(mUserModel.getJwt());
        mChatModel = new ViewModelProvider(getActivity()).get(ChatViewModel.class);
        mContacts = new ViewModelProvider(getActivity()).get(ContactListViewModel.class);
        mUser = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);

        mContacts.connectGet(mUser.getJwt());
       //TODO need to access chatRoomID something like
       // mChatModel.getChatRoomID


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentChatsDisplayBinding binding = FragmentChatsDisplayBinding.bind(getView());

        binding.swipeContainer.setRefreshing(true);

        final RecyclerView rv = binding.listRoot;
        rv.setAdapter(new ContactsRecyclerViewAdapter(new ArrayList<>()));

        binding.swipeContainer.setOnRefreshListener(() -> mContacts.connectGet(mUser.getJwt()));

        mContacts.addContactListObserver(getViewLifecycleOwner(), contactList -> {
            rv.setAdapter(new ContactsRecyclerViewAdapter(contactList));
            binding.swipeContainer.setRefreshing(false);
        });
    }
}