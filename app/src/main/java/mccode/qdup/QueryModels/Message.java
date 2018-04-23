package mccode.qdup.QueryModels;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Message(){

    }


    public Message(options o, String message, String key){
        this.option = o;
        this.message = message;
        this.key = key;
    }

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

    public options getOption(){
        return this.option;
    }

    public void setOption(options option){
        this.option = option;
    }

    public String getMessage(){
        return this.message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getKey(){
        return this.key;
    }

    public void setKey(String key){
        this.key = key;
    }


    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Message withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }
}
