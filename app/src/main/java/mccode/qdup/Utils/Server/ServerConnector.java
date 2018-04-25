package mccode.qdup.Utils.Server;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import mccode.qdup.Utils.Listeners.ConnectListener;

import static mccode.qdup.Activities.PortalActivity.getServerPort;
import static mccode.qdup.Activities.PortalActivity.getRouterUrl;
import static mccode.qdup.Activities.PortalActivity.routerSocket;
import static mccode.qdup.Activities.PortalActivity.serverKey;
import static mccode.qdup.Activities.PortalActivity.requestNewServerKey;

/**
 * Created by Will on 6/13/2017.
 */

public class ServerConnector extends AsyncTask<String, Integer, ArrayList<String>> {
    private ArrayList<String> response = new ArrayList<String>();
    ConnectListener listener;
    private static int localPort = 50000;
    public void setOnConnectListener(ConnectListener listener){
        this.listener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings)
    {
        //Socket socket = new Socket();
        routerSocket = new Socket();
        boolean connected = false;
        boolean attempted = false;
        while(!connected)
        {
            try
            {
                routerSocket = new Socket();
                routerSocket.connect(new InetSocketAddress(getRouterUrl(), getServerPort()));
                connected = true;
                /**TODO:
                 * remove print statement and show on phone
                 */
                Log.i("Server Connector", "Connected to router");
            }
            catch (IOException e)
            {
                Log.e("Server Connector", e.toString());
                if(!attempted)
                {
                    Log.i("Server Connector", "Waiting for router; 1");
                    attempted = true;
                }
                try
                {
                    synchronized (this){
                        this.wait(1000);
                    }
                }
                catch(InterruptedException f)
                {
                    Log.e("Server Connector", "interrupted 1");
                }
            }
            if(connected)
            {
                try
                {
                    PrintStream out = new PrintStream(routerSocket.getOutputStream());
                    Scanner in = new Scanner(routerSocket.getInputStream());
                    if(requestNewServerKey){
                        out.write(("server:").getBytes());
                    } else {
                        Log.i("Server Connector", "reconnecting with serverCode: " + serverKey);
                        requestNewServerKey = true;
                        out.write(serverKey.getBytes());
                    }
                    String routerResponse = in.nextLine();
                    if (routerResponse.equals("NA"))
                    {
                        response.add(routerResponse);
                        in.close();
                        routerSocket.close();
                        routerSocket = new Socket();
                        return response;
                    }
                    else
                    {
                        serverKey = routerResponse;
                        Log.i("Server Connector", serverKey);
                    }
                    //in.close();
                }
                catch (IOException e)
                {
                    Log.e("Server Connector", e.toString());
                    if(!attempted)
                    {
                        Log.i("Server Connector", "Waiting for router; 2");
                        attempted = true;
                        connected = false;
                    }
                    try
                    {
                        synchronized (this){
                            this.wait(1000);
                        }
                    }
                    catch(InterruptedException f)
                    {
                        Log.e("Server Connector", "interrupted 2");
                    }
                }
            }
        }
        Log.i("Server Connector", "connected! Yo");
        Log.i("Server Connector", ("serverCode:" + serverKey));
        response.add("" + serverKey);
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
