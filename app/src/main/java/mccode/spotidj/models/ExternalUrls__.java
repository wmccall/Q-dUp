
package mccode.spotidj.models;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ExternalUrls__ implements Serializable, Parcelable
{

    private String spotify;
    public final static Parcelable.Creator<ExternalUrls__> CREATOR = new Creator<ExternalUrls__>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ExternalUrls__ createFromParcel(Parcel in) {
            ExternalUrls__ instance = new ExternalUrls__();
            instance.spotify = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ExternalUrls__[] newArray(int size) {
            return (new ExternalUrls__[size]);
        }

    }
    ;
    private final static long serialVersionUID = -8294109345409293603L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ExternalUrls__() {
    }

    /**
     * 
     * @param spotify
     */
    public ExternalUrls__(String spotify) {
        super();
        this.spotify = spotify;
    }

    public String getSpotify() {
        return spotify;
    }

    public void setSpotify(String spotify) {
        this.spotify = spotify;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(spotify);
    }

    public int describeContents() {
        return  0;
    }

}
