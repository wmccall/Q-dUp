package mccode.qdup.Utils.Requester;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import mccode.qdup.Utils.Listeners.ConnectListener;

import static mccode.qdup.Activities.PortalActivity.getClientPort;
import static mccode.qdup.Activities.PortalActivity.getRouterUrl;
import static mccode.qdup.Activities.PortalActivity.privateKey;
import static mccode.qdup.Activities.PortalActivity.serverKey;
import static mccode.qdup.Activities.PortalActivity.routerSocket;

/**
 * Created by Will on 6/12/2017.
 */

public class RequesterConnector extends AsyncTask<String, Integer, ArrayList<String>> {

    private ArrayList<String> response = new ArrayList<String>();
    String key;
    ConnectListener listener;

    public RequesterConnector(String s)
    {
        this.key = s;
    }
    public void setOnConnectListener(ConnectListener listener){
        this.listener = listener;
    }

    private String appType = "McCode-RequesterConnector";

    @Override
    protected ArrayList<String> doInBackground(String... strings)
    {
        boolean connected = false;
        boolean attempted = false;
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
                Log.d(appType, "Connected to router");
            }
            catch (IOException e)
            {
                if(!attempted)
                {
                    Log.e(appType, "Waiting for router");
                    attempted = true;
                }
            }

            if(connected)
            {
                try
                {
                    PrintWriter out = new PrintWriter(routerSocket.getOutputStream(), true);
                    out.println(this.key.toUpperCase());
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
                        serverKey = routerResponse;
                        privateKey = in.nextLine();
                        response.add(privateKey);
                    }
                }
                catch (IOException e)
                {
                    if(!attempted)
                    {
                        Log.e(appType, "Waiting for router");
                        attempted = true;
                        connected = false;
                    }
                }
                catch (NoSuchElementException e){
                    if(!attempted)
                    {
                        Log.e(appType, "Waiting for router");
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
