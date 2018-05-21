package mccode.qdup.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;



import mccode.qdup.R;

/**
 * Created by Will on 4/18/2018.
 */

public class AuthActivity extends Activity implements Player.NotificationCallback, ConnectionStateCallback {

    public static final String CLIENT_ID = "dfa2a91d372d42db9cb74bed20fb5630"; //for Spotify
    private static final String REDIRECT_URI = "mccode-qdup://callback";        //callback for Spotify
    private static final int REQUEST_CODE = 1337;                       // Request code that will be used to verify if the result comes from correct activity
    public static AuthenticationResponse authenticationResponse = null;               //Spotify authentication response
    private String appType = "McCode-AuthActivity";

    public static String responseToken = "";
    public static boolean isPremium = true;                                           //variable that holds if user has Spotify premium

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(appType, "OnCreate running");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_layout);
        getAppSignature();
        logIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(appType, "OnActivityResult running");
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE) {
            authenticationResponse = AuthenticationClient.getResponse(resultCode, intent);
            Log.d(appType, "authenticationResponseType: " + authenticationResponse.getType().toString());
            if (authenticationResponse.getType() == AuthenticationResponse.Type.TOKEN) {
                afterSuccessfulAuthenticationResponse();
            }else{
                startErrorActivity();
            }
        }
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d(appType, "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d(appType, "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d(appType, "User logged in");
        startMainActivity();
    }

    @Override
    public void onLoggedOut() {
        Log.d(appType, "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d(appType, "Login failed: " + error.toString());
        if(error.toString().equals("kSpErrorNeedsPremium")){
            isPremium = false;
            logIn();
        }else{
            startErrorActivity();
        }
    }

    @Override
    public void onTemporaryError() {
        Log.d(appType, "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d(appType, "Received connection message: " + message);
    }

    @Override
    public void onResume() {
        Log.d(appType, "OnResume running");
        super.onResume();
        setContentView(R.layout.auth_layout);
    }

    private void logIn(){
        Log.d(appType, "Logging in as " + (isPremium ? "premium" : "free"));
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        if(isPremium) {
            builder.setScopes(new String[]{"user-read-private", "streaming"});
        }else{

        }
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    private void setupPlayer(){
        Log.d(appType, "Setting up the spotify player");
        Config playerConfig = new Config(this, AuthActivity.authenticationResponse.getAccessToken(), AuthActivity.CLIENT_ID);
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                Log.d(appType, "spotifyPlayer.onInitialized");
                QueueActivity.musicPlayer = spotifyPlayer;
                QueueActivity.musicPlayer.addConnectionStateCallback(AuthActivity.this);
                QueueActivity.musicPlayer.addNotificationCallback(AuthActivity.this);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(appType, "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    private void afterSuccessfulAuthenticationResponse(){
        responseToken = authenticationResponse.getAccessToken();
        Log.d(appType, "Setting Response Token: " + responseToken);
        if(!isPremium){
            startMainActivity();
        }else {
            setupPlayer();
        }
    }

    private void startMainActivity(){
        Intent intent = new Intent(AuthActivity.this, PortalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void startErrorActivity(){
        Intent intent = new Intent(AuthActivity.this, ErrorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void getAppSignature(){
        Signature[] sigs = new Signature[0];
        try {
            sigs = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES).signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        for (Signature sig : sigs)
        {
            Log.i(appType, "Signature hashcode : " + sig.hashCode());
        }
    }
}
