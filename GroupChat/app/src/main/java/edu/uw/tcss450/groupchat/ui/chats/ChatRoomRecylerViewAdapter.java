package edu.uw.tcss450.groupchat.ui.chats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentChatCardBinding;


public class ChatRoomRecylerViewAdapter extends
        RecyclerView.Adapter<ChatRoomRecylerViewAdapter.ChatRoomViewHolder> {

    private final List<ChatRoom> mChatRooms;

    /**
     * Constructor to initialize the list of contacts.
     *
     * @param items List of Contact objects
     */
    public ChatRoomRecylerViewAdapter(List<ChatRoom> items) {
        this.mChatRooms = items;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatRoomViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_contact_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        holder.setChatRoom(mChatRooms.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(v.equals(holder.binding)){
                    Navigation.findNavController(v).
                            navigate(ChatsDisplayFragmentDirections.
                                    actionChatsDisplayFragmentToChatListFragment());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChatRooms.size();
    }

    /**
     * The class describe how each Contact should look on the page.
     *
     * @version November 5
     */
    public class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        /** The current View object of page. */
        public final View mView;

        /** Binding for view object */
        public FragmentChatCardBinding binding;

        private ChatRoom mChatRoom;

        /**
         * Initialize the ViewHolder.
         *
         * @param view current view context for page
         */
        public ChatRoomViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatCardBinding.bind(view);
        }

        /**
         *
         * @param chatRoom
         */
        void setChatRoom(final ChatRoom chatRoom) {
            mChatRoom = chatRoom;
            binding.textChatname.setText(chatRoom.getChatName());
        }
    }
}
