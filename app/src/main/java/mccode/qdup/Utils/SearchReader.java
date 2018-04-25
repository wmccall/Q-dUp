package mccode.qdup.Utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import mccode.qdup.Activities.AuthActivity;
import mccode.qdup.Utils.Listeners.SearchListener;

/**
 * SearchReader queries spotify with a search term and gives a response to the response wrapper
 */

public class SearchReader extends AsyncTask<String, Integer, ArrayList<String>> {

    private ArrayList<String> response = new ArrayList<>();
    private SearchListener listener;

    public void setOnSearchListener(SearchListener listener){
        this.listener = listener;
    }


    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL(strings[0]);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try{
            assert url != null;
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + AuthActivity.responseToken);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.add(inputLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    protected void onProgressUpdate() {
        //called when the background task makes any progress
    }

    protected void onPreExecute() {
        //called before doInBackground() is started
    }
    protected void onPostExecute(ArrayList<String> result) {
        //called after doInBackground() has finished
        listener.onSearchSucceeded(result);
    }


}
