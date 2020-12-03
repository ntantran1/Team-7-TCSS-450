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
            binding.textUsername.setText(contact.getUsername());
            binding.textName.setText(contact.getName());
            binding.textEmail.setText(contact.getEmail());

            switch (mContact.getType()) {
                case 1:
                    mView.setOnClickListener(this::contactPopup);
                    break;
                case 2:
                    mView.setOnClickListener(this::incomingPopup);
                    break;
                case 3:
                    mView.setOnClickListener(this::outgoingPopup);
                    break;
                case 4:
                    mView.setOnClickListener(this::searchPopup);
                    break;
                default:
                    Log.d("Contact Holder", "OnClickListener not set up properly");
                    break;
            }
        }

        private void contactPopup(final View view) {
            View popupView = LayoutInflater.from(view.getContext())
                    .inflate(R.layout.popup_contact_home, null);

            final PopupWindow popupWindow = new PopupWindow(popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true);
            popupWindow.setElevation(10);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            PopupContactHomeBinding popupBinding =
                    PopupContactHomeBinding.bind(popupView);

            popupBinding.labelContact.setText(mContact.getUsername());

            popupBinding.labelRemoveContact.setOnClickListener(click -> {
                mModel.connectRemove(mUserModel.getJwt(), mContact.getEmail());
                popupWindow.dismiss();
            });

            popupBinding.labelCancel.setOnClickListener(click ->
                    popupWindow.dismiss());
        }

        private void incomingPopup(final View view) {
            View popupView = LayoutInflater.from(view.getContext())
                    .inflate(R.layout.popup_contact_incoming, null);

            final PopupWindow popupWindow = new PopupWindow(popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true);
            popupWindow.setElevation(10);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            PopupContactIncomingBinding popupBinding =
                    PopupContactIncomingBinding.bind(popupView);

            popupBinding.labelContact.setText(mContact.getUsername());

            popupBinding.labelAcceptIncoming.setOnClickListener(click -> {
                mModel.connectAccept(mUserModel.getJwt(), mContact.getEmail());
                popupWindow.dismiss();
            });

            popupBinding.labelRejectIncoming.setOnClickListener(click -> {
                mModel.connectRemove(mUserModel.getJwt(), mContact.getEmail());
                popupWindow.dismiss();
            });

            popupBinding.labelCancel.setOnClickListener(click ->
                    popupWindow.dismiss());
        }

        private void outgoingPopup(final View view) {
            View popupView = LayoutInflater.from(view.getContext())
                    .inflate(R.layout.popup_contact_outgoing, null);

            final PopupWindow popupWindow = new PopupWindow(popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true);
            popupWindow.setElevation(10);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            PopupContactOutgoingBinding popupBinding =
                    PopupContactOutgoingBinding.bind(popupView);

            popupBinding.labelContact.setText(mContact.getUsername());

            popupBinding.labelRemoveOutgoing.setOnClickListener(click -> {
                mModel.connectRemove(mUserModel.getJwt(), mContact.getEmail());
                popupWindow.dismiss();
            });

            popupBinding.labelCancel.setOnClickListener(click ->
                    popupWindow.dismiss());
        }

        private void searchPopup(final View view) {
            View popupView = LayoutInflater.from(view.getContext())
                    .inflate(R.layout.popup_contact_search, null);

            final PopupWindow popupWindow = new PopupWindow(popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true);
            popupWindow.setElevation(10);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            PopupContactSearchBinding popupBinding =
                    PopupContactSearchBinding.bind(popupView);

            popupBinding.labelContact.setText(mContact.getUsername());

            popupBinding.labelAddContact.setOnClickListener(click -> {
                mModel.connectAdd(mUserModel.getJwt(), mContact.getEmail());
                popupWindow.dismiss();
            });

            popupBinding.labelCancel.setOnClickListener(click ->
                    popupWindow.dismiss());
        }
    }
}
