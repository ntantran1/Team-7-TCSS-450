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

public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.RoomViewHolder> {

    private List<ChatRoom> mRooms;

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

    class RoomViewHolder extends RecyclerView.ViewHolder {

        private final View mView;

        private ChatRoom mRoom;

        private FragmentChatCardBinding binding;

        public RoomViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatCardBinding.bind(view);
        }

        void setRoom(final ChatRoom room) {
            mRoom = room;;
            binding.labelName.setText(room.getName());
            mView.setOnClickListener(view -> {
                Navigation.findNavController(mView).navigate(
                        ChatsHomeFragmentDirections
                                .actionNavigationChatsToChatListFragment(room));
            });
        }
    }
}
