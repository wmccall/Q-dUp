
package mccode.spotidj.models;

import java.io.Serializable;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Item implements Serializable, Parcelable
{

    private Album album;
    private List<Artist_> artists = null;
    private List<String> availableMarkets = null;
    private Integer discNumber;
    private Integer durationMs;
    private Boolean explicit;
    private ExternalIds externalIds;
    private ExternalUrls___ externalUrls;
    private String href;
    private String id;
    private String name;
    private Integer popularity;
    private String previewUrl;
    private Integer trackNumber;
    private String type;
    private String uri;
    public final static Parcelable.Creator<Item> CREATOR = new Creator<Item>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Item createFromParcel(Parcel in) {
            Item instance = new Item();
            instance.album = ((Album) in.readValue((Album.class.getClassLoader())));
            in.readList(instance.artists, (mccode.spotidj.models.Artist_.class.getClassLoader()));
            in.readList(instance.availableMarkets, (java.lang.String.class.getClassLoader()));
            instance.discNumber = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.durationMs = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.explicit = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.externalIds = ((ExternalIds) in.readValue((ExternalIds.class.getClassLoader())));
            instance.externalUrls = ((ExternalUrls___) in.readValue((ExternalUrls___.class.getClassLoader())));
            instance.href = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.popularity = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.previewUrl = ((String) in.readValue((String.class.getClassLoader())));
            instance.trackNumber = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.type = ((String) in.readValue((String.class.getClassLoader())));
            instance.uri = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Item[] newArray(int size) {
            return (new Item[size]);
        }

    }
    ;
    private final static long serialVersionUID = -385979408704187575L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Item() {
    }

    /**
     * 
     * @param externalIds
     * @param album
     * @param trackNumber
     * @param type
     * @param uri
     * @param discNumber
     * @param previewUrl
     * @param id
     * @param artists
     * @param durationMs
     * @param explicit
     * @param externalUrls
     * @param name
     * @param availableMarkets
     * @param href
     * @param popularity
     */
    public Item(Album album, List<Artist_> artists, List<String> availableMarkets, Integer discNumber, Integer durationMs, Boolean explicit, ExternalIds externalIds, ExternalUrls___ externalUrls, String href, String id, String name, Integer popularity, String previewUrl, Integer trackNumber, String type, String uri) {
        super();
        this.album = album;
        this.artists = artists;
        this.availableMarkets = availableMarkets;
        this.discNumber = discNumber;
        this.durationMs = durationMs;
        this.explicit = explicit;
        this.externalIds = externalIds;
        this.externalUrls = externalUrls;
        this.href = href;
        this.id = id;
        this.name = name;
        this.popularity = popularity;
        this.previewUrl = previewUrl;
        this.trackNumber = trackNumber;
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

    public List<String> getAvailableMarkets() {
        return availableMarkets;
    }

    public void setAvailableMarkets(List<String> availableMarkets) {
        this.availableMarkets = availableMarkets;
    }

    public Integer getDiscNumber() {
        return discNumber;
    }

    public void setDiscNumber(Integer discNumber) {
        this.discNumber = discNumber;
    }

    public Integer getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Integer durationMs) {
        this.durationMs = durationMs;
    }

    public Boolean getExplicit() {
        return explicit;
    }

    public void setExplicit(Boolean explicit) {
        this.explicit = explicit;
    }

    public ExternalIds getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(ExternalIds externalIds) {
        this.externalIds = externalIds;
    }

    public ExternalUrls___ getExternalUrls() {
        return externalUrls;
    }

    public void setExternalUrls(ExternalUrls___ externalUrls) {
        this.externalUrls = externalUrls;
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

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
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
        dest.writeList(availableMarkets);
        dest.writeValue(discNumber);
        dest.writeValue(durationMs);
        dest.writeValue(explicit);
        dest.writeValue(externalIds);
        dest.writeValue(externalUrls);
        dest.writeValue(href);
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(popularity);
        dest.writeValue(previewUrl);
        dest.writeValue(trackNumber);
        dest.writeValue(type);
        dest.writeValue(uri);
    }

    public int describeContents() {
        return  0;
    }

}
