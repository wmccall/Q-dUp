package mccode.spotidj;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mammo on 3/24/2017.
 */

public class SearchReader extends AsyncTask<String, Integer, ArrayList<String>> {

    private ArrayList<String> response = new ArrayList<String>();
    SearchListener listener;

    public void setOnSearchListener(SearchListener listener){
        this.listener = listener;
    }
    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        URL url = null;
        System.out.println("11111");
        try {
            //url = new URL("https://api.spotify.com/v1/search?q=" + p + "&type=track");
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {
            System.out.println("22222");
            e.printStackTrace();
        }
        System.out.println(url.toString());
        try{
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                //System.out.println(inputLine);
                response.add(inputLine);
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
        System.out.println("WOWOWOW");
        listener.onSearchSucceeded(result);
    }


}
