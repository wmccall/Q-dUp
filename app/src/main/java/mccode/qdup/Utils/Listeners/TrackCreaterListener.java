package mccode.qdup.Utils.Listeners;

import android.view.View;

import mccode.qdup.QueryModels.TrackResponse;

/**
 * Created by mammo on 3/24/2017.
 */

public interface TrackCreaterListener {
    public void onCreateSucceeded(View result, TrackResponse t);
}
