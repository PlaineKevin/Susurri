package edu.hmc.willarcherkevin.susurri;

import android.location.Criteria;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.LocationCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

/**
 * Created by archerwheeler on 2/26/15.
 */
public class RoomPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> roomList;

    private ParseGeoPoint gPoint = new ParseGeoPoint(0,0);

    public RoomPagerAdapter(FragmentManager fm){
        super(fm);
        roomList = new ArrayList<String>();
        getLocation();

        //Hard code rooms by defult
        roomList.add("Main Room");
        roomList.add("Secret Room");
    }

    public void updateRooms(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("roomObject");
        query.whereWithinKilometers("location", gPoint, 1);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("Error", "Find location error");
                } else {
                    roomList.set(0, object.getString("room"));
                }
            }
        });
    }

    public void getLocation(){
        // Criteria defaults to web settings
        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_LOW);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
        ParseGeoPoint.getCurrentLocationInBackground(10000, criteria, new LocationCallback() {
            @Override
            public void done(ParseGeoPoint parseGeoPoint, ParseException e) {
                if (e == null) {
                    gPoint = parseGeoPoint;
                    Log.d("Hello", "getLocation works");


                } else {
                    Log.e("Location", "Error Getting Location", e);
                }
            }

        });

    }


    @Override
    public Fragment getItem(int i) {
        ChatActivity fragment = new ChatActivity();
        fragment.setRoom(roomList.get(i));
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return roomList.get(position);
    }


    @Override
    public int getCount() {
        return 2;
    }
}
