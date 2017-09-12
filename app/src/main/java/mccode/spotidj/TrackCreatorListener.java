package mccode.spotidj;

import android.view.View;

import java.util.ArrayList;

import mccode.spotidj.models.TrackResponse;

/**
 * TrackCreatorListener is a interface that is used to create the tracks coming back from
 * a search result
 */

public interface TrackCreatorListener {
    void onCreateSucceeded(View result, TrackResponse t);
}
