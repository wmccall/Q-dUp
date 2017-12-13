package mccode.qdup.Utils.Client;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import static mccode.qdup.MainActivity.routerSocket;

/**
 * Created by Will on 6/14/2017.
 *
 * Writes from the client to the router when sending a song over to the server
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
            //System.out.println(strings[0]);
            out.write((strings[0].replace("\n", "").replace("\r", "") + "\n").getBytes());
        } catch (IOException e) {
            // TODO catch write error
            System.out.println("Closing client writer");
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
