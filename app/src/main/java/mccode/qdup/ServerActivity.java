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

import static mccode.qdup.MainActivity.serverCode;
import static mccode.qdup.MainActivity.musicPlayer;
import static mccode.qdup.MainActivity.jsonConverter;
import static mccode.qdup.MainActivity.routerSocket;
import static mccode.qdup.MainActivity.isServer;
import static mccode.qdup.Utils.GeneralUIUtils.animateButtonClick;

public class ServerActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    private static final String CLIENT_ID = "dfa2a91d372d42db9cb74bed20fb5630";
    private static final String REDIRECT_URI = "mccode-qdup://callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    private int itemCount = 0;

    //public static ArrayList<Item> queue = new ArrayList<>();
    public static final int jumpBackNum = 10;
    public static int position = -1;
    public static int count = 0;
    boolean adding = false;
    //private Player musicPlayer;
    private boolean alreadyChanged = false;
    private String appType;

    TextView serverKey;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_server);
        appType = isServer ? "serverActivity" : "clientActivity";
        initializeScreenElements(isServer);
        setupTrackCreatorListenerAndSearchListener();
        createButtonListeners(isServer);
        createAndRunRouterListener();
        Log.d(appType, serverCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
        trackCreatorListener = createTrackCreatorListener();
        searchListener = createSearchListener(trackCreatorListener);
    }

    public void initializeScreenElements(boolean isServer){
        hookUpElementsWithFrontEnd();
        setupRecyclerView();
        showAndHideElementsBasedOffOfServerOrClient(isServer);
    }

    public void hookUpElementsWithFrontEnd(){
        // colors
        colorBackground = ContextCompat.getColor(getApplicationContext(), R.color.background);
        colorBackgroundClicked = ContextCompat.getColor(getApplicationContext(), R.color.backgroundClicked);
        colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        colorFaded = ContextCompat.getColor(getApplicationContext(), R.color.faded);
        colorPrimaryClicked = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryClicked);

        // server and client
        serverKey = (TextView) findViewById(R.id.ServerKey);
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
        adapter = new HostRecyclerListAdapter(this);
        callback = new HostItemTouchHelper(adapter);
        touchHelper = new ItemTouchHelper(callback);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    public void showAndHideElementsBasedOffOfServerOrClient(boolean isServer){
        serverKey.setText(serverCode);
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
        return new View.OnClickListener(){
            public void onClick(View v){
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
        return new View.OnClickListener(){
            public void onClick(View v){
                animateButtonClick(colorPrimary, colorPrimaryClicked, 250, nextButton);
                String temp = adapter.next();
                alreadyChanged = true;
                if (!temp.equals("")){
                    musicPlayer.playUri(null, temp, 0, 0);
                    setText(playPause, "Pause");
                }
                else{
                    if(musicPlayer.getPlaybackState().isPlaying) {
                        musicPlayer.pause(null);
                        setText(playPause, "Play");
                        musicPlayer.skipToNext(null);
                    }
                }

            }
        };
    }

    public TrackCreatorListener createTrackCreatorListener() {
        return new TrackCreatorListener() {
            @Override
            public void onCreateSucceeded(View v, final TrackResponse t) {
                int j = 0;
                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = 2;
                int localTrackCount = 0;
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
        return new View.OnClickListener(){
            public void onClick(View v){
                animateButtonClick(colorPrimary, colorPrimaryClicked, 250, backButton);
                String temp = adapter.prev();
                alreadyChanged = true;
                if (!temp.equals("")){
                    musicPlayer.playUri(null, temp, 0, 0);
                    setText(playPause, "Pause");
                }
                else{
                    if(musicPlayer.getPlaybackState().isPlaying) {
                        musicPlayer.pause(null);
                        setText(playPause, "Play");
                        musicPlayer.skipToNext(null);
                    }
                }
            }
        };
    }

    public SearchListener createSearchListener(final TrackCreatorListener trackCreatorListener){
        return new SearchListener() {
            @Override
            public void onSearchSucceeded(ArrayList<String> result) {
                ResponseWrapper responseWrapper = new ResponseWrapper();
                responseWrapper.setOnCreateListener(trackCreatorListener);
                responseWrapper.setView(searchResultView);
                responseWrapper.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);

            }
        };
    }

    public View.OnClickListener createFindButtonOnClickListener(final SearchListener searchListener){
        return new View.OnClickListener() {
            //TODO: update this to query the database for songs
            @Override
            public void onClick(View v) {
                String p = search.getText().toString().trim();
                if (p.length()>0) {
                    //musicPlayer.pause(null);
                    animateButtonClick(colorPrimary, colorPrimaryClicked, 250, findButton);
                    searchResultView.removeAllViews();
                    loadingCircle.setVisibility(View.VISIBLE);
                    p = p.replaceAll("\\s{2,}", " ").trim();
                    p = p.replaceAll(" ", "%20");
                    SearchReader search = new SearchReader();
                    search.setOnSearchListener(searchListener);
                    search.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://api.spotify.com/v1/search?q=" + p + "&type=track");
                }
            }
        };
    }

    public View.OnClickListener createAddSongOnClickListener(){
        return new View.OnClickListener(){
            public void onClick(View v){
                animateButtonClick(colorPrimary, colorPrimaryClicked, 250, addSong);
                if(adding){
                    loadingCircle.setVisibility(View.GONE);
                    search.setVisibility(View.GONE);
                    findButton.setVisibility(View.GONE);
                    scrollView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    queueOrSearch.setText("Queue");
                    addSong.setText("Add Song");
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
        return new MessageListener(){
            @Override
            public void onMessageSucceeded(String result) {
                try {
                    final Message m = jsonConverter.readValue(result, Message.class);
                    switch (m.getCode()) {
                        case ADD: {
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
                                Log.d(appType, "sending message: " + m.getCode().toString());
                                sendMessage(m);
                                //recyclerView.addView(btn, params);
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.swap(m.getVal1(), m.getVal2());
                                }
                            });
                            break;
                        }
                        case REMOVE:{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.remove(m.getVal1());
                                }
                            });
                            break;
                        }
                        case CHANGE_PLAYING:{
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
        return new Player.NotificationCallback() {
            @Override
            public void onPlaybackEvent(PlayerEvent playerEvent) {
                System.out.println(playerEvent);
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
                    Log.d("requester activity", "sending song");
                    Message m = new Message(i);
                    sendMessage(m);
                }
            };
        }

    }

    public void createAndRunRouterListener(){
        messageListener = createMessageListener();
        serverListener = new ServerListener();
        serverListener.setOnServerListnerListener(messageListener);
        serverListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void addButton(final LinearLayout queueBox, final Button btn, final LinearLayout.LayoutParams params){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                queueBox.addView(btn, params);
            }
        });
    }

    private void setText(final Button b, final String text){
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
        musicPlayer.playUri(null, uri, 0,0);
        alreadyChanged = true;
    }

    public void sendMessage(Message m){
        try {
            new ServerWriter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonConverter.writeValueAsString(m));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void informRouterOfQuit(){
        new ServerWriter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Quit");
    }
}

