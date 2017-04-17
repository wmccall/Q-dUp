
package mccode.spotidj.models;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ExternalIds implements Serializable, Parcelable
{

    private String isrc;
    public final static Parcelable.Creator<ExternalIds> CREATOR = new Creator<ExternalIds>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ExternalIds createFromParcel(Parcel in) {
            ExternalIds instance = new ExternalIds();
            instance.isrc = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ExternalIds[] newArray(int size) {
            return (new ExternalIds[size]);
        }

    }
    ;
    private final static long serialVersionUID = -6234753774975870261L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ExternalIds() {
    }

    /**
     * 
     * @param isrc
     */
    public ExternalIds(String isrc) {
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
