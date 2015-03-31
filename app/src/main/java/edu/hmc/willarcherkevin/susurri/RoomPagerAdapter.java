package edu.hmc.willarcherkevin.susurri;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.parse.FindCallback;
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

        // we've created a box by manually putting in an upper right and bottom
        // left coordinate. If the current location falls within the area of the box,
        // then put user into specified chatroom
        double lat =  activity.mLastLocation.getLatitude();
        double lng =  activity.mLastLocation.getLongitude();
        roomList = new ArrayList<String>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("RoomObject");

        Log.i("LAT: ", "" + lat);
        Log.i("LNG: ", "" + lng);

        query.whereLessThan("bl_lat", lat);
        query.whereGreaterThan("ur_lat", lat);

        query.whereLessThan("bl_lng", lng);
        query.whereGreaterThan("ur_lng", lng);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                for (ParseObject obj : parseObjects){
                    roomList.add(obj.getString("name"));
                }
                if (roomList.size() == 0){
                    roomList.add(activity.getString(R.string.default_room));
                }
                activity.startRoom();
            }
        });
    }


    @Override
    public Fragment getItem(int i) {
        ChatRoomFragment fragment = new ChatRoomFragment();
        fragment.setRoom(roomList.get(i));
        Log.i("app:",fragment.getRoom());
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return roomList.get(position);
    }


    @Override
    public int getCount() {
        return roomList.size();
    }
}
