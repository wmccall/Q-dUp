
package mccode.qdup.QueryModels;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Item implements Parcelable
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
    private Boolean is_local;
    private String name;
    private Integer popularity;
    private String preview_url;
    private Integer track_number;
    private String type;
    private String uri;
    public final static Parcelable.Creator<Item> CREATOR = new Creator<Item>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return (new Item[size]);
        }

    }
    ;

    protected Item(Parcel in) {
        this.album = ((Album) in.readValue((Album.class.getClassLoader())));
        in.readList(this.artists, (mccode.qdup.QueryModels.Artist_.class.getClassLoader()));
        in.readList(this.available_markets, (java.lang.String.class.getClassLoader()));
        this.disc_number = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.duration_ms = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.explicit = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.external_ids = ((External_ids) in.readValue((External_ids.class.getClassLoader())));
        this.external_urls = ((External_urls___) in.readValue((External_urls___.class.getClassLoader())));
        this.href = ((String) in.readValue((String.class.getClassLoader())));
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.is_local = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.popularity = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.preview_url = ((String) in.readValue((String.class.getClassLoader())));
        this.track_number = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.uri = ((String) in.readValue((String.class.getClassLoader())));
    }

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
     * @param is_local
     */
    public Item(Album album, List<Artist_> artists, List<String> available_markets, Integer disc_number, Integer duration_ms, Boolean explicit, External_ids external_ids, External_urls___ external_urls, String href, String id, Boolean is_local, String name, Integer popularity, String preview_url, Integer track_number, String type, String uri) {
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
        this.is_local = is_local;
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

    public List<Artist_> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist_> artists) {
        this.artists = artists;
    }

    public List<String> getAvailable_markets() {
        return available_markets;
    }

    public void setAvailable_markets(List<String> available_markets) {
        this.available_markets = available_markets;
    }

    public Integer getDisc_number() {
        return disc_number;
    }

    public void setDisc_number(Integer disc_number) {
        this.disc_number = disc_number;
    }

    public Integer getDuration_ms() {
        return duration_ms;
    }

    public void setDuration_ms(Integer duration_ms) {
        this.duration_ms = duration_ms;
    }

    public Boolean getExplicit() {
        return explicit;
    }

    public void setExplicit(Boolean explicit) {
        this.explicit = explicit;
    }

    public External_ids getExternal_ids() {
        return external_ids;
    }

    public void setExternal_ids(External_ids external_ids) {
        this.external_ids = external_ids;
    }

    public External_urls___ getExternal_urls() {
        return external_urls;
    }

    public void setExternal_urls(External_urls___ external_urls) {
        this.external_urls = external_urls;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIs_local() {
        return is_local;
    }

    public void setIs_local(Boolean is_local) {
        this.is_local = is_local;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }

    public Integer getTrack_number() {
        return track_number;
    }

    public void setTrack_number(Integer track_number) {
        this.track_number = track_number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(album);
        dest.writeList(artists);
        dest.writeList(available_markets);
        dest.writeValue(disc_number);
        dest.writeValue(duration_ms);
        dest.writeValue(explicit);
        dest.writeValue(external_ids);
        dest.writeValue(external_urls);
        dest.writeValue(href);
        dest.writeValue(id);
        dest.writeValue(is_local);
        dest.writeValue(name);
        dest.writeValue(popularity);
        dest.writeValue(preview_url);
        dest.writeValue(track_number);
        dest.writeValue(type);
        dest.writeValue(uri);
    }

    public int describeContents() {
        return  0;
    }

}
