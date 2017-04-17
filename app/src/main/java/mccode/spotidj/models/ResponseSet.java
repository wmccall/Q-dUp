
package mccode.spotidj.models;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ResponseSet implements Serializable, Parcelable
{

    private Tracks tracks;
    public final static Parcelable.Creator<ResponseSet> CREATOR = new Creator<ResponseSet>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ResponseSet createFromParcel(Parcel in) {
            ResponseSet instance = new ResponseSet();
            instance.tracks = ((Tracks) in.readValue((Tracks.class.getClassLoader())));
            return instance;
        }

        public ResponseSet[] newArray(int size) {
            return (new ResponseSet[size]);
        }

    }
    ;
    private final static long serialVersionUID = 5915597623879386516L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ResponseSet() {
    }

    /**
     * 
     * @param tracks
     */
    public ResponseSet(Tracks tracks) {
        super();
        this.tracks = tracks;
    }

    public Tracks getTracks() {
        return tracks;
    }

    public void setTracks(Tracks tracks) {
        this.tracks = tracks;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(tracks);
    }

    public int describeContents() {
        return  0;
    }

}
