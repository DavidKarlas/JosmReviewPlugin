package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.event.MouseEvent;
import java.awt.Color;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

import java.awt.event.MouseAdapter;

import org.openstreetmap.josm.data.osm.DataSelectionListener;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.event.SelectionEventManager;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.data.osm.visitor.OsmPrimitiveVisitor;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.SideButton;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerOrderChangeEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;
import org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.Actions.NextItemAction;
import org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.Actions.NextUnreviewedItemAction;
import org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.Actions.PreviousItemAction;
import org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.Actions.PreviousUnreviewedItemAction;
import org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.Actions.StartReviewAction;
import org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.Actions.ToggleAction;
import org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.Actions.ToggleMoveNextAction;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Shortcut;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ReviewListDialog extends ToggleDialog implements DataSelectionListener, LayerChangeListener {

    private Color defaultSelectionColor;
    private Color grayedOutSelectionColor;
    private JList<ReviewItem> displayList;
    private ReviewListModel model = new ReviewListModel();
    boolean ignoringSelectionChanges;
    private Layer reviewedLayer;

    public ReviewListDialog() {
        super(tr("Review List"), "reviewPlugin/icon", tr("Open the review list window."),
                Shortcut.registerShortcut("subwindow:reviewchanges", tr("Windows: {0}", tr("Review List")),
                        KeyEvent.VK_E, Shortcut.ALT_SHIFT),
                200, true);

        List<AbstractAction> buttons = new LinkedList<>();

        displayList = new JList<>(model);
        displayList.setCellRenderer(new ReviewItemRenderer());
        displayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        defaultSelectionColor = displayList.getSelectionBackground();
        grayedOutSelectionColor = new Color(153, 153, 153);
        displayList.addListSelectionListener(e -> {
            if (!ignoringSelectionChanges) {
                ZoomToSelectedItem();
            }
        });
        displayList.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                ZoomToSelectedItem();
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        // displayList.addKeyListener(new KeyListener() {
        // @Override
        // public void keyTyped(KeyEvent e) {
        // if (e.getKeyChar() == ' ') {
        // ReviewItem sel = displayList.getSelectedValue();
        // if (sel == null)
        // return;
        // model.ToggleReviewed(sel);
        // }
        // }

        // @Override
        // public void keyPressed(KeyEvent e) {

        // }

        // @Override
        // public void keyReleased(KeyEvent e) {

        // }
        // });

        // displayList.addMouseListener(new MouseAdapter() {
        // @Override
        // public void mouseClicked(MouseEvent e) {
        // if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
        // ToggleReviewed();
        // }
        // }
        // });
        buttons.add(new StartReviewAction());
        buttons.add(new ToggleAction());
        buttons.add(new ToggleMoveNextAction());
        buttons.add(new NextItemAction());
        buttons.add(new NextUnreviewedItemAction());
        buttons.add(new PreviousItemAction());
        buttons.add(new PreviousUnreviewedItemAction());

        createLayout(displayList, true,
                buttons.stream().map((AbstractAction a) -> new SideButton(a)).collect(Collectors.toList()));

        MainApplication.getLayerManager().addLayerChangeListener(this);
    }

    public void ToggleReviewed(boolean forceReviewed) {
        ReviewItem sel = displayList.getSelectedValue();
        if (sel == null)
            return;
        if (forceReviewed && sel.getReviewed())
            return;
        model.ToggleReviewed(sel);
    }

    @Override
    public void destroy() {
        MainApplication.getLayerManager().removeLayerChangeListener(this);
    }

    @Override
    public void showNotify() {
        SelectionEventManager.getInstance().addSelectionListener(this);
    }

    @Override
    public void hideNotify() {
        SelectionEventManager.getInstance().removeSelectionListener(this);
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
        MainApplication.getLayerManager().getEditDataSet().setSelected(sel.getItem());
    }

    public void StartReview() {
        boolean keepCurrentProgress = false;
        boolean loadValidationErrors = false;
        ImageIcon icon = ImageProvider.get("dialogs", "reviewPlugin/icon", ImageProvider.ImageSizes.DEFAULT);
        if (MainApplication.getLayerManager().getEditLayer().validationErrors.size() > 0) {
            int selectedValidationErrorsCount = 0;
            for (TestError validationError : MainApplication.getLayerManager().getEditLayer().validationErrors) {
                if (validationError.isSelected()) {
                    selectedValidationErrorsCount++;
                }
            }
            if (selectedValidationErrorsCount > 1) {
                int n = JOptionPane.showConfirmDialog(MainApplication.getMainFrame(),
                        "You have multiple validation errors selected. Would you like to load validation errors?",
                        "Load validation errors?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.YES_OPTION,
                        icon);
                if (n == 0) {
                    loadValidationErrors = true;
                }
            }
        }

        if (loadValidationErrors == false && model.getSize() > 0) {
            Object[] options = { "Restart and keep progress", "Restart and reset progress", "Cancel" };
            int n = JOptionPane.showOptionDialog(MainApplication.getMainFrame(),
                    "How do you want to restart tracking progress?",
                    "Restart reviewing?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.YES_OPTION,
                    icon,
                    options,
                    options[0]);
            if (n == 2) {
                return;
            }
            keepCurrentProgress = n == 0;
        }
        model.StartReview(loadValidationErrors, keepCurrentProgress);
        reviewedLayer = MainApplication.getLayerManager().getActiveLayer();
        buttonShown();
        Collection<OsmPrimitive> selection = MainApplication.getLayerManager().getActiveDataSet().getSelected();
        if (selection.size() == 1 && selectPrimitive((OsmPrimitive) selection.toArray()[0])) {
            // Successfully moved list to already selected item
        } else {
            setSelectedIndex(0);
        }
    }

    private boolean selectPrimitive(OsmPrimitive primitive) {
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).getItem() == primitive) {
                ignoringSelectionChanges = true;
                setSelectedIndex(i);
                ignoringSelectionChanges = false;
                return true;
            }
        }
        return false;
    }

    @Override
    public void selectionChanged(SelectionChangeEvent event) {
        if (event.getSelection().size() == 1) {
            OsmPrimitive selectedItem = (OsmPrimitive) event.getSelection().toArray()[0];
            if (selectPrimitive(selectedItem)) {
                return;
            }
        }
        displayList.setSelectionBackground(grayedOutSelectionColor);
    }

    private void setSelectedIndex(int index) {
        displayList.setSelectedIndex(index);
        displayList.ensureIndexIsVisible(index);
        displayList.setSelectionBackground(defaultSelectionColor);
    }

    public void NextItem() {
        int selectedIndex = displayList.getSelectedIndex();
        int newIndex = selectedIndex + 1;
        if (newIndex < model.getSize()) {
            setSelectedIndex(newIndex);
        }
    }

    public void PreviousItem() {
        int selectedIndex = displayList.getSelectedIndex();
        int newIndex = selectedIndex - 1;
        if (newIndex >= 0) {
            setSelectedIndex(newIndex);
        }
    }

    public boolean PreviousUnreviewedItem() {
        int selectedIndex = displayList.getSelectedIndex();
        for (int i = selectedIndex - 1; i >= 0; i--) {
            if (!model.getElementAt(i).getReviewed()) {
                setSelectedIndex(i);
                return true;
            }
        }

        for (int i = model.getSize() - 1; i >= 0; i--) {
            if (!model.getElementAt(i).getReviewed()) {
                setSelectedIndex(i);
                return true;
            }
        }

        EverythingIsReviewedMessage();
        return false;
    }

    public boolean NextUnreviewedItem() {
        int selectedIndex = displayList.getSelectedIndex();

        for (int i = selectedIndex + 1; i < model.getSize(); i++) {
            if (!model.getElementAt(i).getReviewed()) {
                setSelectedIndex(i);
                return true;
            }
        }

        for (int i = 0; i < model.getSize(); i++) {
            if (!model.getElementAt(i).getReviewed()) {
                setSelectedIndex(i);
                return true;
            }
        }

        EverythingIsReviewedMessage();
        return false;
    }

    private void EverythingIsReviewedMessage() {
        new Notification("Congratulation, everything is reviewed.").setIcon(JOptionPane.INFORMATION_MESSAGE)
                .setDuration(Notification.TIME_SHORT).show();
    }

    @Override
    public void layerAdded(LayerAddEvent e) {
    }

    @Override
    public void layerRemoving(LayerRemoveEvent e) {
        if (e.getRemovedLayer() == reviewedLayer) {
            model.Clear();
            reviewedLayer = null;
        }
    }

    @Override
    public void layerOrderChanged(LayerOrderChangeEvent e) {
    }
}
