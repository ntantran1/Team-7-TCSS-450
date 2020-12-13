package edu.uw.tcss450.groupchat.ui.contacts;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentContactCardBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;
import edu.uw.tcss450.groupchat.model.chats.ChatRoomViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsIncomingViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsMainViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsOutgoingViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsSearchViewModel;
import edu.uw.tcss450.groupchat.ui.chats.ChatRoom;

/**
 * The class describe how each Contact should look on the page and manage
 * the list of contacts.
 *
 * @version November 5
 */
public class ContactsRecyclerViewAdapter extends
        RecyclerView.Adapter<ContactsRecyclerViewAdapter.ContactViewHolder> {

    private List<Contact> mContacts;

    private ContactsMainViewModel mContactsModel;

    private ContactsIncomingViewModel mIncomingModel;

    private ContactsOutgoingViewModel mOutgoingModel;

    private ContactsSearchViewModel mSearchModel;

    private ChatRoomViewModel mChatRoomModel;

    private UserInfoViewModel mUserModel;

    private FragmentActivity mActivity;

    /**
     * Constructor to initialize the list of contacts.
     *
     * @param items List of Contact objects
     */
    public ContactsRecyclerViewAdapter(final List<Contact> items,
                                       final FragmentActivity activity) {
        mContacts = items;
        mActivity = activity;

        ViewModelProvider provider = new ViewModelProvider(activity);
        mContactsModel = provider.get(ContactsMainViewModel.class);
        mIncomingModel = provider.get(ContactsIncomingViewModel.class);
        mOutgoingModel = provider.get(ContactsOutgoingViewModel.class);
        mSearchModel = provider.get(ContactsSearchViewModel.class);
        mChatRoomModel = provider.get(ChatRoomViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);

        mChatRoomModel.connect(mUserModel.getJwt());
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_contact_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.setContact(mContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public void setList(final List<Contact> items) {
        this.mContacts = items;
        notifyDataSetChanged();
    }

    /**
     * Describes how each Contact should look on the page and provides
     * initialization of interactions.
     *
     * @version December 4, 2020
     */
    public class ContactViewHolder extends RecyclerView.ViewHolder {

        /** The current View object of the page. */
        public final View mView;

        /** The ViewBinding for view object */
        public FragmentContactCardBinding binding;

        private Contact mContact;

        /**
         * Initialize the ViewHolder.
         *
         * @param view current view context for page
         */
        public ContactViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentContactCardBinding.bind(view);
        }

        /**
         * Initialize Contact object and populate binding.
         *
         * @param contact Contact object
         */
        void setContact(final Contact contact) {
            mContact = contact;
            binding.textUsername.setText(mContact.getUsername());
            binding.textName.setText(mContact.getName());
            binding.textEmail.setText(mContact.getEmail());

            final String jwt = mUserModel.getJwt();
            final String name = mContact.getUsername();

            switch (mContact.getType()) {
                case 1:
                    binding.imageChat.setOnClickListener(this::addUserToChat);
                    binding.imageRemove.setOnClickListener(click -> {
                        mContactsModel.connectRemove(jwt, name);
                        Snackbar snack = Snackbar.make(mView, "Removed " + name + " from contacts",
                                Snackbar.LENGTH_LONG);
                        snack.getView().findViewById(com.google.android.material.R.id.snackbar_text)
                                .setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        snack.show();
                    });
                    binding.imageAdd.setVisibility(View.INVISIBLE);
                    binding.imageAccept.setVisibility(View.INVISIBLE);
                    binding.imageReject.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    binding.imageAccept.setOnClickListener(click -> {
                        mIncomingModel.connectAccept(jwt, name);
                        Snackbar snack = Snackbar.make(mView, "Accepted " + name + "'s request",
                                Snackbar.LENGTH_LONG);
                        snack.getView().findViewById(com.google.android.material.R.id.snackbar_text)
                                .setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        snack.show();
                    });
                    binding.imageReject.setOnClickListener(click -> {
                        mIncomingModel.connectReject(jwt, name);
                        Snackbar snack = Snackbar.make(mView, "Rejected " + name + "'s request",
                                Snackbar.LENGTH_LONG);
                        snack.getView().findViewById(com.google.android.material.R.id.snackbar_text)
                                .setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        snack.show();
                    });
                    binding.imageAdd.setVisibility(View.INVISIBLE);
                    binding.imageRemove.setVisibility(View.INVISIBLE);
                    binding.imageChat.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    binding.imageRemove.setOnClickListener(click -> {
                        mOutgoingModel.connectCancel(jwt, name);
                        Snackbar snack = Snackbar.make(mView, "Removed request to " + name,
                                Snackbar.LENGTH_LONG);
                        snack.getView().findViewById(com.google.android.material.R.id.snackbar_text)
                                .setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        snack.show();
                    });
                    binding.imageAdd.setVisibility(View.INVISIBLE);
                    binding.imageAccept.setVisibility(View.INVISIBLE);
                    binding.imageReject.setVisibility(View.INVISIBLE);
                    binding.imageChat.setVisibility(View.INVISIBLE);
                    break;
                case 4:
                    binding.imageAdd.setOnClickListener(click -> {
                        mSearchModel.connectAdd(jwt, name);
                        Snackbar snack = Snackbar.make(mView, "Sent request to " + name,
                                Snackbar.LENGTH_LONG);
                        snack.getView().findViewById(com.google.android.material.R.id.snackbar_text)
                                .setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        snack.show();
                    });
                    binding.imageAccept.setVisibility(View.INVISIBLE);
                    binding.imageReject.setVisibility(View.INVISIBLE);
                    binding.imageRemove.setVisibility(View.INVISIBLE);
                    binding.imageChat.setVisibility(View.INVISIBLE);
                    break;
                default:
                    Log.d("Contact Holder", "OnClickListener not set up properly");
                    break;
            }
        }

        private void addUserToChat(View view) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
            dialog.setTitle("Add to Chat Room");

            List<String> rooms = new ArrayList<>();
            for (ChatRoom room : mChatRoomModel.getRooms()) {
                rooms.add(room.getName());
            }
            String[] roomNames = rooms.toArray(new String[rooms.size()]);

            AtomicInteger selected = new AtomicInteger(-1);
            dialog.setSingleChoiceItems(roomNames, selected.get(), (dlg, i) -> selected.set(i));

            dialog.setPositiveButton("Add", (dlg, i) -> {
                int roomId = mChatRoomModel.getRoomFromName(roomNames[selected.get()]);
                mContactsModel.connectAdd(mUserModel.getJwt(), mContact.getUsername(), roomId);
                Snackbar snack = Snackbar.make(mView, mContact.getUsername() + "has been added to "
                         + roomNames[selected.get()], Snackbar.LENGTH_LONG);
                snack.getView().findViewById(com.google.android.material.R.id.snackbar_text)
                        .setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                snack.show();
                dlg.dismiss();
            });

            dialog.setNegativeButton("Cancel", (dlg, i) -> dlg.dismiss());

            dialog.show();
        }
    }
}
