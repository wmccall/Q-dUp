
package mccode.qdup.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tracks implements Serializable
{

    private String href;
    private List<Item> items = null;
    private Integer limit;
    private String next;
    private Integer offset;
    private Object previous;
    private Integer total;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 6530690084933129786L;

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

    public Tracks withHref(String href) {
        this.href = href;
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Tracks withItems(List<Item> items) {
        this.items = items;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Tracks withLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public Tracks withNext(String next) {
        this.next = next;
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Tracks withOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Object getPrevious() {
        return previous;
    }

    public void setPrevious(Object previous) {
        this.previous = previous;
    }

    public Tracks withPrevious(Object previous) {
        this.previous = previous;
        return this;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Tracks withTotal(Integer total) {
        this.total = total;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Tracks withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
