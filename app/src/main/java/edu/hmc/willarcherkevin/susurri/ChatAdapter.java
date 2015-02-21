package edu.hmc.willarcherkevin.susurri;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
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

    public ChatAdapter(Context context){
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("commentObject");
                query.whereEqualTo("room", "mainroom");
                query.orderByAscending("createdAt");
                return query;
            }
        });
        
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.message, null);
        }

        super.getItemView(object, v, parent);

        TextView commentLine = (TextView) v.findViewById(R.id.line);
        commentLine.setText(object.getString("comment"));

        Date time = object.getCreatedAt();
        Format formatter = new SimpleDateFormat("HH:mm");
        String s = formatter.format(time);

        TextView timeLine = (TextView) v.findViewById(R.id.secondline);
        timeLine.setText(s);

        return v;
    }
}
