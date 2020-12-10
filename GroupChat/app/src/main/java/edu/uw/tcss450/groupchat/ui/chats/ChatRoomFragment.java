package edu.uw.tcss450.groupchat.ui.chats;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentChatRoomBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;
import edu.uw.tcss450.groupchat.model.chats.ChatMembersViewModel;
import edu.uw.tcss450.groupchat.model.chats.ChatMessageViewModel;
import edu.uw.tcss450.groupchat.model.chats.ChatRoomViewModel;
import edu.uw.tcss450.groupchat.model.chats.ChatSendViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsMainViewModel;
import edu.uw.tcss450.groupchat.ui.contacts.Contact;

/**
 * Fragment Displays chat messages
 *
 * @version November 19 2020
 */
public class ChatRoomFragment extends Fragment {

    private UserInfoViewModel mUserModel;

    private ChatMessageViewModel mChatModel;

    private ChatSendViewModel mSendModel;

    private ChatRoomViewModel mRoomModel;

    private ContactsMainViewModel mContactModel;

    private ChatMembersViewModel mMembersModel;

    private ChatRoomFragmentArgs mRoomArgs;

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
        mRoomModel = provider.get(ChatRoomViewModel.class);
        mContactModel = provider.get(ContactsMainViewModel.class);
        mMembersModel = provider.get(ChatMembersViewModel.class);

        mRoomArgs = ChatRoomFragmentArgs.fromBundle(getArguments());
        mChatModel.getFirstMessages(mRoomArgs.getRoom().getId(), mUserModel.getJwt());
        mRoomModel.setCurrentRoom(mRoomArgs.getRoom().getId());
        mContactModel.connect(mUserModel.getJwt());
        mMembersModel.connect(mRoomArgs.getRoom().getId(), mUserModel.getJwt());

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

        binding.edittextChatbox.setKeyBoardInputCallbackListener((inputContentInfo, flags, opts) -> {
            // use image here
            //mSendModel.uploadImage(inputContentInfo.getLinkUri().toString());
            mSendModel.sendMessage(args.getRoom().getId(),
                    mUserModel.getJwt(),
                    inputContentInfo.getLinkUri().toString());
        });

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
        menu.findItem(R.id.action_chat_members).setVisible(true);
        menu.findItem(R.id.action_chat_add).setVisible(true);
        menu.findItem(R.id.action_chat_leave).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_chat_members) showMembers();
        else if(item.getItemId() == R.id.action_chat_add) addUserToChat();
        else if(item.getItemId() == R.id.action_chat_leave) leaveRoom();
        return super.onOptionsItemSelected(item);
    }

    private void showMembers() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chat Members");

        List<String> members = mMembersModel.getMembersListByChatId(mRoomArgs.getRoom().getId());
        String[] emails = new String[members.size()];
        emails = members.toArray(emails);
        builder.setItems(emails, (dlg, i) -> {
            //do nothing since getting overridden
        });

        builder.setPositiveButton("Done", (dlg, i) -> dlg.dismiss());

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getListView().setOnItemClickListener((p, v, i, id) -> {
            //do nothing on click
        });
    }

    private void addUserToChat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add from Contacts");

        List<String> contacts = new ArrayList<>();
        for(Contact contact: mContactModel.getContacts()){
            contacts.add(contact.getUsername());
        }
        String[] contactNames = contacts.toArray(new String[contacts.size()]);

        AtomicInteger selected = new AtomicInteger(-1);
        builder.setSingleChoiceItems(contactNames, selected.get(), (dlg, i) -> selected.set(i));

        builder.setPositiveButton("Add", (dlg, i) -> {
            String contactId = mContactModel.getContactFromUserName(contactNames[selected.get()]);
            String chatName = (String) Navigation.findNavController(getView())
                    .getCurrentDestination().getLabel();
            mRoomModel.connectAddToChat(mUserModel.getJwt(), contactId, mRoomModel.getCurrentRoom());
            mMembersModel.addMember(mRoomArgs.getRoom().getId(), contactId);
            Toast.makeText(getContext(), contactId + " has been added to " + chatName,
                    Toast.LENGTH_LONG).show();
        });

        builder.setNegativeButton("Cancel", (dlg, i) -> dlg.cancel());

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void leaveRoom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Leave Room?");

        builder.setPositiveButton("Leave", (dlg, i) -> {
            mRoomModel.connectLeave(mUserModel.getJwt(),
                    mRoomModel.getCurrentRoom(), mUserModel.getEmail());
            NavController navController = Navigation.findNavController(getView());
            String chatName = (String) navController.getCurrentDestination().getLabel();
            navController.navigate(ChatRoomFragmentDirections.
                    actionChatDisplayFragmentToNavigationChats());
            Toast.makeText(getContext(), "You left " + chatName, Toast.LENGTH_LONG).show();
        });

        builder.setNegativeButton("Cancel", (dlg, i) -> dlg.cancel());

        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}