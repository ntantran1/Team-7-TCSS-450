package edu.uw.tcss450.groupchat.model.weather;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * This abstract view model is the parent for the actual weather view models.
 *
 * @version December, 2020
 */
public abstract class WeatherViewModel extends AndroidViewModel {

    protected MutableLiveData<JSONObject> mResponse;

    /**
     * Default constructor for this view model.
     * @param application reference to the current application
     */
    public WeatherViewModel(@NonNull Application application) {
        super(application);
        mResponse = new MutableLiveData<>(new JSONObject());
    }

    /**
     * Add observer for receiving server's responses.
     *
     * @param owner The LifeCycle owner that will control the observer
     * @param observer The observer that will receive the events
     */
    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    /**
     * Makes a request to the web service to get weather information at the given location.
     * @param lat the latitude of the location
     * @param lon the longitude of the location
     */
    public abstract void connect(final double lat, final double lon);

    protected void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                mResponse.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            try {
                mResponse.setValue(new JSONObject("{" +
                        "code:" + error.networkResponse.statusCode +
                        ", data:" + data +
                        "}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }
}
