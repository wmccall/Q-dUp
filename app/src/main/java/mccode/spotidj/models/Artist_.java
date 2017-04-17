
package mccode.spotidj.models;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Artist_ implements Serializable, Parcelable
{

    private ExternalUrls__ externalUrls;
    private String href;
    private String id;
    private String name;
    private String type;
    private String uri;
    public final static Parcelable.Creator<Artist_> CREATOR = new Creator<Artist_>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Artist_ createFromParcel(Parcel in) {
            Artist_ instance = new Artist_();
            instance.externalUrls = ((ExternalUrls__) in.readValue((ExternalUrls__.class.getClassLoader())));
            instance.href = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.type = ((String) in.readValue((String.class.getClassLoader())));
            instance.uri = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Artist_[] newArray(int size) {
            return (new Artist_[size]);
        }

    }
    ;
    private final static long serialVersionUID = -4770614030686121269L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Artist_() {
    }

    /**
     * 
     * @param id
     * @param externalUrls
     * @param name
     * @param type
     * @param uri
     * @param href
     */
    public Artist_(ExternalUrls__ externalUrls, String href, String id, String name, String type, String uri) {
        super();
        this.externalUrls = externalUrls;
        this.href = href;
        this.id = id;
        this.name = name;
        this.type = type;
        this.uri = uri;
    }

    public ExternalUrls__ getExternalUrls() {
        return externalUrls;
    }

    public void setExternalUrls(ExternalUrls__ externalUrls) {
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
        dest.writeValue(externalUrls);
        dest.writeValue(href);
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(type);
        dest.writeValue(uri);
    }

    public int describeContents() {
        return  0;
    }

}
