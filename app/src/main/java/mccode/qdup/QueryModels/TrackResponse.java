
package mccode.qdup.QueryModels;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class TrackResponse implements Parcelable
{

    private Tracks tracks;
    public final static Parcelable.Creator<TrackResponse> CREATOR = new Creator<TrackResponse>() {


        @SuppressWarnings({
            "unchecked"
        })
        public TrackResponse createFromParcel(Parcel in) {
            return new TrackResponse(in);
        }

        public TrackResponse[] newArray(int size) {
            return (new TrackResponse[size]);
        }

    }
    ;

    protected TrackResponse(Parcel in) {
        this.tracks = ((Tracks) in.readValue((Tracks.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public TrackResponse() {
    }

    /**
     * 
     * @param tracks
     */
    public TrackResponse(Tracks tracks) {
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
