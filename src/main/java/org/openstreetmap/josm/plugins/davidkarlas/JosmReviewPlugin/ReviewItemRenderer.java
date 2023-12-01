package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.openstreetmap.josm.data.osm.DefaultNameFormatter;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.ImageProvider.ImageSizes;

public class ReviewItemRenderer implements ListCellRenderer<ReviewItem> {

    private final DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
    private final DefaultNameFormatter formatter = DefaultNameFormatter.getInstance();
    private ImageIcon reviewedImageIcon = new ImageProvider("reviewplugin/check-one").setSize(ImageSizes.SMALLICON).get();
    private ImageIcon unreviewedImageIcon = new ImageProvider("reviewplugin/close-one").setSize(ImageSizes.SMALLICON).get();

    @Override
    public Component getListCellRendererComponent(JList<? extends ReviewItem> list, ReviewItem item, int index,
            boolean isSelected, boolean cellHasFocus) {
        Component comp = defaultListCellRenderer.getListCellRendererComponent(list, item, index, isSelected,
                cellHasFocus);
        if (item != null && comp instanceof JLabel) {
            OsmPrimitive osm = item.getItem();
            JLabel jlabel = (JLabel) comp;
            jlabel.setText(item.getChangeLabel() + ": " + formatter.format(osm));
            jlabel.setToolTipText(formatter.buildDefaultToolTip(osm));
            jlabel.setIcon(item.getReviewed() ? reviewedImageIcon : unreviewedImageIcon);
        }
        return comp;
    }
}
