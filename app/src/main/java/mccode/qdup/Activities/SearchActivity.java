package mccode.qdup.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;

import mccode.qdup.QueryModels.Item;
import mccode.qdup.QueryModels.ResponseWrapper;
import mccode.qdup.QueryModels.TrackResponse;

import mccode.qdup.R;
import mccode.qdup.Utils.GeneralNetworkingUtils;
import mccode.qdup.Utils.SearchReader;
import mccode.qdup.Utils.Listeners.SearchListener;
import mccode.qdup.Utils.Listeners.TrackCreatorListener;
import mccode.qdup.Utils.Messaging.Message;


import static mccode.qdup.Utils.GeneralUIUtils.animateButtonClick;

public class SearchActivity extends Activity{

    public static int count = 0;
    boolean adding = false;
    private String appType;

    int colorBackground;
    int colorBackgroundClicked;
    int colorPrimary;
    int colorFaded;
    int colorPrimaryClicked;

    private static ProgressBar searchLoadingCircle;
    private static Button searchSwitchToQueueButton;
    private static TextView searchServerKeyView;
    private static LinearLayout searchResultView;
    private static EditText searchBar;
    private static Button searchButton;
    public static ScrollView searchView;

    TrackCreatorListener trackCreatorListener;
    SearchListener searchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appType = "McCode-SearchActivity-" + (PortalActivity.isServer ? "Server" : "Client");
        Log.d(appType, "OnCreate running");
        super.onCreate(savedInstanceState);
        initializeScreenElements();
        setupTrackCreatorListenerAndSearchListener();
        createButtonListeners();
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
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupTrackCreatorListenerAndSearchListener(){
        Log.d(appType, "Setting up TrackCreatorListener and SearchListener");
        trackCreatorListener = createTrackCreatorListener();
        searchListener = createSearchListener(trackCreatorListener);
    }

    public void initializeScreenElements(){
        Log.d(appType, "Initializing screen elements");
        setContentView(R.layout.search_layout);
        hookUpElementsWithFrontEnd();
        showAndHideElements();
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
        searchServerKeyView = (TextView) findViewById(R.id.SearchServerKey);
        searchServerKeyView.setText(PortalActivity.serverKey);
        searchResultView = (LinearLayout) findViewById(R.id.SearchButtonLocation);
        searchBar = (EditText) findViewById(R.id.SearchBar);
        searchButton = (Button) findViewById(R.id.SearchButton);
        searchView = (ScrollView) findViewById(R.id.SearchView);
        searchLoadingCircle = (ProgressBar) findViewById(R.id.SearchProgressBar);
        searchSwitchToQueueButton = (Button) findViewById(R.id.SearchViewQueueButton);

    }

    public void showAndHideElements(){
//        Log.d(appType, "Showing and hiding elements for the " + (isServer ? "server" : "client"));
        searchLoadingCircle.setVisibility(View.GONE);
    }

    public void createButtonListeners(){
        Log.d(appType, "Creating button listeners");
        searchSwitchToQueueButton.setOnClickListener(createSearchSwitchToQueueButtonOnClickListener());
        searchButton.setOnClickListener(createSearchButtonOnClickListener(searchListener));
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
                        searchLoadingCircle.setVisibility(View.GONE);
                    }
                });
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

    public View.OnClickListener createSearchButtonOnClickListener(final SearchListener searchListener){
        Log.d(appType, "Creating searchButton's OnClickListener");
        return new View.OnClickListener() {
            //TODO: update this to query the database for songs
            @Override
            public void onClick(View v) {
                String p = searchBar.getText().toString().trim();
                if (p.length()>0) {
                    Log.d(appType, "Searching for songs");
                    animateButtonClick(colorPrimary, colorPrimaryClicked, 250, searchButton);
                    searchResultView.removeAllViews();
                    searchLoadingCircle.setVisibility(View.VISIBLE);
                    p = p.replaceAll("\\s{2,}", " ").trim();
                    p = p.replaceAll(" ", "%20");
                    SearchReader search = new SearchReader();
                    search.setOnSearchListener(searchListener);
                    search.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://api.spotify.com/v1/search?q=" + p + "&type=track");
                } else { Log.d(appType, "Must enter non-white-text to search"); }
            }
        };
    }

    public View.OnClickListener createSearchSwitchToQueueButtonOnClickListener(){
        Log.d(appType, "Creating searchSwitchToQueueButton's on Click Listener");
        return new View.OnClickListener(){
            public void onClick(View v){
                Log.d(appType, "Changing layout from " + (adding ? "adding songs to viewing queue" : "viewing queue to adding songs"));
                animateButtonClick(colorPrimary, colorPrimaryClicked, 250, searchSwitchToQueueButton);
                finish();
                overridePendingTransition(0, 0);
            }
        };
    }

    public View.OnClickListener createSongButtonOnClickListener(final Button btn, final Item i){
        Log.d(appType, "Creating SongButton's OnClickListener");
        if(PortalActivity.isServer){
            return new View.OnClickListener() {
                public void onClick(View view) {
                    animateButtonClick(colorBackground, colorBackgroundClicked, 250, btn);
                    count++;
                    QueueActivity.queueViewAdapter.addItem(i, generateButtonText(i).toString());
                    if (QueueActivity.musicPlayer.getMetadata().currentTrack == null && !QueueActivity.musicPlayer.getPlaybackState().isPlaying) {
                        QueueActivity.musicPlayer.playUri(null, QueueActivity.queueViewAdapter.next(), 0, 0);
                        changePlayingIcon(QueueActivity.queuePlayPause, true);
                        QueueActivity.alreadyChanged = true;
                    }

                    Message m = new Message(i);
                    Log.d(appType, "sending message: " + m.getCode().toString());
                    GeneralNetworkingUtils.sendMessage(m);
                }
            };
        } else {
            return new View.OnClickListener(){
                public void onClick(View view){
                    animateButtonClick(colorBackground, colorBackgroundClicked, 250, btn);
                    Message m = new Message(i);
                    Log.d(appType, "sending message: " + m.getCode().toString());
                    GeneralNetworkingUtils.sendMessage(m);
                }
            };
        }

    }

    private void setImage(final ImageButton b, final int resource){
        Log.d(appType, "Setting button's text");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                b.setImageResource(resource);
            }
        });
    }

    private void changePlayingIcon(final Button queuePlayPause, final boolean playToPause){
        Log.d(appType, "Setting button's Image");
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

    /**
     * given an Item i, generates the formatted text that would go on a track displayed from
     * a search result
     * @param i Item to be turned into a formatted track string
     * @return Spann
     */
    private SpannableString generateButtonText(Item i){
        SpannableString text;
        if(i!=null){
            Log.d(appType, "Generating button text");
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

