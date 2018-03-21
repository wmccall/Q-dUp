package mccode.qdup;

import android.view.View;

import mccode.qdup.QueryModels.TrackResponse;

/**
 * TrackCreatorListener is a interface that is used to create the tracks coming back from
 * a search result
 */

public interface TrackCreatorListener {
    void onCreateSucceeded(View result, TrackResponse t);
}
