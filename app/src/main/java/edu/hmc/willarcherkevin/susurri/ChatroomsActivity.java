package edu.hmc.willarcherkevin.susurri;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
public class ChatroomsActivity extends FragmentActivity implements View.OnClickListener{

    RoomPagerAdapter roomPagerAdapter;
    ViewPager mViewPager;

    Button mainButton;
    EditText mainEditText;

    static ParseUser user;

    public static ParseUser getUser(){return user;}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.room_pages);

        // Initialize the Parse SDK.
        Parse.initialize(this, "9nWnCUTdcZrrXtlGQKOjgPJWayPRKyMSQzU2bXhX", "dCjilcjkIqYAlyx55CIwFqyVjzl1GvKAuML64sXo");

        user = ParseUser.getCurrentUser();
        if (user != null){
            ParseUser.getCurrentUser().saveInBackground();
        }

        ParseACL defaultACL = new ParseACL();
//        Optionally enable public read access.
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);

        setupNotifications();

        // allows read and write access to all users
        ParseACL postACL = new ParseACL(ParseUser.getCurrentUser());
        postACL.setPublicReadAccess(true);
        postACL.setPublicWriteAccess(true);

        // 2. Access the Button defined in layout XML
        // and listen for it here
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        // 3. Access the EditText defined in layout XML
        mainEditText = (EditText) findViewById(R.id.main_edittext);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        roomPagerAdapter =
                new RoomPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(roomPagerAdapter);

    }

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

//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//    }

    @Override
    public void onClick(View v) {
        int pos = mViewPager.getCurrentItem();
        String room = mViewPager.getAdapter().getPageTitle(pos).toString();
        sendtoParse(room);
        sendToChannel(room);

        mainEditText.setText("");
    }

    private void sendtoParse(String room){

        String comment = mainEditText.getText().toString();

        ParseObject commentObject = new ParseObject("commentObject");

        commentObject.put("comment", comment);
        commentObject.put("room", room);
        commentObject.saveInBackground();
    }

    public void sendToChannel(String room) {
        // Also add that value to the list shown in the ListView
        String comment = mainEditText.getText().toString();

        // TODO Auto-generated method stub
        JSONObject obj;
        try {
            obj =new JSONObject();

            obj.put("action","edu.hmc.willarcherkevin.susurri." + room.toUpperCase().replaceAll("\\s","_"));

            ParsePush push = new ParsePush();
            ParseQuery query = ParseInstallation.getQuery();


            // Notification for Android users
            push.setChannel("NewChatRoom");
            push.setQuery(query);
            push.setData(obj);
            push.sendInBackground();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
