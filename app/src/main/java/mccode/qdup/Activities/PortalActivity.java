package mccode.qdup.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.sdk.android.player.Spotify;

import org.codehaus.jackson.map.ObjectMapper;

import java.net.Socket;
import java.util.ArrayList;

import mccode.qdup.R;
import mccode.qdup.Utils.Requester.RequesterConnector;
import mccode.qdup.Utils.Host.HostConnector;
import mccode.qdup.Utils.Listeners.ConnectListener;

import static mccode.qdup.Utils.GeneralUIUtils.*;

public class PortalActivity extends Activity{
    //Program Fields
    private HostConnector hostConnector;                                    //tool to connect the server to the router
    private RequesterConnector requesterConnector;                                    //tool to connect the client to the router

//    public static final String ROUTER_URL = "mccoderouter.ddns.net";           //hostname of the router
    public static final String ROUTER_URL = "ec2-52-15-188-227.us-east-2.compute.amazonaws.com";
    public static int CLIENT_PORT = 16455;                                     //client port number on the router
    public static int SERVER_PORT = 16456;                                     //server port number on the router
    public static String serverKey = "";                                        //string that holds the server serverKey
    public static String privateKey = "";                                        //string that holds the server serverKey
    public static boolean requestNewServerKey = true;                           //flag if going to need a new serverKey
    public static ObjectMapper jsonConverter = new ObjectMapper();              //jsonConverter to do json parsing
    public static Socket routerSocket;                                          //socket connection to the router
    public static boolean isServer;

    //Buttons
    private ImageView qdUpImage;
    private TextView qdUpText;
    private View hostDivider;
    private Button hostButton;
    private View joinDivider;
    private EditText keySearch;
    private Button joinButton;

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
        setContentView(R.layout.new_portal_layout);
        hookUpElementsWithFrontEnd();
        setupOnClickListeners();
    }

    public void hookUpElementsWithFrontEnd(){
        Log.d(appType, "Hooking up elements with front end");
        qdUpImage = (ImageView) findViewById(R.id.qdUpImage);
        qdUpText = (TextView) findViewById(R.id.searchServerText);
        hostDivider = findViewById(R.id.hostDivider);
        hostButton = (Button) findViewById(R.id.hostButton);
        joinDivider = findViewById(R.id.joinDivider);
        joinButton = (Button) findViewById(R.id.joinButton);
        keySearch = (EditText) findViewById(R.id.keySearch);
        colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        colorFaded = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryClicked);
        mainView = (ViewGroup) findViewById(R.id.mainView);
        if(!AuthActivity.isPremium){
            hostButton.setVisibility(View.GONE);
            hostDivider.setVisibility(View.GONE);
        }
    }

    public void setupOnClickListeners(){
        Log.d(appType, "Setting up OnClickListeners for buttons");
        hostButton.setOnClickListener(createConfirmTypeOnClickListener(createConnectListener(true), true));
        joinButton.setOnClickListener(createConfirmTypeOnClickListener(createConnectListener(false), true));
    }

    public ConnectListener createConnectListener(final boolean isServer){
        Log.d(appType, "Creating the connect listener");
        return new ConnectListener() {
            @Override
            public void onConnectSucceeded(ArrayList<String> result) {
                if (!result.get(0).equals("NA")) {
                    serverKey = result.get(0);
                    Log.d(appType, "Starting QueueActivity as a Server");
                    PortalActivity.isServer = isServer;
                    Intent intent = new Intent(PortalActivity.this, QueueActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }
        };
    }

    public OnClickListener createConfirmTypeOnClickListener(final ConnectListener connectListener, final boolean isServer){
        Log.d(appType, "Creating confirmType's OnClickListener");
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(colorPrimary, colorFaded, 250, isServer ? hostButton : joinButton);
                if(isServer){
                    Log.d(appType, "Connecting to the router as a Server");
                    hostConnector = new HostConnector();
                    hostConnector.setOnConnectListener(connectListener);
                    hostConnector.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else{
                    Log.d(appType, "Connecting to the router as a Client");
                    serverKey = keySearch.getText().toString();
                    requesterConnector = new RequesterConnector(serverKey);
                    requesterConnector.setOnConnectListener(connectListener);
                    requesterConnector.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        };
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