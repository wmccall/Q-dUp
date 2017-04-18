package mccode.spotidj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.view.View;
import android.widget.EditText;

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

import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;

public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    private static final String CLIENT_ID = "dfa2a91d372d42db9cb74bed20fb5630";
    private static final String REDIRECT_URI = "mccode-spotidj://callback";
    private static final String HOST = "mammothtr0n.student.rit.edu";
    private static int PORT = 5000;
    public static ModelProxy mp;
    public static ObjectMapper mapper = new ObjectMapper();

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;
    public static Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addNotificationCallback(MainActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
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
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
        setContentView(R.layout.activity_main);
        //mPlayer.playUri(null, "spotify:track:7oK9VyNzrYvRFo7nQEYkWN", 0, 0);

        final CompoundButton serverOrClient = (CompoundButton) findViewById(R.id.serverOrClient);
        final Button confirmType = (Button) findViewById(R.id.confirmType);
        final EditText keySearch = (EditText) findViewById(R.id.key_search);
        final ViewGroup mainView = (ViewGroup) findViewById(R.id.mainView);

        final ConnectListener listener = new ConnectListener() {
            @Override
            public void onConnectSucceeded(ArrayList<String> result) {
                //is checked means it is server, not is client
                if(serverOrClient.isChecked()){
                    if(result.get(0)=="Y"){
                        Intent intent = new Intent(MainActivity.this, ServerActivity.class);
                        startActivity(intent);
                    }
                }else{
                    if(result.get(0)=="Y") {
                        //TODO:Relocate so no network on main
                        //mp.join("ABF$S");
                        Intent intent = new Intent(MainActivity.this, RequesterActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };

        confirmType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connector c = new Connector();
                c.setOnConnectListener(listener);
                c.execute();
            }
        });

        serverOrClient.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                TransitionManager.beginDelayedTransition(mainView);
                keySearch.setVisibility(serverOrClient.isChecked() ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    public static String getHost(){
        return HOST;
    }

    public static int getPort(){
        return PORT;
    }
}