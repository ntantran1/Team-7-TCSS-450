package edu.uw.tcss450.groupchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.uw.tcss450.groupchat.databinding.ActivityMainBinding;
import edu.uw.tcss450.groupchat.model.chats.ChatMessageViewModel;
import edu.uw.tcss450.groupchat.model.chats.ChatRoomViewModel;
import edu.uw.tcss450.groupchat.model.chats.NewMessageCountViewModel;
import edu.uw.tcss450.groupchat.model.PushyTokenViewModel;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;
import edu.uw.tcss450.groupchat.services.PushReceiver;
import edu.uw.tcss450.groupchat.ui.chats.ChatMessage;

/**
 * Activity after the user is authenticated, for all the features of the application.
 *
 * @version December 4, 2020
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private ActivityMainBinding binding;

    private MainPushMessageReceiver mPushMessageReceiver;

    private NewMessageCountViewModel mNewMessageModel;

    private UserInfoViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());
        String email = args.getEmail();
        String jwt = args.getJwt();

        new ViewModelProvider(this,
                new UserInfoViewModel.UserInfoViewModelFactory(email, jwt)).get(UserInfoViewModel.class);

        mNewMessageModel = new ViewModelProvider(this).get(NewMessageCountViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserInfoViewModel.class);

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

        navController.addOnDestinationChangedListener(((controller, destination, arguments) -> {
            if(destination.getId() == R.id.chatDisplayFragment){
                //when user navigates to chat page, reset new message count
                mNewMessageModel.reset();
            }
        }));

        mNewMessageModel.addMessageCountObserver(this, count -> {
            BadgeDrawable badge = binding.navView.getOrCreateBadge(R.id.navigation_chats);
            badge.setMaxCharacterCount(2);
            if(count > 0) {
                //mew messages
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPushMessageReceiver != null){
            unregisterReceiver(mPushMessageReceiver);
        }
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

        @Override
        public void onReceive(Context context, Intent intent) {
            NavController nc = Navigation.findNavController(MainActivity.this,
                    R.id.nav_host_fragment);
            NavDestination nd = nc.getCurrentDestination();

            if(intent.hasExtra("chatMessage")) {
                ChatMessage cm = (ChatMessage) intent.getSerializableExtra("chatMessage");

                //if user is not on chat screen, update NewMessageCountView Model
                if(nd.getId() != R.id.chatDisplayFragment
                        || mRoomModel.getCurrentRoom() != intent.getIntExtra("chatid", -1)){
                    mNewMessageModel.increment();
                }

                //inform view model holding chatroom messages of the ones
                mChatModel.addMessage(intent.getIntExtra("chatid", -1), cm);
            }
        }
    }
}