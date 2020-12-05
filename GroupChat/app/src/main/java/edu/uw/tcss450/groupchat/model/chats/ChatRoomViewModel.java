package edu.uw.tcss450.groupchat.model.chats;

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

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.io.RequestQueueSingleton;
import edu.uw.tcss450.groupchat.ui.chats.ChatRoom;

public class ChatRoomViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mResponse;

    private MutableLiveData<List<ChatRoom>> mRooms;

    private int mCurrent;

    public ChatRoomViewModel(@NonNull Application application) {
        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
        mRooms = new MutableLiveData<>();
        mRooms.setValue(new ArrayList<>());
        mCurrent = -1;
    }

    public int getCurrentRoom() {
        return mCurrent;
    }

    public void setCurrentRoom(final int id) {
        mCurrent = id;
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    public void addRoomsObserver(@NonNull LifecycleOwner owner,
                                 @NonNull Observer<? super List<ChatRoom>> observer) {
        mRooms.observe(owner, observer);
    }

    public void connect(final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url)
                + "chatrooms";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                this::handleRooms,
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
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    public void connectCreate(final String jwt, final String name) {
        String url = "https://dhill30-groupchat-backend.herokuapp.com/chats?name=";

        JSONObject body = new JSONObject();
        try {
            body.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body, //push token found in the JSONObject body
                mResponse::setValue,
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

    private void handleRooms(final JSONObject result) {
        List<ChatRoom> sorted = new ArrayList<>();
        try {
            JSONObject root = result;
            if (root.has("rows")) {
                JSONArray rooms = root.getJSONArray("rows");

                for (int i = 0; i < rooms.length(); i++) {
                    JSONObject jsonRoom = rooms.getJSONObject(i);
                    ChatRoom room = new ChatRoom(
                            jsonRoom.getInt("chatid"),
                            jsonRoom.getString("name"));
                    sorted.add(room);
                }
            } else {
                Log.e("ERROR", "No rows array");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }
        Collections.sort(sorted);
        mRooms.setValue(sorted);
    }

    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode +
                            " " + data);
        }
    }
}
