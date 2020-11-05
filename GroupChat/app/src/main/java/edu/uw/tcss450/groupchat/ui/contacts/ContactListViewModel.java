package edu.uw.tcss450.groupchat.ui.contacts;

import android.app.Application;

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

import java.util.ArrayList;
import java.util.List;

public class ContactListViewModel extends AndroidViewModel {

    private MutableLiveData<List<Contact>> mContactList;

    public ContactListViewModel(@NonNull Application application) {
        super(application);
        mContactList = new MutableLiveData<>();
        mContactList.setValue(new ArrayList<>());
    }

    public void addContactListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<Contact>> observer) {
        mContactList.observe(owner, observer);
    }

//    public void connectGet(final String email) {
//        String url = "https://dhill30-groupchat-backend.herokuapp.com/contacts?email=" + email;
//
//        Request request = new JsonObjectRequest(
//                Request.Method.GET,
//                url,
//                null, //no body for this get request
//                this::handleResult,
//                this::handleError);
//
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                10_000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        //Instantiate the RequestQueue and add the request to the queue
//        Volley.newRequestQueue(getApplication().getApplicationContext()).add(request);
//    }
//
//    private void handleError(final VolleyError error) {
//
//    }
//
//    private void handleResult(final JSONObject result) {
//
//    }
}
