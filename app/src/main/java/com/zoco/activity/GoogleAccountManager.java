package com.zoco.activity;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.zoco.common.ZocoNetwork;
import com.zoco.common.ZocoPassword;
import com.zoco.obj.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.ListIterator;


/**
 * A base class to wrap communication with the Google Play Services PlusClient.
 */
public abstract class GoogleAccountManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, PlusClient.OnAccessRevokedListener {

    private static final String TAG = "NARA";//PlusBaseActivity.class.getSimpleName();

    // A magic number we will use to know that our sign-in error resolution activity has completed
    public static final int OUR_REQUEST_CODE = 49404;

    // A flag to stop multiple dialogues appearing for the user
    public boolean mAutoResolveOnFail;

    // A flag to track when a connection is already in progress
    public boolean mPlusClientIsConnecting = false;

    // This is the helper object that connects to Google Play Services.
    private GoogleApiClient mPlusClient;

    // The saved result from {@link #onConnectionFailed(ConnectionResult)}.  If a connection
    // attempt has been made, this is non-null.
    // If this IS null, then the connect method is still running.
    private ConnectionResult mConnectionResult;


    /**
     * Called when the {@link PlusClient} revokes access to this app.
     */
    protected abstract void onPlusClientRevokeAccess();

    /**
     * Called when the PlusClient is successfully connected.
     */
    protected abstract void onPlusClientSignIn();

    /**
     * Called when the {@link PlusClient} is disconnected.
     */
    protected abstract void onPlusClientSignOut();

    /**
     * Called when the {@link PlusClient} is blocking the UI.  If you have a progress bar widget,
     * this tells you when to show or hide it.
     */
    protected abstract void onPlusClientBlockingUI(boolean show);

    /**
     * Called when there is a change in connection state.  If you have "Sign in"/ "Connect",
     * "Sign out"/ "Disconnect", or "Revoke access" buttons, this lets you know when their states
     * need to be updated.
     */
    protected abstract void updateConnectButtonState();

    private Activity activity;
    private User user;

    public GoogleAccountManager(Context context, Activity activity) {
        mPlusClient = new GoogleApiClient.Builder(context).addApi(Plus.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                        // .setAccountName("users.account.name@gmail.com")
                .build();
        this.activity = activity;
    }

    /**
     * Try to sign in the user.
     */
    public void signIn() {
        if (!mPlusClient.isConnected()) {
            // Show the dialog as we are now signing in.
            setProgressBarVisible(true);
            // Make sure that we will start the resolution (e.g. fire the intent and pop up a
            // dialog for the user) for any errors that come in.
            mAutoResolveOnFail = true;
            // We should always have a connection result ready to resolve,
            // so we can start that process.
            if (mConnectionResult != null) {
                startResolution();
            } else {
                // If we don't have one though, we can start connect in
                // order to retrieve one.
                initiatePlusClientConnect();
            }
        }

        updateConnectButtonState();
    }

    /**
     * Connect the {@link PlusClient} only if a connection isn't already in progress.  This will
     * call back to {@link #onConnected(android.os.Bundle)} or
     * {@link #onConnectionFailed(com.google.android.gms.common.ConnectionResult)}.
     */
    public void initiatePlusClientConnect() {
        if (!mPlusClient.isConnected() && !mPlusClient.isConnecting()) {
            mPlusClient.connect();
        }
    }

    /**
     * Disconnect the {@link PlusClient} only if it is connected (otherwise, it can throw an error.)
     * This will call back to {@link #onDisconnected()}.
     */
    public void initiatePlusClientDisconnect() {
        if (mPlusClient.isConnected()) {
            mPlusClient.disconnect();
        }
    }

    /**
     * Sign out the user (so they can switch to another account).
     */
    public void signOut() {

        // We only want to sign out if we're connected.
        if (mPlusClient.isConnected()) {
            // Clear the default account in order to
            // allow the user to potentially choose a
            // different account from the account chooser.
            mPlusClient.clearDefaultAccountAndReconnect();

            // Disconnect from Google Play Services, then reconnect in order to restart the
            // process from scratch.
            initiatePlusClientDisconnect();

            Log.v(TAG, "Sign out successful!");
        }

        Log.v(TAG, "mPlusClient.isConnected() : " + mPlusClient.isConnected());

        updateConnectButtonState();
    }

    /**
     * Revoke Google+ authorization completely.
     */
//    public void revokeAccess() {
//
//        if (mPlusClient.isConnected()) {
//            // Clear the default account as in the Sign Out.
//            mPlusClient.clearDefaultAccountAndReconnect();
//
//            // Revoke access to this entire application. This will call back to
//            // onAccessRevoked when it is complete, as it needs to reach the Google
//            // authentication servers to revoke all tokens.
//            mPlusClient.(new GoogleApiClient().OnAccessRevokedListener() {
//                public void onAccessRevoked(ConnectionResult result) {
//                    updateConnectButtonState();
//                    onPlusClientRevokeAccess();
//                }
//            });
//        }
//
//    }
    public boolean isPlusClientConnecting() {
        return mPlusClientIsConnecting;
    }

    public void setProgressBarVisible(boolean flag) {
        mPlusClientIsConnecting = flag;
        onPlusClientBlockingUI(flag);
    }

    /**
     * A helper method to flip the mResolveOnFail flag and start the resolution
     * of the ConnectionResult from the failed connect() call.
     */
    private void startResolution() {
        try {
            // Don't start another resolution now until we have a result from the activity we're
            // about to start.
            mAutoResolveOnFail = false;
            // If we can resolve the error, then call start resolution and pass it an integer tag
            // we can use to track.
            // This means that when we get the onActivityResult callback we'll know it's from
            // being started here.
            mConnectionResult.startResolutionForResult(activity, OUR_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            // Any problems, just try to connect() again so we get a new ConnectionResult.
            mConnectionResult = null;
            initiatePlusClientConnect();
        }
    }

    /**
     * An earlier connection failed, and we're now receiving the result of the resolution attempt
     * by PlusClient.
     *
     * @see #onConnectionFailed(ConnectionResult)
     */


    /**
     * Successfully connected (called by PlusClient)
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        user = refreshUser();
        updateConnectButtonState();
        setProgressBarVisible(false);
        onPlusClientSignIn();
        Log.d(TAG, " onConnected ");

    }

    /**
     * Successfully disconnected (called by PlusClient)
     */
    public void onDisconnected() {
        updateConnectButtonState();
        onPlusClientSignOut();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        updateConnectButtonState();

        // Most of the time, the connection will fail with a user resolvable result. We can store
        // that in our mConnectionResult property ready to be used when the user clicks the
        // sign-in button.
        if (result.hasResolution()) {
            mConnectionResult = result;
            if (mAutoResolveOnFail) {
                // This is a local helper function that starts the resolution of the problem,
                // which may be showing the user an account chooser or similar.
                startResolution();
            }
        }
    }

    public GoogleApiClient getPlusClient() {
        return mPlusClient;
    }

    public User refreshUser() {
        User user;
        String personName;
        String email = "";
        String provider = "google";
        String univ = "";
        String password = "";
        String nickname = "";
        Person person = Plus.PeopleApi.getCurrentPerson(mPlusClient);
        Log.d(TAG, "person info : " + person);

        if (person != null) {
            nickname = person.getDisplayName();
            email = Plus.AccountApi.getAccountName(mPlusClient);

            try {
                String organizationStr = person.getOrganizations().toString();
                JSONArray organization = new JSONArray(organizationStr);
                for (int i = 0; i < organization.length(); i++) {
                    JSONObject education = organization.getJSONObject(i);
                    if (education.getString("type").equals("school")) {
                        univ = education.getString("name");
                        Log.d(TAG, "univ : " + univ);
                        password = ZocoPassword.createPassword(provider + email + univ);
                        break;
                    }
                }

            } catch (JSONException e) {
                Log.d(TAG, "json error in organization");
            }

            Log.d("NARA", " personName : " + person.getName());
            Log.d("NARA", " email : " + email);
        } else {
            Log.d("NARA", " person is null");
        }
        user = new User(email, nickname, provider, univ, password);
        Log.d("NARA", " Googleaccnt user : " + user);
        return user;
    }

    public User getUser() {
        return user;
    }
}
