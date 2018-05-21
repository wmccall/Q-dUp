
package mccode.qdup.QueryModels;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class External_ids implements Parcelable
{

    private String isrc;
    public final static Parcelable.Creator<External_ids> CREATOR = new Creator<External_ids>() {


        @SuppressWarnings({
            "unchecked"
        })
        public External_ids createFromParcel(Parcel in) {
            return new External_ids(in);
        }

        public External_ids[] newArray(int size) {
            return (new External_ids[size]);
        }

    }
    ;

    protected External_ids(Parcel in) {
        this.isrc = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public External_ids() {
    }

    /**
     * 
     * @param isrc
     */
    public External_ids(String isrc) {
        super();
        this.isrc = isrc;
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(isrc);
    }

    public int describeContents() {
        return  0;
    }

}
