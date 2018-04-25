package mccode.qdup.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spotify.sdk.android.player.Spotify;

import mccode.qdup.R;

import static mccode.qdup.Utils.GeneralUIUtils.animateButtonClick;

/**
 * Created by Will on 4/18/2018.
 */

public class ErrorActivity extends Activity{

    private int colorPrimary;
    private int colorFaded;
    private Button retry;

    String appType = "McCode-ErrorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(appType, "OnCreate running");
        super.onCreate(savedInstanceState);
        initializeScreenElements();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(appType, "OnActivityResult running: " + resultCode);
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        Log.d(appType, "OnResume running");
        super.onResume();
        initializeScreenElements();
    }

    public void initializeScreenElements(){
        Log.d(appType, "Initializing Screen Elements");
        setContentView(R.layout.error_layout);
        hookUpElementsWithFrontEnd();
        setupOnClickListeners();
    }

    public void hookUpElementsWithFrontEnd(){
        Log.d(appType, "Hooking up elements with front end");
        colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        colorFaded = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryClicked);
        retry = (Button) findViewById(R.id.retry);
    }

    public void setupOnClickListeners(){
        Log.d(appType, "Setting up OnClickListeners for buttons");
        retry.setOnClickListener(createRetryButtonOnClickListener());
    }

    public View.OnClickListener createRetryButtonOnClickListener(){
        Log.d(appType, "Creating retry's OnClickListener");
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(appType, "Re-trying to connect with spotify");
                animateButtonClick(colorPrimary, colorFaded, 250, retry);
                Intent intent = new Intent(ErrorActivity.this, AuthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        };
    }
}
