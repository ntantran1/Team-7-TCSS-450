package edu.uw.tcss450.groupchat.ui.chats;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentChatsHomeBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;

/**
 * Fragment for Home Page of the application.
 *
 * @version November 5
 */
public class ChatsHomeFragment extends Fragment implements View.OnClickListener {
    private ChatViewModel mChatModel;

    private UserInfoViewModel mUserModel;

    private ChatRoomStartViewModel mNewChatModel;

    private FragmentChatsHomeBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mChatModel = provider.get(ChatViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);
        mNewChatModel = provider.get(ChatRoomStartViewModel.class);

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

        binding = FragmentChatsHomeBinding.bind(getView());

        binding.swipeContainer.setRefreshing(true);

        final RecyclerView rv = binding.listRoot;
        rv.setAdapter(new ChatHomeRecyclerViewAdapter(new ArrayList<>()));

        binding.swipeContainer.setOnRefreshListener(() ->
                mChatModel.connectRooms(mUserModel.getJwt()));

        mChatModel.addRoomsObserver(getViewLifecycleOwner(), rooms -> {
            rv.setAdapter(new ChatHomeRecyclerViewAdapter(rooms));
            binding.swipeContainer.setRefreshing(false);
        });

        binding.buttonStartChatRoom.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        if(v == binding.buttonStartChatRoom){
            AlertDialog.Builder mdialog = new AlertDialog.Builder(getContext());
            mdialog.setTitle("Start New Chat!");

            final EditText chatName = new EditText(getContext());
            mdialog.setView(chatName);

            mdialog.setPositiveButton("Start", (dialog, i) -> {
                mNewChatModel.startNewChatRoom(mUserModel.getJwt(),
                        chatName.getText().toString());

                mNewChatModel.addRoomRequestObserver(getViewLifecycleOwner(), response -> dialog.dismiss());
            });

            mdialog.setNegativeButton("Close", (dialog, i) -> dialog.cancel());

            mdialog.show();
        }
    }
}