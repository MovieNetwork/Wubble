package com.proxima.Wubble.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.proxima.Wubble.R;

import java.io.ByteArrayOutputStream;


public class SignUpActivity extends Activity {

    protected EditText editUserName;
    protected EditText editPassword;
    protected EditText editMail;
    protected Button signUpButton;
    protected Button loginButton;

    private Context context;

    private boolean isTransferComplete = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        context = this;

        editUserName = (EditText) findViewById(R.id.signUpEditUsername);
        editPassword = (EditText) findViewById(R.id.signUpEditPassword);
        editMail = (EditText) findViewById(R.id.signUpEditMail);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        loginButton = (Button) findViewById(R.id.signUpPageLoginButton);


        // signup button here
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //setProgressBarIndeterminateVisibility(true);
                //get String values from editTexts
                final String newUserName = editUserName.getText().toString().trim();
                String newPassword = editPassword.getText().toString().trim();
                String newMail = editMail.getText().toString().trim();
                if (isTransferComplete) {


//
                    // Handle empty credentials
                    if (newUserName.isEmpty() | newPassword.isEmpty() | newMail.isEmpty()) {
                        Toast.makeText(SignUpActivity.this, "Fill all boxes", Toast.LENGTH_SHORT).show();
                    } else {
                        isTransferComplete = false;
                        // create a new Parse User
                        final ParseUser newUser = new ParseUser();

                        newUser.setUsername(newUserName);
                        newUser.setPassword(newPassword);
                        newUser.setEmail(newMail);
                        newUser.put("hasPicture", true);

                        ////////////////////////////
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                                R.drawable.defaultpic);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                        byte[] image = stream.toByteArray();


                        final ParseFile file = new ParseFile("default.png", image);
                        try {
                            file.save();
                            newUser.put("profilepic", file);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        if (connectionCheck()) {
                            // Signing up new Parse User
                            newUser.signUpInBackground(new SignUpCallback() {
                                public void done(ParseException e) {
                                    isTransferComplete = true;
                                    // seems like signup is succesfull
                                    if (e == null) {
                                        Toast.makeText(SignUpActivity.this, "Sign Up succesful", Toast.LENGTH_SHORT);

                                        // also add this user to FollowRelations

                                        ParseObject newFollowEntry = new ParseObject("FollowRelations");
                                        newFollowEntry.put("Username", newUserName);
                                        newFollowEntry.saveInBackground();

                                        // lead user to feed page
                                        //setProgressBarIndeterminateVisibility(false);
                                        Intent toMain = new Intent(SignUpActivity.this, FeedActivity.class);
                                        startActivity(toMain);
                                        finish();
                                    }
                                    // there is a problem in signing up . alert user
                                    else {
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SignUpActivity.this);
                                        alertBuilder.setMessage(e.getMessage());
                                        alertBuilder.setTitle("Oops..");
                                        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        AlertDialog alertDialog = alertBuilder.create();
                                        alertDialog.show();

                                    }
                                    // there is a problem in signing up . alert user

                                }
                            });
                        } else {
                            isTransferComplete = true;
                            String mMessage = "Please check your internet connection";
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SignUpActivity.this);
                            alertBuilder.setMessage(mMessage);
                            alertBuilder.setTitle("Oops..");
                            alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alertDialog = alertBuilder.create();
                            alertDialog.show();
                        }


                    }
                }
            }
        });


        // handle login button click here
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTransferComplete) {
                    Intent toLogin = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(toLogin);
                    finish();
                }
            }
        });//


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public boolean connectionCheck() {
        boolean result = false;

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connManager != null) {
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mWifi != null) {
                if (mWifi.isConnected()) result = true;

            }
            if (mMobile != null) {
                if (mMobile.isConnected()) result = true;
            }
        } else if (connManager == null) result = false;

        return result;
    }
}
