
package mccode.spotidj.models;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ExternalUrls_ implements Serializable, Parcelable
{

    private String spotify;
    public final static Parcelable.Creator<ExternalUrls_> CREATOR = new Creator<ExternalUrls_>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ExternalUrls_ createFromParcel(Parcel in) {
            ExternalUrls_ instance = new ExternalUrls_();
            instance.spotify = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ExternalUrls_[] newArray(int size) {
            return (new ExternalUrls_[size]);
        }

    }
    ;
    private final static long serialVersionUID = 4034947096500693007L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ExternalUrls_() {
    }

    /**
     * 
     * @param spotify
     */
    public ExternalUrls_(String spotify) {
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
