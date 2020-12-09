package edu.uw.tcss450.groupchat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;

import edu.uw.tcss450.groupchat.databinding.ActivityMainBinding;
import edu.uw.tcss450.groupchat.model.chats.ChatMessageViewModel;
import edu.uw.tcss450.groupchat.model.chats.ChatNotificationsViewModel;
import edu.uw.tcss450.groupchat.model.chats.ChatRoomViewModel;
import edu.uw.tcss450.groupchat.model.PushyTokenViewModel;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactNotificationsViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsIncomingViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsMainViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsOutgoingViewModel;
import edu.uw.tcss450.groupchat.model.contacts.ContactsSearchViewModel;
import edu.uw.tcss450.groupchat.model.weather.LocationViewModel;
import edu.uw.tcss450.groupchat.model.weather.WeatherViewModel;
import edu.uw.tcss450.groupchat.services.PushReceiver;
import edu.uw.tcss450.groupchat.ui.chats.ChatMessage;

/**
 * Activity after the user is authenticated, for all the features of the application.
 *
 * @version December 4, 2020
 */
public class MainActivity extends AppCompatActivity {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 60000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final int MY_PERMISSIONS_LOCATIONS = 8414;

    private LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationClient;

    private LocationCallback mLocationCallback;

    private LocationViewModel mLocationModel;

    private ChatNotificationsViewModel mNewChatModel;

    private ContactNotificationsViewModel mNewContactModel;

    private UserInfoViewModel mUserViewModel;

    private AppBarConfiguration mAppBarConfiguration;

    private MainPushMessageReceiver mPushMessageReceiver;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());
        String email = args.getEmail();
        String jwt = args.getJwt();

        new ViewModelProvider(this,
                new UserInfoViewModel.UserInfoViewModelFactory(email, jwt))
                .get(UserInfoViewModel.class);

        mNewChatModel = new ViewModelProvider(this).get(ChatNotificationsViewModel.class);
        mNewContactModel = new ViewModelProvider(this).get(ContactNotificationsViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserInfoViewModel.class);
        ChatRoomViewModel chatRoomModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        setTheme(mUserViewModel.getTheme());

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_contacts,
                        R.id.navigation_home,
                        R.id.navigation_chats,
                        R.id.navigation_weather).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_contacts) {
                mNewContactModel.reset();
            } else if (destination.getId() == R.id.navigation_chats) {
                mNewChatModel.resetChat();
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        } else {
            // the user has already allowed the use of Locations. Get the current location.
            requestLocation();
        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    // update UI with location data
                    Log.d("LOCATION UPDATE", location.toString());
                    if (mLocationModel == null) {
                        mLocationModel = new ViewModelProvider(MainActivity.this)
                                .get(LocationViewModel.class);
                    }
                    mLocationModel.setLocation(location);
                }
            }
        };

        createLocationRequest();

        mUserViewModel.addThemeObserver(this, theme -> {
            BadgeDrawable contactBadge = binding.navView.getOrCreateBadge(R.id.navigation_contacts);
            contactBadge.setMaxCharacterCount(2);

            BadgeDrawable chatBadge = binding.navView.getOrCreateBadge(R.id.navigation_chats);
            chatBadge.setMaxCharacterCount(2);

            contactBadge.setVisible(contactBadge.getNumber() != 0);
            chatBadge.setVisible(chatBadge.getNumber() != 0);
        });

        chatRoomModel.addCurrentRoomObserver(this, chatId -> mNewChatModel.reset(chatId));

        mNewChatModel.addMessageCountObserver(this, notifications -> {
            BadgeDrawable badge = binding.navView.getOrCreateBadge(R.id.navigation_chats);
            badge.setMaxCharacterCount(2);

            int count = 0;
            for (int chatId : notifications.keySet()) {
                count += notifications.get(chatId);
            }

            if(count > 0) {
                //new messages
                badge.setNumber(mNewChatModel.getNewChatCount() + count);
                badge.setVisible(true);
            } else {
                //remove badge
                if (mNewChatModel.getNewChatCount() == 0) {
                    badge.clearNumber();
                    badge.setVisible(false);
                } else {
                    badge.setNumber(mNewChatModel.getNewChatCount());
                    badge.setVisible(true);
                }
            }
        });

        mNewChatModel.addChatCountObserver(this, count -> {
            BadgeDrawable badge = binding.navView.getOrCreateBadge(R.id.navigation_chats);
            badge.setMaxCharacterCount(2);

            if(count > 0) {
                //new contacts
                badge.setNumber(mNewChatModel.getNewMessageCount() + count);
                badge.setVisible(true);
            } else {
                //remove badge
                if (mNewChatModel.getNewMessageCount() == 0) {
                    badge.clearNumber();
                    badge.setVisible(false);
                } else {
                    badge.setNumber(mNewChatModel.getNewMessageCount());
                    badge.setVisible(true);
                }
            }
        });

        mNewContactModel.addContactCountObserver(this, count -> {
            BadgeDrawable badge = binding.navView.getOrCreateBadge(R.id.navigation_contacts);
            badge.setMaxCharacterCount(2);

            if(count > 0) {
                //new contacts
                badge.setNumber(count);
                badge.setVisible(true);
            } else {
                //remove badge
                badge.clearNumber();
                badge.setVisible(false);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mPushMessageReceiver == null){
            mPushMessageReceiver = new MainPushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        registerReceiver(mPushMessageReceiver, iFilter);
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPushMessageReceiver != null){
            unregisterReceiver(mPushMessageReceiver);
        }
        stopLocationUpdates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        if (id == R.id.navigation_settings) {
            return NavigationUI.onNavDestinationSelected(item, navController);
        } else if (id == R.id.action_signout) {
            signOut();
            return true;
        } else if (id == R.id.navigation_change_password){
            return NavigationUI.onNavDestinationSelected(item, navController);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch(requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // if request is cancelled, the result arrays are empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted, do the locations-related tasks needed
                    requestLocation();
                } else {
                    // permission denied, disable the functionality that depends on this
                    Log.d("PERMISSION DENIED", "Nothing to see or do here.");

                    // shut down the app (in production release notify the user)
                    finishAndRemoveTask();
                }
                return;
            }

            // other 'case' lines to check for other permissions the app might request
        }
    }

    /**
     * Request the device location from the API.
     */
    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("REQUEST LOCATION", "User did NOT allow permission to request location.");
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // got last known location, in some rare situations this can be null
                            if (location != null) {
                                Log.d("LOCATION", location.toString());
                                if (mLocationModel == null) {
                                    mLocationModel = new ViewModelProvider(MainActivity.this)
                                            .get(LocationViewModel.class);
                                }
                                mLocationModel.setLocation(location);
                            }
                        }
                    });
        }
    }

    /**
     * Create and configure a Location Request used when retrieving location updates.
     */
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();

        // Sets the desired interval for active location updates. This interval is inexact. You
        // may not receive updates at all if no location sources are available, or you may receive
        // them slower than requested. You may also receive update faster than requested if other
        // applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        // It is good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially recommended in
        // applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void signOut() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        prefs.edit().remove(getString(R.string.keys_prefs_jwt)).apply();

        PushyTokenViewModel model = new ViewModelProvider(this)
                .get(PushyTokenViewModel.class);

        //when we hear back from the web service, quit
        model.addResponseObserver(this, result -> finishAndRemoveTask());

        model.deleteTokenFromWebservice(
                new ViewModelProvider(this)
                        .get(UserInfoViewModel.class)
                        .getJwt());
    }

    public void changeColorTheme(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.settings_color_pg:
                if (checked && mUserViewModel.getTheme() != R.style.Theme_PurpleGold) {
                    mUserViewModel.setTheme(R.style.Theme_PurpleGold);
                    recreate();
                }
                break;
            case R.id.settings_color_ig:
                if (checked && mUserViewModel.getTheme() != R.style.Theme_IndigoGreen) {
                    mUserViewModel.setTheme(R.style.Theme_IndigoGreen);
                    recreate();
                }
                break;
            case R.id.settings_color_go:
                if (checked && mUserViewModel.getTheme() != R.style.Theme_GreyOrange) {
                    mUserViewModel.setTheme(R.style.Theme_GreyOrange);
                    recreate();
                }
                break;
        }
    }

    /**
     * BroadcastReceiver that listens to messages sent from PushReceiver
     */
    private class MainPushMessageReceiver extends BroadcastReceiver {

        private ChatMessageViewModel mChatModel =
                new ViewModelProvider(MainActivity.this).get(ChatMessageViewModel.class);

        private ChatRoomViewModel mRoomModel =
                new ViewModelProvider(MainActivity.this).get(ChatRoomViewModel.class);

        private ContactsMainViewModel mContactsModel =
                new ViewModelProvider(MainActivity.this).get(ContactsMainViewModel.class);

        private ContactsIncomingViewModel mIncomingModel =
                new ViewModelProvider(MainActivity.this).get(ContactsIncomingViewModel.class);

        private ContactsOutgoingViewModel mOutgoingModel =
                new ViewModelProvider(MainActivity.this).get(ContactsOutgoingViewModel.class);

        private ContactsSearchViewModel mSearchModel =
                new ViewModelProvider(MainActivity.this).get(ContactsSearchViewModel.class);

        @Override
        public void onReceive(Context context, Intent intent) {
            NavController nc = Navigation.findNavController(MainActivity.this,
                    R.id.nav_host_fragment);
            NavDestination nd = nc.getCurrentDestination();

            if(intent.hasExtra("chatMessage")) {
                ChatMessage cm = (ChatMessage) intent.getSerializableExtra("chatMessage");
                int chatId = intent.getIntExtra("chatid", -1);

                //if user is not on chat screen, update NewMessageCountView Model
                if(nd.getId() != R.id.chatDisplayFragment || mRoomModel.getCurrentRoom() != chatId){
                    mNewChatModel.increment(chatId);
                }

                //inform view model holding chatroom messages of the new ones
                mChatModel.addMessage(intent.getIntExtra("chatid", -1), cm);
                mRoomModel.connectRecent(mUserViewModel.getJwt());
            } else if (intent.hasExtra("contact")) {

                if (nd.getId() != R.id.navigation_contacts) {
                    mNewContactModel.increment();
                }

                mContactsModel.connect(mUserViewModel.getJwt());
                mIncomingModel.connect(mUserViewModel.getJwt());
                mOutgoingModel.connect(mUserViewModel.getJwt());
                mSearchModel.connect(mUserViewModel.getJwt());
            } else if (intent.hasExtra("chat")) {

                if (nd.getId() != R.id.navigation_chats) {
                    mNewChatModel.incrementChat();
                }

                mRoomModel.connect(mUserViewModel.getJwt());
            }
        }
    }
}