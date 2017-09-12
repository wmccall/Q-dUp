package mccode.spotidj;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.IOException;
import java.util.ArrayList;

import mccode.spotidj.Utils.Client.ClientWriter;
import mccode.spotidj.Utils.Listeners.SearchListener;
import mccode.spotidj.Utils.Listeners.TrackCreaterListener;
import mccode.spotidj.models.Item;
import mccode.spotidj.models.ResponseWrapper;
import mccode.spotidj.models.TrackResponse;

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

    /**
     * occurs when the page is created
     * creates classes and listeners to handle button presses and searches
     * @param savedInstanceState whatever
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_requester);
        final Button find = (Button) findViewById(R.id.find_button);
        final EditText search = (EditText) findViewById(R.id.search_bar);
        final LinearLayout searchResultView = (LinearLayout) findViewById(R.id.ButtonLocation);
        final ProgressBar loadingCircle = (ProgressBar) findViewById(R.id.progressBar);
        loadingCircle.setVisibility(View.GONE);

        //handles creating buttons for each of the tracks resulting from a search
        final TrackCreatorListener creatorListener = new TrackCreatorListener() {
            @Override
            public void onCreateSucceeded(View v, final TrackResponse t) {
                int j = 0;
                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = 2;
                for(final Item i: t.getTracks().getItems()){
                    final Button btn = new Button(new ContextThemeWrapper(getApplicationContext(), R.style.Track) ,null, R.style.Track);
                    btn.setId(j);
                    btn.setText(generateButtonText(i), TextView.BufferType.SPANNABLE);
                    searchResultView.post(new Runnable() {
                        public void run() {
                        searchResultView.addView(btn, params);
                    }
                        });
                    btn.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View view){
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
        find.setOnClickListener(new View.OnClickListener() {
            //TODO: update this to query the database for songs
            @Override
            public void onClick(View v) {
                String p = search.getText().toString().trim();
                if (p.length()>0) {
                    //mPlayer.pause(null);
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

    /**
     * given an Item i, generates the formatted text that would go on a track displayed from
     * a search result
     * @param i Item to be turned into a formatted track string
     * @return Spann
     */
    private SpannableString generateButtonText(Item i){
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
        SpannableString text = new SpannableString(artists);
        text.setSpan(new TextAppearanceSpan(getApplicationContext(), R.style.TrackTitle),
                0, firstLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new TextAppearanceSpan(getApplicationContext(), R.style.TrackArtist),
                firstLength, total,  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text;
    }
}