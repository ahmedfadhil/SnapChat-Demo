package com.parse.starter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {

    ArrayList<String> usernames;
    ArrayAdapter arrayAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);


        usernames = new ArrayList<String>();
        ListView = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usernames);
        listView.setAdapter(arrayAdapter);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        usernames.clear();
                        for (ParseUser user : objects) {
                            usernames.add(user.getUsername());
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                } else {

                }
            }
        });


//        usernames = new ArrayList<String>();
//        listView = (ListView) findViewById(R.id.listView);
//
//        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usernames);
//        listView.setAdapter(arrayAdapter);
//
//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
//        query.findInBackground(new FindCallback<ParseUser>() {
//            public void done(List<ParseUser> objects, ParseException e) {
//                if (e == null) {
//
//                    if (objects.size() > 0) {
//
//                        usernames.clear();
//
//                        for (ParseUser user : objects) {
//
//                            usernames.add(user.getUsername());
//
//                        }
//
//                        arrayAdapter.notifyDataSetChanged();
//
//                    }
//
//
//                } else {
//                    // Something went wrong.
//                }
//            }
//        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
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
}
