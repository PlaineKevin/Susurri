package edu.hmc.willarcherkevin.susurri;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by archerwheeler on 2/17/15.
 */

public class ChatAdapter extends ParseQueryAdapter {

    static final int MAX_LENGTH = 15;

    public ChatAdapter(Context context, final String room){
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("commentObject");
                query.whereEqualTo("room", room);
                query.orderByDescending("createdAt");
                query.setLimit(MAX_LENGTH);
                return query;
            }
        });
        this.setPaginationEnabled(false);

    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {

//        if(object.getString("userid") == null){
//            v = View.inflate(getContext(), R.layout.message, null);

        String chosenAvatar = object.getString("avatar");
        Log.d("avatar", chosenAvatar);
        int id = 0;
        if (chosenAvatar.equals("bear")) {
            id = R.drawable.walrus;
        } else if (chosenAvatar.equals("cow")) {
            id = R.drawable.cow;
        } else if (chosenAvatar.equals("giraffe")) {
            id = R.drawable.giraffe;
        } else if (chosenAvatar.equals("monkey")) {
            id = R.drawable.monkey;
        } else if (chosenAvatar.equals("narwal")) {
            id = R.drawable.narwal;
        } else if (chosenAvatar.equals("walrus")) {
            id = R.drawable.walrus;
        } else {
            id = R.drawable.walrus;
        }

        if (object.getString("userid").equals(ChatroomsActivity.androidId) ) {



            v = View.inflate(getContext(), R.layout.right_message, null);
            ImageView userIcon = (ImageView) v.findViewById(R.id.right_icon);
            userIcon.setImageResource(id);
        }
        else {
            v = View.inflate(getContext(), R.layout.message, null);
            ImageView userIcon = (ImageView) v.findViewById(R.id.icon);
            userIcon.setImageResource(id);
        }



        super.getItemView(object, v, parent);

        TextView commentLine = (TextView) v.findViewById(R.id.line);
        commentLine.setText(object.getString("screenName") +": " + object.getString("comment"));

        // set the id to icon right/ icon left in right_message xml and left_message xml

//        int id = R.drawable.walrus;


        Date time = object.getCreatedAt();

        Format formatter = new SimpleDateFormat("HH:mm");
        String s = formatter.format(time);

        TextView timeLine = (TextView) v.findViewById(R.id.secondline);
        timeLine.setText(s);

        return v;

    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }


    @Override
    public ParseObject getItem(int i){
        int flip = getCount() - i - 1;
        return super.getItem(flip);
    }

}
