package edu.hmc.willarcherkevin.susurri;

import android.app.Application;
import android.content.res.Configuration;
import android.provider.Settings;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by archerwheeler on 3/29/15.
 */
public class Susurri extends Application {

    //Store the device unique user ID
    public static String androidId;

    //Create a global ParseUser object
    public ParseUser theUser;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        setUpUsers();
        // Initialize the Parse SDK.
        Parse.initialize(this, "9nWnCUTdcZrrXtlGQKOjgPJWayPRKyMSQzU2bXhX", "dCjilcjkIqYAlyx55CIwFqyVjzl1GvKAuML64sXo");
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        // allows read and write access to all users
        ParseACL postACL = new ParseACL(ParseUser.getCurrentUser());
        postACL.setPublicReadAccess(true);
        postACL.setPublicWriteAccess(true);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        logoutUsers();
    }


    // setUpUsers first checks to see if a ParseUser exists and if so it will log the user in
    // otherwise, it creates a new ParseUser with the username and password being the androidID
    private void setUpUsers() {
        ParseUser.logInInBackground(androidId, androidId, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                } else {
                    theUser = new ParseUser();
                    theUser.setUsername(androidId);
                    theUser.setPassword(androidId);

                    // other fields can be set just like with ParseObject
                    theUser.put("avatar", "Snail");

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Let them use the app now.
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                            }
                        }
                    });
                }
            }
        });
    }

    private void resumeUsers() {
        theUser = ParseUser.getCurrentUser();
        if (theUser != null) {
            // Cool Beans the user is restored
        } else {
            setUpUsers();
        }
    }

    private void logoutUsers() {
        ParseUser.logOut();
        theUser = ParseUser.getCurrentUser(); // this will now be null
    }

}
