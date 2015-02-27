package edu.hmc.willarcherkevin.susurri;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

/**
 * Created by archerwheeler on 2/26/15.
 */
public class ChatroomsActivity extends FragmentActivity {

    RoomPagerAdapter roomPagerAdapter;
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.room_pages);

        // Initialize the Parse SDK.
        Parse.initialize(this, "9nWnCUTdcZrrXtlGQKOjgPJWayPRKyMSQzU2bXhX", "dCjilcjkIqYAlyx55CIwFqyVjzl1GvKAuML64sXo");

        ParseUser user = ParseUser.getCurrentUser();
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

}
