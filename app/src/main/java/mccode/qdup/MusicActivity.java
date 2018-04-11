package mccode.qdup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.IOException;
import java.util.ArrayList;

import mccode.qdup.Utils.Listeners.TrackCreatorListener;
import mccode.qdup.Utils.Messaging.Message;
import mccode.qdup.Utils.QueueView.HostItemTouchHelper;
import mccode.qdup.Utils.Listeners.MessageListener;
import mccode.qdup.Utils.Listeners.SearchListener;
import mccode.qdup.Utils.QueueView.HostRecyclerListAdapter;
import mccode.qdup.Utils.Server.ServerListener;
import mccode.qdup.Utils.Server.ServerWriter;
import mccode.qdup.QueryModels.Item;
import mccode.qdup.QueryModels.ResponseWrapper;
import mccode.qdup.QueryModels.TrackResponse;

import static mccode.qdup.MainActivity.serverKey;
import static mccode.qdup.MainActivity.musicPlayer;
import static mccode.qdup.MainActivity.jsonConverter;
import static mccode.qdup.MainActivity.routerSocket;
import static mccode.qdup.MainActivity.isServer;
import static mccode.qdup.Utils.GeneralUIUtils.animateButtonClick;

public class MusicActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    public static int count = 0;
    boolean adding = false;

    private boolean alreadyChanged = false;
    private String appType;

    TextView serverKeyView;
    Button playPause;
    Button nextButton;
    Button backButton;
    Button addSong;
    TextView queueOrSearch;
    ProgressBar loadingCircle;
    LinearLayout searchResultView;
    int colorBackground;
    int colorBackgroundClicked;
    int colorPrimary;
    int colorFaded;
    int colorPrimaryClicked;
    EditText search;
    Button findButton;
    ScrollView scrollView;
    RecyclerView recyclerView;
    HostRecyclerListAdapter adapter;
    ItemTouchHelper.Callback callback;
    ItemTouchHelper touchHelper;

    TrackCreatorListener trackCreatorListener;
    SearchListener searchListener;
    MessageListener messageListener;

    ServerListener serverListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appType = "MusicActivity-" + (isServer ? "Server" : "Client");
        Log.d(appType, "OnCreate running");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_activity);
        initializeScreenElements(isServer);
        setupTrackCreatorListenerAndSearchListener();
        createButtonListeners(isServer);
        createAndRunRouterListener();
        Log.d(appType, "Assigned Server Key: " + serverKey);
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
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d(appType, "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
//            case kSpPlaybackNotifyTrackChanged:
//                position++;
//                ((Button) findViewById(position)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
//                if(position>0){
//                    ((Button) findViewById(position-1)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.faded));
//                }
//                break;
//            case kSpPlaybackNotifyPlay:
//                position++;
//                ((Button) findViewById(position)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
//                break;
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
            informRouterOfQuit();
            musicPlayer.pause(null);
            try {
                routerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupTrackCreatorListenerAndSearchListener(){
        Log.d(appType, "Setting up TrackCreatorListener and SearchListener");
        trackCreatorListener = createTrackCreatorListener();
        searchListener = createSearchListener(trackCreatorListener);
    }

    public void initializeScreenElements(boolean isServer){
        Log.d(appType, "Initializing screen elements");
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

        // server and client
        serverKeyView = (TextView) findViewById(R.id.ServerKey);
        serverKeyView.setText(serverKey);
        addSong = (Button) findViewById(R.id.AddSong);
        queueOrSearch = (TextView) findViewById(R.id.QueueText);
        loadingCircle = (ProgressBar) findViewById(R.id.progressBar);
        searchResultView = (LinearLayout) findViewById(R.id.ButtonLocation);
        search = (EditText) findViewById(R.id.search_bar);
        findButton = (Button) findViewById(R.id.find_button);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        recyclerView = (RecyclerView) findViewById(R.id.QueueBox);

        // server only
        playPause = (Button) findViewById(R.id.PlayPause);
        nextButton = (Button) findViewById(R.id.Skip);
        backButton = (Button) findViewById(R.id.Back);
    }

    private void setupRecyclerView(){
        Log.d(appType, "Setting up the RecyclerView");
        adapter = new HostRecyclerListAdapter(this);
        callback = new HostItemTouchHelper(adapter);
        touchHelper = new ItemTouchHelper(callback);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    public void showAndHideElementsBasedOffOfServerOrClient(boolean isServer){
        Log.d(appType, "Showing and hiding elements for the " + (isServer ? "server" : "client"));
        loadingCircle.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
        findButton.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
        if(!isServer){
            playPause.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
        }
    }

    public void createButtonListeners(boolean isServer){
        Log.d(appType, "Creating button listeners");
        if(isServer){
            playPause.setOnClickListener(createPlayPauseOnClickListener());
            nextButton.setOnClickListener(createNextButtonOnClickListener());
            backButton.setOnClickListener(createBackButtonOnClickListener());
            musicPlayer.addNotificationCallback(createPlayerNotificationCallback());
        }
        addSong.setOnClickListener(createAddSongOnClickListener());
        findButton.setOnClickListener(createFindButtonOnClickListener(searchListener));
    }

    public View.OnClickListener createPlayPauseOnClickListener(){
        Log.d(appType, "Creating playPause button's OnClickListener");
        return new View.OnClickListener(){
            public void onClick(View v){
                Log.d(appType, "Changing playPause button to" + (musicPlayer.getPlaybackState().isPlaying ? "play" : "pause"));
                animateButtonClick(colorPrimary, colorPrimaryClicked, 250, playPause);
                if(musicPlayer.getPlaybackState().isPlaying){
                    playPause.setText("Play");
                    musicPlayer.pause(null);
                }else{
                    playPause.setText("Pause");
                    if(adapter.isCurrValid())
                        musicPlayer.resume(null);
                    else
                        musicPlayer.playUri(null, adapter.playFromBeginning(), 0, 0);
                    alreadyChanged = true;
                }
            }
        };
    }

    public View.OnClickListener createNextButtonOnClickListener(){
        Log.d(appType, "Creating nextButton's OnClickListener");
        return new View.OnClickListener(){
            public void onClick(View v){
                animateButtonClick(colorPrimary, colorPrimaryClicked, 250, nextButton);
                String temp = adapter.next();
                alreadyChanged = true;
                if (!temp.equals("")){
                    Log.d(appType, "Going to next song");
                    musicPlayer.playUri(null, temp, 0, 0);
                    setText(playPause, "Pause");
                }
                else{
                    if(musicPlayer.getPlaybackState().isPlaying) {
                        Log.d(appType, "Skipped last song; stopped playing songs");
                        musicPlayer.pause(null);
                        setText(playPause, "Play");
                        musicPlayer.skipToNext(null);
                    }
                }

            }
        };
    }

    public TrackCreatorListener createTrackCreatorListener() {
        Log.d(appType, "Creating a TrackCreatorListener");
        return new TrackCreatorListener() {
            @Override
            public void onCreateSucceeded(View v, final TrackResponse t) {
                Log.d(appType, "Finished creating tracks from the search");
                int j = 0;
                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = 2;
                int localTrackCount = 0;
                Log.d(appType, "Creating buttons for the tracks (if there are any)");
                for (final Item i : t.getTracks().getItems()) {
                    localTrackCount++;
                    @SuppressLint("RestrictedApi") final Button btn = new Button(new ContextThemeWrapper(getApplicationContext(), R.style.Track), null, R.style.Track);
                    btn.setId(j);
                    btn.setText(generateButtonText(i), TextView.BufferType.SPANNABLE);
                    searchResultView.post(new Runnable() {
                        public void run() {
                            searchResultView.addView(btn, params);
                        }
                    });
                    btn.setOnClickListener(createSongButtonOnClickListener(btn, i));
                    j++;
                }
                if (localTrackCount == 0) {
                    Log.d(appType, "No tracks found");
                    @SuppressLint("RestrictedApi") final Button btn = new Button(new ContextThemeWrapper(getApplicationContext(), R.style.Track), null, R.style.Track);
                    btn.setId(0);
                    btn.setText(generateButtonText(null), TextView.BufferType.SPANNABLE);
                    btn.setGravity(Gravity.CENTER_HORIZONTAL);
                    searchResultView.post(new Runnable() {
                        public void run() {
                            searchResultView.addView(btn, params);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingCircle.setVisibility(View.GONE);
                    }
                });
            }
        };
    }

    public View.OnClickListener createBackButtonOnClickListener(){
        Log.d(appType, "Creating backButton OnClickListener");
        return new View.OnClickListener(){
            public void onClick(View v){
                Log.d(appType, "Clicked the back button");
                animateButtonClick(colorPrimary, colorPrimaryClicked, 250, backButton);
                String temp = adapter.prev();
                alreadyChanged = true;
                if (!temp.equals("")){
                    Log.d(appType, "Skipping to previous track");
                    musicPlayer.playUri(null, temp, 0, 0);
                    setText(playPause, "Pause");
                }
                else{
                    if(musicPlayer.getPlaybackState().isPlaying) {
                        Log.d(appType, "Skipped back past first song; stopped playing songs");
                        musicPlayer.pause(null);
                        setText(playPause, "Play");
                        musicPlayer.skipToNext(null);
                    }
                }
            }
        };
    }

    public SearchListener createSearchListener(final TrackCreatorListener trackCreatorListener){
        Log.d(appType, "Creating a SearchListener");
        return new SearchListener() {
            @Override
            public void onSearchSucceeded(ArrayList<String> result) {
                Log.d(appType, "Search succeeded, wrapping responses before displaying them");
                ResponseWrapper responseWrapper = new ResponseWrapper();
                responseWrapper.setOnCreateListener(trackCreatorListener);
                responseWrapper.setView(searchResultView);
                responseWrapper.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);

            }
        };
    }

    public View.OnClickListener createFindButtonOnClickListener(final SearchListener searchListener){
        Log.d(appType, "Creating FindButton's OnClickListener");
        return new View.OnClickListener() {
            //TODO: update this to query the database for songs
            @Override
            public void onClick(View v) {
                String p = search.getText().toString().trim();
                if (p.length()>0) {
                    Log.d(appType, "Searching for songs");
                    //musicPlayer.pause(null);
                    animateButtonClick(colorPrimary, colorPrimaryClicked, 250, findButton);
                    searchResultView.removeAllViews();
                    loadingCircle.setVisibility(View.VISIBLE);
                    p = p.replaceAll("\\s{2,}", " ").trim();
                    p = p.replaceAll(" ", "%20");
                    SearchReader search = new SearchReader();
                    search.setOnSearchListener(searchListener);
                    search.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://api.spotify.com/v1/search?q=" + p + "&type=track");
                } else { Log.d(appType, "Must enter non-white-text to search"); }
            }
        };
    }

    public View.OnClickListener createAddSongOnClickListener(){
        Log.d(appType, "Creating AddSong's on Click Listener");
        return new View.OnClickListener(){
            public void onClick(View v){
                Log.d(appType, "Changing layout from " + (adding ? "adding songs to viewing queue" : "viewing queue to adding songs"));
                animateButtonClick(colorPrimary, colorPrimaryClicked, 250, addSong);
                if(adding){
                    loadingCircle.setVisibility(View.GONE);
                    search.setVisibility(View.GONE);
                    findButton.setVisibility(View.GONE);
                    scrollView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    queueOrSearch.setText("Queue");
                    addSong.setText("Search");
                    if(isServer) {
                        playPause.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                        backButton.setVisibility(View.VISIBLE);
                    }
                    adding = false;
                }else{
                    search.setVisibility(View.VISIBLE);
                    findButton.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    queueOrSearch.setText("Search");
                    addSong.setText("View Queue");
                    playPause.setVisibility(View.GONE);
                    nextButton.setVisibility(View.GONE);
                    backButton.setVisibility(View.GONE);
                    adding = true;
                }
            }
        };
    }

    public MessageListener createMessageListener(){
        Log.d(appType, "Creating a MessageListener");
        return new MessageListener(){
            @Override
            public void onMessageSucceeded(String result) {
                try {
                    final Message m = jsonConverter.readValue(result, Message.class);
                    switch (m.getCode()) {
                        case ADD: {
                            Log.d(appType, "Received message of ADD type");
                            if(isServer){
                                final Item i = m.getItem();
                                count++;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.addItem(i, generateButtonText(i).toString());
                                        if (musicPlayer.getMetadata().currentTrack == null && !musicPlayer.getPlaybackState().isPlaying) {
                                            musicPlayer.playUri(null, adapter.next(), 0, 0);
                                            setText(playPause, "Pause");
                                            alreadyChanged = true;
                                        }
                                    }
                                });
                                sendMessage(m);
                            } else {
                                final Item i = m.getItem();
                                count++;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.addItem(i, generateButtonText(i).toString());
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
                                    adapter.swap(m.getVal1(), m.getVal2());
                                }
                            });
                            break;
                        }
                        case REMOVE:{
                            Log.d(appType, "Received message of REMOVE type");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.remove(m.getVal1());
                                }
                            });
                            break;
                        }
                        case CHANGE_PLAYING:{
                            Log.d(appType, "Received message of CHANGE_PLAYING type");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.changePlaying(m.getVal1());
                                }
                            });
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    public Player.NotificationCallback createPlayerNotificationCallback(){
        Log.d(appType, "Creating a PlayerNotificationCallback");
        return new Player.NotificationCallback() {
            @Override
            public void onPlaybackEvent(PlayerEvent playerEvent) {
                Log.d(appType, "Received player event: " + playerEvent.toString());
                if (playerEvent == PlayerEvent.kSpPlaybackNotifyTrackChanged){
                    if(!alreadyChanged) {
                        String temp = adapter.next();
                        if (!temp.equals("")) {
                            musicPlayer.playUri(null, temp, 0, 0);
                            setText(playPause, "Pause");
                        } else {
                            setText(playPause, "Play");
                        }
                        alreadyChanged = true;
                    }
                    else{
                        alreadyChanged = false;
                    }
                }
            }

            @Override
            public void onPlaybackError(Error error) {

            }
        };
    }

    public View.OnClickListener createSongButtonOnClickListener(final Button btn, final Item i){
        Log.d(appType, "Creating SongButton's OnClickListener");
        if(isServer){
            return new View.OnClickListener() {
                public void onClick(View view) {
                    animateButtonClick(colorBackground, colorBackgroundClicked, 250, btn);
                    count++;
                    adapter.addItem(i, generateButtonText(i).toString());
                    if (musicPlayer.getMetadata().currentTrack == null && !musicPlayer.getPlaybackState().isPlaying) {
                        musicPlayer.playUri(null, adapter.next(), 0, 0);
                        setText(playPause, "Pause");
                        alreadyChanged = true;
                    }

                    Message m = new Message(i);
                    Log.d(appType, "sending message: " + m.getCode().toString());
                    sendMessage(m);
                    //recyclerView.addView(btn, params);

                }
            };
        } else {
            return new View.OnClickListener(){
                public void onClick(View view){
                    animateButtonClick(colorBackground, colorBackgroundClicked, 250, btn);
                    Message m = new Message(i);
                    Log.d(appType, "sending message: " + m.getCode().toString());
                    sendMessage(m);
                }
            };
        }

    }

    public void createAndRunRouterListener(){
        Log.d(appType, "Creating and running routerListener");
        messageListener = createMessageListener();
        serverListener = new ServerListener();
        serverListener.setOnServerListnerListener(messageListener);
        serverListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void addButton(final LinearLayout queueBox, final Button btn, final LinearLayout.LayoutParams params){
        Log.d(appType, "Adding a button");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                queueBox.addView(btn, params);
            }
        });
    }

    private void setText(final Button b, final String text){
        Log.d(appType, "Setting button's text");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                b.setText(text);
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
            Log.i(appType, "Generating button text");
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

    public void sendMessage(Message m){
        Log.d(appType, "Sending a message to the router");
        try {
            new ServerWriter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonConverter.writeValueAsString(m));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void informRouterOfQuit(){
        Log.d(appType, "Informing the router that the server is quitting");
        new ServerWriter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Quit");
    }
}

