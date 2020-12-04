package edu.uw.tcss450.groupchat.ui.chats;

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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.groupchat.io.RequestQueueSingleton;

public class ChatRoomStartViewModel extends AndroidViewModel {

    private final MutableLiveData<JSONObject> mRoomStart;

    private MutableLiveData<List<ChatRoom>> mRooms;




    public ChatRoomStartViewModel(@NonNull Application application) {
        super(application);
        mRoomStart = new MutableLiveData<>();
        mRoomStart.setValue(new JSONObject());

        mRooms = new MutableLiveData<>();
        mRooms.setValue(new ArrayList<>());
    }

    /**
     * Add an observer to the chat room.
     *
     * @param owner the LifecyleOwner object of the chat room
     * @param observer an observer to observe
     */
    public void addRoomRequestObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mRoomStart.observe(owner, observer);
    }

    public void startNewChatRoom(final String jwt, final String roomName){
        String url = "https://dhill30-groupchat-backend.herokuapp.com/chats?name="
                +roomName;

        JSONObject body = new JSONObject();
        try {
            body.put("name", roomName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body, //push token found in the JSONObject body
                mRoomStart::setValue,
                this::handleError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }


    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode +
                            " " +
                            data);
        }
    }


}
