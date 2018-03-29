package mccode.qdup;

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
import android.view.View.OnClickListener;
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

import static mccode.qdup.Utils.GeneralUIUtils.*;

public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    //Program Fields
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
    private boolean accountType = true;                                         //variable that holds if user has Spotify premium
    private static boolean failedConnect = false;                               //remembers if the user couldnt connect
    // Request code that will be used to verify if the result comes from correct activity
    private static final int REQUEST_CODE = 1337;
    public static Player musicPlayer;                                           //Spotify music player
    public static boolean isServer;

    //Buttons
    private CompoundButton serverOrClientButton;
    private Button confirmType;
    private EditText keySearch;
    private Button retry;
    private TextView error;
    private int colorPrimary;
    private int colorFaded;
    private ViewGroup mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Method:      OnCreate
         * Purpose:     gets called when the activity is created. Sets up the buttons and then logs
         *              the user in.
         */
        if(!failedConnect){     //if the person has not failed to connect, it will update the screen
            setContentView(R.layout.activity_main);
            initializeScreenElements();
            error.setVisibility(View.GONE);
            retry.setVisibility(View.GONE);
            serverOrClientButton.setVisibility(View.GONE);
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
        initializeScreenElements();
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
                ConnectListener connectListener = createConnectListener();
                OnClickListener confirmTypeOnClickListener = createConfirmTypeOnClickListener(connectListener);
                confirmType.setOnClickListener(confirmTypeOnClickListener);
                setServerOrClientButtonFunctionality(accountType);
                routerSocket = new Socket();
            }else{
                failedConnect = true;
                retry.setOnClickListener(createRetryButtonOnClickListener());
                retry.setVisibility(View.VISIBLE);
                error.setVisibility(View.VISIBLE);
                serverOrClientButton.setVisibility(View.GONE);
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

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("MainActivity", "Login failed");
        if(error.toString().equals("kSpErrorNeedsPremium")){
            accountType = false;
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

    public void initializeScreenElements(){
        setContentView(R.layout.activity_main);
        serverOrClientButton = (CompoundButton) findViewById(R.id.serverOrClient);
        confirmType = (Button) findViewById(R.id.confirmType);
        keySearch = (EditText) findViewById(R.id.key_search);
        retry = (Button) findViewById(R.id.retry);
        error = (TextView) findViewById(R.id.errorConnect);
        colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        colorFaded = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryClicked);
        mainView = (ViewGroup) findViewById(R.id.mainView);
    }

    public ConnectListener createConnectListener(){
        return new ConnectListener() {
            @Override
            public void onConnectSucceeded(ArrayList<String> result) {
                if (!result.get(0).equals("NA")) {
                    serverCode = result.get(0);
                    if (serverOrClientButton.isChecked()) {
                        isServer = true;
                        Intent intent = new Intent(MainActivity.this, ServerActivity.class);
                        startActivity(intent);
                    } else {
                        isServer = false;
                        Intent intent = new Intent(MainActivity.this, ServerActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };
    }

    public OnClickListener createConfirmTypeOnClickListener(final ConnectListener connectListener){
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(colorPrimary, colorFaded, 250, confirmType);
                if(serverOrClientButton.isChecked()){
                    serverConnector = new ServerConnector();
                    serverConnector.setOnConnectListener(connectListener);
                    serverConnector.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else{
                    serverCode = keySearch.getText().toString();
                    clientConnector = new ClientConnector(serverCode);
                    clientConnector.setOnConnectListener(connectListener);
                    clientConnector.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        };
    }

    public OnClickListener createServerOrClientOnClickListener(){
        return new View.OnClickListener() {
            public void onClick(View v) {
                animateButtonClick(colorPrimary, colorFaded, 250, serverOrClientButton);
                TransitionManager.beginDelayedTransition(mainView);
                keySearch.setVisibility(serverOrClientButton.isChecked() ? View.GONE : View.VISIBLE);
            }
        };
    }

    public OnClickListener createRetryButtonOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButtonClick(colorPrimary, colorFaded, 250, retry);
                onCreate(null);
            }
        };
    }

    public void logIn(){
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        if(accountType) {
            builder.setScopes(new String[]{"user-read-private", "streaming"});
        }else{

        }
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    public void setServerOrClientButtonFunctionality(boolean premium){
        if(premium) {
            serverOrClientButton.setOnClickListener(createServerOrClientOnClickListener());
        }else{
            serverOrClientButton.setEnabled(false);
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