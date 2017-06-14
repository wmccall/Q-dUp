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

/**
 * Created by Will on 6/13/2017.
 */

public class ServerConnector extends AsyncTask<String, Integer, ArrayList<String>> {
    private ArrayList<String> response = new ArrayList<String>();
    ConnectListener listener;

    public void setOnConnectListener(ConnectListener listener){
        this.listener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings)
    {
        Socket socket = new Socket();
        boolean connected = false;
        boolean attempted = false;
        String connectKey = "";
        while(!connected)
        {
            try
            {
                socket.connect(new InetSocketAddress(getHost(), getSPort()));
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
                try
                {
                    this.wait(1000);
                }
                catch(InterruptedException f)
                {

                }
            }
            System.out.println("1");
            if(connected)
            {
                System.out.println("2");
                try
                {
                    System.out.println("3");
                    PrintStream out = new PrintStream(socket.getOutputStream());
                    System.out.println("4");
                    out.write(("server").getBytes());
                    System.out.println("5");
                    Scanner in = new Scanner(socket.getInputStream());
                    System.out.println("6");
                    String routerResponse = in.nextLine();
                    System.out.println("7");
                    if (routerResponse.equals("NA"))
                    {
                        System.out.println("8");
                        response.add(routerResponse);
                        in.close();
                        socket.close();
                        return response;
                    }
                    else
                    {
                        System.out.println("9");
                        connectKey = routerResponse;
                        System.out.println(connectKey);
                    }
                    in.close();
                    socket.close();
                }
                catch (IOException e)
                {
                    if(!attempted)
                    {
                        System.out.println("Waiting for router");
                        attempted = true;
                        connected = false;
                    }
                    try
                    {
                        this.wait(1000);
                    }
                    catch(InterruptedException f)
                    {

                    }
                }
            }
        }
        System.out.println("!!");
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
