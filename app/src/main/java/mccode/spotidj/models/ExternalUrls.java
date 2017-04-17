
package mccode.spotidj.models;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ExternalUrls implements Serializable, Parcelable
{

    private String spotify;
    public final static Parcelable.Creator<ExternalUrls> CREATOR = new Creator<ExternalUrls>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ExternalUrls createFromParcel(Parcel in) {
            ExternalUrls instance = new ExternalUrls();
            instance.spotify = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ExternalUrls[] newArray(int size) {
            return (new ExternalUrls[size]);
        }

    }
    ;
    private final static long serialVersionUID = -4954989389059260288L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ExternalUrls() {
    }

    /**
     * 
     * @param spotify
     */
    public ExternalUrls(String spotify) {
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
