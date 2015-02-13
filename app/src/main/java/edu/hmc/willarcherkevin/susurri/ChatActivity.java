package edu.hmc.willarcherkevin.susurri;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;


public class ChatActivity extends ActionBarActivity implements View.OnClickListener{

    Button mainButton;
    EditText mainEditText;

    ListView mainListView;
    ArrayAdapter mArrayAdapter;
    ArrayList mNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat);

            // Enable Local Datastore.
            Parse.enableLocalDatastore(this);

            // Add your initialization code here
            Parse.initialize(this, "9nWnCUTdcZrrXtlGQKOjgPJWayPRKyMSQzU2bXhX", "dCjilcjkIqYAlyx55CIwFqyVjzl1GvKAuML64sXo");

            ParseUser.enableAutomaticUser();
            ParseACL defaultACL = new ParseACL();
            // Optionally enable public read access.
            // defaultACL.setPublicReadAccess(true);
            ParseACL.setDefaultACL(defaultACL, true);


            //Non-Parse thingys
            mNameList = new ArrayList();
            ParseObject textThread = new ParseObject("textThread");
            textThread.put("thread", mNameList);
            textThread.saveInBackground();

            textThread.fetchInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        // Success!
                        mArrayAdapter.notifyDataSetChanged();
                    } else {
                        // Failure!x
                    }
                }
            });




            // 2. Access the Button defined in layout XML
            // and listen for it here
            mainButton = (Button) findViewById(R.id.main_button);
            mainButton.setOnClickListener(this);

            // 3. Access the EditText defined in layout XML
            mainEditText = (EditText) findViewById(R.id.main_edittext);

            // 4. Access the ListView
            mainListView = (ListView) findViewById(R.id.main_listview);

            // Create an ArrayAdapter for the ListView
            mArrayAdapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1,
                    mNameList);
            // Set the ListView to use the ArrayAdapter
            mainListView.setAdapter(mArrayAdapter);
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

    @Override
    public void onClick(View v) {
        // Also add that value to the list shown in the ListView
        String comment = mainEditText.getText().toString();
        mNameList.add(comment);
        mArrayAdapter.notifyDataSetChanged();


    }
}
