package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class ReviewItem {
    private OsmPrimitive item;
    private boolean reviewed = false;

    public ReviewItem(OsmPrimitive item) {
        this.item = item;
    } 

    public OsmPrimitive getItem() {
        return item;
    }

    public boolean getReviewed() {
        return reviewed;
    }

    public boolean ToggleReviewed() {
        reviewed = !reviewed;
        return reviewed;
    }

    public String getChangeLabel() {
        if (item.isDeleted()) {
            if (item.getId() < 1)
                return "new+deleted";
            return "deleted";
        }
        if (item.isModified()) {
            if (item.getId() < 1)
                return "new+modified";
            return "modified";
        }
        if (item.isNew()) {
            return "new";
        }
        // If not deleted,modified or added
        // it must came here via child node being moved
        // hence mark it as moved.
        return "moved";
    }
}