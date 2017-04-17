
package mccode.spotidj.models;

import java.io.Serializable;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Tracks implements Serializable, Parcelable
{

    private String href;
    private List<Item> items = null;
    private Integer limit;
    private String next;
    private Integer offset;
    private Object previous;
    private Integer total;
    public final static Parcelable.Creator<Tracks> CREATOR = new Creator<Tracks>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Tracks createFromParcel(Parcel in) {
            Tracks instance = new Tracks();
            instance.href = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.items, (mccode.spotidj.models.Item.class.getClassLoader()));
            instance.limit = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.next = ((String) in.readValue((String.class.getClassLoader())));
            instance.offset = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.previous = ((Object) in.readValue((Object.class.getClassLoader())));
            instance.total = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public Tracks[] newArray(int size) {
            return (new Tracks[size]);
        }

    }
    ;
    private final static long serialVersionUID = 3690002167447563252L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Tracks() {
    }

    /**
     * 
     * @param total
     * @param limit
     * @param previous
     * @param items
     * @param next
     * @param offset
     * @param href
     */
    public Tracks(String href, List<Item> items, Integer limit, String next, Integer offset, Object previous, Integer total) {
        super();
        this.href = href;
        this.items = items;
        this.limit = limit;
        this.next = next;
        this.offset = offset;
        this.previous = previous;
        this.total = total;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Object getPrevious() {
        return previous;
    }

    public void setPrevious(Object previous) {
        this.previous = previous;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(href);
        dest.writeList(items);
        dest.writeValue(limit);
        dest.writeValue(next);
        dest.writeValue(offset);
        dest.writeValue(previous);
        dest.writeValue(total);
    }

    public int describeContents() {
        return  0;
    }

}
