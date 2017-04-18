
package mccode.spotidj.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TrackResponse implements Serializable
{

    private Tracks tracks;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 4497914800914680396L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TrackResponse() {
    }

    /**
     * 
     * @param tracks
     */
    public TrackResponse(Tracks tracks) {
        super();
        this.tracks = tracks;
    }

    public Tracks getTracks() {
        return tracks;
    }

    public void setTracks(Tracks tracks) {
        this.tracks = tracks;
    }

    public TrackResponse withTracks(Tracks tracks) {
        this.tracks = tracks;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public TrackResponse withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
