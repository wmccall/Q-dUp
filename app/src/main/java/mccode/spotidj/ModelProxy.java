package mccode.spotidj;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import mccode.spotidj.models.Item;

/**
 * Client side model representation that communicates with
 * the server's view proxy
 * @author Connor McAuliffe
 * @version 4/2/2017
 */
public class ModelProxy implements ViewListener{

    private ModelListener modelListener;
    private Socket socket;
    private PrintStream out;
    private Scanner in;

    /**
     * creating a new model proxy and connecting to the socket
     * @param socket the socket for communication
     * @exception  IOException
     *     Thrown if an I/O error occurred.
     */
    public ModelProxy(Socket socket)throws IOException {
        this.socket = socket;
        socket.setTcpNoDelay (true);
        out = new PrintStream(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());
    }

    /**
     * sets the model listener for this model proxy
     * @param modelListener model listener
     */
    public void setModelListener( ModelListener modelListener) {

        this.modelListener = modelListener;
        new ReaderThread() .start();
    }

    /**
     * joins a session
     * @param proxy reference to a viewProxy
     * @param name name of the player
     * @exception  IOException
     *     Thrown if an I/O error occurred.
     */
//    public void join(ViewProxy proxy, String name) throws IOException {
//        out.printf("join %s%n", name);
//    }


    @Override
    public void addSong(Item song) throws IOException {

    }

    @Override
    public void removeSong(Item song) throws IOException {

    }

    @Override
    public void moveSong(Item song, int newPosition) throws IOException {

    }

    /**
     * class used to read the commands from the clients
     */
    private class ReaderThread
            extends Thread
    {
        /**
         * runs the thread that is constantly reading incoming messages
         */
        public void run()
        {
            try
            {
                for (;;)
                {
                    String message = in.nextLine();
                    Scanner scanner = new Scanner (message);
                    String s = scanner.next();
                }
            }
//            catch (IOException exc)
//            {
//            }
            finally
            {
                try
                {
                    socket.close();
                }
                catch (IOException exc)
                {
                }
            }
        }
    }
}
