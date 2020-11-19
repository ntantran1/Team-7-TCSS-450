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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.ui.contacts.Contact;

public class ChatRoomListViewModel extends AndroidViewModel {



    private MutableLiveData<List<ChatRoom>> mChatRoomList;

    public ChatRoomListViewModel(@NonNull Application application) {
        super(application);

        mChatRoomList = new MutableLiveData<>();
        mChatRoomList.setValue(new ArrayList<>());

    }

    public void addChatRoomListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<ChatRoom>> observer) {
        mChatRoomList.observe(owner, observer);
    }

    public void connectGet(final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url)
                + "chatrooms";

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
        List<ChatRoom> sorted = new ArrayList<>();
        try {
            JSONObject root = result;
            if (root.has("chatId")) {
                JSONArray chatroom = root.getJSONArray("chatId");
                mChatRoomList.setValue(new ArrayList<>());

                for (int i = 0; i < chatroom.length(); i++) {
                    JSONObject jsonChatRoom = chatroom.getJSONObject(i);
                    ChatRoom chatRoom = new ChatRoom(jsonChatRoom.getInt("chatId"),
                            jsonChatRoom.getString("chatname"));
                    sorted.add(chatRoom);
                }
            } else {
                Log.e("ERROR", "No chatroom array");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }
        //sort the list of contacts alphabetically
        Collections.sort(sorted);
        mChatRoomList.setValue(sorted);
    }


}
