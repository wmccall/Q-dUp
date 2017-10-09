package mccode.spotidj;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
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

import mccode.spotidj.Utils.Client.ClientListener;
import mccode.spotidj.Utils.Client.ClientWriter;
import mccode.spotidj.Utils.Listeners.MessageListener;
import mccode.spotidj.Utils.Listeners.SearchListener;
import mccode.spotidj.models.Item;
import mccode.spotidj.models.ResponseWrapper;
import mccode.spotidj.models.TrackResponse;

import static mccode.spotidj.MainActivity.key;
import static mccode.spotidj.MainActivity.mPlayer;
import static mccode.spotidj.MainActivity.mapper;

public class RequesterActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{

    private static final String CLIENT_ID = "dfa2a91d372d42db9cb74bed20fb5630";
    private static final String REDIRECT_URI = "mccode-spotidj://callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;
    boolean adding = true;
    public static int count = 0;

    /**
     * occurs when the page is created
     * creates classes and listeners to handle button presses and searches
     * @param savedInstanceState whatever
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_requester);
        TextView serverKey = (TextView) findViewById(R.id.ServerKey);
        serverKey.setText(key);
        final LinearLayout queueBox = (LinearLayout) findViewById(R.id.QueueBox);
        final Button addSong = (Button) findViewById(R.id.AddSong);
        final TextView queueOrSearch = (TextView) findViewById(R.id.QueueText);
        final ScrollView scrollView2 = (ScrollView) findViewById(R.id.scrollView2);
        final ProgressBar loadingCircle = (ProgressBar) findViewById(R.id.progressBar);
        final LinearLayout searchResultView = (LinearLayout) findViewById(R.id.ButtonLocation);
        final int colorBackground = ContextCompat.getColor(getApplicationContext(), R.color.background);
        final int colorBackgroundClicked = ContextCompat.getColor(getApplicationContext(), R.color.backgroundClicked);
        final int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        final int colorFaded = ContextCompat.getColor(getApplicationContext(), R.color.faded);
        final int colorPrimaryClicked = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryClicked);
        loadingCircle.setVisibility(View.GONE);
        final EditText search = (EditText) findViewById(R.id.search_bar);
        final Button findButton = (Button) findViewById(R.id.find_button);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView2.setVisibility(View.GONE);

        //handles creating buttons for each of the tracks resulting from a search
        final TrackCreatorListener creatorListener = new TrackCreatorListener() {
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
                    final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorBackground, colorBackgroundClicked);
                    colorAnimation.setDuration(250);
                    final ValueAnimator colorAnimationRev = ValueAnimator.ofObject(new ArgbEvaluator(), colorBackgroundClicked, colorBackground);
                    colorAnimationRev.setDuration(250);
                    final Button btn = new Button(new ContextThemeWrapper(getApplicationContext(), R.style.Track) ,null, R.style.Track);
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            btn.setBackgroundColor((int) animator.getAnimatedValue());
                        }
                    });
                    colorAnimationRev.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            btn.setBackgroundColor((int) animator.getAnimatedValue());
                        }
                    });
                    btn.setId(j);
                    btn.setText(generateButtonText(i), TextView.BufferType.SPANNABLE);
                    searchResultView.post(new Runnable() {
                        public void run() {
                        searchResultView.addView(btn, params);
                    }
                        });
                    btn.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View view){
                            colorAnimation.start();
                            colorAnimationRev.start();
                            ClientWriter w = new ClientWriter();
                            try {
                                w.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mapper.writeValueAsString(i));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    j++;
                }
                if(localTrackCount==0){
                    final Button btn = new Button(new ContextThemeWrapper(getApplicationContext(), R.style.Track) ,null, R.style.Track);
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

        //object that listens to the spotify query
        //when it gets something it responds by using the above TrackCreatorListener
        final SearchListener listener = new SearchListener() {
            @Override
            public void onSearchSucceeded(ArrayList<String> result) {
                ResponseWrapper responseWrapper = new ResponseWrapper();
                responseWrapper.setOnCreateListener(creatorListener);
                responseWrapper.setView(searchResultView);
                responseWrapper.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);

            }
        };

        //deals with clicking on the search button
        //takes the text and queries spotify with it
        findButton.setOnClickListener(new View.OnClickListener() {
            //TODO: update this to query the database for songs
            @Override
            public void onClick(View v) {
                final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimary, colorPrimaryClicked);
                colorAnimation.setDuration(250);
                final ValueAnimator colorAnimationRev = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimaryClicked, colorPrimary);
                colorAnimationRev.setDuration(250);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        findButton.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimationRev.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        findButton.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                String p = search.getText().toString().trim();
                if (p.length()>0) {
                    //mPlayer.pause(null);
                    colorAnimation.start();
                    colorAnimationRev.start();
                    searchResultView.removeAllViews();
                    loadingCircle.setVisibility(View.VISIBLE);
                    p = p.replaceAll("\\s{2,}", " ").trim();
                    p = p.replaceAll(" ", "%20");
                    SearchReader search = new SearchReader();
                    search.setOnSearchListener(listener);
                    search.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://api.spotify.com/v1/search?q=" + p + "&type=track");
                }
            }
        });
        addSong.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimary, colorPrimaryClicked);
                colorAnimation.setDuration(250);
                final ValueAnimator colorAnimationRev = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimaryClicked, colorPrimary);
                colorAnimationRev.setDuration(250);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        addSong.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimationRev.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        addSong.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimation.start();
                colorAnimationRev.start();
                if(adding){
                    loadingCircle.setVisibility(View.GONE);
                    search.setVisibility(View.GONE);
                    findButton.setVisibility(View.GONE);
                    scrollView.setVisibility(View.GONE);
                    scrollView2.setVisibility(View.VISIBLE);
                    queueOrSearch.setText("Queue");
                    addSong.setText("Add Song");
                    adding = false;
                }else{
                    search.setVisibility(View.VISIBLE);
                    findButton.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    scrollView2.setVisibility(View.GONE);
                    queueOrSearch.setText("Search");
                    addSong.setText("View Queue");
                    adding = true;
                }
            }
        });
        final MessageListener ml = new MessageListener(){

            @Override
            public void onMessageSucceeded(String result) {
                try {
                    System.out.println("HEY HEY HEY");
                    Item i = mapper.readValue(result, Item.class);
                    //mPlayer.playUri(null, i.getUri(), 0, 0);
                    //queue.add(i);
                    count++;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.bottomMargin = 2;
                    Button btn = new Button(new ContextThemeWrapper(getApplicationContext(), R.style.Track) ,null, R.style.Track);
                    btn.setId(count);
                    btn.setText(generateButtonText(i), TextView.BufferType.SPANNABLE);
                    //queueBox.addView(btn, params);
                    addButton(queueBox,btn,params);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        ClientListener clientListener = new ClientListener();
        clientListener.setOnServerListnerListener(ml);
        clientListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        mPlayer.pause(null);
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

    private void addButton(final LinearLayout queueBox, final Button btn, final LinearLayout.LayoutParams params){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                queueBox.addView(btn, params);
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