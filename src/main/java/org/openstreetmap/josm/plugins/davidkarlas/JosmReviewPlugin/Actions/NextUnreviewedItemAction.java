package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.Actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.ReviewListDialog;
import org.openstreetmap.josm.tools.Shortcut;
import org.openstreetmap.josm.tools.ImageProvider;

public class NextUnreviewedItemAction extends JosmAction {

    public NextUnreviewedItemAction() {
        super(null, new ImageProvider("dialogs/reviewPlugin/circle-double-down"),
                "Moves to next unreviewed item in list.",
                Shortcut.registerShortcut("Moves to next unreviewed item in Review plugin list.",
                        "Moves to next unreviewed item in Review plugin list.",
                        KeyEvent.VK_RIGHT, Shortcut.ALT_CTRL),
                false, "reviewChanges", true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ReviewListDialog reviewListDialog = MainApplication.getMap().getToggleDialog(ReviewListDialog.class);
        reviewListDialog.NextUnreviewedItem();
    }
}
