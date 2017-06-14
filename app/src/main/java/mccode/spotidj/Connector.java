package mccode.spotidj;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import mccode.spotidj.Utils.ModelProxy;

import static mccode.spotidj.MainActivity.getHost;
import static mccode.spotidj.MainActivity.getCPort;
import static mccode.spotidj.MainActivity.mp;
/**
 * Created by mammo on 3/24/2017.
 */

public class Connector extends AsyncTask<String, Integer, ArrayList<String>> {

    private ArrayList<String> response = new ArrayList<String>();
    ConnectListener listener;

    public void setOnConnectListener(ConnectListener listener){
        this.listener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(getHost(), getCPort()));
            socket.getOutputStream();
            mp = new ModelProxy(socket);
            mp.join("ABF$S");
            //TODO: connor needs to do server work here
            response.add("Y");
            return response;
        } catch (IOException e) {
            response.add("N");
            return response;
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
