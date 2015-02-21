package edu.hmc.willarcherkevin.susurri;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class ChatActivity extends ActionBarActivity implements View.OnClickListener{

    Button mainButton;
    EditText mainEditText;

    ListView mainListView;

    private ChatAdapter mainAdapter;
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

        // 2. Access the Button defined in layout XML
        // and listen for it here
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        // 3. Access the EditText defined in layout XML
        mainEditText = (EditText) findViewById(R.id.main_edittext);

        // 4. Access the ListView
        mainListView = (ListView) findViewById(R.id.main_listview);


        setupFrontend();

    }

    private void setupFrontend(){
        mainAdapter = new ChatAdapter(this);
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
            updateChat();
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
    private void updateChat(){
        mainAdapter.loadObjects();
        mainAdapter.notifyDataSetChanged();
    }


    private void sendtoParse(){
        String comment = mainEditText.getText().toString();
        mainEditText.setText("");

        ParseObject commentObject = new ParseObject("commentObject");

        commentObject.put("comment", comment);
        commentObject.put("room", "mainroom");
        commentObject.saveInBackground();

        updateChat();
    }

    @Override
    public void onClick(View v) {
        sendtoParse();
    }
}
