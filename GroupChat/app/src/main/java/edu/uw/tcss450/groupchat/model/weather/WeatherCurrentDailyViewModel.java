package edu.uw.tcss450.groupchat.model.weather;

import android.app.Application;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import edu.uw.tcss450.groupchat.R;

/**
 * This view model handles the response from the web service for getting weather information.
 *
 * @version December, 2020
 */
public class WeatherCurrentDailyViewModel extends WeatherViewModel {

    /**
     * Default constructor for this view model.
     * @param application reference to the current application
     */
    public WeatherCurrentDailyViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Makes a request to the web service to get detailed weather information at the given location.
     * @param lat the latitude of the location
     * @param lon the longitude of the location
     */
    public void connect(final double lat, final double lon) {
        String url = getApplication().getResources().getString(R.string.base_url)
                + "weather/daily?lat=" + lat + "&lon=" + lon;

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
