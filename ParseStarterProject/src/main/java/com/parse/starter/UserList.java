package com.parse.starter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UserList extends AppCompatActivity {

    ArrayList<String> usernames;
    ArrayAdapter arrayAdapter;
    ListView listView;
    Bitmap bitmap;

    public void checkForImages() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.whereEqualTo("recipientUsername", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> images, ParseException e) {
                if (e == null) {
                    if (images.size() > 0) {
                        for (ParseObject image : images) {
                            image.deletInBackground();
                            ParseFile file = image.getParseFile("image");
                            byte[] byteArray = new byte[0];
                            try {
                                byteArray = file.getData();
                                bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }

                            if (bitmap != null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserList.this);
                                builder.setTitle("You have a message!");
                                TextView content = new TextView(UserList.this);
                                content.setGravity(Gravity.CENTER_HORIZONTALR);
                                content.setText(image.getString("senderUsername") + " has sent you a message");
                                builder.setView(content);
                                builder.setPositiveButton("View", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.i("AppInfo", "Display image");


                                        AlertDialog.Builder builder = new AlertDialog.Builder(UserList.this);
                                        builder.setTitle("Your message!");
                                        ImageView content = new ImageView(UserList.this);
                                        content.setImageBitmap(bitmap);
                                        builder.setView(content);
                                        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Log.i("AppInfo", "Image Closed");
                                            }
                                        });
                                        builder.show();


                                    }
                                });
                                builder.show();
                            }
                        }
                    }

                } else {
                    Log.i("score", "Error" + e.getMessage());
                }
            }
        });

    }

    protected void OnActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                ParseFile file = new ParseFile("image.png", byteArray);

                ParseObject image = new ParseObject("image");
                image.put("senderUsername", ParseUser.getCurrentUser().getUsername());
                image.put("recipientUsername", usernames.get(requestCode));
                image.put("image", file);
                image.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Image sent successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Image was not sent!", Toast.LENGTH_LONG).show();
                        }
                    }
                })
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);


        usernames = new ArrayList<String>();
        ListView = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usernames);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, position);
            }
        });

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


        checkForImages();


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
