package mccode.qdup.Utils.Client;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import mccode.qdup.Utils.Listeners.ConnectListener;

import static mccode.qdup.Activities.PortalActivity.getClientPort;
import static mccode.qdup.Activities.PortalActivity.getRouterUrl;
//import static mccode.qdup.PortalActivity.mp;
import static mccode.qdup.Activities.PortalActivity.routerSocket;

/**
 * Created by Will on 6/12/2017.
 */

public class ClientConnector extends AsyncTask<String, Integer, ArrayList<String>> {

    private ArrayList<String> response = new ArrayList<String>();
    String key;
    ConnectListener listener;

    public ClientConnector(String s)
    {
        this.key = s;
    }

    public void setOnConnectListener(ConnectListener listener){
        this.listener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings)
    {
        //String connectKey = strings[0];
        //Socket socket = new Socket();
        boolean connected = false;
        boolean attempted = false;
        int port = 0;
        boolean exists = false;
        String routerResponse = "";
        routerSocket = new Socket();
        while(!connected)
        {
            try
            {
                routerSocket.connect(new InetSocketAddress(getRouterUrl(), getClientPort()));
                connected = true;
                /**TODO:
                 * remove print statement and show on phone
                 */
                Log.i("Client Connector", "Connected to router");
            }
            catch (IOException e)
            {
                if(!attempted)
                {
                    Log.e("Client Connector", "Waiting for router");
                    attempted = true;
                }
//                try
//                {
//                    this.wait(1000);
//                }
//                catch(InterruptedException f)
//                {
//
//                }
            }

            if(connected)
            {
                try
                {
                    PrintStream out = new PrintStream(routerSocket.getOutputStream());
                    out.write((this.key).toUpperCase().getBytes());
                    Scanner in = new Scanner(routerSocket.getInputStream());
                    routerResponse = in.nextLine();
                    if (routerResponse.equals("NA"))
                    {
                        response.add(routerResponse);
                        in.close();
                        out.close();
                        routerSocket.close();
                        routerSocket = new Socket();
                        return response;
                    }
                    else
                    {
                        response.add(routerResponse);
                    }
//                    in.close();
//                    out.close();
                }
                catch (IOException e)
                {
                    //Log.e("Client Connector", e.toString());
                    if(!attempted)
                    {
                        Log.e("Client Connector", "Waiting for router");
                        attempted = true;
                        connected = false;
                    }
//                    try
//                    {
//                        this.wait(1000);
//                    }
//                    catch(InterruptedException f)
//                    {
//
//                    }
                }
                catch (NoSuchElementException e){
                    if(!attempted)
                    {
                        Log.e("Client Connector", "Waiting for router");
                        attempted = true;
                        connected = false;
                    }
                }
            }
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
        listener.onConnectSucceeded(result);
    }
}
