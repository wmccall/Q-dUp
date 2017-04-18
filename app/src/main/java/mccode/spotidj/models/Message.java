package mccode.spotidj.models;

import java.io.Serializable;

/**
 * Created by mammo on 4/14/2017.
 */
public class Message implements Serializable {
    public enum options{
        open,
        close,
        join,
        request,
    }
    public options option;
    public String key;
    public String message;

    public Message(int op, String message, String key){
        switch(op){
            case 0:
                this.option = options.open;
                break;

            case 1:
                this.option = options.close;
                this.key = key;
                break;

            case 2:
                this.option = options.join;
                this.key = key;
                break;

            case 3:
                this.option = options.request;
                this.message = message;
                break;
        }
    }

}
