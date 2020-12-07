package edu.uw.tcss450.groupchat.model.contacts;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

/**
 * View Model for new incoming contact notifications.
 *
 * @version December 6, 2020
 */
public class ContactNotificationsViewModel extends ViewModel {

    private MutableLiveData<Integer> mNewContactCount;

    /**
     * Constructor to initialize the data structure.
     */
    public ContactNotificationsViewModel() {
        mNewContactCount = new MutableLiveData<>();
        mNewContactCount.setValue(0);
    }

    /**
     * Add observer for receiving the server's rsponses.
     *
     * @param owner The Lifecycle owner that will control the observer
     * @param observer The observe that will receive the events
     */
    public void addContactCountObserver(@NonNull LifecycleOwner owner,
                                        @NonNull Observer<? super Integer> observer) {
        mNewContactCount.observe(owner, observer);
    }

    /**
     * Increment new contact count.
     */
    public void increment() {
        mNewContactCount.setValue(mNewContactCount.getValue() + 1);
    }

    /**
     * Reset new contact count.
     */
    public void reset() {
        mNewContactCount.setValue(0);
    }
}
