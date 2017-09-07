package mccode.spotidj.Utils.Server;

import android.os.AsyncTask;
import android.widget.EditText;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import mccode.spotidj.ConnectListener;

import static mccode.spotidj.MainActivity.getSPort;
import static mccode.spotidj.MainActivity.getHost;
import static mccode.spotidj.MainActivity.routerSocket;

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
        String connectKey = "";
        while(!connected)
        {
            try
            {
                System.out.println(getHost());
                System.out.println(getSPort());
                routerSocket.connect(new InetSocketAddress(getHost(), getSPort()));
                connected = true;
                /**TODO:
                 * remove print statement and show on phone
                 */
                System.out.println("connected to router");
            }
            catch (IOException e)
            {
                if(!attempted)
                {
                    System.out.println("Waiting for router");
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
                    out.write(("server").getBytes());
                    Scanner in = new Scanner(routerSocket.getInputStream());
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
                        connectKey = routerResponse;
                        System.out.println(connectKey);
                    }
                    //in.close();
                }
                catch (IOException e)
                {
                    if(!attempted)
                    {
                        System.out.println("Waiting for router");
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
            }
        }
        response.add("" + connectKey);
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
