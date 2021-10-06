package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.APIDataSet;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;
import org.openstreetmap.josm.tools.Shortcut;
import org.openstreetmap.josm.tools.ImageProvider;

public class StartReviewAction extends JosmAction {
    ReviewListDialog reviewListDialog;

    public StartReviewAction() {
        super("Review Changes", new ImageProvider("dialogs/reviewPlugin/icon"), "Review Changes",
                Shortcut.registerShortcut("Review Changes", "Review Changes", KeyEvent.VK_R, Shortcut.CTRL_SHIFT),
                false, "reviewChanges", true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (MainApplication.getMap().getToggleDialog(ReviewListDialog.class) == null) {
            reviewListDialog = new ReviewListDialog();
        }
        APIDataSet apiData = new APIDataSet(getLayerManager().getEditDataSet());
        reviewListDialog.setData(apiData);

        if (MainApplication.getMap().getToggleDialog(ReviewListDialog.class) == null) {
            MainApplication.getMap().addToggleDialog(reviewListDialog);
        }
        reviewListDialog.buttonShown();
    }

    @Override
    protected void updateEnabledState() {
        OsmDataLayer editLayer = getLayerManager().getEditLayer();
        setEnabled(editLayer != null && editLayer.requiresUploadToServer());
    }

    private final PropertyChangeListener updateOnRequireUploadChange = evt -> {
        if (OsmDataLayer.REQUIRES_UPLOAD_TO_SERVER_PROP.equals(evt.getPropertyName())) {
            updateEnabledState();
        }
    };

    @Override
    protected LayerChangeAdapter buildLayerChangeAdapter() {
        return new LayerChangeAdapter() {
            @Override
            public void layerAdded(LayerAddEvent e) {
                if (e.getAddedLayer() instanceof OsmDataLayer) {
                    e.getAddedLayer().addPropertyChangeListener(updateOnRequireUploadChange);
                }
                super.layerAdded(e);
            }

            @Override
            public void layerRemoving(LayerRemoveEvent e) {
                if (e.getRemovedLayer() instanceof OsmDataLayer) {
                    e.getRemovedLayer().removePropertyChangeListener(updateOnRequireUploadChange);
                }
                super.layerRemoving(e);
            }
        };
    }
}
