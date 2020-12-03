package edu.uw.tcss450.groupchat.ui.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentChatCardBinding;

/**
 * The class describes how each chat room should look on the home page and manage the list of
 * chat rooms.
 *
 * @version November 27 2020
 */
public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.RoomViewHolder> {

    private List<ChatRoom> mRooms;

    /**
     * Constructor initialize list of rooms.
     *
     * @param items List of ChatRoom objects visible to the user.
     */
    public ChatListRecyclerViewAdapter(List<ChatRoom> items) {
        this.mRooms = items;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_chat_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        holder.setRoom(mRooms.get(position));
    }

    @Override
    public int getItemCount() {
        return mRooms.size();
    }

    /**
     * The class describes how each Chatroom should look on the page.
     *
     * @version November 19 2020
     */
    class RoomViewHolder extends RecyclerView.ViewHolder {

        private final View mView;

        private ChatRoom mRoom;

        private FragmentChatCardBinding binding;

        /**
         * Initialize the ViewHolder.
         *
         * @param view current view context for page
         */
        public RoomViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatCardBinding.bind(view);
        }

        /**
         * Initialize ChatRoom object and populate binding.
         *
         * @param room ChatRoom object
         */
        void setRoom(final ChatRoom room) {
            mRoom = room;
            binding.labelName.setText(room.getName());
            //when someone clicks on a chat, takes to that chat list
            mView.setOnClickListener(view -> {
                Navigation.findNavController(mView).getGraph().findNode(R.id.chatListFragment).setLabel(mRoom.getName());
                Navigation.findNavController(mView).navigate(
                        ChatsHomeFragmentDirections
                                .actionNavigationChatsToChatListFragment(mRoom));

            });


        }
    }
}
