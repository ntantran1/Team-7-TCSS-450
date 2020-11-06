package edu.uw.tcss450.groupchat.ui.chats;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.groupchat.R;

/**
 * Fragment for Home Page of the application.
 *
 * @version November 5
 */
public class ChatsHomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats_home, container, false);
    }
}