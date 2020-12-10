package edu.uw.tcss450.groupchat.ui.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.os.BuildCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentChatRoomBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;
import edu.uw.tcss450.groupchat.model.chats.ChatMessageViewModel;
import edu.uw.tcss450.groupchat.model.chats.ChatRoomViewModel;
import edu.uw.tcss450.groupchat.model.chats.ChatSendViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsMainViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsViewModel;
import edu.uw.tcss450.groupchat.ui.contacts.Contact;

/**
 * Fragment Displays chat messages
 *
 * @version November 19 2020
 */
public class ChatRoomFragment extends Fragment {

    private ChatMessageViewModel mChatModel;

    private ChatSendViewModel mSendModel;

    private UserInfoViewModel mUserModel;

    private ContactsMainViewModel mContactViewModel;

    private ChatRoomViewModel mRoomModel;



    /**
     * Empty default constructor.
     */
    public ChatRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mChatModel = provider.get(ChatMessageViewModel.class);
        mSendModel = provider.get(ChatSendViewModel.class);
        mContactViewModel = provider.get(ContactsMainViewModel.class);
        mRoomModel = provider.get(ChatRoomViewModel.class);

        ChatRoomFragmentArgs args = ChatRoomFragmentArgs.fromBundle(getArguments());
        mChatModel.getFirstMessages(args.getRoom().getId(), mUserModel.getJwt());
        mRoomModel.setCurrentRoom(args.getRoom().getId());
        mContactViewModel.connect(mUserModel.getJwt());

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ChatRoomFragmentArgs args = ChatRoomFragmentArgs.fromBundle(getArguments());

        FragmentChatRoomBinding binding = FragmentChatRoomBinding.bind(getView());


        binding.edittextChatbox.setKeyBoardInputCallbackListener(new ChatEditText.KeyBoardInputCallbackListener() {
            @Override
            public void onCommitContent(InputContentInfoCompat inputContentInfo,
                                        int flags, Bundle opts) {
                // use image here
                //mSendModel.uploadImage(inputContentInfo.getLinkUri().toString());
                mSendModel.sendMessage(args.getRoom().getId(),
                        mUserModel.getJwt(),
                        inputContentInfo.getLinkUri().toString());
            }
        });


//        ChatRoomViewModel roomModel = new ViewModelProvider(getActivity()).get(ChatRoomViewModel.class);
//        roomModel.setCurrentRoom(args.getRoom().getId());

        //SetRefreshing shows the internal Swiper view progress bar. Show this until messages load
        binding.swipeContainer.setRefreshing(true);

        final RecyclerView rv = binding.recyclerviewChatDisplay;

        //Set the Adapter to hold a reference to the list FOR THIS chat ID that the ViewModel holds.
        rv.setAdapter(new ChatMessageRecyclerViewAdapter(
                mChatModel.getMessageListByChatId(args.getRoom().getId()),
                mUserModel.getEmail()));


        //When the user scrolls to the top of the RV, the swiper list will "refresh"
        //The user is out of messages, go out to the service and get more
        binding.swipeContainer.setOnRefreshListener(() -> {
            mChatModel.getNextMessages(args.getRoom().getId(), mUserModel.getJwt());
        });

        mChatModel.addMessageObserver(args.getRoom().getId(), getViewLifecycleOwner(),
                list -> {
                    /*
                     * This solution needs work on the scroll position. As a group,
                     * you will need to come up with some solution to manage the
                     * recyclerview scroll position. You also should consider a
                     * solution for when the keyboard is on the screen.
                     */
                    //inform the RV that the underlying list has (possibly) changed
                    rv.getAdapter().notifyDataSetChanged();
                    rv.scrollToPosition(rv.getAdapter().getItemCount() - 1);
                    binding.swipeContainer.setRefreshing(false);
                });
        //Send button click -> send message via SendViewModel
        binding.buttonChatboxSend.setOnClickListener(button -> {
            mSendModel.sendMessage(args.getRoom().getId(),
                    mUserModel.getJwt(),
                    binding.edittextChatbox.getText().toString());
        });
        //when we get response back from server, clear edit text
        mSendModel.addResponseObserver(getViewLifecycleOwner(), response -> {
            binding.edittextChatbox.setText("");

            InputMethodManager manager =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(binding.edittextChatbox.getWindowToken(), 0);
        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.chatOptionsAdd).setVisible(true);
        menu.findItem(R.id.chatOptionsRemove).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.chatOptionsAdd){
            addUserToChat();
        } else if(item.getItemId() == R.id.chatOptionsRemove){
            leaveRoom();

        }
        return super.onOptionsItemSelected(item);
    }

    private void addUserToChat(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add from Contacts");

        List<String> contacts = new ArrayList<>();
        for(Contact contact: mContactViewModel.getContacts()){
            contacts.add(contact.getUsername());
        }
        String[] contactNames = contacts.toArray(new String[contacts.size()]);

        AtomicInteger selected = new AtomicInteger(-1);
        builder.setSingleChoiceItems(contactNames, selected.get(), (dlg, i) -> selected.set(i));

        builder.setPositiveButton("Add", (dlg, i) -> {
           String contactId = mContactViewModel.getContactFromUserName(contactNames[selected.get()]);
           mRoomModel.connectAddToChat(mUserModel.getJwt(), contactId, mRoomModel.getCurrentRoom());
            Toast.makeText(getContext(), contactId + " has been added to chat",
                    Toast.LENGTH_LONG);
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void leaveRoom(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Leave Room?");

        builder.setPositiveButton("Leave", (dlg, i) -> {
            mRoomModel.requestLeaveRoom(mUserModel.getJwt(),
                    mRoomModel.getCurrentRoom(), mUserModel.getEmail());
            NavController navController = Navigation.findNavController(getView());
            navController.navigate(ChatRoomFragmentDirections.
                    actionChatDisplayFragmentToNavigationChats());
            Toast.makeText(getContext(), "You left " + mRoomModel.getCurrentRoom(),
                    Toast.LENGTH_LONG);
        });

        builder.setNegativeButton("Cancel", (dlg, i) -> dlg.cancel());

        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}