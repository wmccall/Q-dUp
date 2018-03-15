package mccode.qdup;

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
import android.widget.TextView;

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
import java.util.Set;

import mccode.qdup.Utils.Client.ClientConnector;
import mccode.qdup.Utils.Listeners.ConnectListener;
import mccode.qdup.Utils.Server.ServerConnector;

public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    private static final String CLIENT_ID = "dfa2a91d372d42db9cb74bed20fb5630"; //for Spotify
    private static final String REDIRECT_URI = "mccode-qdup://callback";        //callback for Spotify
    private static final String ROUTER_URL = "mccoderouter.ddns.net";           //hostname of the router
    private static int CLIENT_PORT = 16455;                                     //client port number on the router
    private static int SERVER_PORT = 16456;                                     //server port number on the router
    public static String serverCode = "";                                       //string that holds the server serverCode
    public static boolean requestNewServerCode = true;                          //flag if going to need a new serverCode
    public static ObjectMapper jsonConverter = new ObjectMapper();              //jsonConverter to do json parsing
    public static Socket routerSocket;                                          //socket connection to the router
    public static String responseToken = "";
    private ServerConnector serverConnector;                                    //tool to connect the server to the router
    private ClientConnector clientConnector;                                    //tool to connect the client to the router
    private AuthenticationResponse authenticationResponse = null;               //Spotify authentication response
    private boolean premium = true;                                             //variable that holds if user has Spotify premium
    private static boolean failedConnect = false;                               //remembers if the user couldnt connect
    // Request code that will be used to verify if the result comes from correct activity
    private static final int REQUEST_CODE = 1337;
    public static Player musicPlayer;                                           //Spotify music player

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Method:      OnCreate
         * Purpose:     gets called when the activity is created. Sets up the buttons and then logs
         *              the user in.
         */
        if(!failedConnect){     //if the person has not failed to connect, it will update the screen
            setContentView(R.layout.activity_main);
            final CompoundButton serverOrClient = (CompoundButton) findViewById(R.id.serverOrClient);
            final Button confirmType = (Button) findViewById(R.id.confirmType);
            final EditText keySearch = (EditText) findViewById(R.id.key_search);
            final Button retry = (Button) findViewById(R.id.retry);
            final TextView error = (TextView) findViewById(R.id.errorConnect);
            error.setVisibility(View.GONE);
            retry.setVisibility(View.GONE);
            serverOrClient.setVisibility(View.GONE);
            confirmType.setVisibility(View.GONE);
            keySearch.setVisibility(View.GONE);
        }
        super.onCreate(savedInstanceState);
        logIn();    //logs the user in with Spotify
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        setContentView(R.layout.activity_main);
        final Button retry = (Button) findViewById(R.id.retry);
        final CompoundButton serverOrClient = (CompoundButton) findViewById(R.id.serverOrClient);
        final Button confirmType = (Button) findViewById(R.id.confirmType);
        final EditText keySearch = (EditText) findViewById(R.id.key_search);
        final TextView error = (TextView) findViewById(R.id.errorConnect);
        final int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        final int colorFaded = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryClicked);
        final ViewGroup mainView = (ViewGroup) findViewById(R.id.mainView);

        if (requestCode == REQUEST_CODE) {
            authenticationResponse = AuthenticationClient.getResponse(resultCode, intent);
            if (authenticationResponse.getType() == AuthenticationResponse.Type.TOKEN) {
                retry.setVisibility(View.GONE);
                error.setVisibility(View.GONE);
                responseToken = authenticationResponse.getAccessToken();
                Config playerConfig = new Config(this, authenticationResponse.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        musicPlayer = spotifyPlayer;
                        musicPlayer.addConnectionStateCallback(MainActivity.this);
                        musicPlayer.addNotificationCallback(MainActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
                final ConnectListener listener = new ConnectListener() {
                    @Override
                    public void onConnectSucceeded(ArrayList<String> result) {
                        //is checked means it is server, not is client
                        if(!result.get(0).equals("NA")) {
                            serverCode = result.get(0);
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
                        final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimary, colorFaded);
                        colorAnimation.setDuration(250);
                        final ValueAnimator colorAnimationRev = ValueAnimator.ofObject(new ArgbEvaluator(), colorFaded, colorPrimary);
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
                            serverConnector = new ServerConnector();
                            serverConnector.setOnConnectListener(listener);
                            //s.execute();
                            serverConnector.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }else{
                            serverCode = keySearch.getText().toString();
                            clientConnector = new ClientConnector(serverCode);
                            clientConnector.setOnConnectListener(listener);
                            //c.execute();
                            clientConnector.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }
                });
                if(premium) {
                    serverOrClient.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimary, colorFaded);
                            colorAnimation.setDuration(250);
                            final ValueAnimator colorAnimationRev = ValueAnimator.ofObject(new ArgbEvaluator(), colorFaded, colorPrimary);
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
                }else{
                    serverOrClient.setEnabled(false);
                }
                routerSocket = new Socket();
            }else{
                failedConnect = true;
                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimary, colorFaded);
                        colorAnimation.setDuration(250);
                        final ValueAnimator colorAnimationRev = ValueAnimator.ofObject(new ArgbEvaluator(), colorFaded, colorPrimary);
                        colorAnimationRev.setDuration(250);
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                retry.setBackgroundColor((int) animator.getAnimatedValue());
                            }
                        });
                        colorAnimationRev.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                retry.setBackgroundColor((int) animator.getAnimatedValue());
                            }
                        });
                        colorAnimation.start();
                        colorAnimationRev.start();
                        onCreate(null);
                    }
                });
                retry.setVisibility(View.VISIBLE);
                error.setVisibility(View.VISIBLE);
                serverOrClient.setVisibility(View.GONE);
                confirmType.setVisibility(View.GONE);
                keySearch.setVisibility(View.GONE);
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
    }

    public void logIn(){
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        if(premium) {
            builder.setScopes(new String[]{"user-read-private", "streaming"});
        }else{

        }
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }
    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("MainActivity", "Login failed");
        if(error.toString().equals("kSpErrorNeedsPremium")){
            premium = false;
            logIn();
        }
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(authenticationResponse != null && authenticationResponse.getType() == AuthenticationResponse.Type.TOKEN){
            routerSocket = new Socket();
        }
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread t: threadSet
                ) {
            Log.d("Main Activity", (t.getId() + ": " + t.getName() + "-" ));
            for (StackTraceElement s :t.getStackTrace()
                    ) {
                Log.d("Main Activity", s.toString());
            }
        }
    }

    public static String getRouterUrl(){
        return ROUTER_URL;
    }

    public static int getClientPort(){
        return CLIENT_PORT;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }
}