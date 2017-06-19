package mccode.spotidj.Utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Will on 6/18/2017.
 */

public class SpotifyConnection extends URLConnection {

    public SpotifyConnection(String header, URL url){
        super(url);
        super.setRequestProperty("Authorization", "Bearer " + header);
    }

    @Override
    public void connect() throws IOException {

    }
}
