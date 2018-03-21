package mccode.qdup.Utils.Listeners;



import java.io.IOException;

import mccode.qdup.QueryModels.Item;

/**
 * The controller interface that tells the view what the model changed
 * @author Connor McAuliffe
 * @version 4/2/2017
 */
public interface ModelListener{

    /**
     * lets the clients know that a song was added at the given index
     * @param song the song as an item
     * @param index the index that it belongs at
     */
    public void added(Item song, int index) throws IOException;


    /**
     * lets the clients know that a song was removed at the given index
     * @param song the song as an item
     * @param index the index of the song removed
     */
    public void removed(Item song, int index) throws IOException;


    /**
     * says that a song moved from the previous position to the new position
     * @param prev the old index of the song
     * @param newPosition the new index of the song
     */
    public void moved(int prev, int newPosition) throws IOException;



}
