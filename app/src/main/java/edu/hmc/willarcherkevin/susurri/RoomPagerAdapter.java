package edu.hmc.willarcherkevin.susurri;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by archerwheeler on 2/26/15.
 */
public class RoomPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> roomList;

    public RoomPagerAdapter(FragmentManager fm){
        super(fm);
        roomList = new ArrayList<String>();

        //Hard code rooms by defult
        roomList.add("Main Room");
        roomList.add("Second Room");
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
