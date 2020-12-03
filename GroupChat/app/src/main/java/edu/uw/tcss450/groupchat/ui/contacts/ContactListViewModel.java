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
 * View Model for the lists of Contacts
 *
 * @version December 2, 2020
 */
public class ContactListViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mResponse;

    private MutableLiveData<List<Contact>> mContactList;

    private MutableLiveData<List<Contact>> mIncomingList;

    private MutableLiveData<List<Contact>> mOutgoingList;

    private MutableLiveData<List<Contact>> mSearchedList;

    /**
     * Main default constructor for View Model.
     *
     * @param application reference to the current application
     */
    public ContactListViewModel(@NonNull Application application) {
        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
        mContactList = new MutableLiveData<>();
        mContactList.setValue(new ArrayList<>());
        mIncomingList = new MutableLiveData<>();
        mIncomingList.setValue(new ArrayList<>());
        mOutgoingList = new MutableLiveData<>();
        mOutgoingList.setValue(new ArrayList<>());
        mSearchedList = new MutableLiveData<>();
        mSearchedList.setValue(new ArrayList<>());
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
     * Add observer to the Contact list for receiving server's responses.
     *
     * @param owner The LifeCycle owner that will control the observer
     * @param observer The observer that will receive the events
     */
    public void addContactListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<Contact>> observer) {
        mContactList.observe(owner, observer);
    }

    /**
     * Add observer to the Incoming list for receiving server's responses.
     *
     * @param owner The LifeCycle owner that will control the observer
     * @param observer The observer that will receive the events
     */
    public void addIncomingListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<Contact>> observer) {
        mIncomingList.observe(owner, observer);
    }

    /**
     * Add observer to the Outgoing list for receiving server's responses.
     *
     * @param owner The LifeCycle owner that will control the observer
     * @param observer The observer that will receive the events
     */
    public void addOutgoingListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<Contact>> observer) {
        mOutgoingList.observe(owner, observer);
    }

    /**
     * Add observer to the Searched list for receiving server's responses.
     *
     * @param owner The LifeCycle owner that will control the observer
     * @param observer The observer that will receive the events
     */
    public void addSearchedListObserver(@NonNull LifecycleOwner owner,
                                         @NonNull Observer<? super List<Contact>> observer) {
        mSearchedList.observe(owner, observer);
    }

    /**
     * Perform an HTTP request for retrieving current contacts available to the user.
     *
     * @param jwt user sign-in token
     */
    public void connectContacts(final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url)
                + "contacts";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleContacts,
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

    /**
     * Perform an HTTP request for retrieving current incoming contact requests of the user.
     *
     * @param jwt user sign-in token
     */
    public void connectIncoming(final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url)
                + "requests?type=2";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleIncoming,
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

    /**
     * Perform an HTTP request for retrieving current outgoing contact requests of the user.
     *
     * @param jwt user sign-in token
     */
    public void connectOutgoing(final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url)
                + "requests?type=1";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleOutgoing,
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

    /**
     * Perform an HTTP request for retrieving users available for the current user to add.
     *
     * @param jwt user sign-in token
     */
    public void connectSearch(final String jwt, final String term) {
        String url = getApplication().getResources().getString(R.string.base_url)
                + "search?term=" + term + "%";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleSearch,
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

    public void connectAccept(final String jwt, final String email) {
        String url = getApplication().getResources().getString(R.string.base_url)
                + "requests?email=" + email;

        Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null, //no body for this put request
                mResponse::setValue,
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

    public void connectRemove(final String jwt, final String email) {
        String url = getApplication().getResources().getString(R.string.base_url)
                + "contacts?email=" + email;

        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null, //no body for this delete request
                mResponse::setValue,
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

    public void connectAdd(final String jwt, final String email) {
        String url = getApplication().getResources().getString(R.string.base_url)
                + "contacts?email=" + email;

        Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null, //no body for this put request
                mResponse::setValue,
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

    private void handleContacts(final JSONObject result) {
        List<Contact> sorted = new ArrayList<>();
        try {
            JSONObject root = result;
            if (root.has("contacts")) {
                JSONArray contacts = root.getJSONArray("contacts");

                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject jsonContact = contacts.getJSONObject(i);
                    Contact contact = new Contact(jsonContact.getString("username"),
                            jsonContact.getString("name"),
                            jsonContact.getString("email"),
                            1);
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

    private void handleIncoming(final JSONObject result) {
        List<Contact> sorted = new ArrayList<>();
        try {
            JSONObject root = result;
            if (root.has("contacts")) {
                JSONArray contacts = root.getJSONArray("contacts");

                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject jsonContact = contacts.getJSONObject(i);
                    Contact contact = new Contact(jsonContact.getString("username"),
                            jsonContact.getString("name"),
                            jsonContact.getString("email"),
                            2);
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
        mIncomingList.setValue(sorted);
    }

    private void handleOutgoing(final JSONObject result) {
        List<Contact> sorted = new ArrayList<>();
        try {
            JSONObject root = result;
            if (root.has("contacts")) {
                JSONArray contacts = root.getJSONArray("contacts");

                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject jsonContact = contacts.getJSONObject(i);
                    Contact contact = new Contact(jsonContact.getString("username"),
                            jsonContact.getString("name"),
                            jsonContact.getString("email"),
                            3);
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
        mOutgoingList.setValue(sorted);
    }

    private void handleSearch(final JSONObject result) {
        List<Contact> sorted = new ArrayList<>();
        try {
            JSONObject root = result;
            if (root.has("users")) {
                JSONArray contacts = root.getJSONArray("users");

                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject jsonContact = contacts.getJSONObject(i);
                    Contact contact = new Contact(jsonContact.getString("username"),
                            jsonContact.getString("name"),
                            jsonContact.getString("email"),
                            4);
                    sorted.add(contact);
                }
            } else {
                Log.e("ERROR", "No users array");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }
        //sort the list of users alphabetically
        Collections.sort(sorted);
        mSearchedList.setValue(sorted);
    }
}
