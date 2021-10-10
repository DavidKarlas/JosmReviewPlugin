package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.data.APIDataSet;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.Notification;

public class ReviewListModel extends AbstractListModel<ReviewItem> {
    private List<ReviewItem> items = new LinkedList<>();

    public void StartReview() {
        // TODO: Don't lose state of already reviewed items...
        APIDataSet data = new APIDataSet(MainApplication.getLayerManager().getActiveDataSet());
        items = new ArrayList<>();
        LinkedHashSet<OsmPrimitive> newItems = new LinkedHashSet<>();
        LinkedHashSet<Way> parentWays = new LinkedHashSet<>();
        LinkedHashSet<Relation> parentRelations = new LinkedHashSet<>();
        for (OsmPrimitive osm : data.getPrimitives()) {
            if (osm instanceof Node) {
                Node node = (Node) osm;
                for (Way w : node.getParentWays()) {
                    parentWays.add(w);
                }
                for (Relation r : OsmPrimitive.getParentRelations(Collections.singletonList(node))) {
                    parentRelations.add(r);
                }

                // If node is not tagged
                // its probably part of Way/Relation
                // ignore it for review..
                if (osm.isTagged()) {
                    newItems.add(osm);
                }
            } else {
                newItems.add(osm);
            }
        }
        for (OsmPrimitive o : newItems) {
            items.add(new ReviewItem(o));
        }
        for (OsmPrimitive way : parentWays) {
            if (!newItems.contains(way))
                items.add(new ReviewItem(way));
        }
        for (OsmPrimitive relation : parentRelations) {
            if (!newItems.contains(relation))
                items.add(new ReviewItem(relation));
        }
        items.sort(new Comparator<ReviewItem>() {
            @Override
            public int compare(ReviewItem o1, ReviewItem o2) {
                LatLon l1 = o1.getItem().getBBox().getCenter();
                LatLon l2 = o2.getItem().getBBox().getCenter();
                int b1 = (int) (l1.lat() * 500);
                int b2 = (int) (l2.lat() * 500);
                if (b1 == b2) {
                    return Double.compare(l1.lon(), l2.lon());
                }
                return Integer.compare(b1, b2);
            }
        });
        fireContentsChanged(this, 0, getSize());

        if (getSize() == 0) {
            new Notification("No changes detected.").setIcon(JOptionPane.INFORMATION_MESSAGE)
                    .setDuration(Notification.TIME_SHORT).show();
        }
    }

    @Override
    public int getSize() {
        return items.size();
    }

    @Override
    public org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.ReviewItem getElementAt(int index) {
        return items.get(index);
    }

    public void ToggleReviewed(ReviewItem sel) {
        sel.ToggleReviewed();
        int index = items.indexOf(sel);
        fireContentsChanged(this, index, index);
    }

}
