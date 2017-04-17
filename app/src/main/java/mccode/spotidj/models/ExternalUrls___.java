
package mccode.spotidj.models;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ExternalUrls___ implements Serializable, Parcelable
{

    private String spotify;
    public final static Parcelable.Creator<ExternalUrls___> CREATOR = new Creator<ExternalUrls___>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ExternalUrls___ createFromParcel(Parcel in) {
            ExternalUrls___ instance = new ExternalUrls___();
            instance.spotify = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ExternalUrls___[] newArray(int size) {
            return (new ExternalUrls___[size]);
        }

    }
    ;
    private final static long serialVersionUID = 5800816330175371024L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ExternalUrls___() {
    }

    /**
     * 
     * @param spotify
     */
    public ExternalUrls___(String spotify) {
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
