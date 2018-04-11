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
    public static String serverKey = "";                                        //string that holds the server serverKey
    public static boolean requestNewServerKey = true;                           //flag if going to need a new serverKey
    public static ObjectMapper jsonConverter = new ObjectMapper();              //jsonConverter to do json parsing
    public static Socket routerSocket;                                          //socket connection to the router
    public static String responseToken = "";
    private ServerConnector serverConnector;                                    //tool to connect the server to the router
    private ClientConnector clientConnector;                                    //tool to connect the client to the router
    private AuthenticationResponse authenticationResponse = null;               //Spotify authentication response
    private boolean isPremium = true;                                         //variable that holds if user has Spotify premium
    // Request code that will be used to verify if the result comes from correct activity
    private static final int REQUEST_CODE = 1337;
    public static Player musicPlayer;                                           //Spotify music player
    public static boolean isServer;
    public static boolean buttonsSetup = false;

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
        Log.d("MainActivity", "OnCreate running");
        super.onCreate(savedInstanceState);
        initializeScreenElements();
        logIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d("MainActivity", "OnActivityResult running");
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE) {
            authenticationResponse = AuthenticationClient.getResponse(resultCode, intent);
            if (authenticationResponse.getType() == AuthenticationResponse.Type.TOKEN) {
                changingElementsVisibility(true, false);
                responseToken = authenticationResponse.getAccessToken();
                setupPlayer();
                setServerOrClientButtonFunctionality(isPremium);
                routerSocket = new Socket();
            }else{
                changingElementsVisibility(false, false);
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
            isPremium = false;
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
    public void onResume() {
        Log.d("MainActivity", "OnResume running");
        super.onResume();
        if(authenticationResponse != null && authenticationResponse.getType() == AuthenticationResponse.Type.TOKEN){
            routerSocket = new Socket();
        }
//        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//        for (Thread t: threadSet
//                ) {
//            Log.d("Main Activity", (t.getId() + ": " + t.getName() + "-" ));
//            for (StackTraceElement s :t.getStackTrace()
//                    ) {
//                Log.d("Main Activity", s.toString());
//            }
//        }
    }

    public void initializeScreenElements(){
        Log.d("MainActivity", "Initializing Screen Elements");
        if(!buttonsSetup) {
            setContentView(R.layout.activity_main);
            hookUpElementsWithFrontEnd();
            setupOnClickListeners();
        }
        changingElementsVisibility(false, true);
    }

    public void hookUpElementsWithFrontEnd(){
        Log.d("MainActivity", "Hooking up elements with front end");
        serverOrClientButton = (CompoundButton) findViewById(R.id.serverOrClient);
        confirmType = (Button) findViewById(R.id.confirmType);
        keySearch = (EditText) findViewById(R.id.key_search);
        retry = (Button) findViewById(R.id.retry);
        error = (TextView) findViewById(R.id.errorConnect);
        colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        colorFaded = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryClicked);
        mainView = (ViewGroup) findViewById(R.id.mainView);
    }

    public void changingElementsVisibility(boolean loggedIn, boolean hide){
        Log.d("MainActivity", "Changing element visibility");
        int optionOne = View.GONE;
        int optionTwo = hide ? View.GONE : View.VISIBLE;
        error.setVisibility(loggedIn ? optionOne : optionTwo);
        retry.setVisibility(loggedIn ? optionOne : optionTwo);
        serverOrClientButton.setVisibility(loggedIn ? optionTwo : optionOne);
        confirmType.setVisibility(loggedIn ? optionTwo : optionOne);
        keySearch.setVisibility(loggedIn ? optionTwo : optionOne);
    }

    public void setupOnClickListeners(){
        Log.d("MainActivity", "Setting up OnClickListeners for buttons");
        ConnectListener connectListener = createConnectListener();
        OnClickListener confirmTypeOnClickListener = createConfirmTypeOnClickListener(connectListener);
        confirmType.setOnClickListener(confirmTypeOnClickListener);

        retry.setOnClickListener(createRetryButtonOnClickListener());
        buttonsSetup = true;
    }

    public void setupPlayer(){
        Log.d("MainActivity", "Setting up the spotify player");
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
    }

    public ConnectListener createConnectListener(){
        Log.d("MainActivity", "Creating the connect listener");
        return new ConnectListener() {
            @Override
            public void onConnectSucceeded(ArrayList<String> result) {
                if (!result.get(0).equals("NA")) {
                    serverKey = result.get(0);
                    if (serverOrClientButton.isChecked()) {
                        Log.d("MainActivity", "Starting MusicActivity as a Server");
                        isServer = true;
                        Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                        startActivity(intent);
                    } else {
                        Log.d("MainActivity", "Starting MusicActivity as a Client");
                        isServer = false;
                        Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };
    }

    public OnClickListener createConfirmTypeOnClickListener(final ConnectListener connectListener){
        Log.d("MainActivity", "Creating confirmType's OnClickListener");
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(colorPrimary, colorFaded, 250, confirmType);
                if(serverOrClientButton.isChecked()){
                    Log.d("MainActivity", "Connecting to the router as a Server");
                    serverConnector = new ServerConnector();
                    serverConnector.setOnConnectListener(connectListener);
                    serverConnector.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else{
                    Log.d("MainActivity", "Connecting to the router as a Client");
                    serverKey = keySearch.getText().toString();
                    clientConnector = new ClientConnector(serverKey);
                    clientConnector.setOnConnectListener(connectListener);
                    clientConnector.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        };
    }

    public OnClickListener createServerOrClientOnClickListener(){
        Log.d("MainActivity", "Creating serverOrClient's OnClickListener");
        return new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("MainActivity", "Changing from " + (serverOrClientButton.isChecked() ? "Client to Server" : "Server to Client"));
                animateButtonClick(colorPrimary, colorFaded, 250, serverOrClientButton);
                TransitionManager.beginDelayedTransition(mainView);
                keySearch.setVisibility(serverOrClientButton.isChecked() ? View.GONE : View.VISIBLE);
            }
        };
    }

    public OnClickListener createRetryButtonOnClickListener(){
        Log.d("MainActivity", "Creating retry's OnClickListener");
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity", "Re-trying to connect with spotify");
                animateButtonClick(colorPrimary, colorFaded, 250, retry);
                onCreate(null);
            }
        };
    }

    public void logIn(){
        Log.d("MainActivity", "Logging in as " + (isPremium ? "premium" : "free"));
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

    public void setServerOrClientButtonFunctionality(boolean premium){
        Log.d("MainActivity", "Setting serverOrClient's functionality to " + (premium ? "functional" : "nonfunctional"));
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