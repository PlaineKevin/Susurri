package edu.hmc.willarcherkevin.susurri;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends ActionBarActivity implements View.OnClickListener{

    Button mainButton;
    EditText mainEditText;

    ListView mainListView;
    ArrayAdapter mArrayAdapter;
    ArrayList mNameList;

    private ParseQueryAdapter<ParseObject> mainAdapter;
    //update interval time
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this, "9nWnCUTdcZrrXtlGQKOjgPJWayPRKyMSQzU2bXhX", "dCjilcjkIqYAlyx55CIwFqyVjzl1GvKAuML64sXo");

        ParseUser.enableAutomaticUser();
        ParseUser.getCurrentUser().saveInBackground();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);


        //Non-Parse thingys
        mNameList = new ArrayList();

        // 2. Access the Button defined in layout XML
        // and listen for it here
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        // 3. Access the EditText defined in layout XML
        mainEditText = (EditText) findViewById(R.id.main_edittext);

        // 4. Access the ListView
        mainListView = (ListView) findViewById(R.id.main_listview);

        ParseQueryAdapter.QueryFactory<ParseObject> factory =
                new ParseQueryAdapter.QueryFactory<ParseObject>() {
                    public ParseQuery create() {
                        ParseQuery query = new ParseQuery("commentObject");
                        query.whereEqualTo("room", "mainroom");
                        query.orderByAscending("time");
                        return query;
                    }
                };


        mainAdapter = new ParseQueryAdapter<ParseObject>(this, factory);
        mainAdapter.setTextKey("comment");

        mainListView.setAdapter(mainAdapter);
        mainAdapter.loadObjects();

        //Start refreshing app
        mHandler = new Handler();
        mStatusChecker.run();
    }

    //Gets the app to update every 5 sec
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            mainAdapter.loadObjects();
            mainAdapter.notifyDataSetChanged();
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Old code. Not used
    private void updateChat(String room){
        //clear the list, will get everything from server in sec
        mNameList.clear();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("commentObject");
        query.whereEqualTo("room", room);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> commentList, ParseException e) {
                if (e == null) {
                    for (ParseObject c: commentList){
                        mNameList.add(c.getString("comment"));
                    }

                    mArrayAdapter.notifyDataSetChanged();

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        // Also add that value to the list shown in the ListView
        String comment = mainEditText.getText().toString();
        mainEditText.setText("");

        ParseObject commentObject = new ParseObject("commentObject");

        long time = System.currentTimeMillis();
        commentObject.put("comment", comment);
        commentObject.put("time", time);
        commentObject.put("room", "mainroom");
        commentObject.saveInBackground();

        mainAdapter.loadObjects();
        mainAdapter.notifyDataSetChanged();

    }
}
