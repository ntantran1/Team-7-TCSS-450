package edu.uw.tcss450.groupchat.ui.chats;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentChatsHomeBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;
import edu.uw.tcss450.groupchat.ui.contacts.ContactListViewModel;

/**
 * Fragment for Home Page of the application.
 *
 * @version November 5
 */
public class ChatsHomeFragment extends Fragment {
    private ChatViewModel mChatModel;

    private UserInfoViewModel mUserModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mChatModel = provider.get(ChatViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);

        mChatModel.connectRooms(mUserModel.getJwt());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentChatsHomeBinding binding = FragmentChatsHomeBinding.bind(getView());

        binding.swipeContainer.setRefreshing(true);

        final RecyclerView rv = binding.listRoot;
        rv.setAdapter(new ChatListRecyclerViewAdapter(new ArrayList<>()));

        binding.swipeContainer.setOnRefreshListener(() ->
                mChatModel.connectRooms(mUserModel.getJwt()));

        mChatModel.addRoomsObserver(getViewLifecycleOwner(), rooms -> {
            rv.setAdapter(new ChatListRecyclerViewAdapter(rooms));
            binding.swipeContainer.setRefreshing(false);
        });
    }
}