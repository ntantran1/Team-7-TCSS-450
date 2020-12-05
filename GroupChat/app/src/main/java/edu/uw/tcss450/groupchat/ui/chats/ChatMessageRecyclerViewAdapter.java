package edu.uw.tcss450.groupchat.ui.chats;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import edu.uw.tcss450.groupchat.R;


/**
 * @author Sukhraj Kaur (Sofia)
 * The class describes how each Messages should look in a chat room and mange the list
 * of messages.
 *
 * @version November 27, 2020
 */
public class ChatMessageRecyclerViewAdapter extends RecyclerView.Adapter {

    private final List<ChatMessage> mMessages;

    private final String mEmail;

    /**
     * Constructor to initialize fields.
     *
     * @param messages the List of messages in the chat room
     * @param email email of the current user
     */
    public ChatMessageRecyclerViewAdapter(List<ChatMessage> messages, String email) {
        this.mMessages = messages;
        mEmail = email;
    }

    @Override
    public int getItemViewType(int position) {
        if(mMessages.get(position).getSender().equals(mEmail)){
            //this is message from user (sent)
            return 0;
        }
        return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        if(viewType == 0){
            view = layoutInflater.inflate(R.layout.fragment_chat_sent, parent, false);
            return new ViewHolderSent(view);
        }

        view = layoutInflater.inflate(R.layout.fragment_chat_received, parent, false);
        return new ViewHolderReceived(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(mMessages.get(position).getSender().equals(mEmail)){
            //sent
            ViewHolderSent viewHolderSent = (ViewHolderSent) holder;
            viewHolderSent.sentMessage.setText(mMessages.get(position).getMessage());
            viewHolderSent.sentTime.setText(mMessages.get(position).getTimeStamp().
                    substring(11, 16));
        } else {
            //received
            ViewHolderReceived viewHolderReceived = (ViewHolderReceived) holder;
            viewHolderReceived.receivedMessage.setText(mMessages.get(position).getMessage());
            viewHolderReceived.senderName.setText(mMessages.get(position).getSender());
            viewHolderReceived.receivedTime.setText(mMessages.get(position).getTimeStamp().
                    substring(11, 16));


        }

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    class ViewHolderReceived extends RecyclerView.ViewHolder {

        TextView senderName, receivedMessage, receivedTime;

        public ViewHolderReceived(@NonNull View itemView) {
            super(itemView);

            senderName = itemView.findViewById(R.id.text_message_name);
            receivedMessage = itemView.findViewById(R.id.text_message_body);
            receivedTime = itemView.findViewById(R.id.text_message_time);
        }
    }

    class ViewHolderSent extends RecyclerView.ViewHolder {

        TextView sentMessage, sentTime;

        public ViewHolderSent(@NonNull View itemView) {
            super(itemView);

            sentMessage = itemView.findViewById(R.id.text_message_body);
            sentTime = itemView.findViewById(R.id.text_message_time);
        }
    }
}
