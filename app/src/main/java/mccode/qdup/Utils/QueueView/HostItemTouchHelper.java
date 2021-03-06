

package mccode.qdup.Utils.QueueView;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import static mccode.qdup.Activities.PortalActivity.isServer;

/**
 * Author: Connor McAuliffe
 * Created: 12/6/2017
 * Last Revised: 12/6/2017
 */

public class HostItemTouchHelper extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;

    public HostItemTouchHelper(ItemTouchHelperAdapter adapter){
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return isServer;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return isServer;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        if(isServer) {
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (isServer) mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemSelected();
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
        itemViewHolder.onItemClear();
    }

}

