package edu.uw.tcss450.groupchat.ui.chats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentChatDetailedBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;
import edu.uw.tcss450.groupchat.model.chats.ChatMessageViewModel;
import edu.uw.tcss450.groupchat.model.chats.ChatNotificationsViewModel;
import edu.uw.tcss450.groupchat.ui.HomeFragmentDirections;

public class ChatDetailedRecyclerViewAdapter extends
        RecyclerView.Adapter<ChatDetailedRecyclerViewAdapter.DetailedViewHolder> {

    private List<ChatRoom> mRooms;

    private List<ChatMessage> mMessages;

    private FragmentActivity mActivity;

    private ChatNotificationsViewModel mNewChatModel;

    private ChatMessageViewModel mMessageModel;

    private UserInfoViewModel mUserModel;

    public ChatDetailedRecyclerViewAdapter(Map<ChatRoom, ChatMessage> chats, FragmentActivity activity) {
        mRooms = new ArrayList<>();
        mMessages = new ArrayList<>();
        for (Map.Entry<ChatRoom, ChatMessage> entry : chats.entrySet()) {
            mRooms.add(entry.getKey());
            mMessages.add(entry.getValue());
        }
        mActivity = activity;
        mNewChatModel = new ViewModelProvider(activity).get(ChatNotificationsViewModel.class);
        mMessageModel = new ViewModelProvider(activity).get(ChatMessageViewModel.class);
        mUserModel = new ViewModelProvider(activity).get(UserInfoViewModel.class);
    }

    @NonNull
    @Override
    public DetailedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailedViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_chat_detailed, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailedViewHolder holder, int position) {
        holder.setChat(mRooms.get(position), mMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return mRooms.size();
    }

    class DetailedViewHolder extends RecyclerView.ViewHolder {

        private final View mView;

        private ChatRoom mRoom;

        private FragmentChatDetailedBinding binding;

        /**
         * Initialize the ViewHolder.
         *
         * @param view current view context for the page
         */
        public DetailedViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatDetailedBinding.bind(view);
        }

        void setChat(final ChatRoom room, final ChatMessage message) {
            mRoom = room;
            mMessageModel.getFirstMessages(room.getId(), mUserModel.getJwt());

            binding.labelChatName.setText(room.getName());
            binding.textMessageName.setText(message.getSender());
            binding.textMessageBody.setText(message.getMessage());
            binding.textMessageTime.setText(message.getTimeStamp().substring(11, 16));
            binding.imageNotification.setVisibility(View.INVISIBLE);

            mView.setOnClickListener(view -> {
                NavController navController = Navigation.findNavController(mView);

                navController.getGraph().findNode(R.id.chatDisplayFragment).setLabel(mRoom.getName());
                navController.navigate(HomeFragmentDirections
                        .actionNavigationHomeToChatDisplayFragment(mRoom));
            });

            mNewChatModel.addMessageCountObserver(mActivity, notifications -> {
                int count = 0;
                if (notifications.containsKey(mRoom.getId())) {
                    count = notifications.get(mRoom.getId());
                }

                if(count > 0) {
                    //mew messages
                    binding.imageNotification.setVisibility(View.VISIBLE);
                } else {
                    //remove badge
                    binding.imageNotification.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
}
