
package mccode.qdup.QueryModels;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class External_urls implements Parcelable
{

    private String spotify;
    public final static Parcelable.Creator<External_urls> CREATOR = new Creator<External_urls>() {


        @SuppressWarnings({
            "unchecked"
        })
        public External_urls createFromParcel(Parcel in) {
            return new External_urls(in);
        }

        public External_urls[] newArray(int size) {
            return (new External_urls[size]);
        }

    }
    ;

    protected External_urls(Parcel in) {
        this.spotify = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public External_urls() {
    }

    /**
     * 
     * @param spotify
     */
    public External_urls(String spotify) {
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
