
package mccode.qdup.QueryModels;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class External_urls_ implements Parcelable
{

    private String spotify;
    public final static Parcelable.Creator<External_urls_> CREATOR = new Creator<External_urls_>() {


        @SuppressWarnings({
            "unchecked"
        })
        public External_urls_ createFromParcel(Parcel in) {
            return new External_urls_(in);
        }

        public External_urls_[] newArray(int size) {
            return (new External_urls_[size]);
        }

    }
    ;

    protected External_urls_(Parcel in) {
        this.spotify = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public External_urls_() {
    }

    /**
     * 
     * @param spotify
     */
    public External_urls_(String spotify) {
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
