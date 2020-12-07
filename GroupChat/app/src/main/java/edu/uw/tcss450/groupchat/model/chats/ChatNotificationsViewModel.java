package edu.uw.tcss450.groupchat.model.chats;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

/**
 * View Model for new incoming chat messages.
 *
 * @version December 4, 2020
 */
public class ChatNotificationsViewModel extends ViewModel {

    private MutableLiveData<Map<Integer, Integer>> mNewChatCount;

    /**
     * Constructor to initialize the data structure.
     */
    public ChatNotificationsViewModel() {
        mNewChatCount = new MutableLiveData<>();
        mNewChatCount.setValue(new HashMap<>());
    }

    /**
     * Add observer for receiving the server's responses.
     *
     * @param owner The Lifecycle owner that will control the observer
     * @param observer The observer that will receive the events
     */
    public void addMessageCountObserver(@NonNull LifecycleOwner owner,
                                        @NonNull Observer<? super Map<Integer, Integer>> observer) {
        mNewChatCount.observe(owner, observer);
    }

    /**
     * Increment new message count of the specified chat room.
     *
     * @param chatId The chat ID of the chat room
     */
    public void increment(int chatId) {
        if (!mNewChatCount.getValue().containsKey(chatId)) {
            mNewChatCount.getValue().put(chatId, 0);
        }
        mNewChatCount.getValue().put(chatId, mNewChatCount.getValue().get(chatId) + 1);
        mNewChatCount.setValue(mNewChatCount.getValue());
    }

    /**
     * Reset new message count of the specified chat room.
     *
     * @param chatId The chat ID of the chat room
     */
    public void reset(int chatId) {
        mNewChatCount.getValue().put(chatId, 0);
        mNewChatCount.setValue(mNewChatCount.getValue());
    }
}
