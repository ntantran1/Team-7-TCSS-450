package edu.uw.tcss450.groupchat.model.contacts;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import edu.uw.tcss450.groupchat.ui.chats.ChatRoom;
import edu.uw.tcss450.groupchat.ui.contacts.Contact;

public abstract class ContactsViewModel extends AndroidViewModel {

    private MutableLiveData<List<Contact>> mContacts;

    protected MutableLiveData<JSONObject> mResponse;

    protected int mContactType;

    public ContactsViewModel(@NonNull Application application) {
        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
        mContacts = new MutableLiveData<>();
        mContacts.setValue(new ArrayList<>());
        mContactType = -1;
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    public void addContactsObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super List<Contact>> observer) {
        mContacts.observe(owner, observer);
    }

    public abstract void connect(final String jwt);

    public List<Contact> getContacts(){
        return mContacts.getValue();
    }

    public String getContactFromUserName(final String name) {
        for (Contact contact : mContacts.getValue()) {
            if (name.equals(contact.getUsername())) {
                return contact.getEmail();
            }
        }
        return "-1";
    }

    protected void handleSuccess(final JSONObject result) {
        List<Contact> sorted = new ArrayList<>();
        try {
            if (result.has("contacts")) {
                JSONArray contacts = result.getJSONArray("contacts");

                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject jsonContact = contacts.getJSONObject(i);
                    Contact contact = new Contact(jsonContact.getString("username"),
                            jsonContact.getString("name"),
                            jsonContact.getString("email"),
                            mContactType);
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
        mContacts.setValue(sorted);
    }

    protected void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                mResponse.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() + "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            try {
                mResponse.setValue(new JSONObject("{" +
                        "code:" + error.networkResponse.statusCode +
                        ", data:" + data + "}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }
}
