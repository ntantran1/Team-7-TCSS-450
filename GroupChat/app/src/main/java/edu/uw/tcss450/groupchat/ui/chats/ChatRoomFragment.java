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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

        // define the action for the interface class
        binding.edittextChatbox.setKeyBoardInputCallbackListener((inputContentInfo, flags, opts) -> {
            // use image here
            try {
                InputStream iStream = getContext().getContentResolver().openInputStream(inputContentInfo.getContentUri());
                byte[] inputData = getBytes(iStream);
                mSendModel.uploadImage(inputData, args.getRoom().getId(), mUserModel.getJwt());

            } catch (IOException e) {
                System.out.println(e);
            }

        });

        //SetRefreshing shows the internal Swiper view progress bar. Show this until messages load
        binding.swipeContainer.setRefreshing(true);

        final RecyclerView rv = binding.recyclerviewChatDisplay;

        //Set the Adapter to hold a reference to the list FOR THIS chat ID that the ViewModel holds.
        rv.setAdapter(new ChatMessageRecyclerViewAdapter(
                mChatModel.getMessageListByChatId(args.getRoom().getId()),
                mUserModel.getEmail()));

        AtomicInteger numMessages = new AtomicInteger(0);

        //When the user scrolls to the top of the RV, the swiper list will "refresh"
        //The user is out of messages, go out to the service and get more
        binding.swipeContainer.setOnRefreshListener(() -> {
            numMessages.set(rv.getAdapter().getItemCount());
            mChatModel.getNextMessages(args.getRoom().getId(), mUserModel.getJwt());
        });

        mChatModel.addMessageObserver(args.getRoom().getId(), getViewLifecycleOwner(), list -> {
            int last = ((LinearLayoutManager) rv.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            int dif = rv.getAdapter().getItemCount() - numMessages.get();
            //inform the RV that the underlying list has (possibly) changed
            rv.getAdapter().notifyDataSetChanged();
            if (list.size() == 0) {
                rv.scrollToPosition(0);
            } else if (list.get(list.size() - 1).getSender().equals(mUserModel.getEmail())
                    || rv.getAdapter().getItemCount() <= 15 || last > 14) {
                rv.scrollToPosition(rv.getAdapter().getItemCount() - 1);
            } else {
                rv.scrollToPosition(last + dif);
            }
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

    /**
     * Show a list of members who are part of a chatroom
     * via alert dialog
     */
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

    /**
     * Prompt to add contact to a chat via alert dialog
     */
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
            // do nothing because it's going to be overridden
        });

        builder.setNegativeButton("Cancel", (dlg, i) -> dlg.cancel());

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String email = mContactModel.getContactFromUserName(contactNames[selected.get()]);
            mRoomModel.connectAddToChat(mUserModel.getJwt(), contactNames[selected.get()],
                    mRoomModel.getCurrentRoom());

            mRoomModel.addResponseObserver(getViewLifecycleOwner(), response -> {
                if (response.length() > 0) {
                    if (response.has("code")) {
                        try {
                            Snackbar snack = Snackbar.make(view,
                                    response.getJSONObject("data").getString("message"),
                                    Snackbar.LENGTH_LONG);
                            snack.getView().findViewById(com.google.android.material.R.id.snackbar_text)
                                    .setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            snack.show();
                        } catch (JSONException e) {
                            Log.e("JSON Parse Error", e.getMessage());
                        }
                    } else {
                        mMembersModel.addMember(mRoomArgs.getRoom().getId(), email);
                        String chatName = (String) Navigation.findNavController(getView())
                                .getCurrentDestination().getLabel();
                        Snackbar snack = Snackbar.make(getView(), email + " has been added to "
                                + chatName, Snackbar.LENGTH_LONG);
                        snack.getView().findViewById(com.google.android.material.R.id.snackbar_text)
                                .setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        snack.show();
                        dialog.dismiss();
                    }
                } else {
                    Log.d("JSON Response", "No Response");
                }
            });
        });
    }

    /**
     * Method deletes the user from chatroom
     */
    private void leaveRoom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Leave Room?");

        builder.setPositiveButton("Leave", (dlg, i) -> {
            mRoomModel.connectLeave(mUserModel.getJwt(), mRoomModel.getCurrentRoom());
            NavController navController = Navigation.findNavController(getView());
            String chatName = (String) navController.getCurrentDestination().getLabel();
            navController.navigate(ChatRoomFragmentDirections.
                    actionChatDisplayFragmentToNavigationChats());
            Snackbar snack = Snackbar.make(getView(), "You left " + chatName, Snackbar.LENGTH_LONG);
            snack.getView().findViewById(com.google.android.material.R.id.snackbar_text)
                    .setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            snack.show();
        });

        builder.setNegativeButton("Cancel", (dlg, i) -> dlg.cancel());

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
