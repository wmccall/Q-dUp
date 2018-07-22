package mccode.qdup.Utils.RouterInteraction;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import mccode.qdup.Utils.Listeners.MessageListener;

import static mccode.qdup.Activities.PortalActivity.routerSocket;

/**
 * Created by Will on 6/14/2017.
 */

public class RouterListener extends AsyncTask<String, Integer, ArrayList<String>> {
    MessageListener listener;

    private String response = "";

    private String appType = "McCode-RouterListener";

    public RouterListener(){

    }

    public void setOnServerListnerListener(MessageListener listener){
        this.listener = listener;
    }
    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        Scanner in;
        try {
            in = new Scanner(routerSocket.getInputStream());
            while(true) {
                try {
                    response = in.nextLine();
                    Log.d(appType, response);
                    listener.onMessageSucceeded(response);
                    response = "";
                } catch (NoSuchElementException e) {
                    Log.e(appType, "no line found, but its okay");
                    in.close();
                    response = "err";
                    listener.onMessageSucceeded(response);
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(appType, "Closing server listener: io exception");
        } catch (RuntimeException e){
            e.printStackTrace();
            Log.e(appType, "Closing server listener: runtime");
        }


        return null;
    }

    protected void onPostExecute(ArrayList<String> result)
    {
        //called after doInBackground() has finished
    }
}
