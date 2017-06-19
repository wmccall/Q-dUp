package mccode.spotidj.Utils.Client;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import mccode.spotidj.ConnectListener;

import static mccode.spotidj.MainActivity.getCPort;
import static mccode.spotidj.MainActivity.getHost;
import static mccode.spotidj.MainActivity.routerSocket;

/**
 * Created by Will on 6/14/2017.
 */

public class ClientWriter extends AsyncTask<String, Integer, ArrayList<String>>
{
    private ArrayList<String> response = new ArrayList<String>();
    //ConnectListener listener;

    public ClientWriter(){
    }

    //public void setOnClientWriterListener(ConnectListener listener){
    //    this.listener = listener;
    //}

    @Override
    protected ArrayList<String> doInBackground(String... strings)
    {
        PrintStream out;
        try {
            out = new PrintStream(routerSocket.getOutputStream());
            System.out.println(strings[0]);
            out.write(strings[0].getBytes());
        } catch (IOException e) {
            System.out.println("whoops");
        }

        return response;
    }
    protected void onProgressUpdate()
    {
        //called when the background task makes any progress
    }

    protected void onPreExecute()
    {
        //called before doInBackground() is started
    }
    protected void onPostExecute(ArrayList<String> result)
    {
        //called after doInBackground() has finished
        //listener.onConnectSucceeded(result);
    }
}
