package mccode.qdup.Utils.Server;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import mccode.qdup.MainActivity;
import mccode.qdup.ServerActivity;
import mccode.qdup.Utils.Listeners.ConnectListener;
import mccode.qdup.Utils.Listeners.MessageListener;

import static mccode.qdup.MainActivity.routerSocket;

/**
 * Created by Will on 6/14/2017.
 */

public class ServerListener extends AsyncTask<String, Integer, ArrayList<String>> {
    MessageListener listener;

    private String response = "";

    public  ServerListener(){

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
                    Log.i("Server Listener", response);
                    listener.onMessageSucceeded(response);
                    response = "";
                } catch (NoSuchElementException e) {
                    Log.e("Server Listener", "no line found, but its okay");
                    in.close();
                    response = "err";
                    listener.onMessageSucceeded(response);
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Server Listener", "Closing server listener: io exception");
        } catch (RuntimeException e){
            e.printStackTrace();
            Log.e("Server Listener", "Closing server listener: runtime");
        }


        return null;
    }

    protected void onPostExecute(ArrayList<String> result)
    {
        //called after doInBackground() has finished
    }
}
