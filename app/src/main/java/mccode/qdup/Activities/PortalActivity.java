package mccode.qdup.Activities;

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

import com.spotify.sdk.android.player.Spotify;

import org.codehaus.jackson.map.ObjectMapper;

import java.net.Socket;
import java.util.ArrayList;

import mccode.qdup.R;
import mccode.qdup.Utils.Client.ClientConnector;
import mccode.qdup.Utils.Listeners.ConnectListener;
import mccode.qdup.Utils.Server.ServerConnector;

import static mccode.qdup.Utils.GeneralUIUtils.*;

public class PortalActivity extends Activity{
    //Program Fields
    private ServerConnector serverConnector;                                    //tool to connect the server to the router
    private ClientConnector clientConnector;                                    //tool to connect the client to the router

//    public static final String ROUTER_URL = "mccoderouter.ddns.net";           //hostname of the router
    public static final String ROUTER_URL = "ec2-52-15-188-227.us-east-2.compute.amazonaws.com";
    public static int CLIENT_PORT = 16455;                                     //client port number on the router
    public static int SERVER_PORT = 16456;                                     //server port number on the router
    public static String serverKey = "";                                        //string that holds the server serverKey
    public static boolean requestNewServerKey = true;                           //flag if going to need a new serverKey
    public static ObjectMapper jsonConverter = new ObjectMapper();              //jsonConverter to do json parsing
    public static Socket routerSocket;                                          //socket connection to the router
    public static boolean isServer;

    //Buttons
    private CompoundButton serverOrClientButton;
    private Button confirmType;
    private EditText keySearch;

    private int colorPrimary;
    private int colorFaded;
    private ViewGroup mainView;

    String appType = "McCode-PortalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(appType, "OnCreate running");
        super.onCreate(savedInstanceState);
        routerSocket = new Socket();
        initializeScreenElements();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(appType, "OnActivityResult running");
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        Log.d(appType, "OnResume running");
        super.onResume();
        initializeScreenElements();
//        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//        for (Thread t: threadSet
//                ) {
//            Log.d(appType, (t.getId() + ": " + t.getName() + "-" ));
//            for (StackTraceElement s :t.getStackTrace()
//                    ) {
//                Log.d("AppType", s.toString());
//            }
//        }
    }

    public void initializeScreenElements(){
        Log.d(appType, "Initializing Screen Elements");
        setContentView(R.layout.portal_layout);
        hookUpElementsWithFrontEnd();
        setupOnClickListeners();
        setServerOrClientButtonFunctionality(AuthActivity.isPremium);

    }

    public void hookUpElementsWithFrontEnd(){
        Log.d(appType, "Hooking up elements with front end");
        serverOrClientButton = (CompoundButton) findViewById(R.id.serverOrClient);
        confirmType = (Button) findViewById(R.id.confirmType);
        keySearch = (EditText) findViewById(R.id.key_search);
        colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        colorFaded = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryClicked);
        mainView = (ViewGroup) findViewById(R.id.mainView);
    }

    public void setupOnClickListeners(){
        Log.d(appType, "Setting up OnClickListeners for buttons");
        ConnectListener connectListener = createConnectListener();
        OnClickListener confirmTypeOnClickListener = createConfirmTypeOnClickListener(connectListener);
        confirmType.setOnClickListener(confirmTypeOnClickListener);
    }

    public ConnectListener createConnectListener(){
        Log.d(appType, "Creating the connect listener");
        return new ConnectListener() {
            @Override
            public void onConnectSucceeded(ArrayList<String> result) {
                if (!result.get(0).equals("NA")) {
                    serverKey = result.get(0);
                    if (serverOrClientButton.isChecked()) {
                        Log.d(appType, "Starting QueueActivity as a Server");
                        isServer = true;
                        Intent intent = new Intent(PortalActivity.this, QueueActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    } else {
                        Log.d(appType, "Starting QueueActivity as a Client");
                        isServer = false;
                        Intent intent = new Intent(PortalActivity.this, QueueActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                }
            }
        };
    }

    public OnClickListener createConfirmTypeOnClickListener(final ConnectListener connectListener){
        Log.d(appType, "Creating confirmType's OnClickListener");
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(colorPrimary, colorFaded, 250, confirmType);
                if(serverOrClientButton.isChecked()){
                    Log.d(appType, "Connecting to the router as a Server");
                    serverConnector = new ServerConnector();
                    serverConnector.setOnConnectListener(connectListener);
                    serverConnector.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else{
                    Log.d(appType, "Connecting to the router as a Client");
                    serverKey = keySearch.getText().toString();
                    clientConnector = new ClientConnector(serverKey);
                    clientConnector.setOnConnectListener(connectListener);
                    clientConnector.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        };
    }

    public OnClickListener createServerOrClientOnClickListener(){
        Log.d(appType, "Creating serverOrClient's OnClickListener");
        return new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(appType, "Changing from " + (serverOrClientButton.isChecked() ? "Client to Server" : "Server to Client"));
                animateButtonClick(colorPrimary, colorFaded, 250, serverOrClientButton);
                TransitionManager.beginDelayedTransition(mainView);
                keySearch.setVisibility(serverOrClientButton.isChecked() ? View.GONE : View.VISIBLE);
            }
        };
    }

    public void setServerOrClientButtonFunctionality(boolean premium){
        Log.d(appType, "Setting serverOrClient's functionality to " + (premium ? "functional" : "nonfunctional"));
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