package mccode.spotidj;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import static mccode.spotidj.MainActivity.HOST;
import static mccode.spotidj.MainActivity.PORT;
import static mccode.spotidj.MainActivity.mp;
/**
 * Created by mammo on 3/24/2017.
 */

public class Connecter extends AsyncTask<String, Integer, ArrayList<String>> {

    private ArrayList<String> response = new ArrayList<String>();
    ConnectListener listener;

    public void setOnConnectListener(ConnectListener listener){
        this.listener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(HOST, PORT));
            mp = new ModelProxy(socket);
            System.out.println("TEST");
            //TODO: connor needs to do server work here
            ArrayList<String> list = new ArrayList<String>();
            list.add("Y");
            return list;
        } catch (IOException e) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("N");
            return list;
        }
    }
    protected void onProgressUpdate() {
        //called when the background task makes any progress
    }

    protected void onPreExecute() {
        //called before doInBackground() is started
    }
    protected void onPostExecute(ArrayList<String> result) {
        //called after doInBackground() has finished
        listener.onConnectSucceeded(result);
    }


}
