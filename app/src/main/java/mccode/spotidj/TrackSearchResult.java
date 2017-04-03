package mccode.spotidj;

import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by mammo on 3/24/2017.
 */

public class TrackSearchResult {
//    {
//        "tracks" : {
//            "href" : "https://api.spotify.com/v1/search?query=recover&type=track&offset=0&limit=2",
//            "items" : [ {
//            "album" : {},
//            "artists" : [ {} ],
//            "available_markets" : [ "CA", "MX", "US" ],
//            "disc_number" : 1,
//            "duration_ms" : 224240,
//            "explicit" : false,
//            "external_ids" : {},
//            "external_urls" : {},
//            "href" : "https://api.spotify.com/v1/tracks/4QQg6DXsx6G3lv3W4A15CZ",
//            "id" : "4QQg6DXsx6G3lv3W4A15CZ",
//            "name" : "Recover",
//            "popularity" : 52,
//            "preview_url" : "https://p.scdn.co/mp3-preview/38b97cbf72c46b2b5cb6c2b8935729ae017f6885?cid=null",
//            "track_number" : 7,
//            "type" : "track",
//            "uri" : "spotify:track:4QQg6DXsx6G3lv3W4A15CZ"
//            } ],
//            "limit" : 1,
//            "next" : "https://api.spotify.com/v1/search?query=recover&type=track&offset=2&limit=2",
//            "offset" : 0,
//            "previous" : null,
//            "total" : 1349
//        }
//    }

    private String trackID;
    private String trackName;
    private String artistName;
    TrackCreaterListener listener;

    public void setOnCreateListener(TrackCreaterListener listener){
        this.listener = listener;
    }

    public TrackSearchResult(ArrayList<String> in, TrackCreaterListener listener, View v){
        this.setOnCreateListener(listener);
        int dep = 0;
        for(String line : in){
            if(line.contains("{")){
                dep++;
            }
            if(line.contains("}")){
                dep--;
            }
            if(dep == 3 && line.contains("\"id\"")){
                this.trackID = line.replaceAll("\"id\" : \"", "");
                this.trackID = this.getTrackID().replaceAll("\",","").trim();
                System.out.println("id: " + this.getTrackID());
            }else if(dep == 3 && line.contains("\"name\"")){
                this.trackName = line.replaceAll("\"name\" : \"", "");
                this.trackName = this.getTrackName().replaceAll("\",","");
                System.out.println("name: " + this.getTrackName());
            }if(dep == 5 && line.contains("\"name\"")){
                this.artistName = line.replaceAll("\"name\" : \"", "");
                this.artistName = this.getArtistName().replaceAll("\",","");
                System.out.println("artistname: " + this.getArtistName());
            }
        }
        //loc.setText("artist: "+ this.getArtistName() +"\ntrackID: " + this.getTrackID() +"\ntrackName: "+ this.getTrackName());
        listener.onCreateSucceeded(v, this);
    }

    public String getTrackID(){return trackID;}
    public String getTrackName(){return trackName;}
    public String getArtistName(){return artistName;}
}
