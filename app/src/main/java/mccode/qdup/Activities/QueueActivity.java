package mccode.qdup.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.IOException;

import mccode.qdup.R;
import mccode.qdup.Utils.GeneralNetworkingUtils;
import mccode.qdup.Utils.Messaging.Message;
import mccode.qdup.Utils.Messaging.MessageCode;
import mccode.qdup.Utils.QueueView.HostItemTouchHelper;
import mccode.qdup.Utils.Listeners.MessageListener;
import mccode.qdup.Utils.QueueView.HostRecyclerListAdapter;
import mccode.qdup.Utils.RouterInteraction.RouterListener;
import mccode.qdup.QueryModels.Item;

import static mccode.qdup.Utils.GeneralUIUtils.animateButtonClick;

public class QueueActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    public static int count = 0;
    private boolean adding = false;

    public static boolean alreadyChanged = false;
    private static boolean playing = false;
    public static String appType;

    int colorBackground;
    int colorBackgroundClicked;
    int colorPrimary;
    int colorFaded;
    int colorPrimaryClicked;
    Drawable buttonPrimary;
    Drawable buttonPrimaryClicked;

    private static ViewGroup constraintLayout;
    private static TextView queueServerKeyView;
    private static ProgressBar queueTrackProgressBar;
    public static Button queuePlayPause;
    private static Button queueNextSongButton;
    private static Button queuePreviousSongButton;
    private static Button queueSwitchToSearchButton;
    private static RecyclerView queueView;
    public static HostRecyclerListAdapter queueViewAdapter;
    public static Player musicPlayer;                                           //Spotify music player

    ItemTouchHelper.Callback callback;
    ItemTouchHelper touchHelper;

    MessageListener messageListener;

    RouterListener routerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appType = "McCode-QueueActivity-" + (PortalActivity.isServer ? "Server" : "Client");
        Log.d(appType, "OnCreate running");
        super.onCreate(savedInstanceState);
        initializeScreenElements(PortalActivity.isServer);
        createButtonListeners(PortalActivity.isServer);
        createAndRunRouterListener();
        Thread progreassUpdater = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    long total;
                    long pos;
                    try {
                        total = musicPlayer.getMetadata().currentTrack.durationMs;
                        pos = musicPlayer.getPlaybackState().positionMs;
                    } catch (Exception e) {
                        total = 100;
                        pos = 0;
                    }
                    queueTrackProgressBar.setMax((int) total);
                    queueTrackProgressBar.setProgress((int) pos);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        progreassUpdater.start();
        if(!PortalActivity.isServer){
            GeneralNetworkingUtils.sendMessage(new Message(MessageCode.REQUEST_ALL));
        }
        Log.d(appType, "Assigned Server Key: " + PortalActivity.serverKey);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(appType, "OnActivityResult running");
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onDestroy() {
        count = 0;
        adding = false;
        Log.d(appType, "onDestroy running");
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d(appType, "Received player event: " + playerEvent.toString());
        if (playerEvent == PlayerEvent.kSpPlaybackNotifyTrackChanged){
            if(!alreadyChanged) {
                String temp = queueViewAdapter.next();
                if (!temp.equals("")) {
                    musicPlayer.playUri(null, temp, 0, 0);
                    changePlayingIcon(queuePlayPause, true);
                } else {
                    changePlayingIcon(queuePlayPause, false);
                }
                alreadyChanged = true;
            }
            else{
                alreadyChanged = false;
            }
        } else if (playerEvent == PlayerEvent.kSpPlaybackNotifyPause){
            queueViewAdapter.pause();
            changePlayingIcon(queuePlayPause, false);
            GeneralNetworkingUtils.sendMessage(new Message(MessageCode.PAUSE));
        } else if (playerEvent == PlayerEvent.kSpPlaybackNotifyPlay){
            queueViewAdapter.play();
            changePlayingIcon(queuePlayPause, true);
            GeneralNetworkingUtils.sendMessage(new Message(MessageCode.PLAY));
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
    }

    @Override
    public void onLoggedOut() {
        Log.d(appType, "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d(appType, "Login failed");
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
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            GeneralNetworkingUtils.informRouterOfQuit();
            musicPlayer.pause(null);
            try {
                PortalActivity.routerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finish();
            overridePendingTransition(0, 0);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void initializeScreenElements(boolean isServer){
        Log.d(appType, "Initializing screen elements");
        setContentView(R.layout.queue_layout);
        hookUpElementsWithFrontEnd();
        setupRecyclerView();
        showAndHideElementsBasedOffOfServerOrClient(isServer);
    }

    public void hookUpElementsWithFrontEnd(){
        Log.d(appType, "Hooking up elements with frontend");
        // colors
        colorBackground = ContextCompat.getColor(getApplicationContext(), R.color.background);
        colorBackgroundClicked = ContextCompat.getColor(getApplicationContext(), R.color.backgroundClicked);
        colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        colorFaded = ContextCompat.getColor(getApplicationContext(), R.color.faded);
        colorPrimaryClicked = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryClicked);
        buttonPrimary = ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_primary);
        buttonPrimaryClicked = ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_primary_clicked);

        // server and client
        queueServerKeyView = (TextView) findViewById(R.id.QueueServerKey);
        queueServerKeyView.setText(PortalActivity.serverKey);
        queueSwitchToSearchButton = (Button) findViewById(R.id.QueueViewSearchButton);
        queueView = (RecyclerView) findViewById(R.id.QueueBox);

        // server only
        queuePlayPause = (Button) findViewById(R.id.QueuePlayPauseSong);
        queueNextSongButton = (Button) findViewById(R.id.QueueNextSong);
        queuePreviousSongButton = (Button) findViewById(R.id.QueuePreviousSong);
        queueTrackProgressBar = (ProgressBar) findViewById(R.id.QueueTrackProgressBar);
    }

    private void setupRecyclerView(){
        Log.d(appType, "Setting up the RecyclerView");
        queueViewAdapter = new HostRecyclerListAdapter(this);
        callback = new HostItemTouchHelper(queueViewAdapter);
        touchHelper = new ItemTouchHelper(callback);
        queueView.setLayoutManager(new LinearLayoutManager(queueView.getContext()));
        queueView.setAdapter(queueViewAdapter);
        touchHelper.attachToRecyclerView(queueView);
    }

    public void showAndHideElementsBasedOffOfServerOrClient(boolean isServer){
        Log.d(appType, "Showing and hiding elements for the " + (isServer ? "server" : "client"));
        if(!isServer){
            queuePlayPause.setVisibility(View.GONE);
            queueNextSongButton.setVisibility(View.GONE);
            queuePreviousSongButton.setVisibility(View.GONE);
            queueTrackProgressBar.setVisibility(View.GONE);
        }
    }

    public void createButtonListeners(boolean isServer){
        Log.d(appType, "Creating button_primary listeners: " + (isServer ? "server" : "client"));
        if(isServer){
            queuePlayPause.setOnClickListener(createQueuePlayPauseOnClickListener());
            queueNextSongButton.setOnClickListener(createQueueNextButtonOnClickListener());
            queuePreviousSongButton.setOnClickListener(createQueuePreviousSongButtonOnClickListener());
            setupPlayer();
        }
        queueSwitchToSearchButton.setOnClickListener(createQueueSwitchToSearchOnClickListener());
    }

    public View.OnClickListener createQueuePlayPauseOnClickListener(){
        Log.d(appType, "Creating queuePlayPause button_primary's OnClickListener");
        return new View.OnClickListener(){
            public void onClick(View v){
                Log.d(appType, "Changing queuePlayPause button_primary to" + (musicPlayer.getPlaybackState().isPlaying ? "Play" : "Pause"));
                if(musicPlayer.getPlaybackState().isPlaying){
                    changePlayingIcon(queuePlayPause, false);
                    musicPlayer.pause(null);
                }else{
                    changePlayingIcon(queuePlayPause, true);
                    if(queueViewAdapter.isCurrValid())
                        musicPlayer.resume(null);
                    else
                        musicPlayer.playUri(null, queueViewAdapter.playFromBeginning(), 0, 0);
                    alreadyChanged = true;
                }
            }
        };
    }

    public View.OnClickListener createQueueNextButtonOnClickListener(){
        Log.d(appType, "Creating queueNextSongButton's OnClickListener");
        return new View.OnClickListener(){
            public void onClick(View v){
                animateForwardBackIcon(queueNextSongButton, true);
                String temp = queueViewAdapter.next();
                alreadyChanged = true;
                if (!temp.equals("")){
                    Log.d(appType, "Going to next song");
                    musicPlayer.playUri(null, temp, 0, 0);
                    changePlayingIcon(queuePlayPause, true);
                }
                else{
                    if(musicPlayer.getPlaybackState().isPlaying) {
                        Log.d(appType, "Skipped last song; stopped playing songs");
                        musicPlayer.pause(null);
                        changePlayingIcon(queuePlayPause, false);
                        musicPlayer.skipToNext(null);
                    }
                }

            }
        };
    }

    public View.OnClickListener createQueuePreviousSongButtonOnClickListener(){
        Log.d(appType, "Creating queuePreviousSongButton OnClickListener");
        return new View.OnClickListener(){
            public void onClick(View v){
                Log.d(appType, "Clicked the back button_primary");
                animateForwardBackIcon(queuePreviousSongButton, false);
                String temp = queueViewAdapter.prev();
                alreadyChanged = true;
                if (!temp.equals("")){
                    Log.d(appType, "Skipping to previous track");
                    musicPlayer.playUri(null, temp, 0, 0);
                    changePlayingIcon(queuePlayPause, true);
                }
                else{
                    if(musicPlayer.getPlaybackState().isPlaying) {
                        Log.d(appType, "Skipped back past first song; stopped playing songs");
                        musicPlayer.pause(null);
                        changePlayingIcon(queuePlayPause, false);
                        musicPlayer.skipToNext(null);
                    }
                }
            }
        };
    }

    public View.OnClickListener createQueueSwitchToSearchOnClickListener(){
        Log.d(appType, "Creating queueSwitchToSearch's on Click Listener");
        return new View.OnClickListener(){
            public void onClick(View v){
                Log.d(appType, "Changing layout from " + (adding ? "adding songs to viewing queue" : "viewing queue to adding songs"));
                animateButtonClick(colorPrimary, colorPrimaryClicked, 250, queueSwitchToSearchButton);
                Intent intent = new Intent(QueueActivity.this, SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        };
    }

    public MessageListener createMessageListener(){
        Log.d(appType, "Creating a MessageListener");
        return new MessageListener(){
            @Override
            public void onMessageSucceeded(String result) {
                try {
                    final Message m = PortalActivity.jsonConverter.readValue(result, Message.class);
                    switch (m.getCode()) {
                        case ADD: {
                            Log.d(appType, "Received message of ADD type");
                            if(PortalActivity.isServer){
                                final Item i = m.getItem();
                                count++;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        queueViewAdapter.addItem(i, generateButtonText(i).toString());
                                        if (musicPlayer.getMetadata().currentTrack == null && !musicPlayer.getPlaybackState().isPlaying) {
                                            musicPlayer.playUri(null, queueViewAdapter.next(), 0, 0);
                                            changePlayingIcon(queuePlayPause, true);
                                            alreadyChanged = true;
                                        }
                                    }
                                });
                                GeneralNetworkingUtils.sendMessage(m);
                            } else {
                                final Item i = m.getItem();
                                count++;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        queueViewAdapter.addItem(i, generateButtonText(i).toString());
                                    }
                                });
                                break;
                            }
                        }
                        case SWAP:{
                            Log.d(appType, "Received message of SWAP type");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    queueViewAdapter.swap(m.getVal1(), m.getVal2());
                                }
                            });
                            break;
                        }
                        case REMOVE:{
                            Log.d(appType, "Received message of REMOVE type");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    queueViewAdapter.remove(m.getVal1());
                                }
                            });
                            break;
                        }
                        case CHANGE_PLAYING:{
                            Log.d(appType, "Received message of CHANGE_PLAYING type");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    queueViewAdapter.changePlaying(m.getVal1());
                                }
                            });
                            break;
                        }
                        case PAUSE:{
                            Log.d(appType, "Received message of PAUSE type");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    queueViewAdapter.pause();
                                }
                            });
                            break;
                        }
                        case PLAY:{
                            Log.d(appType, "Received message of PLAY type");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    queueViewAdapter.play();
                                }
                            });
                            break;
                        }
                        case REQUEST_ALL:{
                            Log.d(appType, "Received message of REQUEST_ALL type");
                            for (Item i: queueViewAdapter.getmItems()
                                 ) {
                                GeneralNetworkingUtils.sendMessage(new Message(i));
                            }
                            GeneralNetworkingUtils.sendMessage(new Message(MessageCode.CHANGE_PLAYING, queueViewAdapter.getCurrentPlaying()));
                            break;
                        }
                        case QUIT:{
                            Log.d(appType, "Received message of QUIT type");
                            finish();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    public void createAndRunRouterListener(){
        Log.d(appType, "Creating and running routerListener");
        messageListener = createMessageListener();
        routerListener = new RouterListener();
        routerListener.setOnServerListnerListener(messageListener);
        routerListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setText(final Button b, final String text){
        Log.d(appType, "Setting button_primary's text");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                b.setText(text);
            }
        });
    }

    private void changePlayingIcon(final Button queuePlayPause, final boolean playToPause){
        Log.d(appType, "Setting button_primary's Image");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (playToPause) {
                    queuePlayPause.setBackgroundResource(R.drawable.play_to_pause_animation);
                    AnimationDrawable frameAnimation = (AnimationDrawable) queuePlayPause.getBackground();
                    frameAnimation.start();
                }else{
                    queuePlayPause.setBackgroundResource(R.drawable.pause_to_play_animation);
                    AnimationDrawable frameAnimation = (AnimationDrawable) queuePlayPause.getBackground();
                    frameAnimation.start();
                }
            }
        });
    }

    private void animateForwardBackIcon(final Button forwardOrBack, final boolean isForward){
        Log.d(appType, "Setting button_primary's Image");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isForward) {
                    forwardOrBack.setBackgroundResource(R.drawable.forward_animation);
                    AnimationDrawable frameAnimation = (AnimationDrawable) forwardOrBack.getBackground();
                    frameAnimation.stop();
                    frameAnimation.start();
                }else{
                    forwardOrBack.setBackgroundResource(R.drawable.back_animation);
                    forwardOrBack.refreshDrawableState();
                    AnimationDrawable frameAnimation = (AnimationDrawable) forwardOrBack.getBackground();
                    frameAnimation.stop();
                    frameAnimation.start();
                }
            }
        });
    }

    /**
     * given an Item i, generates the formatted text that would go on a track displayed from
     * a search result
     * @param i Item to be turned into a formatted track string
     * @return Spann
     */
    private SpannableString generateButtonText(Item i){
        SpannableString text;
        if(i!=null){
            Log.d(appType, "Generating button_primary text");
            String artists;
            int size = i.getArtists().size();
            artists = i.getArtists().get(0).getName();
            if(size > 1){
                for(int k = 1; k < size; k++){
                    artists += ", " + i.getArtists().get(k).getName();
                }
            }
            artists =i.getName() + "\n" + artists + " - " + i.getAlbum().getName();
            int firstLength = i.getName().length();
            int total = artists.length();
            text = new SpannableString(artists);
            text.setSpan(new TextAppearanceSpan(getApplicationContext(), R.style.TrackTitle),
                    0, firstLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setSpan(new TextAppearanceSpan(getApplicationContext(), R.style.TrackArtist),
                    firstLength, total,  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else{
            text = new SpannableString("No Results");
            text.setSpan(new TextAppearanceSpan(getApplicationContext(), R.style.NoTrack),
                    0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return text;
    }

    public void playSong(String uri){
        Log.d(appType, "Playing a song");
        musicPlayer.playUri(null, uri, 0,0);
        alreadyChanged = true;
    }

    private void setupPlayer(){
        Log.d(appType, "Setting up the spotify player");
        Config playerConfig = new Config(this, AuthActivity.authenticationResponse.getAccessToken(), AuthActivity.CLIENT_ID);
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                Log.d(appType, "spotifyPlayer.onInitialized");
                musicPlayer = spotifyPlayer;
                musicPlayer.addConnectionStateCallback(QueueActivity.this);
                musicPlayer.addNotificationCallback(QueueActivity.this);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(appType, "Could not initialize player: " + throwable.getMessage());
            }
        });
    }
}

