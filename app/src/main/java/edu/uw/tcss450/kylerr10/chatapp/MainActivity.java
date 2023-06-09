package edu.uw.tcss450.kylerr10.chatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.uw.tcss450.kylerr10.chatapp.listdata.Notification;
import edu.uw.tcss450.kylerr10.chatapp.model.PushyTokenViewModel;
import edu.uw.tcss450.kylerr10.chatapp.model.UserInfoViewModel;
import edu.uw.tcss450.kylerr10.chatapp.services.PushReceiver;
import edu.uw.tcss450.kylerr10.chatapp.ui.ThemeManager;
import edu.uw.tcss450.kylerr10.chatapp.ui.chat.ChatViewModel;
import edu.uw.tcss450.kylerr10.chatapp.ui.contacts.ContactsViewModel;
import edu.uw.tcss450.kylerr10.chatapp.ui.home.NotificationsViewModel;
import edu.uw.tcss450.kylerr10.chatapp.ui.setting.AboutDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import edu.uw.tcss450.kylerr10.chatapp.ui.weather.ForecastViewModel;
import edu.uw.tcss450.kylerr10.chatapp.ui.weather.LocationViewModel;
import edu.uw.tcss450.kylerr10.chatapp.ui.weather.UserLocationViewModel;

/**
 * Main activity of the application.
 *
 * @author Kyler Robison
 */
public class MainActivity extends AppCompatActivity {
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    // A constant int for the permissions request code. Must be a 16 bit number
    private static final int MY_PERMISSIONS_LOCATIONS = 8414;
    private LocationRequest mLocationRequest;
    //Use a FusedLocationProviderClient to request the location
    private FusedLocationProviderClient mFusedLocationClient;
    // Will use this call back to decide what to do when a location change is detected
    private LocationCallback mLocationCallback;
    // The ViewModel that will store the current location
    private LocationViewModel mLocationModel;
    // The ViewModel that will store the user's saved locations
    private UserLocationViewModel mUserLocationModel;
    // The ViewModel that will store the forecast data
    private ForecastViewModel mForecastModel;
    /**
     * Bottom navigation
     */
    private AppBarConfiguration mAppBarConfiguration;

    private MainPushReceiver mPushMessageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this); // Required to apply user's chosen theme to activity
        super.onCreate(savedInstanceState);

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());
        JWT jwt = new JWT(args.getJwt());

        UserInfoViewModel model = new ViewModelProvider(this).get(UserInfoViewModel.class);
        model.setToken(jwt);

        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.app_toolbar));
        initializeBottomNav();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        } else {
            requestLocation();
        }
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReceiver == null) {
            mPushMessageReceiver = new MainPushReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        registerReceiver(mPushMessageReceiver, iFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReceiver != null){
            unregisterReceiver(mPushMessageReceiver);
        }
    }

    /**
     * Helper method to set up the bottom navigation bar.
     *
     * @author Kyler Robison
     */
    private void initializeBottomNav() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_chat,
                R.id.navigation_contacts,
                R.id.navigation_weather
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                new AboutDialog(this).show();
                return true;
            case R.id.settings:
                if (findViewById(R.id.settings_root) == null) {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                            .navigate(R.id.navigation_settings);
                }
                return true;
            case R.id.action_log_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Takes steps to sign the user out of the application.
     */
    private void signOut() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        prefs.edit().remove(getString(R.string.keys_prefs_jwt)).apply();

        new ViewModelProvider(this).get(PushyTokenViewModel.class).deleteTokenFromWebservice(
                new ViewModelProvider(this).get(UserInfoViewModel.class).getJWT().toString()
        );
        //End the app completely
        finishAndRemoveTask();
    }

    /**
     * Allows the title bar back button to navigate up the navigation stack.
     * @return true if the navigation was successful.
     */
    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_LOCATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation();
            } else {
                Log.e("LOCATION", "Necessary permissions not granted.");
                finishAndRemoveTask();
            }
        }
    }

    /**
     * Helper method to create a location request.
     */
    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("LOCATION", "Necessary permissions not granted.");
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, Objects.requireNonNull(location -> {
                if (location != null) {
                    Log.d("LOCATION", location.toString());
                    if (mLocationModel == null) {
                        mLocationModel = new ViewModelProvider(MainActivity.this)
                                .get(LocationViewModel.class);
                    }
                    mLocationModel.setLocation(location);
                    mForecastModel = new ViewModelProvider(MainActivity.this).get(ForecastViewModel.class);
                    mForecastModel.connectGet(MainActivity.this, location.getLatitude(), location.getLongitude());
                } else {
                    Log.d("LOCATION", "No Location retrieved.");
                }
            }));
        }
    }

    /**
     * Create and configure a Location Request used when retrieving location updates
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL_IN_MILLISECONDS)
            .setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
            .build();
    }

    /**
     * A BroadcastReceiver that listens for messages sent from PushReceiver
     */
    private class MainPushReceiver extends BroadcastReceiver {
        private final ContactsViewModel mContactsViewModel =
                new ViewModelProvider(MainActivity.this).get(ContactsViewModel.class);

        private final NotificationsViewModel mNotificationsViewModel =
                new ViewModelProvider(MainActivity.this).get(NotificationsViewModel.class);
        @Override
        public void onReceive(Context context, Intent intent) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String currentTime = dateFormat.format(new Date());

            if (intent.hasExtra("contact") && intent.getStringExtra("contact").equals("newRequest")) {
                mContactsViewModel.updateContacts();

                mNotificationsViewModel.addNotification(
                        new Notification(
                                Notification.Type.CONTACT,
                                "New Contact Request",
                                "From ",
                                currentTime)
                );
            }
            if (intent.hasExtra("chat")) {
                mNotificationsViewModel.addNotification(
                        new Notification(
                                Notification.Type.CHAT,
                                "New Chat Room",
                                trimString(intent.getStringExtra("name")),
                                currentTime)
                );

                new ViewModelProvider(MainActivity.this).get(ChatViewModel.class).getChatRooms();
            }
            if (intent.hasExtra("chatMessage")) {
                mNotificationsViewModel.addNotification(
                        new Notification(
                                Notification.Type.MESSAGE,
                                intent.getStringExtra("sender"),
                                trimString(intent.getStringExtra("chatMessage")),
                                currentTime)
                );
            }

        }

        public String trimString(String string) {
            final int length = 20;
            if(string.length() > length) {
                return string.substring(0, length) + "...";
            }
            return string;
        }
    }
}