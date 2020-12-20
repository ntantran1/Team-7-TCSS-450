package edu.uw.tcss450.groupchat.model.contacts;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

/**
 * View Model for new incoming contact notifications.
 *
 * @version December 6, 2020
 */
public class ContactNotificationsViewModel extends ViewModel {

    private Map<String, MutableLiveData<Integer>> mNewContactCount;

    private String mSelectedTab;

    /**
     * Constructor to initialize the data structure.
     */
    public ContactNotificationsViewModel() {
        mNewContactCount = new HashMap<>();
        mSelectedTab = "";
    }

    /**
     * Add observer for receiving the server's responses.
     * @param tab the contact tab to observe notifications
     * @param owner The Lifecycle owner that will control the observer
     * @param observer The observe that will receive the events
     */
    public void addContactCountObserver(String tab,
                                        @NonNull LifecycleOwner owner,
                                        @NonNull Observer<? super Integer> observer) {
        getOrCreateMapEntry(tab).observe(owner, observer);
    }

    /**
     * Returns the total number of contact notifications.
     * @return total number of notifications
     */
    public int getNotificationCount() {
        int count = 0;
        for (String tab : mNewContactCount.keySet()) {
            count += mNewContactCount.get(tab).getValue();
        }
        return count;
    }

    public String getSelectedTab() {
        return mSelectedTab;
    }

    public void setSelectedTab(final String tab) {
        mSelectedTab = tab;
    }

    /**
     * Increment new contact count.
     * @param tab the contact tab to increment notifications
     */
    public void increment(String tab) {
        getOrCreateMapEntry(tab).setValue(getOrCreateMapEntry(tab).getValue() + 1);
    }

    /**
     * Reset new contact count.
     * @param tab the contact tab to reset notifications
     */
    public void reset(String tab) {
        getOrCreateMapEntry(tab).setValue(0);
    }

    private MutableLiveData<Integer> getOrCreateMapEntry(String tab) {
        if (!mNewContactCount.containsKey(tab)) {
            mNewContactCount.put(tab, new MutableLiveData<>(0));
        }
        return mNewContactCount.get(tab);
    }
}
