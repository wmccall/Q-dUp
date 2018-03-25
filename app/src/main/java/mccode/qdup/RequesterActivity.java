package mccode.qdup;

import android.animation.ValueAnimator;
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
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.IOException;
import java.util.ArrayList;

import mccode.qdup.Utils.Client.ClientListener;
import mccode.qdup.Utils.Client.ClientWriter;
import mccode.qdup.Utils.Listeners.MessageListener;
import mccode.qdup.Utils.Listeners.SearchListener;
import mccode.qdup.Utils.Listeners.TrackCreatorListener;
import mccode.qdup.Utils.Messaging.Message;
import mccode.qdup.Utils.QueueView.RequesterItemTouchHelper;
import mccode.qdup.Utils.QueueView.RequesterRecyclerListAdapter;
import mccode.qdup.QueryModels.Item;
import mccode.qdup.QueryModels.ResponseWrapper;
import mccode.qdup.QueryModels.TrackResponse;

import static mccode.qdup.MainActivity.serverCode;
import static mccode.qdup.MainActivity.musicPlayer;
import static mccode.qdup.MainActivity.jsonConverter;
import static mccode.qdup.MainActivity.routerSocket;
import static mccode.qdup.Utils.GeneralUIUtils.animateButtonClick;
import static mccode.qdup.Utils.GeneralUIUtils.initializeValueAnimator;

public class RequesterActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{

    private static final String CLIENT_ID = "dfa2a91d372d42db9cb74bed20fb5630";
    private static final String REDIRECT_URI = "mccode-qdup://callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;
    boolean adding = true;
    public static int count = 0;

    private TextView serverKey;
    private Button addSong;
    private TextView queueOrSearch;
    ProgressBar loadingCircle;
    LinearLayout searchResultView;
    EditText search;
    Button findButton;
    ScrollView scrollView;
    RecyclerView recyclerView;
    RequesterRecyclerListAdapter adapter;
    ItemTouchHelper.Callback callback;
    ItemTouchHelper touchHelper;
    int colorBackground;
    int colorBackgroundClicked;
    int colorPrimary;
    int colorFaded;
    int colorPrimaryClicked;

    ClientListener clientListener;
    /**
     * occurs when the page is created
     * creates classes and listeners to handle button presses and searches
     * @param savedInstanceState whatever
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_requester);
        initializeScreenElements();
        serverKey.setText(serverCode);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setVisibility(View.GONE);

        //handles creating buttons for each of the tracks resulting from a search
        final TrackCreatorListener creatorListener = createTrackCreatorListener();

        //object that listens to the spotify query
        //when it gets something it responds by using the above TrackCreatorListener
        final SearchListener listener = createSearchListener(creatorListener);

        //deals with clicking on the search button
        //takes the text and queries spotify with it
        findButton.setOnClickListener(createFindButtonOnClickListener(listener));

        addSong.setOnClickListener(addSongOnClickListener());

        final MessageListener messageListener = createMessageListener();

        createAndStartClientListener(messageListener);
    }

    /**
     * does something about an activity result.
     * just calls the supers method so check the docs for that if you care
     * @param requestCode the request code
     * @param resultCode the result code
     * @param intent the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * destroys the spotify player when the class is destroyed to prevent leaks
     */
    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        musicPlayer.pause(null);
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    /**
     * logs a media player event
     * @param playerEvent the player event to be logged
     */
    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    /**
     * logs a playback error
     * @param error the playback error
     */
    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    public void initializeScreenElements(){
        setContentView(R.layout.type_requester);
        colorBackground = ContextCompat.getColor(getApplicationContext(), R.color.background);
        colorBackgroundClicked = ContextCompat.getColor(getApplicationContext(), R.color.backgroundClicked);
        colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        colorFaded = ContextCompat.getColor(getApplicationContext(), R.color.faded);
        colorPrimaryClicked = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryClicked);
        serverKey = (TextView) findViewById(R.id.ServerKey);
        addSong = (Button) findViewById(R.id.AddSong);
        queueOrSearch = (TextView) findViewById(R.id.QueueText);
        loadingCircle = (ProgressBar) findViewById(R.id.progressBar);
        searchResultView = (LinearLayout) findViewById(R.id.ButtonLocation);
        loadingCircle.setVisibility(View.GONE);
        search = (EditText) findViewById(R.id.search_bar);
        findButton = (Button) findViewById(R.id.find_button);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        recyclerView = (RecyclerView) findViewById(R.id.QueueBox);
        adapter = new RequesterRecyclerListAdapter(this);
        callback = new RequesterItemTouchHelper(adapter);
        touchHelper = new ItemTouchHelper(callback);
    }

    public TrackCreatorListener createTrackCreatorListener(){
        return new TrackCreatorListener() {
            @Override
            public void onCreateSucceeded(View v, final TrackResponse t) {
                int j = 0;
                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = 2;
                int localTrackCount = 0;
                for(final Item i: t.getTracks().getItems()){
                    localTrackCount++;
                    @SuppressLint("RestrictedApi") final Button btn = new Button(new ContextThemeWrapper(getApplicationContext(), R.style.Track) ,null, R.style.Track);
                    btn.setId(j);
                    btn.setText(generateButtonText(i), TextView.BufferType.SPANNABLE);
                    searchResultView.post(new Runnable() {
                        public void run() {
                            searchResultView.addView(btn, params);
                        }
                    });
                    btn.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View view){
                            animateButtonClick(colorBackground, colorBackgroundClicked, 250, btn);
                            ClientWriter w = new ClientWriter();
                            try {
                                Log.d("requester activity", "sending song");
                                Message m = new Message(i);
                                w.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonConverter.writeValueAsString(m));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    j++;
                }
                if(localTrackCount==0){
                    @SuppressLint("RestrictedApi") final Button btn = new Button(new ContextThemeWrapper(getApplicationContext(), R.style.Track) ,null, R.style.Track);
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

    public SearchListener createSearchListener(final TrackCreatorListener creatorListener){
        return new SearchListener() {
            @Override
            public void onSearchSucceeded(ArrayList<String> result) {
                ResponseWrapper responseWrapper = new ResponseWrapper();
                responseWrapper.setOnCreateListener(creatorListener);
                responseWrapper.setView(searchResultView);
                responseWrapper.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);

            }
        };
    }

    public View.OnClickListener createFindButtonOnClickListener(final SearchListener listener){
        return new View.OnClickListener() {
            //TODO: update this to query the database for songs
            @Override
            public void onClick(View v) {
                String p = search.getText().toString().trim();
                if (p.length()>0) {
                    animateButtonClick(colorPrimary, colorPrimaryClicked, 250, findButton);
                    searchResultView.removeAllViews();
                    loadingCircle.setVisibility(View.VISIBLE);
                    p = p.replaceAll("\\s{2,}", " ").trim();
                    p = p.replaceAll(" ", "%20");
                    SearchReader search = new SearchReader();
                    search.setOnSearchListener(listener);
                    search.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://api.spotify.com/v1/search?q=" + p + "&type=track");
                }
            }
        };
    }

    public View.OnClickListener addSongOnClickListener(){
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
                    addSong.setText("add Song");
                    adding = false;
                }else{
                    search.setVisibility(View.VISIBLE);
                    findButton.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    queueOrSearch.setText("Search");
                    addSong.setText("View Queue");
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
                    switch(m.getCode()) {
                        case ADD: {
                            final Item i = m.getItem();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.addItem(i, generateButtonText(i).toString());
                                }
                            });
                            break;
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

    public void createAndStartClientListener(MessageListener messageListener){
        clientListener = new ClientListener();
        clientListener.setOnClientListnerListener(messageListener);
        clientListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * logs the user logging in
     */
    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    /**
     * logs the user logging out
     */
    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    /**
     * logs login error
     * @param error the login error
     */
    @Override
    public void onLoginFailed(Error error) {
        Log.d("MainActivity", "Login failed");
    }

    /**
     * logs a temporary error
     */
    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    /**
     * logs when a connection occurs with a given message
     * @param message connection message
     */
    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            ClientWriter c = new ClientWriter();
            c.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Quit");
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
    /**
     * given an Item i, generates the formatted text that would go on a track displayed from
     * a search result
     * @param i Item to be turned into a formatted track string
     * @return Spann
     */
    private SpannableString generateButtonText(Item i){
        SpannableString text;
        if(i!=null){
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
}

