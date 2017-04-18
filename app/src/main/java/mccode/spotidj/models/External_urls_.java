
package mccode.spotidj.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class External_urls_ implements Serializable
{

    private String spotify;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 8135491972488674019L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public External_urls_() {
    }

    /**
     * 
     * @param spotify
     */
    public External_urls_(String spotify) {
        super();
        this.spotify = spotify;
    }

    public String getSpotify() {
        return spotify;
    }

    public void setSpotify(String spotify) {
        this.spotify = spotify;
    }

    public External_urls_ withSpotify(String spotify) {
        this.spotify = spotify;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public External_urls_ withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
