package edu.uw.tcss450.groupchat.ui.contacts;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentContactCardBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsIncomingViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsMainViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsOutgoingViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsSearchViewModel;

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

    private UserInfoViewModel mUserModel;

    /**
     * Constructor to initialize the list of contacts.
     *
     * @param items List of Contact objects
     */
    public ContactsRecyclerViewAdapter(final List<Contact> items,
                                       final FragmentActivity activity) {
        this.mContacts = items;

        ViewModelProvider provider = new ViewModelProvider(activity);
        mContactsModel = provider.get(ContactsMainViewModel.class);
        mIncomingModel = provider.get(ContactsIncomingViewModel.class);
        mOutgoingModel = provider.get(ContactsOutgoingViewModel.class);
        mSearchModel = provider.get(ContactsSearchViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);
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
            binding.textUsername.setText(contact.getUsername());
            binding.textName.setText(contact.getName());
            binding.textEmail.setText(contact.getEmail());

            final String jwt = mUserModel.getJwt();
            final String email = contact.getEmail();

            switch (contact.getType()) {
                case 1:
                    binding.imageRemove.setOnClickListener(click ->
                            mContactsModel.connectRemove(jwt, email));
                    binding.imageAdd.setVisibility(View.INVISIBLE);
                    binding.imageAccept.setVisibility(View.INVISIBLE);
                    binding.imageReject.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    binding.imageAccept.setOnClickListener(click ->
                            mIncomingModel.connectAccept(jwt, email));
                    binding.imageReject.setOnClickListener(click ->
                            mIncomingModel.connectReject(jwt, email));
                    binding.imageAdd.setVisibility(View.INVISIBLE);
                    binding.imageRemove.setVisibility(View.INVISIBLE);
                    binding.imageChat.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    binding.imageRemove.setOnClickListener(click ->
                            mOutgoingModel.connectCancel(jwt, email));
                    binding.imageAdd.setVisibility(View.INVISIBLE);
                    binding.imageAccept.setVisibility(View.INVISIBLE);
                    binding.imageReject.setVisibility(View.INVISIBLE);
                    binding.imageChat.setVisibility(View.INVISIBLE);
                    break;
                case 4:
                    binding.imageAdd.setOnClickListener(click ->
                            mSearchModel.connectAdd(jwt, email));
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
