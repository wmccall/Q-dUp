package mccode.qdup.Utils.Messaging;

import mccode.qdup.QueryModels.Item;

import java.security.InvalidParameterException;

/**
 * The messages that will get sent between client and server
 * Has codes to determine what the action to be taken is
 * will take up more space than just item transfering but is more
 * flexible and versatile
 * @author: Connor McAuliffe
 */
public class Message {

    private MessageCode code;
    private Item item;
    private int val1;
    private int val2;

    public Message(){

    }

    public Message(MessageCode code, Item item, int val1, int val2){
        this.code = code;
        this.item = item;
        this.val1 = val1;
        this.val2 = val2;
    }

    /**
     * message constructor for either a remove or change_playing
     * @param c either REMOVE or CHANGE_PLAYING
     * @param idx the index that the event should happen at
     */
    public Message(MessageCode c, int idx){
        if( c == MessageCode.REMOVE || c == MessageCode.CHANGE_PLAYING) {
            code = c;
            val1 = idx;
        }
        else{
            throw new InvalidParameterException("This Message format is for a REMOVE or CHANGE PLAYING message only");
        }
    }

    public Message(MessageCode c){
        code = c;
    }

    /**
     * message constructor for a swap
     * @param idx1 original index
     * @param idx2 final index
     */
    public Message(int idx1, int idx2){
        code = MessageCode.SWAP;
        val1 = idx1;
        val2 = idx2;
    }

    /**
     * message constructor for an ADD message
     * @param i the item to be added
     */
    public Message(Item i){
        code = MessageCode.ADD;
        item = i;
    }

    public Item getItem() {
        return item;
    }

    public MessageCode getCode() {
        return code;
    }

    public int getVal1() {
        return val1;
    }

    public int getVal2() {
        return val2;
    }
}
