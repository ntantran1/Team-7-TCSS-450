package edu.uw.tcss450.groupchat.ui.chats;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.groupchat.R;

public class ChatReceivedHolder extends RecyclerView.ViewHolder {

    TextView mMessage, mTextTime, mID;
    ImageView mProfileImage;

    ChatReceivedHolder(View v){
        super(v);
        mMessage = (TextView) v.findViewById(R.id.text_message_body);
        mTextTime = (TextView) v.findViewById(R.id.text_message_time);
        mID = (TextView) v.findViewById(R.id.text_message_name);
        mProfileImage = (ImageView) v.findViewById(R.id.image_sender_profile);
    }

    void bind(ChatMessage message) {
        mMessage.setText(message.getMessage());

        // Format the stored timestamp into a readable String using method.
        mTextTime.setText(message.getTimeStamp());
        mID.setText(message.getSender());

        // TODO: Insert the profile image from the URL into the ImageView.

    }


}
