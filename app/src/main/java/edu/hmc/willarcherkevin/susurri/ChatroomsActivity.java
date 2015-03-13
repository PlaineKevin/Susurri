package edu.hmc.willarcherkevin.susurri;

import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by archerwheeler on 2/26/15.
 */
public class ChatroomsActivity extends ActionBarActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //Controls room fragments
    RoomPagerAdapter roomPagerAdapter;
    ViewPager mViewPager;

    //Outside of the fragments
    Button mainButton;
    EditText mainEditText;

    //For location data
    public Location mLastLocation;
    protected GoogleApiClient mGoogleApiClient;

    //loading spinner
    private ProgressBar progress;

    //TODO replace with Parse user
    //Unique device ID
    public static String androidId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_pages);

        //Get unique device ID
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize the Parse SDK.
        Parse.initialize(this, "9nWnCUTdcZrrXtlGQKOjgPJWayPRKyMSQzU2bXhX", "dCjilcjkIqYAlyx55CIwFqyVjzl1GvKAuML64sXo");
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        setupNotifications();

        // allows read and write access to all users
        ParseACL postACL = new ParseACL(ParseUser.getCurrentUser());
        postACL.setPublicReadAccess(true);
        postACL.setPublicWriteAccess(true);


        // Set up button and text edit
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);
        mainEditText = (EditText) findViewById(R.id.main_edittext);

        progress = (ProgressBar) findViewById(R.id.progress);
//        ProgressDialog progress = new ProgressDialog(this);
        buildGoogleApiClient();


    }

    //Called by onConnect once location has been found
    private void createRooms(){
        roomPagerAdapter =
                new RoomPagerAdapter(
                        getSupportFragmentManager(), this);
    }

    //Call back function in Pager Aptr starts the display of
    //The rooms once the device knows which rooms it is in
    public void startRoom(){
        //Display rooms pulled from parse cloud
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(roomPagerAdapter);

        //Restore view
        progress.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
    }


    //Builds Google API Client so that location can be found
    //Location is determined with connection callback interface
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //Set up push notifications
    public void setupNotifications() {
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        // Specify an Activity to handle all pushes by default.
        PushService.setDefaultPushCallback(this, ChatroomsActivity.class);
        ParseUser.enableAutomaticUser();

        // Save the current Installation to Parse.
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParsePush.subscribeInBackground("NewChatRoom", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }

    //Called when "Post" button is pressed
    @Override
    public void onClick(View v) {
        //Find what chat room is displayed
        int pos = mViewPager.getCurrentItem();
        String room = mViewPager.getAdapter().getPageTitle(pos).toString();

        //Send comment to parse and send push notification
        sendtoParse(room);
        sendToChannel(room);

        //clear textedit
        mainEditText.setText("");
    }

    //Send comment info to parse
    private void sendtoParse(String room){
        String comment = mainEditText.getText().toString();
        ParseObject commentObject = new ParseObject("commentObject");

        commentObject.put("comment", comment);
        commentObject.put("room", room);
        commentObject.put("userid", androidId);
        commentObject.saveInBackground();
    }

    public void sendToChannel(String room) {

        // TODO Auto-generated method stub
        JSONObject obj;
        try {
            //Create notification to everyone in current room
            obj =new JSONObject();
            obj.put("action","edu.hmc.willarcherkevin.susurri." + room.toUpperCase().replaceAll("\\s","_"));

            ParsePush push = new ParsePush();
            ParseQuery query = ParseInstallation.getQuery();

            //Send notification for Android users
            push.setChannel("NewChatRoom");
            push.setQuery(query);
            push.setData(obj);
            push.sendInBackground();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        update();
    }

    private void update(){
        //hide current view and creating loading ico
        if (mViewPager != null) mViewPager.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        //Check if connected
        if (!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }

        //otherwise update location directly
        else{
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            createRooms();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Stop looking for location
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Get last know location from android System
        //Should be accurate and battery efficient
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.i("Location", "CONNECTED");

        //If location found query parse for rooms nearby
        if (mLastLocation != null) {
            createRooms();
        }
        //No location found. User probably has GPS turned off
        else {
            Log.i("Location", "NULL location");
            //TODO Display message asking user to turn on location settings
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_refresh:
                onStart();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("Location", "Connection suspended");
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("Location", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
}
