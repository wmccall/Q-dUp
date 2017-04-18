package mccode.spotidj.models;

import android.view.View;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

import mccode.spotidj.TrackCreaterListener;

import static mccode.spotidj.MainActivity.mapper;

/**
 * Created by mammo on 4/17/2017.
 */

public class ResponseWrapper {

    TrackCreaterListener listener;
    TrackResponse response;

    public ResponseWrapper(ArrayList<String> in, TrackCreaterListener listener, View v ){
        this.setOnCreateListener(listener);
        String tot = "";
        for(String s: in){
            tot += s;
        }
        System.out.println(tot);
//        ObjectMapper mapper = new ObjectMapper();
        try{
            response = mapper.readValue(tot, TrackResponse.class);
        }catch (JsonParseException e) { e.printStackTrace();}
        catch (JsonMappingException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
        listener.onCreateSucceeded(v, this.response);
    }

    public void setOnCreateListener(TrackCreaterListener listener){
        this.listener = listener;
    }
}
