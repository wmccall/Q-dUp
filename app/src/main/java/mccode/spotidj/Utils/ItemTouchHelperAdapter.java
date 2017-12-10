package mccode.spotidj.Utils;

/**
 * Author: Connor McAuliffe
 * Created: 12/6/2017
 * Last Revised: 12/6/2017
 */

interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
