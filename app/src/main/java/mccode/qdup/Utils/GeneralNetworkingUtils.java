package mccode.qdup.Utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import mccode.qdup.Activities.PortalActivity;
import mccode.qdup.Utils.Messaging.Message;
import mccode.qdup.Utils.Messaging.MessageCode;
import mccode.qdup.Utils.RouterInteraction.RouterWriter;

import static mccode.qdup.Activities.QueueActivity.appType;

/**
 * Created by Will on 4/9/2018.
 */

public class GeneralNetworkingUtils {

    public static boolean hostAvailable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return true;
        } catch (IOException e) {
            // Either we have a timeout or unreachable host or failed DNS lookup
            System.out.println(e);
            return false;
        }
    }

    public static void sendMessage(Object m){
        Log.d(appType, "Sending a message to the router");
        try {
            new RouterWriter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    PortalActivity.jsonConverter.writeValueAsString(m));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void informRouterOfQuit(){
        Log.d(appType, "Informing the router that the server is quitting");
        sendMessage(new Message(MessageCode.QUIT));
        new RouterWriter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Quit");
    }
}
