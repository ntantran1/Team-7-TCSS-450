package edu.uw.tcss450.groupchat.model.weather;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import edu.uw.tcss450.groupchat.R;

/**
 * This view model handles the response from the web service for getting weather information
 * for the searched location.
 *
 * @version December, 2020
 */
public class WeatherSearchViewModel extends WeatherViewModel {

    private Location mLocation;

    private boolean mInit = false;

    /**
     * Default constructor for this view model.
     * @param application reference to the current application
     */
    public WeatherSearchViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Returns whether or not the view model has been initialized.
     * @return true if the view model has been initialized
     */
    public boolean isInitialized() {
        return mInit;
    }

    /**
     * Initializes the view model with the given location.
     * @param location the location to initialize with
     */
    public void initialize(final Location location) {
        mInit = true;
        mLocation = location;
    }

    /**
     * Returns the saved weather location.
     * @return the saved location
     */
    public Location getLocation() {
        return new Location(mLocation);
    }

    /**
     * Sets the location for this view model to the passed location.
     * @param location the location to set
     */
    public void setLocation(final Location location) {
        if (mLocation == null
                || location.getLatitude() != mLocation.getLatitude()
                || location.getLongitude() != mLocation.getLongitude()) {
            mLocation = location;
        }
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

    /**
     * Makes a request to the web service to get weather information at the given location.
     * @param zip the zip code to get weather of
     */
    public void connectZip(final String zip) {
        String url = getApplication().getResources().getString(R.string.base_url)
                + "weather?zip=" + zip;

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
