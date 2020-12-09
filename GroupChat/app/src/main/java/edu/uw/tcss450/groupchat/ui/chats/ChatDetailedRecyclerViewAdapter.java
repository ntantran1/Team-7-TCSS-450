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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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
            binding.textMessageTime.setText(getLocalTime(message.getTimeStamp()));
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

        private String getLocalTime(final String timeStamp) {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat other = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
            SimpleDateFormat today = new SimpleDateFormat("h:mm a", Locale.getDefault());
            in.setTimeZone(TimeZone.getTimeZone("UTC"));
            today.setTimeZone(TimeZone.getDefault());

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date begin = calendar.getTime();

            Date date;
            String time = "";
            try {
                date = in.parse(timeStamp);
                if (date.before(begin)) {
                    time = other.format(date);
                } else {
                    time = today.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return time;
        }
    }
}
