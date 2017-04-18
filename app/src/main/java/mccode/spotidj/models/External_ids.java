
package mccode.spotidj.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class External_ids implements Serializable
{

    private String isrc;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 6449473715479390211L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public External_ids() {
    }

    /**
     * 
     * @param isrc
     */
    public External_ids(String isrc) {
        super();
        this.isrc = isrc;
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public External_ids withIsrc(String isrc) {
        this.isrc = isrc;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public External_ids withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
