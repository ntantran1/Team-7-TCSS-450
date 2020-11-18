package edu.uw.tcss450.groupchat.ui.weather;

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

/*
 * A class that get current weather api
 */
public class WeatherViewModel extends AndroidViewModel {
    private MutableLiveData<JSONObject> mResponse;
    FragmentWeatherHomeBinding binding;
    protected String mZip;
    private boolean mVal = true;

    public boolean getVal() {
        return mVal;
    }

    public void setVal() {
        mVal = false;
    }

    /*
     * A constructor
     */
    public WeatherViewModel(@NonNull Application application) {

        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());

    }

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
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }


}
