package edu.hmc.willarcherkevin.susurri;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ChatActivity extends ActionBarActivity implements View.OnClickListener{

    Button mainButton;
    EditText mainEditText;

    ListView mainListView;
    ArrayList mNameList;

    private ChatAdapter mainAdapter;

    MyCustomReceiver updateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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


        // 2. Access the Button defined in layout XML
        // and listen for it here
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        // 3. Access the EditText defined in layout XML
        mainEditText = (EditText) findViewById(R.id.main_edittext);

        // 4. Access the ListView
        mainListView = (ListView) findViewById(R.id.main_listview);

        setupNotifications();
        setupFrontend();
    }

    private void setupFrontend(){
        mainAdapter = new ChatAdapter(this);
        mainListView.setAdapter(mainAdapter);
        mainAdapter.loadObjects();
    }

    public void setupNotifications() {
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        // Specify an Activity to handle all pushes by default.
        PushService.setDefaultPushCallback(this, ChatActivity.class);
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


    @Override
    protected void onPause() {
        super.onPause();
        //unregister our receiver
        this.unregisterReceiver(updateReceiver);
    }

    @Override
    protected  void onResume(){
        super.onResume();
        updateReceiver = new MyCustomReceiver(this);
        IntentFilter intentFilter = new IntentFilter(
                "edu.hmc.willarcherkevin.susurri.UPDATE_STATUS");
        this.registerReceiver(updateReceiver, intentFilter);
    }


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

    public void updateChat(){
        mainAdapter.loadObjects();
        mainAdapter.notifyDataSetChanged();
        mainListView.smoothScrollToPosition(0);
    }

    @Override
    public void onClick(View v) {
        sendtoParse();
        sendToChannel();

        mainEditText.setText("");
    }

    private void sendtoParse(){
        String comment = mainEditText.getText().toString();

        ParseObject commentObject = new ParseObject("commentObject");

        commentObject.put("comment", comment);
        commentObject.put("room", "mainroom");
        commentObject.saveInBackground();
    }

    public void sendToChannel() {
        // Also add that value to the list shown in the ListView
        String comment = mainEditText.getText().toString();

        // TODO Auto-generated method stub
        JSONObject obj;
        try {
            obj =new JSONObject();
            obj.put("action","edu.hmc.willarcherkevin.susurri.UPDATE_STATUS");

            ParsePush push = new ParsePush();
            ParseQuery query = ParseInstallation.getQuery();


            // Notification for Android users
            push.setChannel("NewChatRoom");
            push.setQuery(query);
            push.setData(obj);
            push.sendInBackground();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
