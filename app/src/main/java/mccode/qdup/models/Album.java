
package mccode.qdup.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Album implements Serializable
{

    private String album_type;
    private List<Artist> artists = null;
    private List<String> available_markets = null;
    private External_urls_ external_urls;
    private String href;
    private String id;
    private List<Image> images = null;
    private String name;
    private String type;
    private String uri;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -6653257237481970710L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Album() {
    }

    /**
     * 
     * @param id
     * @param artists
     * @param external_urls
     * @param available_markets
     * @param name
     * @param images
     * @param type
     * @param album_type
     * @param uri
     * @param href
     */
    public Album(String album_type, List<Artist> artists, List<String> available_markets, External_urls_ external_urls, String href, String id, List<Image> images, String name, String type, String uri) {
        super();
        this.album_type = album_type;
        this.artists = artists;
        this.available_markets = available_markets;
        this.external_urls = external_urls;
        this.href = href;
        this.id = id;
        this.images = images;
        this.name = name;
        this.type = type;
        this.uri = uri;
    }

    public String getAlbum_type() {
        return album_type;
    }

    public void setAlbum_type(String album_type) {
        this.album_type = album_type;
    }

    public Album withAlbum_type(String album_type) {
        this.album_type = album_type;
        return this;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public Album withArtists(List<Artist> artists) {
        this.artists = artists;
        return this;
    }

    public List<String> getAvailable_markets() {
        return available_markets;
    }

    public void setAvailable_markets(List<String> available_markets) {
        this.available_markets = available_markets;
    }

    public Album withAvailable_markets(List<String> available_markets) {
        this.available_markets = available_markets;
        return this;
    }

    public External_urls_ getExternal_urls() {
        return external_urls;
    }

    public void setExternal_urls(External_urls_ external_urls) {
        this.external_urls = external_urls;
    }

    public Album withExternal_urls(External_urls_ external_urls) {
        this.external_urls = external_urls;
        return this;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Album withHref(String href) {
        this.href = href;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Album withId(String id) {
        this.id = id;
        return this;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Album withImages(List<Image> images) {
        this.images = images;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Album withName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Album withType(String type) {
        this.type = type;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Album withUri(String uri) {
        this.uri = uri;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Album withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
