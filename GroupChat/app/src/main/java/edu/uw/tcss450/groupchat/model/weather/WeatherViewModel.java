package edu.uw.tcss450.groupchat.model.weather;

import android.app.Application;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import edu.uw.tcss450.groupchat.databinding.FragmentWeatherHomeBinding;

/**
 * A class that get current weather information.
 *
 * @version November 19, 2020
 */
public class WeatherViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mResponse;

    /** Binding to the weather home page. */
    FragmentWeatherHomeBinding binding;

    private String mZip;

    private boolean mVal = true;

    /**
     * A constructor
     *
     * @param application the reference to the current application.
     */
    public WeatherViewModel(@NonNull Application application) {

        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());

    }

    /**
     * Get value.
     *
     * @return value boolean
     */
    public boolean getVal() {
        return mVal;
    }

    public String getZip() {
        return mZip;
    }

    /**
     * Set value.
     *
     * @return value boolean
     */
    public void setVal() {
        mVal = false;
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

    private void handleError(final VolleyError error) {
        int id = binding.searchZip.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        EditText editText = (EditText) binding.searchZip.findViewById(id);
        editText.setError("Invalid Zip Code!");

    }

    /**
     * Perform an HTTP request to retrieve current weather information.
     *
     * @param b binding to the weather home page
     * @param zip current location zip code
     */
    public void connect(FragmentWeatherHomeBinding b, String zip) {
        binding = b;
        mZip = zip;

        String url = "https://dhill30-groupchat-backend.herokuapp.com/weather?zip=" + zip;

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
