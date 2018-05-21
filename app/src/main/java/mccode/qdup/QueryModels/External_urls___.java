
package mccode.qdup.QueryModels;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class External_urls___ implements Parcelable
{

    private String spotify;
    public final static Parcelable.Creator<External_urls___> CREATOR = new Creator<External_urls___>() {


        @SuppressWarnings({
            "unchecked"
        })
        public External_urls___ createFromParcel(Parcel in) {
            return new External_urls___(in);
        }

        public External_urls___[] newArray(int size) {
            return (new External_urls___[size]);
        }

    }
    ;

    protected External_urls___(Parcel in) {
        this.spotify = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public External_urls___() {
    }

    /**
     * 
     * @param spotify
     */
    public External_urls___(String spotify) {
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
