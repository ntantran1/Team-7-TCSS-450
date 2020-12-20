package edu.uw.tcss450.groupchat.model.weather;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

/**
 * This view model handles the location information.
 *
 * @version December, 2020
 */
public class CurrentLocationViewModel extends ViewModel {

    private MutableLiveData<Location> mLocation;

    /**
     * Default constructor for this view model.
     */
    public CurrentLocationViewModel() {
        mLocation = new MutableLiveData<>();
    }

    /**
     * Add an observer to the Location object.
     * @param owner the fragment's LifecycleOwner
     * @param observer an observer to observe
     */
    public void addLocationObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super Location> observer) {
        mLocation.observe(owner, observer);
    }

    /**
     * Set the stored location to a new, more up-to-date one.
     * @param location the location to set
     */
    public void setLocation(final Location location) {
        if (mLocation.getValue() == null
                || location.getLatitude() != mLocation.getValue().getLatitude()
                || location.getLongitude() != mLocation.getValue().getLongitude()) {
            mLocation.setValue(location);
        }
    }

}
