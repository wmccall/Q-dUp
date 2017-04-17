
package mccode.spotidj.models;

import java.io.Serializable;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Album implements Serializable, Parcelable
{

    private String albumType;
    private List<Artist> artists = null;
    private List<String> availableMarkets = null;
    private ExternalUrls_ externalUrls;
    private String href;
    private String id;
    private List<Image> images = null;
    private String name;
    private String type;
    private String uri;
    public final static Parcelable.Creator<Album> CREATOR = new Creator<Album>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Album createFromParcel(Parcel in) {
            Album instance = new Album();
            instance.albumType = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.artists, (mccode.spotidj.models.Artist.class.getClassLoader()));
            in.readList(instance.availableMarkets, (java.lang.String.class.getClassLoader()));
            instance.externalUrls = ((ExternalUrls_) in.readValue((ExternalUrls_.class.getClassLoader())));
            instance.href = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.images, (mccode.spotidj.models.Image.class.getClassLoader()));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.type = ((String) in.readValue((String.class.getClassLoader())));
            instance.uri = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Album[] newArray(int size) {
            return (new Album[size]);
        }

    }
    ;
    private final static long serialVersionUID = -2066230051310844361L;

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
     * @param externalUrls
     * @param albumType
     * @param name
     * @param availableMarkets
     * @param images
     * @param type
     * @param uri
     * @param href
     */
    public Album(String albumType, List<Artist> artists, List<String> availableMarkets, ExternalUrls_ externalUrls, String href, String id, List<Image> images, String name, String type, String uri) {
        super();
        this.albumType = albumType;
        this.artists = artists;
        this.availableMarkets = availableMarkets;
        this.externalUrls = externalUrls;
        this.href = href;
        this.id = id;
        this.images = images;
        this.name = name;
        this.type = type;
        this.uri = uri;
    }

    public String getAlbumType() {
        return albumType;
    }

    public void setAlbumType(String albumType) {
        this.albumType = albumType;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<String> getAvailableMarkets() {
        return availableMarkets;
    }

    public void setAvailableMarkets(List<String> availableMarkets) {
        this.availableMarkets = availableMarkets;
    }

    public ExternalUrls_ getExternalUrls() {
        return externalUrls;
    }

    public void setExternalUrls(ExternalUrls_ externalUrls) {
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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        dest.writeValue(albumType);
        dest.writeList(artists);
        dest.writeList(availableMarkets);
        dest.writeValue(externalUrls);
        dest.writeValue(href);
        dest.writeValue(id);
        dest.writeList(images);
        dest.writeValue(name);
        dest.writeValue(type);
        dest.writeValue(uri);
    }

    public int describeContents() {
        return  0;
    }

}
