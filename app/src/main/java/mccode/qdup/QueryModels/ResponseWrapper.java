package mccode.qdup.QueryModels;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;

import mccode.qdup.Utils.Listeners.TrackCreatorListener;

import static mccode.qdup.Activities.MainActivity.jsonConverter;

/**
 * ResponseWrapper takes a response from the searchReader and gives the track creator listener a
 * list of tracks to display on the requester side
 */

public class ResponseWrapper extends AsyncTask<ArrayList<String>, Integer, Boolean> {


    private TrackCreatorListener listener;
    private TrackResponse response;
    private View v;

    public void setOnCreateListener(TrackCreatorListener listener){
        this.listener = listener;
    }

    public void setView(View view){
        this.v = view;
    }

    @SafeVarargs
    @Override
    protected final Boolean doInBackground(ArrayList<String>... strings) {
        String tot = "";
        for(String s : strings[0]){
            tot += s;
        }
        Log.d("Response:",tot);
        try{
            response = jsonConverter.readValue(tot, TrackResponse.class);
        } catch (IOException e) { e.printStackTrace(); }
        listener.onCreateSucceeded(v, this.response);

        return true;
    }

    protected void onProgressUpdate() {
        //called when the background task makes any progress
    }

    protected void onPreExecute() {
        //called before doInBackground() is started
    }
    protected void onPostExecute(Boolean result) {
        //called after doInBackground() has finished

    }

}
