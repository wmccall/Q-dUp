package mccode.spotidj;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.codehaus.jackson.map.ObjectMapper;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.IOException;
import java.util.ArrayList;

import mccode.spotidj.Utils.Client.ClientWriter;
import mccode.spotidj.models.Artist_;
import mccode.spotidj.models.Item;
import mccode.spotidj.models.ResponseWrapper;
import mccode.spotidj.models.TrackResponse;

import static mccode.spotidj.MainActivity.mPlayer;
import static mccode.spotidj.MainActivity.mapper;
import static mccode.spotidj.MainActivity.routerSocket;

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
        //final EditText searchResultView = (EditText) findViewById(R.id.search_result);
        final LinearLayout searchResultView = (LinearLayout) findViewById(R.id.ButtonLocation);
        final ProgressBar loadingCircle = (ProgressBar) findViewById(R.id.progressBar);
        //final ClientWriter w = new ClientWriter();
        loadingCircle.setVisibility(View.GONE);
        final TrackCreaterListener createrListener = new TrackCreaterListener() {
            @Override
            public void onCreateSucceeded(View v, final TrackResponse t) {
                int j = 0;
                for(final Item i: t.getTracks().getItems()){
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.bottomMargin = 2;
                    Button btn = new Button(new ContextThemeWrapper(getApplicationContext(), R.style.Track) ,null, R.style.Track);
                    btn.setId(j);


                    String artists;
                    int size = i.getArtists().size();
                    artists = i.getArtists().get(0).getName();
                    if(size > 1){
                        for(int k = 1; k < size; k++){
                            artists += ", " + i.getArtists().get(k).getName();
                        }
                    }
                    int f = j+1;
                    artists =i.getName() + "\n" + artists + " - " + i.getAlbum().getName();
                    int firstLength = i.getName().length();
                    int total = artists.length();
                    SpannableString text = new SpannableString(artists);
                    text.setSpan(new TextAppearanceSpan(getApplicationContext(), R.style.TrackTitle),
                            0, firstLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    text.setSpan(new TextAppearanceSpan(getApplicationContext(), R.style.TrackArtist),
                            firstLength, total,  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    btn.setText(text, TextView.BufferType.SPANNABLE);
                    searchResultView.addView(btn, params);
                    Button tmpbtn = ((Button) findViewById(j));
                    tmpbtn.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View view){
                            //mPlayer.playUri(null, i.getUri(), 0, 0);
                            ClientWriter w = new ClientWriter();
                            //w.execute(i.getUri());
                            try {
                                w.execute(mapper.writeValueAsString(i));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    j++;
                }
                loadingCircle.setVisibility(View.GONE);
            }
        };
        final SearchListener listener = new SearchListener() {
            @Override
            public void onSearchSucceeded(ArrayList<String> result) {
                new ResponseWrapper(result, createrListener, searchResultView);

            }
        };
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
                    search.execute("https://api.spotify.com/v1/search?q=" + p + "&type=track");
                }
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