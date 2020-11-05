package edu.uw.tcss450.groupchat.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentContactsHomeBinding;

/**
 * Fragment for default page of Contacts.
 *
 * @version November 5
 */
public class ContactsHomeFragment extends Fragment {

    private ContactListViewModel mModel;

    private List<Contact> mContacts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(getActivity()).get(ContactListViewModel.class);

        //Create fake sample data for contacts
        mContacts = new ArrayList<>();
        mContacts.add(new Contact("sandrawildda", "Sandra Wild", "sandrawild@mail.com"));
        mContacts.add(new Contact("fernbsarown", "Fern Brown", "fernnyasb@mail.com"));
        mContacts.add(new Contact("reicsjnurn", "Eric Burch", "reicsjnurn@mail.com"));
        mContacts.add(new Contact("roooober", "Robert Clough", "roberto@mail.com"));
        mContacts.add(new Contact("eddienivens", "Eddit Nivens", "eddi@mail.com"));
        mContacts.add(new Contact("maedenna", "Mae Denny", "maedenna@mail.com"));
        mContacts.add(new Contact("lynnasc", "Lynn  Charles", "lynnasc@mail.com"));
        mContacts.add(new Contact("patricia", "Patricia Smith", "patricia@mail.com"));
        mContacts.add(new Contact("quincyistaken", "Quincy Wilson", "quinn@mail.com"));
        mContacts.add(new Contact("falex", "Alex Mullhall", "alexa@mail.com"));
        mContacts.add(new Contact("marcmiller", "Marc Mill", "marcmillerboiiiiiiiii@mail.com"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts_home, container, false);

        if (view instanceof RecyclerView) {
            ((RecyclerView) view).setAdapter(new ContactsRecyclerViewAdapter(mContacts));
        }
        return view;
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        FragmentContactsHomeBinding binding = FragmentContactsHomeBinding.bind(getView());
//
//        binding.listRoot.setAdapter(new ContactsRecyclerViewAdapter(mContacts));
//
////        mModel.addContactListObserver(getViewLifecycleOwner(), contactList -> {
////            if (!contactList.isEmpty()) {
////                binding.listRoot.setAdapter(new ContactsRecyclerViewAdapter(contactList));
////            }
////        });
//    }
}