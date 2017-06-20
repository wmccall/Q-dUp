package mccode.spotidj;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.IOException;
import java.util.ArrayList;

import mccode.spotidj.Utils.MessageListener;
import mccode.spotidj.Utils.Server.ServerListener;
import mccode.spotidj.models.Item;

import static mccode.spotidj.MainActivity.key;
import static mccode.spotidj.MainActivity.mPlayer;
import static mccode.spotidj.MainActivity.mapper;
import static mccode.spotidj.MainActivity.routerSocket;

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
    public static int position = 0;
    public static int count = 0;
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

        nextButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mPlayer.skipToPrevious(null);
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
                    Button btn = new Button(getApplicationContext());
                    btn.setId(count);
                    String artists = "";
                    int size = i.getArtists().size();
                    artists = i.getArtists().get(0).getName();
                    if(size > 1){
                        for(int k = 1; k < size; k++){
                            artists += ", " + i.getArtists().get(k).getName();
                        }
                    }
                    btn.setText(count + ". " + artists + ": " +i.getName());
                    btn.setBackgroundColor(Color.rgb(30, 215, 96));
                    btn.setTextColor(Color.rgb(35, 35, 35));
                    //queueBox.addView(btn, params);
                    addButton(queueBox,btn,params);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        ServerListener listener = new ServerListener();
        listener.setOnServerListnerListener(ml);

        listener.execute();
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
}