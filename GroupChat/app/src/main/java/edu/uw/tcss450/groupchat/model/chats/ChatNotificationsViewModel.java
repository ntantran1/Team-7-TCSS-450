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

    private MutableLiveData<Map<Integer, Integer>> mNewMessageCount;

    private MutableLiveData<Integer> mNewChatCount;

    /**
     * Constructor to initialize the data structure.
     */
    public ChatNotificationsViewModel() {
        mNewMessageCount = new MutableLiveData<>();
        mNewMessageCount.setValue(new HashMap<>());
        mNewChatCount = new MutableLiveData<>();
        mNewChatCount.setValue(0);
    }

    /**
     * Add observer for receiving the server's responses.
     *
     * @param owner The Lifecycle owner that will control the observer
     * @param observer The observer that will receive the events
     */
    public void addMessageCountObserver(@NonNull LifecycleOwner owner,
                                        @NonNull Observer<? super Map<Integer, Integer>> observer) {
        mNewMessageCount.observe(owner, observer);
    }

    /**
     * Add observer for receiving the server's responses.
     *
     * @param owner The Lifecycle owner that will control the observer
     * @param observer The observer that will receive the events
     */
    public void addChatCountObserver(@NonNull LifecycleOwner owner,
                                     @NonNull Observer<? super Integer> observer) {
        mNewChatCount.observe(owner, observer);
    }

    /**
     * Increment new message count of the specified chat room.
     *
     * @param chatId The chat ID of the chat room
     */
    public void increment(int chatId) {
        if (!mNewMessageCount.getValue().containsKey(chatId)) {
            mNewMessageCount.getValue().put(chatId, 0);
        }
        mNewMessageCount.getValue().put(chatId, mNewMessageCount.getValue().get(chatId) + 1);
        mNewMessageCount.setValue(mNewMessageCount.getValue());
    }

    /**
     * Increment new chat room count.
     */
    public void incrementChat() {
        mNewChatCount.setValue(mNewChatCount.getValue() + 1);
    }

    /**
     * Reset new message count of the specified chat room.
     *
     * @param chatId The chat ID of the chat room
     */
    public void reset(int chatId) {
        mNewMessageCount.getValue().put(chatId, 0);
        mNewMessageCount.setValue(mNewMessageCount.getValue());
    }

    /**
     * Reset new chat room count.
     */
    public void resetChat() {
        mNewChatCount.setValue(0);
    }

    /**
     * Returns the total number of new messages.
     *
     * @return total number of new messages
     */
    public int getNewMessageCount() {
        int total = 0;
        for (int chatId : mNewMessageCount.getValue().keySet()) {
            total += mNewMessageCount.getValue().get(chatId);
        }
        return total;
    }

    /**
     * Returns the total number of new chat rooms.
     *
     * @return total number of new chats
     */
    public int getNewChatCount() {
        return mNewChatCount.getValue();
    }
}
