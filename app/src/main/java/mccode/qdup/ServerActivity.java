package mccode.qdup;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
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

import mccode.qdup.Utils.CustomItemTouchHelper;
import mccode.qdup.Utils.Listeners.MessageListener;
import mccode.qdup.Utils.Listeners.SearchListener;
import mccode.qdup.Utils.RecyclerListAdapter;
import mccode.qdup.Utils.Server.ServerListener;
import mccode.qdup.Utils.Server.ServerWriter;
import mccode.qdup.models.Item;
import mccode.qdup.models.ResponseWrapper;
import mccode.qdup.models.TrackResponse;

import static mccode.qdup.MainActivity.key;
import static mccode.qdup.MainActivity.mPlayer;
import static mccode.qdup.MainActivity.mapper;
import static mccode.qdup.MainActivity.routerSocket;

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
    //private Player mPlayer;
    private boolean alreadyChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_server);
        TextView serverKey = (TextView) findViewById(R.id.ServerKey);
        serverKey.setText(key);

        final Button playPause = (Button) findViewById(R.id.PlayPause);
        final Button nextButton = (Button) findViewById(R.id.Skip);
        final Button backButton = (Button) findViewById(R.id.Back);
        final Button addSong = (Button) findViewById(R.id.AddSong);
        final TextView queueOrSearch = (TextView) findViewById(R.id.QueueText);
        final ProgressBar loadingCircle = (ProgressBar) findViewById(R.id.progressBar);
        final LinearLayout searchResultView = (LinearLayout) findViewById(R.id.ButtonLocation);
        final int colorBackground = ContextCompat.getColor(getApplicationContext(), R.color.background);
        final int colorBackgroundClicked = ContextCompat.getColor(getApplicationContext(), R.color.backgroundClicked);
        final int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        final int colorFaded = ContextCompat.getColor(getApplicationContext(), R.color.faded);
        final int colorPrimaryClicked = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryClicked);
        loadingCircle.setVisibility(View.GONE);
        final EditText search = (EditText) findViewById(R.id.search_bar);
        search.setVisibility(View.GONE);
        final Button findButton = (Button) findViewById(R.id.find_button);
        findButton.setVisibility(View.GONE);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.QueueBox);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        final RecyclerListAdapter adapter = new RecyclerListAdapter(this);
        ItemTouchHelper.Callback callback = new CustomItemTouchHelper(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        recyclerView.setAdapter(adapter);
        touchHelper.attachToRecyclerView(recyclerView);




        playPause.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimary, colorPrimaryClicked);
                colorAnimation.setDuration(250);
                final ValueAnimator colorAnimationRev = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimaryClicked, colorPrimary);
                colorAnimationRev.setDuration(250);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        playPause.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimationRev.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        playPause.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimation.start();
                colorAnimationRev.start();
                if(mPlayer.getPlaybackState().isPlaying){
                    playPause.setText("Play");
                    mPlayer.pause(null);
                }else{
                    playPause.setText("Pause");
                    if(adapter.isCurrValid())
                        mPlayer.resume(null);
                    else
                        mPlayer.playUri(null, adapter.playFromBeginning(), 0, 0);
                        alreadyChanged = true;
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimary, colorPrimaryClicked);
                colorAnimation.setDuration(250);
                final ValueAnimator colorAnimationRev = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimaryClicked, colorPrimary);
                colorAnimationRev.setDuration(250);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        nextButton.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimationRev.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        nextButton.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimation.start();
                colorAnimationRev.start();
                String temp = adapter.next();
                alreadyChanged = true;
                if (!temp.equals("")){
                    mPlayer.playUri(null, temp, 0, 0);
                    setText(playPause, "Pause");
                }
                else{
                    if(mPlayer.getPlaybackState().isPlaying) {
                        mPlayer.pause(null);
                        setText(playPause, "Play");
                        mPlayer.skipToNext(null);
                    }
                }

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
                int localTrackCount = 0;
                for(final Item i: t.getTracks().getItems()){
                    localTrackCount++;
                    final Button btn = new Button(new ContextThemeWrapper(getApplicationContext(), R.style.Track) ,null, R.style.Track);
                    btn.setId(j);
                    btn.setText(generateButtonText(i), TextView.BufferType.SPANNABLE);
                    final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorBackground, colorBackgroundClicked);
                    colorAnimation.setDuration(250);
                    final ValueAnimator colorAnimationRev = ValueAnimator.ofObject(new ArgbEvaluator(), colorBackgroundClicked, colorBackground);
                    colorAnimationRev.setDuration(250);
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
                    searchResultView.post(new Runnable() {
                        public void run() {
                            searchResultView.addView(btn, params);
                        }
                    });
                    btn.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View view){
                        colorAnimation.start();
                        colorAnimationRev.start();
                        count++;
                        adapter.addItem(i, generateButtonText(i).toString());
                        if(mPlayer.getMetadata().currentTrack == null && !mPlayer.getPlaybackState().isPlaying){
                            mPlayer.playUri(null, adapter.next(), 0, 0);
                            setText(playPause, "Pause");
                            alreadyChanged = true;
                        }
                        ServerWriter s = new ServerWriter();
                            try {
                                s.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mapper.writeValueAsString(i));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        //recyclerView.addView(btn, params);

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

        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimary, colorPrimaryClicked);
                colorAnimation.setDuration(250);
                final ValueAnimator colorAnimationRev = ValueAnimator.ofObject(new ArgbEvaluator(), colorPrimaryClicked, colorPrimary);
                colorAnimationRev.setDuration(250);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        backButton.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimationRev.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        backButton.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimation.start();
                colorAnimationRev.start();
                String temp = adapter.prev();
                alreadyChanged = true;
                if (!temp.equals("")){
                    mPlayer.playUri(null, temp, 0, 0);
                    setText(playPause, "Pause");
                }
                else{
                    if(mPlayer.getPlaybackState().isPlaying) {
                        mPlayer.pause(null);
                        setText(playPause, "Play");
                        mPlayer.skipToNext(null);
                    }
                }
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
                search.setOnSearchListener(searchListener);
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
                    recyclerView.setVisibility(View.VISIBLE);
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
                    recyclerView.setVisibility(View.GONE);
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
                    final Item i = mapper.readValue(result, Item.class);
                    count++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addItem(i, generateButtonText(i).toString());
                        }
                    });

                    if(mPlayer.getMetadata().currentTrack == null && !mPlayer.getPlaybackState().isPlaying){
                        mPlayer.playUri(null, adapter.next(), 0, 0);
                        setText(playPause, "Pause");
                        alreadyChanged = true;
                    }
                    ServerWriter s = new ServerWriter();
                    try {
                        s.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mapper.writeValueAsString(i));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //recyclerView.addView(btn, params);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        mPlayer.addNotificationCallback(new Player.NotificationCallback() {
            @Override
            public void onPlaybackEvent(PlayerEvent playerEvent) {
                System.out.println(playerEvent);
                if (playerEvent == PlayerEvent.kSpPlaybackNotifyTrackChanged){
                    if(!alreadyChanged) {
                        String temp = adapter.next();
                        if (!temp.equals("")) {
                            mPlayer.playUri(null, temp, 0, 0);
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
        });

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
        count = 0;
        adding = false;
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
            ServerWriter s = new ServerWriter();
            s.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Quit");
            mPlayer.pause(null);
            try {
                routerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void playSong(String uri){
        mPlayer.playUri(null, uri, 0,0);
        alreadyChanged = true;
    }
}