package edu.uw.tcss450.groupchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
import edu.uw.tcss450.groupchat.model.NewMessageCountViewModel;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;
import edu.uw.tcss450.groupchat.services.PushReceiver;
import edu.uw.tcss450.groupchat.ui.chats.ChatMessage;
import edu.uw.tcss450.groupchat.ui.chats.ChatViewModel;

/**
 * Activity after the user is authenticated, for all the features of the application.
 *
 * @version November 5
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private ActivityMainBinding binding;
    private MainPushMessageReceiver mPushMessageReceiver;
    private NewMessageCountViewModel mNewMessageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());
        String email = args.getEmail();
        String jwt = args.getJwt();
        mNewMessageModel = new ViewModelProvider(this).get(NewMessageCountViewModel.class);

        new ViewModelProvider(this,
                new UserInfoViewModel.UserInfoViewModelFactory(email, jwt)).get(UserInfoViewModel.class);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_contacts,
                        R.id.navigation_home,
                        R.id.navigation_chats,
                        R.id.navigation_weather).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener(((controller, destination, arguments) -> {
            if(destination.getId() == R.id.navigation_chats){
                //when user navigates to chat page, reset new message count
                mNewMessageModel.reset();
            }
        }));

        mNewMessageModel.addMessageCountObserver(this, count ->{
            BadgeDrawable badge = binding.navView.getOrCreateBadge(R.id.navigation_chats);
            badge.setMaxCharacterCount(2);
            if(count > 0){
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

        if (id == R.id.action_settings) {
            //TODO open a settings fragment
            Log.d("SETTINGS", "Clicked");
            return true;
        } else if (id == R.id.action_signout) {
            //TODO log the user out
            Log.d("SIGNOUT", "Clicked");
            return true;
        } else if (id == R.id.action_change_password) {
            //TODO open the change password fragment
            Log.d("CHANGE PASSWORD", "Clicked");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * BroadcastReceiver that listens to messages sent from PushReceiver
     */
    private class MainPushMessageReceiver extends BroadcastReceiver {

        private ChatViewModel mModel = new ViewModelProvider(MainActivity.this).
                get(ChatViewModel.class);

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("onReceive");
            NavController nc = Navigation.findNavController(MainActivity.this,
                    R.id.nav_host_fragment);
            NavDestination nd = nc.getCurrentDestination();

            if(intent.hasExtra("chatMessage")){
                ChatMessage cm = (ChatMessage) intent.getSerializableExtra("chatMessage");

                //if user is not on chat screen, update NewMessageCountView Model
                if(nd.getId() != R.id.navigation_chats){
                    mNewMessageModel.increment();
                }

                //inform view model holding chatroom messages of the ones
                mModel.addMessage(intent.getIntExtra("chatid", -1), cm);
            }
        }
    }
}