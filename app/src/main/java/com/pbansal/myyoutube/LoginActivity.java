package com.pbansal.myyoutube;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import java.io.IOException;


/**
 * Created by pbansal on 10/16/15.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private String _accessToken;
    private SignInButton googleSignInBtn;
    private Toolbar toolbar;
//    EditText username;
//    EditText password;
//    Button login, logout;
    TextView loginStatus;
    String mEmail;
    GoogleCredential credential;

    final static int REQUEST_CODE_PICK_ACCOUNT = 1000;
    final static int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

    private final static String PROFILE_ME = "https://www.googleapis.com/auth/plus.me";
    private final static String YOUTUBE_SCOPE = "https://www.googleapis.com/auth/youtube";
    private final static String SCOPE = "oauth2:" + PROFILE_ME + " " + YOUTUBE_SCOPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginStatus = (TextView) findViewById(R.id.login_status);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
//        username = (EditText) findViewById(R.id.username);
//        password = (EditText) findViewById(R.id.password);
//        login = (Button) findViewById(R.id.login_button);
//        logout = (Button) findViewById(R.id.google_sign_out_button);
        googleSignInBtn = (SignInButton) findViewById(R.id.google_sign_in_button);
        setSupportActionBar(toolbar);
        googleSignInBtn.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getBoolean("signout")) {
                logout();
            }
        }

//        logout.setOnClickListener(this);
    }

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
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.google_sign_in_button) {
            loginStatus.setText("Signing in.. Please Wait");
            pickUserAccount();
        }
//        if (v.getId() == R.id.google_sign_out_button) {
//            loginStatus.setText("Logging Out");
//            logout();
//        }

    }

    public void logout() {
        loginStatus.setText("Logging Out...");
        Log.i(TAG, "Logging Out in 5 secs.");
        try {
            new ClearTokenTask();
            loginStatus.setText("Logged Out.");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void pickUserAccount() {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);

    }

    public void getUserName() {
        if (mEmail == null) {
            pickUserAccount();
        } else {
            _accessToken = null;
            new RetriveTokenTask().execute();
        }
    }

    public void handleException(final Exception e) {
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            LoginActivity.this,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        return false;
    }

    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT && resultCode == RESULT_OK) {
            mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            getUserName();
        } else {
            Toast.makeText(this, "Select Account to Login", Toast.LENGTH_SHORT).show();
        }
    }

    private class RetriveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), mEmail, SCOPE);
            } catch (UserRecoverableAuthException e) {
                handleException(e);
                Log.i(TAG, e.getMessage());
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            _accessToken = s;
            if (_accessToken != null) {
                Log.i(TAG, "Access Token - " + _accessToken);
                credential = new GoogleCredential().setAccessToken(_accessToken);
                new YouTubeAPI(credential);
                startMainActivity();
            }
        }

        public void startMainActivity() {
            Intent goToMainActivity = new Intent(LoginActivity.this, MainActivity.class);
            goToMainActivity.putExtra("USER", mEmail);
            goToMainActivity.putExtra("ACCESS_TOKEN", _accessToken);
            startActivity(goToMainActivity);
            finish();
        }
    }

    private class ClearTokenTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                String token = GoogleAuthUtil.getToken(getApplicationContext(),mEmail,SCOPE);
                GoogleAuthUtil.clearToken(getApplicationContext(), token);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (GooglePlayServicesAvailabilityException e) {
                Log.e(TAG, e.getMessage());
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }
    }

}
