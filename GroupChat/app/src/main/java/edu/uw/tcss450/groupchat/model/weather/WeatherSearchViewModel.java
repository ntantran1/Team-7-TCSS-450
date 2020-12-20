package edu.uw.tcss450.groupchat.model.weather;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.math.BigDecimal;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.ui.weather.SavedLocation;

/**
 * This view model handles the response from the web service for getting weather information
 * for the searched location.
 *
 * @version December, 2020
 */
public class WeatherSearchViewModel extends WeatherViewModel {

    private SavedLocation mCurrent;

    private MutableLiveData<SavedLocation> mLocation;

    private boolean mInit = false;

    /**
     * Default constructor for this view model.
     * @param application reference to the current application
     */
    public WeatherSearchViewModel(@NonNull Application application) {
        super(application);
        mLocation = new MutableLiveData<>();
    }

    /**
     * Add an observer to the saved location object.
     * @param owner the fragment's LifecycleOwner
     * @param observer an observer to observe
     */
    public void addLocationObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super SavedLocation> observer) {
        mLocation.observe(owner, observer);
    }

    /**
     * Returns whether or not the view model has been initialized.
     * @return true if the view model has been initialized
     */
    public boolean isInitialized() {
        return mInit;
    }

    /**
     * Initializes the view model with the given SavedLocation object.
     * @param location the SavedLocation to initialize with
     */
    public void initialize(final SavedLocation location) {
        mInit = true;
        mLocation.setValue(new SavedLocation(location));

        BigDecimal lat = BigDecimal.valueOf(location.getLatitude());
        BigDecimal lon = BigDecimal.valueOf(location.getLongitude());
        lat = lat.setScale(2, BigDecimal.ROUND_HALF_UP);
        lon = lon.setScale(2, BigDecimal.ROUND_HALF_UP);
        String name = "Current Location (" + lat + ", " + lon + ")";
        mCurrent = new SavedLocation(name, location.getLatitude(), location.getLongitude());
    }

    /**
     * Returns the saved current location.
     * @return the current location
     */
    public SavedLocation getCurrent() {
        return new SavedLocation(mCurrent);
    }

    /**
     * Returns the saved weather location.
     * @return the saved location
     */
    public SavedLocation getLocation() {
        return new SavedLocation(mLocation.getValue());
    }

    /**
     * Updates the saved current location.
     * @param location the actual current location
     */
    public void setCurrent(final Location location) {
        BigDecimal lat = BigDecimal.valueOf(location.getLatitude());
        BigDecimal lon = BigDecimal.valueOf(location.getLongitude());
        lat = lat.setScale(2, BigDecimal.ROUND_HALF_UP);
        lon = lon.setScale(2, BigDecimal.ROUND_HALF_UP);
        String name = "Current Location (" + lat + ", " + lon + ")";
        mCurrent = new SavedLocation(name, location.getLatitude(), location.getLongitude());
    }

    /**
     * Sets the SavedLocation object for this view model.
     * @param location the SavedLocation to set
     */
    public void setLocation(final SavedLocation location) {
        mLocation.setValue(new SavedLocation(location));
    }

    /**
     * Makes a request to the web service to get weather information at the given location.
     * @param lat the latitude of the location
     * @param lon the longitude of the location
     */
    public void connect(final double lat, final double lon) {
        String url = getApplication().getResources().getString(R.string.base_url)
                + "weather?lat=" + lat + "&lon=" + lon;

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                mResponse::setValue,
                this::handleError);

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext()).add(request);
    }
}
