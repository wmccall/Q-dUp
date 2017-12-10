package mccode.spotidj.Utils;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mccode.spotidj.R;
import mccode.spotidj.ServerActivity;
import mccode.spotidj.models.Item;

/**
 * Author: Connor McAuliffe
 * Created: 12/6/2017
 * Last Revised: 12/6/2017
 */

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final List<String> mDisplays = new ArrayList<>();
    private final List<Item> mItems = new ArrayList<>();

    private ServerActivity parent;
    private int currentPlaying = -1;
    private boolean repeating = false;

    public RecyclerListAdapter(ServerActivity parent) {
        this.parent = parent;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.textView.setText(mDisplays.get(position));
    }

    @Override
    public int getItemCount() {
        return mDisplays.size();
    }

    @Override
    public void onItemDismiss(int position){
        Log.d("RecyclerAdapter", "update: " + currentPlaying);

        mDisplays.remove(position);
        mItems.remove(position);
        if(position < currentPlaying){
            currentPlaying--;
        }
        if(position == currentPlaying){
            currentPlaying--;
            parent.playSong(next());
        }
        notifyItemRemoved(position);
        Log.d("RecyclerAdapter", "post update: " + currentPlaying);

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition){
        if(fromPosition < toPosition){
            for (int i = fromPosition; i < toPosition; i++){
                Collections.swap(mDisplays, i, i+1);
                Collections.swap(mItems, i, i+1);
            }

        }
        else{
            for(int i = fromPosition; i > toPosition; i--){
                Collections.swap(mDisplays, i, i-1);
                Collections.swap(mItems, i, i-1);
            }
        }
        updateCurrentPlaying(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    /**
     * handles updating the current playing change when songs are swapped
     * if the currentPlaying is one of the indices it becomes the other
     * else if it is between them it changes by the increment (either 1 or -1)
     * @param from the lower index of the swapped values
     * @param to the upper index of the swapper values
     */
    private void updateCurrentPlaying(int from, int to){
        Log.d("RecyclerAdapter", "update: " + currentPlaying);

        if(currentPlaying == from){
            currentPlaying = to;
        }
        else if (from < to && currentPlaying < to && currentPlaying > from){
            currentPlaying--;
        }
        else if (to < from && currentPlaying < from && currentPlaying > to){
            currentPlaying++;
        }
        Log.d("RecyclerAdapter", "post update: " + currentPlaying);


    }

    public void addItem(Item item, String string){
        mDisplays.add(string);
        mItems.add(item);
        this.notifyItemInserted(mDisplays.size()-1);

    }

    public String next(){
        Log.d("RecyclerAdapter", "next: " + currentPlaying);
        String result = "";
        currentPlaying++;
        if(repeating){
            currentPlaying = currentPlaying % mItems.size();
        }
        if(currentPlaying < mItems.size()) {
            result =  mItems.get(currentPlaying).getUri();
        }

        return result;
    }


    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView textView;
        public final ImageView handleView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.holder_text);
            handleView = (ImageView) itemView.findViewById(R.id.holder_handle);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
