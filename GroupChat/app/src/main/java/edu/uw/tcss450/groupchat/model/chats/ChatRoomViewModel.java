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
import java.util.TreeMap;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.io.RequestQueueSingleton;
import edu.uw.tcss450.groupchat.ui.chats.ChatMessage;
import edu.uw.tcss450.groupchat.ui.chats.ChatRoom;

public class ChatRoomViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mResponse;

    private MutableLiveData<List<ChatRoom>> mRooms;

    private MutableLiveData<Map<ChatRoom, ChatMessage>> mRecent;

    private MutableLiveData<Integer> mCurrentRoom;

    public ChatRoomViewModel(@NonNull Application application) {
        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
        mRooms = new MutableLiveData<>();
        mRooms.setValue(new ArrayList<>());
        mRecent = new MutableLiveData<>();
        mRecent.setValue(new TreeMap<>());
        mCurrentRoom = new MutableLiveData<>();
        mCurrentRoom.setValue(-1);
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    public void addRoomsObserver(@NonNull LifecycleOwner owner,
                                 @NonNull Observer<? super List<ChatRoom>> observer) {
        mRooms.observe(owner, observer);
    }

    public void addRecentObserver(@NonNull LifecycleOwner owner,
                                 @NonNull Observer<? super Map<ChatRoom, ChatMessage>> observer) {
        mRecent.observe(owner, observer);
    }

    public void addCurrentRoomObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super Integer> observer) {
        mCurrentRoom.observe(owner, observer);
    }

    public int getCurrentRoom() {
        return mCurrentRoom.getValue();
    }

    public List<ChatRoom> getRooms() {
        return mRooms.getValue();
    }

    public int getRoomFromName(final String name) {
        for (ChatRoom room : mRooms.getValue()) {
            if (name.equals(room.getName())) {
                return room.getId();
            }
        }
        return -1;
    }

    public void setCurrentRoom(final int id) {
        mCurrentRoom.setValue(id);
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

    public void connectRecent(final String jwt) {
        String url = "https://dhill30-groupchat-backend.herokuapp.com/chatrooms/recent";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                this::handleRecent,
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
            if (result.has("rows")) {
                JSONArray rooms = result.getJSONArray("rows");

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

    private void handleRecent(final JSONObject result) {
        Map<ChatRoom, ChatMessage> chats = new TreeMap<>();
        try {
            if (result.has("chats")) {
                JSONArray rooms = result.getJSONArray("chats");

                for (int i = 0; i < rooms.length(); i++) {
                    JSONObject jsonRoom = rooms.getJSONObject(i);
                    ChatRoom room = new ChatRoom(
                            jsonRoom.getInt("chatid"),
                            jsonRoom.getString("name"));
                    ChatMessage message = new ChatMessage(
                            jsonRoom.getInt("messageid"),
                            jsonRoom.getString("message"),
                            jsonRoom.getString("email"),
                            jsonRoom.getString("timestamp"));
                    chats.put(room, message);
                }
            } else {
                Log.e("ERROR", "No chats array");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }
        mRecent.setValue(chats);
    }

    private void handleError(final VolleyError error) {
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
