package edu.uw.tcss450.groupchat.ui.contacts;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentContactCardBinding;
import edu.uw.tcss450.groupchat.databinding.PopupContactHomeBinding;
import edu.uw.tcss450.groupchat.databinding.PopupContactIncomingBinding;
import edu.uw.tcss450.groupchat.databinding.PopupContactOutgoingBinding;
import edu.uw.tcss450.groupchat.databinding.PopupContactSearchBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;

/**
 * The class describe how each Contact should look on the page and manage
 * the list of contacts.
 *
 * @version November 5
 */
public class ContactsRecyclerViewAdapter extends
        RecyclerView.Adapter<ContactsRecyclerViewAdapter.ContactViewHolder> {

    private List<Contact> mContacts;

    private ContactListViewModel mModel;

    private UserInfoViewModel mUserModel;

    /**
     * Constructor to initialize the list of contacts.
     *
     * @param items List of Contact objects
     */
    public ContactsRecyclerViewAdapter(final List<Contact> items,
                                       final ContactListViewModel model,
                                       final UserInfoViewModel userModel) {
        this.mContacts = items;
        this.mModel = model;
        this.mUserModel = userModel;
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
     * The class describe how each Contact should look on the page.
     *
     * @version November 5
     */
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        /** The current View object of page. */
        public final View mView;

        /** Binding for view object */
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

            binding.imageAdd.setOnClickListener(click ->
                    mModel.connectAdd(mUserModel.getJwt(), mContact.getEmail()));

            binding.imageAccept.setOnClickListener(click ->
                    mModel.connectAccept(mUserModel.getJwt(), mContact.getEmail()));

            binding.imageReject.setOnClickListener(click ->
                    mModel.connectRemove(mUserModel.getJwt(), mContact.getEmail()));

            binding.imageRemove.setOnClickListener(click ->
                    mModel.connectRemove(mUserModel.getJwt(), mContact.getEmail()));

            switch (mContact.getType()) {
                case 1:
                    binding.imageAdd.setVisibility(View.INVISIBLE);
                    binding.imageAccept.setVisibility(View.INVISIBLE);
                    binding.imageReject.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    binding.imageAdd.setVisibility(View.INVISIBLE);
                    binding.imageRemove.setVisibility(View.INVISIBLE);
                    binding.imageChat.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    binding.imageAdd.setVisibility(View.INVISIBLE);
                    binding.imageAccept.setVisibility(View.INVISIBLE);
                    binding.imageReject.setVisibility(View.INVISIBLE);
                    binding.imageChat.setVisibility(View.INVISIBLE);
                    break;
                case 4:
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
    }
}
