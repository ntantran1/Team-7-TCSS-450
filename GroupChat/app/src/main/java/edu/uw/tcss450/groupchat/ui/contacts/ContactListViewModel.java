package edu.uw.tcss450.groupchat.ui.contacts;

import android.app.Application;
import android.util.Log;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import edu.uw.tcss450.groupchat.R;

/**
 * View Model for list of Contacts
 *
 * @version November 5
 */
public class ContactListViewModel extends AndroidViewModel {

    private MutableLiveData<List<Contact>> mContactList;

    /**
     * Main default constructor for View Model.
     *
     * @param application reference to the current application
     */
    public ContactListViewModel(@NonNull Application application) {
        super(application);
        mContactList = new MutableLiveData<>();
        mContactList.setValue(new ArrayList<>());
    }

    public void addContactListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<Contact>> observer) {
        mContactList.observe(owner, observer);
    }

    public void connectGet(final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url)
                + "contacts";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleResult,
                this::handleError) {

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    //add headers <key, value>
                    headers.put("Authorization", jwt);
                    return headers;
                }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext()).add(request);
    }

    private void handleError(final VolleyError error) {
        //you should add much better error handling in your project
        Log.e("CONNECTION ERROR", error.getLocalizedMessage());
        throw new IllegalStateException(error.getMessage());
    }

    private void handleResult(final JSONObject result) {
        List<Contact> sorted = new ArrayList<>();
        try {
            JSONObject root = result;
            if (root.has("contacts")) {
                JSONArray contacts = root.getJSONArray("contacts");

                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject jsonContact = contacts.getJSONObject(i);
                    Contact contact = new Contact(jsonContact.getString("username"),
                            jsonContact.getString("name"),
                            jsonContact.getString("email"));
                    sorted.add(contact);
                }
            } else {
                Log.e("ERROR", "No contacts array");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }
        //sort the list of contacts alphabetically
        Collections.sort(sorted);
        mContactList.setValue(sorted);
    }
}
