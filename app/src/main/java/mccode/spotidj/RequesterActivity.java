package mccode.spotidj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import static mccode.spotidj.MainActivity.mPlayer;

public class RequesterActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{

    private static final String CLIENT_ID = "dfa2a91d372d42db9cb74bed20fb5630";
    private static final String REDIRECT_URI = "mccode-spotidj://callback";


    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    //private Player mPlayer;
    private ArrayList<String> searchResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_requester);
        final Button find = (Button) findViewById(R.id.find_button);
        final EditText search = (EditText) findViewById(R.id.search_bar);
        final EditText searchResultView = (EditText) findViewById(R.id.search_result);
        final ProgressBar loadingCircle = (ProgressBar) findViewById(R.id.progressBar);
        loadingCircle.setVisibility(View.GONE);
        final TrackCreaterListener createrListener= new TrackCreaterListener() {
            @Override
            public void onCreateSucceeded(View v, final TrackSearchResult t) {
                searchResultView.setText("artist: "+ t.getArtistName() +"\ntrackID: " + t.getTrackID() +"\ntrackName"+ t.getTrackName());
                searchResultView.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        mPlayer.playUri(null, "spotify:track:" + t.getTrackID(), 0, 0);
                    }
                });
            }
        };
        final SearchListener listener = new SearchListener() {
            @Override
            public void onSearchSucceeded(ArrayList<String> result) {
                searchResult = result;
//                String resultstring = new String();
//                String rsnoline = new String();
//                for(Iterator<String> i = searchResult.iterator(); i.hasNext();){
//                    String line = i.next();
//                    resultstring += line;
//                    resultstring += '\n';
//                }
//                loadingCircle.setVisibility(View.GONE);
//                searchResultView.setText(resultstring);
//                System.out.println(rsnoline);
                loadingCircle.setVisibility(View.GONE);
                TrackSearchResult track = new TrackSearchResult(searchResult, createrListener, searchResultView);


            }
        };
        find.setOnClickListener(new View.OnClickListener() {
            //TODO: update this to query the database for songs
            @Override
            public void onClick(View v) {
                mPlayer.pause(null);
                loadingCircle.setVisibility(View.VISIBLE);
                String p = search.getText().toString();
                System.out.println(p);
                p = p.replaceAll("\\s{2,}", " ").trim();
                p = p.replaceAll(" ", "%20");
                SearchReader search = new SearchReader();
                search.setOnSearchListener(listener);
                search.execute("https://api.spotify.com/v1/search?q=" + p + "&type=track&limit=2");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
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
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }
}