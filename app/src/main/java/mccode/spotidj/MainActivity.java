package mccode.spotidj;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

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

import java.net.Socket;
import java.util.ArrayList;

import mccode.spotidj.Utils.Client.ClientConnector;
import mccode.spotidj.Utils.Listeners.ConnectListener;
import mccode.spotidj.Utils.Server.ServerConnector;

public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    private static final String CLIENT_ID = "dfa2a91d372d42db9cb74bed20fb5630";
    private static final String REDIRECT_URI = "mccode-spotidj://callback";
    //private static final String HOST = "mammothtr0n.student.rit.edu";
    private static final String HOST = "spotidjrouter.ddns.net";
    //private static int CPORT = 5000;
    private static int CPORT = 16455;
    private static int SPORT = 16456;
    public static String key = "";
    public static ObjectMapper mapper = new ObjectMapper();
    public static Socket routerSocket;
    public static String responseToken = "";
    public static boolean stopped = false;
    private ServerConnector s;
    private ClientConnector c;

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
                responseToken = response.getAccessToken();
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
                routerSocket = new Socket();
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
        final int colorBackground = ContextCompat.getColor(getApplicationContext(), R.color.background);
        final int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        final int colorFaded = ContextCompat.getColor(getApplicationContext(), R.color.faded);
        final int colorPrimaryClicked = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryClicked);
        final CompoundButton serverOrClient = (CompoundButton) findViewById(R.id.serverOrClient);
        final Button confirmType = (Button) findViewById(R.id.confirmType);
        final EditText keySearch = (EditText) findViewById(R.id.key_search);
        final ViewGroup mainView = (ViewGroup) findViewById(R.id.mainView);
        final ConnectListener listener = new ConnectListener() {
            @Override
            public void onConnectSucceeded(ArrayList<String> result) {
                //is checked means it is server, not is client
                if(!result.get(0).equals("NA")) {
                    key = result.get(0);
                    if(serverOrClient.isChecked()){
                        Intent intent = new Intent(MainActivity.this, ServerActivity.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(MainActivity.this, RequesterActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };

        confirmType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimary, colorPrimaryClicked);
                colorAnimation.setDuration(250);
                final ValueAnimator colorAnimationRev = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimaryClicked, colorPrimary);
                colorAnimationRev.setDuration(250);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        confirmType.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimationRev.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        confirmType.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimation.start();
                colorAnimationRev.start();
                if(serverOrClient.isChecked()){
                    s = new ServerConnector();
                    s.setOnConnectListener(listener);
                    //s.execute();
                    s.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else{
                    key = keySearch.getText().toString();
                    c = new ClientConnector(key);
                    c.setOnConnectListener(listener);
                    //c.execute();
                    c.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        serverOrClient.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimary, colorPrimaryClicked);
                colorAnimation.setDuration(250);
                final ValueAnimator colorAnimationRev = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimaryClicked, colorPrimary);
                colorAnimationRev.setDuration(250);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        serverOrClient.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimationRev.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        serverOrClient.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimation.start();
                colorAnimationRev.start();
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

    public static int getCPort(){
        return CPORT;
    }

    public static int getSPort() { return SPORT;}
}