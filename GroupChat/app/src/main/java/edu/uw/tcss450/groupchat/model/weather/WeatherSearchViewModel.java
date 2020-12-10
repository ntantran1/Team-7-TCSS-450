package edu.uw.tcss450.groupchat.model.weather;

import android.app.Application;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import edu.uw.tcss450.groupchat.R;

public class WeatherSearchViewModel extends WeatherViewModel {

    private boolean mInit = false;

    public WeatherSearchViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean isInitialized() {
        return mInit;
    }

    public void initialize() {
        mInit = true;
    }

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
     * Perform an HTTP request to retrieve current weather information.
     *
     * @param zip current location zip code
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
