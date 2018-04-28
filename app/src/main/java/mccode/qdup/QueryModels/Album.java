
package mccode.qdup.QueryModels;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Album implements Parcelable
{

    private String album_type;
    private List<Artist> artists = null;
    private List<String> available_markets = null;
    private External_urls_ external_urls;
    private String href;
    private String id;
    private List<Image> images = null;
    private String name;
    private String release_date;
    private String release_date_precision;
    private String type;
    private String uri;
    public final static Parcelable.Creator<Album> CREATOR = new Creator<Album>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        public Album[] newArray(int size) {
            return (new Album[size]);
        }

    }
    ;

    protected Album(Parcel in) {
        this.album_type = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.artists, (mccode.qdup.QueryModels.Artist.class.getClassLoader()));
        in.readList(this.available_markets, (java.lang.String.class.getClassLoader()));
        this.external_urls = ((External_urls_) in.readValue((External_urls_.class.getClassLoader())));
        this.href = ((String) in.readValue((String.class.getClassLoader())));
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.images, (mccode.qdup.QueryModels.Image.class.getClassLoader()));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.release_date = ((String) in.readValue((String.class.getClassLoader())));
        this.release_date_precision = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.uri = ((String) in.readValue((String.class.getClassLoader())));
    }

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
     * @param release_date
     * @param images
     * @param release_date_precision
     * @param type
     * @param album_type
     * @param uri
     * @param href
     */
    public Album(String album_type, List<Artist> artists, List<String> available_markets, External_urls_ external_urls, String href, String id, List<Image> images, String name, String release_date, String release_date_precision, String type, String uri) {
        super();
        this.album_type = album_type;
        this.artists = artists;
        this.available_markets = available_markets;
        this.external_urls = external_urls;
        this.href = href;
        this.id = id;
        this.images = images;
        this.name = name;
        this.release_date = release_date;
        this.release_date_precision = release_date_precision;
        this.type = type;
        this.uri = uri;
    }

    public String getAlbum_type() {
        return album_type;
    }

    public void setAlbum_type(String album_type) {
        this.album_type = album_type;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<String> getAvailable_markets() {
        return available_markets;
    }

    public void setAvailable_markets(List<String> available_markets) {
        this.available_markets = available_markets;
    }

    public External_urls_ getExternal_urls() {
        return external_urls;
    }

    public void setExternal_urls(External_urls_ external_urls) {
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

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getRelease_date_precision() {
        return release_date_precision;
    }

    public void setRelease_date_precision(String release_date_precision) {
        this.release_date_precision = release_date_precision;
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
        dest.writeValue(album_type);
        dest.writeList(artists);
        dest.writeList(available_markets);
        dest.writeValue(external_urls);
        dest.writeValue(href);
        dest.writeValue(id);
        dest.writeList(images);
        dest.writeValue(name);
        dest.writeValue(release_date);
        dest.writeValue(release_date_precision);
        dest.writeValue(type);
        dest.writeValue(uri);
    }

    public int describeContents() {
        return  0;
    }

}
