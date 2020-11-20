package edu.uw.tcss450.groupchat.ui.chats;

import java.io.Serializable;

public class ChatRoom implements Serializable, Comparable<ChatRoom> {

    private int mId;

    private String mName;

    public ChatRoom(final int id, final String name) {
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

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
