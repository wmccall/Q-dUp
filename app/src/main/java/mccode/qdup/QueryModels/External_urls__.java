
package mccode.qdup.QueryModels;

import java.util.HashMap;
import java.util.Map;

public class External_urls__ {

    private String spotify;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public External_urls__() {
    }

    /**
     * 
     * @param spotify
     */
    public External_urls__(String spotify) {
        super();
        this.spotify = spotify;
    }

    public String getSpotify() {
        return spotify;
    }

    public void setSpotify(String spotify) {
        this.spotify = spotify;
    }

    public External_urls__ withSpotify(String spotify) {
        this.spotify = spotify;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public External_urls__ withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
