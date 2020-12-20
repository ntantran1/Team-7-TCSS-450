package edu.uw.tcss450.groupchat.ui.chats;

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
        if (mId != ((ChatRoom) other).getId()) return false;
        return mName.equals(((ChatRoom) other).getName());
    }

    @Override
    public int hashCode() {
        return mId + mName.hashCode();
    }

    @Override
    public int compareTo(ChatRoom other) {
        return Integer.compare(mId, other.getId());
    }
}
