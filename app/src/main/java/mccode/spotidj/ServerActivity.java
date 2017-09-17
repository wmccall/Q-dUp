package mccode.spotidj;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
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

import mccode.spotidj.Utils.Client.ClientWriter;
import mccode.spotidj.Utils.Listeners.MessageListener;
import mccode.spotidj.Utils.Listeners.SearchListener;
import mccode.spotidj.Utils.Server.ServerListener;
import mccode.spotidj.models.Item;
import mccode.spotidj.models.ResponseWrapper;
import mccode.spotidj.models.TrackResponse;

import static mccode.spotidj.MainActivity.key;
import static mccode.spotidj.MainActivity.mPlayer;
import static mccode.spotidj.MainActivity.mapper;

public class ServerActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    private static final String CLIENT_ID = "dfa2a91d372d42db9cb74bed20fb5630";
    private static final String REDIRECT_URI = "mccode-spotidj://callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    //public static ArrayList<Item> queue = new ArrayList<>();
    public static final int jumpBackNum = 10;
    public static int position = -1;
    public static int count = 0;
    boolean adding = false;
    //private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_server);
        TextView serverKey = (TextView) findViewById(R.id.ServerKey);
        serverKey.setText(key);
        final LinearLayout queueBox = (LinearLayout) findViewById(R.id.QueueBox);
        final Button playPause = (Button) findViewById(R.id.PlayPause);
        final Button nextButton = (Button) findViewById(R.id.Skip);
        final Button backButton = (Button) findViewById(R.id.Back);
        final Button addSong = (Button) findViewById(R.id.AddSong);
        final TextView queueOrSearch = (TextView) findViewById(R.id.QueueText);
        final ScrollView scrollView2 = (ScrollView) findViewById(R.id.scrollView2);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
        final LinearLayout searchResultView = (LinearLayout) findViewById(R.id.ButtonLocation);
        progress.setVisibility(View.GONE);
        final EditText search = (EditText) findViewById(R.id.search_bar);
        search.setVisibility(View.GONE);
        final Button findButton = (Button) findViewById(R.id.find_button);
        findButton.setVisibility(View.GONE);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);

        playPause.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(mPlayer.getPlaybackState().isPlaying){
                    playPause.setText("Play");
                    mPlayer.pause(null);
                }else{
                    playPause.setText("Pause");
                    mPlayer.resume(null);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mPlayer.skipToNext(null);
            }
        });

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
                                //mPlayer.playUri(null, i.getUri(), 0, 0);
                                //queue.add(i);
                                count++;
                                if(mPlayer.getMetadata().nextTrack == null && !mPlayer.getPlaybackState().isPlaying){
                                    mPlayer.playUri(null, i.getUri(), 0, 0);
                                    setText(playPause, "Pause");
                                }else{
                                    mPlayer.queue(null, i.getUri());
                                }
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.bottomMargin = 2;
                                Button btn = new Button(new ContextThemeWrapper(getApplicationContext(), R.style.Track) ,null, R.style.Track);
                                btn.setId(count);
                                btn.setText(generateButtonText(i), TextView.BufferType.SPANNABLE);
                                //queueBox.addView(btn, params);
                                addButton(queueBox,btn,params);
                        }
                    });
                    j++;
                }
            }
        };

        nextButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mPlayer.skipToPrevious(null);
            }
        });
        final SearchListener searchListener = new SearchListener() {
            @Override
            public void onSearchSucceeded(ArrayList<String> result) {
                ResponseWrapper responseWrapper = new ResponseWrapper();
                responseWrapper.setOnCreateListener(creatorListener);
                responseWrapper.setView(searchResultView);
                responseWrapper.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);

            }
        };
        findButton.setOnClickListener(new View.OnClickListener() {
            //TODO: update this to query the database for songs
            @Override
            public void onClick(View v) {
                String p = search.getText().toString().trim();
                if (p.length()>0) {
                    //mPlayer.pause(null);
                    searchResultView.removeAllViews();
                    progress.setVisibility(View.VISIBLE);
                    p = p.replaceAll("\\s{2,}", " ").trim();
                    p = p.replaceAll(" ", "%20");
                    SearchReader search = new SearchReader();
                    search.setOnSearchListener(searchListener);
                    search.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://api.spotify.com/v1/search?q=" + p + "&type=track");
                }
            }
        });

        addSong.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(adding){
                    progress.setVisibility(View.GONE);
                    search.setVisibility(View.GONE);
                    findButton.setVisibility(View.GONE);
                    scrollView.setVisibility(View.GONE);
                    scrollView2.setVisibility(View.VISIBLE);
                    queueOrSearch.setText("Queue");
                    addSong.setText("Add Song");
                    playPause.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.VISIBLE);
                    backButton.setVisibility(View.VISIBLE);
                    adding = false;
                }else{
                    search.setVisibility(View.VISIBLE);
                    findButton.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    scrollView2.setVisibility(View.GONE);
                    queueOrSearch.setText("Search");
                    addSong.setText("View Queue");
                    playPause.setVisibility(View.GONE);
                    nextButton.setVisibility(View.GONE);
                    backButton.setVisibility(View.GONE);
                    adding = true;
                }
            }
        });

        final MessageListener ml = new MessageListener(){

            @Override
            public void onMessageSucceeded(String result) {
                try {
                    Item i = mapper.readValue(result, Item.class);
                    //mPlayer.playUri(null, i.getUri(), 0, 0);
                    //queue.add(i);
                    count++;
                    if(mPlayer.getMetadata().nextTrack == null && !mPlayer.getPlaybackState().isPlaying){
                        mPlayer.playUri(null, i.getUri(), 0, 0);
                        setText(playPause, "Pause");
                    }else{
                        mPlayer.queue(null, i.getUri());
                    }
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

        ServerListener listener = new ServerListener();
        listener.setOnServerListnerListener(ml);
        listener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            mPlayer.pause(null);
            finish();
        }
        return super.onKeyDown(keyCode, event);
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