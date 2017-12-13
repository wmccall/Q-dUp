
package mccode.qdup.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class External_urls implements Serializable
{

    private String spotify;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -6992500396595992958L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public External_urls() {
    }

    /**
     * 
     * @param spotify
     */
    public External_urls(String spotify) {
        super();
        this.spotify = spotify;
    }

    public String getSpotify() {
        return spotify;
    }

    public void setSpotify(String spotify) {
        this.spotify = spotify;
    }

    public External_urls withSpotify(String spotify) {
        this.spotify = spotify;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public External_urls withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
