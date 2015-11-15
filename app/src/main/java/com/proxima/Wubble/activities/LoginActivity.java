package com.proxima.Wubble.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.proxima.Wubble.R;


public class LoginActivity extends Activity {

    private Context context;

    EditText editUserName;
    EditText editPassword;
    Button signupButton;
    Button loginButton;

    private boolean isTransferComplete = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        editUserName = (EditText) findViewById(R.id.loginPageUsername);
        editPassword = (EditText) findViewById(R.id.loginPagePassword);
        signupButton = (Button) findViewById(R.id.loginPageSignupButton);
        loginButton = (Button) findViewById(R.id.loginPageLoginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isTransferComplete) {

                    //setProgressBarIndeterminateVisibility(true);
                    String userName = editUserName.getText().toString().trim();
                    String passWord = editPassword.getText().toString().trim();

                    // not all credentials are given
                    if (userName.isEmpty() | passWord.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Fill all the boxes", Toast.LENGTH_SHORT).show();
                    } else {
                        isTransferComplete = false;
                        if (connectionCheck()) {
                            ParseUser.logInInBackground(userName, passWord, new LogInCallback() {
                                public void done(ParseUser user, ParseException e) {
                                    // Login succesfull
                                    if (user != null) {
                                        //setProgressBarIndeterminateVisibility(false);
                                        Toast.makeText(LoginActivity.this, "Login succesful", Toast.LENGTH_SHORT).show();
                                        Intent toMain = new Intent(LoginActivity.this, FeedActivity.class);
                                        isTransferComplete = true;
                                        startActivity(toMain);
                                        finish();
                                    }
                                    // problem while login
                                    else {
                                        isTransferComplete = true;
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this);
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
                                }
                            });
                        } else {
                            isTransferComplete = true;
                            String mMessage = "Please check your internet conection";
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this);
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

        // go to signup page
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isTransferComplete) {
                    Intent toSignup = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(toSignup);
                    finish();
                }
            }
        });


    }
//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
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
