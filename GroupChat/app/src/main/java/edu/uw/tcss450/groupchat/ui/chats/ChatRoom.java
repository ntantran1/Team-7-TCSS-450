package edu.uw.tcss450.groupchat.ui.chats;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class ChatRoom implements Serializable, Comparable<ChatRoom> {

    private final int mChatRoomID;
    private final String mChatName;

    public ChatRoom(int chatRoomID, String chatName){
        mChatRoomID = chatRoomID;
        mChatName = chatName;

    }

    public static ChatRoom createFromJsonString(final String cmAsJson) throws JSONException {
        final JSONObject room = new JSONObject(cmAsJson);
        return new ChatRoom(room.getInt("chatID"),
                room.getString("chatName"));
    }

    public int getChatRoomID(){
        return mChatRoomID;
    }
    public String getChatName(){
        return mChatName;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        boolean result = false;
        if (other instanceof ChatRoom) {
            result = mChatRoomID == ((ChatRoom) other).mChatRoomID;
        }
        return result;
    }

    @Override
    public int compareTo(ChatRoom o) {

        return mChatName.compareToIgnoreCase(o.getChatName());
    }
}
