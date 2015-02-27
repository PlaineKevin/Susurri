package edu.hmc.willarcherkevin.susurri;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ChatActivity extends Fragment implements View.OnClickListener{
    public static final String ARG_OBJECT = "object";

    Button mainButton;
    EditText mainEditText;

    ListView mainListView;
    ArrayList mNameList;

    private ChatAdapter mainAdapter;

    MyCustomReceiver updateReceiver;
    //defult room
    //defined here b/c fragments should have defult constructors
    String room = "mainroom";

    //Fragments are required to have empty contructors
    //so use a setRoom method to set the room
    public void setRoom(String r){
        room = r;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(
                R.layout.activity_chat, container, false);

        // 2. Access the Button defined in layout XML
        // and listen for it here
        mainButton = (Button) rootView.findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        // 3. Access the EditText defined in layout XML
        mainEditText = (EditText) rootView.findViewById(R.id.main_edittext);

        // 4. Access the ListView
        mainListView = (ListView) rootView.findViewById(R.id.main_listview);

        mainAdapter = new ChatAdapter(getActivity());
        mainListView.setAdapter(mainAdapter);
        mainAdapter.loadObjects();

        return rootView;
    }


    @Override
    public void onPause() {
        super.onPause();
        //unregister our receiver
        getActivity().unregisterReceiver(updateReceiver);
    }

    @Override
    public void onResume(){
        super.onResume();
        updateReceiver = new MyCustomReceiver(this, room);
        IntentFilter intentFilter = new IntentFilter(
                "edu.hmc.willarcherkevin.susurri.UPDATE_STATUS");
        getActivity().registerReceiver(updateReceiver, intentFilter);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_chat, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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
        commentObject.put("room", room);
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
