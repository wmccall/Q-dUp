package mccode.qdup.Utils.QueueView;

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

import mccode.qdup.R;
import mccode.qdup.ServerActivity;
import mccode.qdup.Utils.Messaging.Message;
import mccode.qdup.Utils.Messaging.MessageCode;
import mccode.qdup.QueryModels.Item;

import static mccode.qdup.MainActivity.isServer;

/**
 * Author: Connor McAuliffe
 * Created: 12/6/2017
 * Last Revised: 12/6/2017
 */

public class HostRecyclerListAdapter extends RecyclerView.Adapter<HostRecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final List<String> mDisplays = new ArrayList<>();
    private final List<Item> mItems = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ServerActivity activity;
    private int currentPlaying = -1;
    private boolean repeating = false;

    public HostRecyclerListAdapter(ServerActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        holder.textView.setText(mDisplays.get(position));
        if(isServer) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final int last = currentPlaying;
                    if (currentPlaying >= 0 && currentPlaying < mItems.size()) {
                        ItemViewHolder currPlayHolder;

                        currPlayHolder = (ItemViewHolder) mRecyclerView.findViewHolderForAdapterPosition(currentPlaying);
                        if (currPlayHolder != null) {
                            final ItemViewHolder finalHolder = currPlayHolder;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finalHolder.textView.setTextColor(Color.parseColor("#ffffff"));
                                }
                            });
                        }

                    }
                    currentPlaying = holder.getAdapterPosition();
                    activity.playSong(mItems.get(currentPlaying).getUri());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.textView.setTextColor(Color.parseColor("#6de873"));
                            notifyItemChanged(currentPlaying);
                            notifyItemChanged(last);
                        }
                    });
                    activity.sendMessage(new Message(MessageCode.CHANGE_PLAYING, currentPlaying));
                }

            });
        }
        if(position == currentPlaying){
            holder.textView.setTextColor(Color.parseColor("#6de873"));
        }
        else {
            holder.textView.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return mDisplays.size();
    }

    @Override
    public void onItemDismiss(int position){
        if(isServer) {
            mDisplays.remove(position);
            mItems.remove(position);
            if (position < currentPlaying) {
                currentPlaying--;
            }
            if (position == currentPlaying) {
                currentPlaying--;
                activity.playSong(next());
            }
            notifyItemRemoved(position);

            activity.sendMessage(new Message(MessageCode.REMOVE, position));
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition){
        if(isServer) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mDisplays, i, i + 1);
                    Collections.swap(mItems, i, i + 1);
                }

            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mDisplays, i, i - 1);
                    Collections.swap(mItems, i, i - 1);
                }
            }
            updateCurrentPlaying(fromPosition, toPosition);
            activity.sendMessage(new Message(fromPosition, toPosition));


            notifyItemMoved(fromPosition, toPosition);
            return true;
        } else {
            return false;
        }
    }

    /**
     * handles updating the current playing change when songs are swapped
     * if the currentPlaying is one of the indices it becomes the other
     * else if it is between them it changes by the increment (either 1 or -1)
     * @param from the lower index of the swapped values
     * @param to the upper index of the swapper values
     */
    private void updateCurrentPlaying(int from, int to){

        if(currentPlaying == from){
            currentPlaying = to;
        }
        else if (from < to && currentPlaying < to && currentPlaying > from){
            currentPlaying--;
        }
        else if (to < from && currentPlaying < from && currentPlaying > to){
            currentPlaying++;
        }

    }

    public void addItem(Item item, String string){
        mDisplays.add(string);
        mItems.add(item);
        this.notifyItemInserted(mDisplays.size()-1);
    }

    public String next(){
        if(currentPlaying>=mItems.size()){
            return "";
        }
        ItemViewHolder holder;
        if(currentPlaying >=0) {
            holder = (ItemViewHolder) mRecyclerView.findViewHolderForAdapterPosition(currentPlaying);
            if(holder != null) {
                final ItemViewHolder finalHolder = holder;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finalHolder.textView.setTextColor(Color.parseColor("#ffffff"));
                    }
                });
            }

        }
        String result = "";
        final int last = currentPlaying;
        currentPlaying++;
        if(repeating){
            currentPlaying = currentPlaying % mItems.size();
        }
        if(currentPlaying < mItems.size()) {
            result =  mItems.get(currentPlaying).getUri();
            holder = (ItemViewHolder)mRecyclerView.findViewHolderForAdapterPosition(currentPlaying);
            if(holder != null) {
                final ItemViewHolder finalHolder = holder;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finalHolder.textView.setTextColor(Color.parseColor("#6de873"));
                        notifyItemChanged(currentPlaying);
                        notifyItemChanged(last);
                    }
                });
            }

        }
        activity.sendMessage(new Message(MessageCode.CHANGE_PLAYING, currentPlaying));
        return result;
    }

    public String prev(){
        Log.d("Adapter", "curr" + currentPlaying);
        if(currentPlaying== -1){
            return "";
        }
        ItemViewHolder holder = (ItemViewHolder) mRecyclerView.findViewHolderForAdapterPosition(currentPlaying);
        if(holder != null)
            holder.textView.setTextColor(Color.parseColor("#ffffff"));

        String result = "";
        int last = currentPlaying;
        currentPlaying--;
        if(repeating){
            currentPlaying = currentPlaying % mItems.size();
        }
        if(currentPlaying >= 0) {
            result =  mItems.get(currentPlaying).getUri();
            holder = (ItemViewHolder)mRecyclerView.findViewHolderForAdapterPosition(currentPlaying);
            if(holder != null)
                holder.textView.setTextColor(Color.parseColor("#6de873"));
            notifyItemChanged(currentPlaying);
            notifyItemChanged(last);
        }
        activity.sendMessage(new Message(MessageCode.CHANGE_PLAYING, currentPlaying));
        return result;

    }

    public String playFromBeginning(){
        currentPlaying = -1;
        return next();
    }

    public boolean isCurrValid(){
        return currentPlaying >= 0 && currentPlaying < mItems.size();
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
            itemView.setBackgroundColor(Color.parseColor("#606060"));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public void swap(int fromPosition, int toPosition){
        if(!isServer) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mDisplays, i, i + 1);
                    Collections.swap(mItems, i, i + 1);
                }

            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mDisplays, i, i - 1);
                    Collections.swap(mItems, i, i - 1);
                }
            }
            updateCurrentPlaying(fromPosition, toPosition);


            notifyItemMoved(fromPosition, toPosition);
        }
    }

    public void changePlaying(int idx) {
        if (!isServer) {
            if (currentPlaying >= mItems.size()) {
                return;
            }
            HostRecyclerListAdapter.ItemViewHolder holder;
            if (currentPlaying >= 0) {
                holder = (HostRecyclerListAdapter.ItemViewHolder) mRecyclerView.findViewHolderForAdapterPosition(currentPlaying);
                if (holder != null) {
                    final HostRecyclerListAdapter.ItemViewHolder finalHolder = holder;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finalHolder.textView.setTextColor(Color.parseColor("#ffffff"));
                        }
                    });
                }

            }
            final int last = currentPlaying;
            currentPlaying = idx;
            if (repeating) {
                currentPlaying = currentPlaying % mItems.size();
            }
            if (currentPlaying < mItems.size()) {
                holder = (HostRecyclerListAdapter.ItemViewHolder) mRecyclerView.findViewHolderForAdapterPosition(currentPlaying);
                if (holder != null) {
                    final HostRecyclerListAdapter.ItemViewHolder finalHolder = holder;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finalHolder.textView.setTextColor(Color.parseColor("#6de873"));
                            notifyItemChanged(currentPlaying);
                            notifyItemChanged(last);
                        }
                    });
                }

            }
        }
    }
    public void remove(int position){
        if(!isServer) {
            mDisplays.remove(position);
            mItems.remove(position);
            if (position < currentPlaying) {
                currentPlaying--;
            }
            if (position == currentPlaying) {
                currentPlaying--;
            }
            notifyItemRemoved(position);
        }
    }
}

