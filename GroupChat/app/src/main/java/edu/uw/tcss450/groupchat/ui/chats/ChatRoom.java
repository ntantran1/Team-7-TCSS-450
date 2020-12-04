package edu.uw.tcss450.groupchat.ui.chats;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * The ChatRoom object represents a chatroom that the user has access to.
 *
 * @version November 19, 2020
 */
public class ChatRoom implements Serializable, Comparable<ChatRoom> {

    private int mId;

    private String mName;

    /**
     * Initialize the object.
     *
     * @param id chatroom id as an integer
     * @param name chatroom name
     */
    public ChatRoom(final int id, final String name) {
        mId = id;
        mName = name;
    }

    public static ChatRoom createFromJsonString(final String crAsJson) throws JSONException {
        final JSONObject room = new JSONObject(crAsJson);
        return new ChatRoom(room.getInt("chatid"), room.getString("name"));
    }

    /**
     * Return the id of chatroom.
     *
     * @return id of chatroom as an integer
     */
    public int getId() {
        return mId;
    }

    /**
     * Return the name of the chatroom.
     *
     * @return chatroom name string
     */
    public String getName() {
        return mName;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ChatRoom)) return false;
        return mId == ((ChatRoom) other).getId();
    }

    @Override
    public int compareTo(ChatRoom other) {
        return Integer.compare(mId, other.getId());
    }
}
