
package mccode.spotidj.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item implements Serializable
{

    private Album album;
    private List<Artist_> artists = null;
    private List<String> available_markets = null;
    private Integer disc_number;
    private Integer duration_ms;
    private Boolean explicit;
    private External_ids external_ids;
    private External_urls___ external_urls;
    private String href;
    private String id;
    private String name;
    private Integer popularity;
    private String preview_url;
    private Integer track_number;
    private String type;
    private String uri;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 3815739687441664603L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Item() {
    }

    /**
     * 
     * @param external_urls
     * @param available_markets
     * @param preview_url
     * @param external_ids
     * @param album
     * @param duration_ms
     * @param type
     * @param uri
     * @param track_number
     * @param id
     * @param artists
     * @param disc_number
     * @param explicit
     * @param name
     * @param href
     * @param popularity
     */
    public Item(Album album, List<Artist_> artists, List<String> available_markets, Integer disc_number, Integer duration_ms, Boolean explicit, External_ids external_ids, External_urls___ external_urls, String href, String id, String name, Integer popularity, String preview_url, Integer track_number, String type, String uri) {
        super();
        this.album = album;
        this.artists = artists;
        this.available_markets = available_markets;
        this.disc_number = disc_number;
        this.duration_ms = duration_ms;
        this.explicit = explicit;
        this.external_ids = external_ids;
        this.external_urls = external_urls;
        this.href = href;
        this.id = id;
        this.name = name;
        this.popularity = popularity;
        this.preview_url = preview_url;
        this.track_number = track_number;
        this.type = type;
        this.uri = uri;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Item withAlbum(Album album) {
        this.album = album;
        return this;
    }

    public List<Artist_> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist_> artists) {
        this.artists = artists;
    }

    public Item withArtists(List<Artist_> artists) {
        this.artists = artists;
        return this;
    }

    public List<String> getAvailable_markets() {
        return available_markets;
    }

    public void setAvailable_markets(List<String> available_markets) {
        this.available_markets = available_markets;
    }

    public Item withAvailable_markets(List<String> available_markets) {
        this.available_markets = available_markets;
        return this;
    }

    public Integer getDisc_number() {
        return disc_number;
    }

    public void setDisc_number(Integer disc_number) {
        this.disc_number = disc_number;
    }

    public Item withDisc_number(Integer disc_number) {
        this.disc_number = disc_number;
        return this;
    }

    public Integer getDuration_ms() {
        return duration_ms;
    }

    public void setDuration_ms(Integer duration_ms) {
        this.duration_ms = duration_ms;
    }

    public Item withDuration_ms(Integer duration_ms) {
        this.duration_ms = duration_ms;
        return this;
    }

    public Boolean getExplicit() {
        return explicit;
    }

    public void setExplicit(Boolean explicit) {
        this.explicit = explicit;
    }

    public Item withExplicit(Boolean explicit) {
        this.explicit = explicit;
        return this;
    }

    public External_ids getExternal_ids() {
        return external_ids;
    }

    public void setExternal_ids(External_ids external_ids) {
        this.external_ids = external_ids;
    }

    public Item withExternal_ids(External_ids external_ids) {
        this.external_ids = external_ids;
        return this;
    }

    public External_urls___ getExternal_urls() {
        return external_urls;
    }

    public void setExternal_urls(External_urls___ external_urls) {
        this.external_urls = external_urls;
    }

    public Item withExternal_urls(External_urls___ external_urls) {
        this.external_urls = external_urls;
        return this;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Item withHref(String href) {
        this.href = href;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Item withId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Item withName(String name) {
        this.name = name;
        return this;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public Item withPopularity(Integer popularity) {
        this.popularity = popularity;
        return this;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }

    public Item withPreview_url(String preview_url) {
        this.preview_url = preview_url;
        return this;
    }

    public Integer getTrack_number() {
        return track_number;
    }

    public void setTrack_number(Integer track_number) {
        this.track_number = track_number;
    }

    public Item withTrack_number(Integer track_number) {
        this.track_number = track_number;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Item withType(String type) {
        this.type = type;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Item withUri(String uri) {
        this.uri = uri;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Item withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
