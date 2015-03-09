package edu.hmc.willarcherkevin.susurri;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by archerwheeler on 2/26/15.
 */
public class RoomPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> roomList;

    public RoomPagerAdapter(FragmentManager fm, final ChatroomsActivity activity){
        super(fm);

        double lat =  activity.mLastLocation.getLatitude();
        double lng =  activity.mLastLocation.getLongitude();
        ParseGeoPoint point = new ParseGeoPoint(lat, lng);
        roomList = new ArrayList<String>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("RoomObject");
        query.whereWithinKilometers("location", point, 1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                for (ParseObject obj : parseObjects){
                    roomList.add(obj.getString("name"));
                }
                activity.startRoom();
            }
        });
        //Hard code rooms by defult
        roomList.add("Main Room");
    }


    @Override
    public Fragment getItem(int i) {
        ChatRoomFragment fragment = new ChatRoomFragment();
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
