package edu.uw.tcss450.groupchat.ui.contacts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentContactCardBinding;

/**
 * The class describe how each Contact should look on the page and manage
 * the list of contacts.
 *
 * @version November 5
 */
public class ContactsRecyclerViewAdapter extends
        RecyclerView.Adapter<ContactsRecyclerViewAdapter.ContactViewHolder> {

    private final List<Contact> mContacts;

    /**
     * Constructor to initialize the list of contacts.
     *
     * @param items List of Contact objects
     */
    public ContactsRecyclerViewAdapter(List<Contact> items) {
        this.mContacts = items;
        System.out.println(mContacts);
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
            binding.textUsername.setText(contact.getUsername());
            binding.textName.setText(contact.getName());
            binding.textEmail.setText(contact.getEmail());
        }
    }
}
