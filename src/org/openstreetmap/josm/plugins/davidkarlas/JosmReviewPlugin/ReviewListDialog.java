package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import java.awt.event.MouseAdapter;

import org.openstreetmap.josm.data.APIDataSet;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.data.osm.visitor.OsmPrimitiveVisitor;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.SideButton;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.util.HighlightHelper;
import org.openstreetmap.josm.tools.Shortcut;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ReviewListDialog extends ToggleDialog {

    private JList<ReviewItem> displayList;
    private ReviewListModel model = new ReviewListModel();
    private final HighlightHelper highlightHelper = new HighlightHelper();

    public ReviewListDialog() {
        super(tr("Review List"), "reviewPlugin/icon", tr("Open the review list window."),
                Shortcut.registerShortcut("subwindow:reviewchanges", tr("Windows: {0}", tr("Review List")),
                        KeyEvent.VK_E, Shortcut.ALT_SHIFT),
                350, false);

        List<SideButton> buttons = new LinkedList<>();

        displayList = new JList<>(model);
        displayList.setCellRenderer(new ReviewItemRenderer());
        displayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        displayList.addListSelectionListener(e -> {
            ZoomToSelectedItem();
        });
        displayList.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                ZoomToSelectedItem();
            }

            @Override
            public void focusLost(FocusEvent e) {
                highlightHelper.clear();
            }
        });

        displayList.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == ' ') {
                    ReviewItem sel = displayList.getSelectedValue();
                    if (sel == null)
                        return;
                    model.ToggleReviewed(sel);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        displayList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    ReviewItem sel = displayList.getSelectedValue();
                    if (sel == null)
                        return;
                    model.ToggleReviewed(sel);
                }
            }
        });
        createLayout(displayList, true, buttons);
    }

    protected void ZoomToSelectedItem() {
        BoundingXYVisitor v = new BoundingXYVisitor();
        ReviewItem sel = displayList.getSelectedValue();
        if (sel == null)
            return;
        sel.getItem().accept((OsmPrimitiveVisitor) v);
        if (v.getBounds() == null)
            return;
        MainApplication.getMap().mapView.zoomTo(v);
        highlightHelper.highlightOnly(sel.getItem());
    }

    public void setData(APIDataSet apiData) {
        model.UpdateData(apiData);
    }
}
