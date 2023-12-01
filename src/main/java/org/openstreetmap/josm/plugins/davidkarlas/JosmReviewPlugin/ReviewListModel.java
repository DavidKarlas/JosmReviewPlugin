package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin;

import java.util.*;

import javax.swing.AbstractListModel;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.data.APIDataSet;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.PrimitiveId;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.tools.Pair;

public class ReviewListModel extends AbstractListModel<ReviewItem> {
    private List<ReviewItem> items = new LinkedList<>();

    public void StartReview(boolean loadValidationErrors, boolean keepCurrentProgress) {
        items = new ArrayList<>();

        if (loadValidationErrors) {
            Map<PrimitiveId, Integer> primitivesCount = new HashMap<PrimitiveId, Integer>();

            for (TestError validationError : MainApplication.getLayerManager().getEditLayer().validationErrors) {
                if (validationError.isSelected())
                    for (OsmPrimitive osmPrimitive : validationError.getPrimitives()) {
                        if (primitivesCount.containsKey(osmPrimitive)) {
                            primitivesCount.put(osmPrimitive, primitivesCount.get(osmPrimitive) + 1);
                        } else {
                            primitivesCount.put(osmPrimitive, 1);
                        }
                    }
            }

            Set<OsmPrimitive> uniqueItems = new HashSet<>();
            for (TestError validationError : MainApplication.getLayerManager().getEditLayer().validationErrors) {
                if (validationError.isSelected()) {
                    OsmPrimitive primitiveWithBiggestCount = validationError.getPrimitives().stream()
                            .map(p -> new Pair<OsmPrimitive, Integer>(p, primitivesCount.get(p)))
                            .sorted(new Comparator<Pair<OsmPrimitive, Integer>>() {
                                public int compare(Pair<OsmPrimitive, Integer> o1, Pair<OsmPrimitive, Integer> o2) {
                                    return o2.b.compareTo(o1.b);
                                }
                            })
                            .findFirst()
                            .get().a;
                    uniqueItems.add(primitiveWithBiggestCount);
                }
            }
            for (OsmPrimitive osmPrimitive : uniqueItems) {
                items.add(new ReviewItem(osmPrimitive));
            }
        } else {
            APIDataSet data = new APIDataSet(MainApplication.getLayerManager().getActiveDataSet());
            Map<PrimitiveId, ReviewItem> oldItems = new HashMap<PrimitiveId, ReviewItem>();
            if (keepCurrentProgress) {
                for (ReviewItem reviewItem : items) {
                    oldItems.put(reviewItem.getItem().getPrimitiveId(), reviewItem);
                }
            }
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
                insertNewItem(oldItems, o);
            }
            for (OsmPrimitive way : parentWays) {
                if (!newItems.contains(way))
                    insertNewItem(oldItems, way);
            }
            for (OsmPrimitive relation : parentRelations) {
                if (!newItems.contains(relation))
                    insertNewItem(oldItems, relation);
            }
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

    private void insertNewItem(Map<PrimitiveId, ReviewItem> oldItems, OsmPrimitive o) {
        ReviewItem newItem = new ReviewItem(o);
        if (oldItems.containsKey(o.getPrimitiveId()) && oldItems.get(o.getPrimitiveId()).getReviewed()) {
            newItem.ToggleReviewed();
        }
        items.add(newItem);
    }

    @Override
    public int getSize() {
        return items.size();
    }

    @Override
    public org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.ReviewItem getElementAt(int index) {
        return items.get(index);
    }

    public boolean ToggleReviewed(ReviewItem sel) {
        boolean newValue = sel.ToggleReviewed();
        int index = items.indexOf(sel);
        fireContentsChanged(this, index, index);
        return newValue;
    }

    public void Clear() {
        items.clear();
        fireContentsChanged(this, 0, 0);
    }

}
