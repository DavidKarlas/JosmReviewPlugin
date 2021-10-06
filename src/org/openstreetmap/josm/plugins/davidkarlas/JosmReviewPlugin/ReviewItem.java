package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class ReviewItem {
    private OsmPrimitive item;
    private boolean reviewed;
    private boolean onlyChildChanged;

    public ReviewItem(OsmPrimitive item, boolean reviewed, boolean onlyChildChanged) {
        this.item = item;
        this.reviewed = reviewed;
        this.onlyChildChanged = onlyChildChanged;
    }

    public boolean isOnlyChildChanged() {
        return onlyChildChanged;
    }

    public OsmPrimitive getItem() {
        return item;
    }

    public boolean getReviewed() {
        return reviewed;
    }

    public void ToggleReviewed() {
        reviewed = !reviewed;
    }
}