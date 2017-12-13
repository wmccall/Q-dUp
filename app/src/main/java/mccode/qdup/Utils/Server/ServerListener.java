package mccode.qdup.Utils.Server;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import mccode.qdup.Utils.Listeners.MessageListener;

import static mccode.qdup.MainActivity.routerSocket;
import static mccode.qdup.MainActivity.stopped;

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
            while(!stopped){
                response = in.nextLine();
                //System.out.println(response);
                listener.onMessageSucceeded(response);
                response = "";
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Closing server listener");
        } catch (RuntimeException e){
            System.out.println("Closing server listener");
        }


        return null;
    }

    protected void onPostExecute(ArrayList<String> result)
    {
        //called after doInBackground() has finished
    }
}
