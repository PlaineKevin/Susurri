package edu.hmc.willarcherkevin.susurri;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by archerwheeler on 2/26/15.
 */
public class RoomPagerAdapter extends FragmentStatePagerAdapter {


    public RoomPagerAdapter(FragmentManager fm){
        super(fm);
    }


    @Override
    public Fragment getItem(int i) {
        ChatActivity fragment = new ChatActivity();
        if (i == 1){
            fragment.setRoom("otherroom");
        }
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(ChatActivity.ARG_OBJECT, i + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
