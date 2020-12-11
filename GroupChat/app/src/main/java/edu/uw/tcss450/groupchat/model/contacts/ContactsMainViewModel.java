package edu.uw.tcss450.groupchat.model.contacts;

import android.app.Application;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.uw.tcss450.groupchat.R;

/**
 * This view model holds a list of the user's contacts.
 *
 * @version December, 2020
 */
public class ContactsMainViewModel extends ContactsViewModel {

    /**
     * Default constructor for this view model.
     * @param application reference to the current application
     */
    public ContactsMainViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Makes a request to the web service to get the list of the user's contacts.
     * @param jwt the user's signed JWT
     */
    public void connect(final String jwt) {
        mContactType = 1;

        String url = getApplication().getResources().getString(R.string.base_url)
                + "contacts";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleSuccess,
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
     * Makes a request to the web service to add the specified contact to a chat room.
     * @param jwt the user's signed JWT
     * @param email the email of the contact to add
     * @param chatId the chat id of the chat room to add to
     */
    public void connectAdd(final String jwt, final String email, final int chatId) {
        String url = getApplication().getResources().getString(R.string.base_url)
                + "chats/" + chatId;

        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body, //user email to add to chat room
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

    /**
     * Makes a request to the web service to remove the specified contact.
     * @param jwt the user's signed JWT
     * @param email the email of the contact to remove
     */
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
}
