import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

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
    public void join(ViewProxy proxy, String name) throws IOException {
        out.printf("join %s%n", name);
    }


    /**
     * stops the game if the window of a player is closed
     *
     * @exception  IOException
     *     Thrown if an I/O error occurred.
     */
    @Override
    public void stop() throws IOException {
        out.printf("stop%n");
    }

    /**
     * guess a letter
     * @param name the name of the player guessing
     * @param letter the guessed letter
     * @exception  IOException
     *     Thrown if an I/O error occurred.
     */
    @Override
    public void guess(String name, char letter) throws IOException {
        out.printf("guess %s %c%n", name, letter);
    }

    /**
     * starts a new game between two players
     * @exception  IOException
     *     Thrown if an I/O error occurred.
     */
    @Override
    public void newGame() throws IOException {
        out.printf("new%n");
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
                    switch (s)
                    {
                        case "guess":
                            String correct = scanner.next();
                            String incorrect = scanner.next();
                            String name = scanner.next();
                            modelListener.guessed(correct, incorrect, name);
                            break;
                        case "new":
                            correct = scanner.next();
                            String currPlayer = scanner.next();
                            modelListener.newGame(correct, currPlayer);
                            break;
                        case "close":
                            modelListener.close();
                            break;
                        case "win":
                            correct = scanner.next();
                            String player = scanner.next();
                            modelListener.winner(correct, player);
                            break;
                        case "wait":
                            modelListener.waiting();
                            break;
                        default:
                            System.err.println ("Bad message");
                            break;
                    }
                }
            }
            catch (IOException exc)
            {
            }
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
