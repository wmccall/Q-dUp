import data.tracks.Item;

import java.io.IOException;

/**
 * The controller interface that tells the model when the
 * view has been interacted with
 * @author Connor McAuliffe
 * @version 4/2/2017
 */
public interface ViewListener{


    /**
     * call to add a song to the queue
     * @param song the song as an item
     */
    public void addSong(Item song) throws IOException;


    /**
     * remove the given song from the queue
     * @param song the song as an item
     */
    public void removeSong(Item song) throws IOException;

    /**
     * move a song to a new position in the list
     * @param song the song as an item
     * @param newPosition the new index into the queue
     */
    public void moveSong(Item song, int newPosition) throws IOException;

}