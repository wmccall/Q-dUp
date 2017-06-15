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
    Socket socket;
    ConnectListener listener;

    public ClientWriter(Socket s)
    {
        this.socket = s;
    }

    public void setOnClientWriterListener(ConnectListener listener){
        this.listener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings)
    {

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
        listener.onConnectSucceeded(result);
    }
}
