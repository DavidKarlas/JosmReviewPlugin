package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.Actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.ReviewListDialog;
import org.openstreetmap.josm.tools.Shortcut;
import org.openstreetmap.josm.tools.ImageProvider;

public class ToggleMoveNextAction extends JosmAction {

    public ToggleMoveNextAction() {
        super(null, new ImageProvider("dialogs/reviewPlugin/check-one"),
                "Marks selected item as reviewed and moves to next unreviewed item.",
                Shortcut.registerShortcut("Review and move to next",
                        "Marks selected item as reviewed and moves to next unreviewed item.",
                        KeyEvent.VK_ENTER, Shortcut.ALT_CTRL),
                false, "reviewChanges", true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ReviewListDialog reviewListDialog = MainApplication.getMap().getToggleDialog(ReviewListDialog.class);
        reviewListDialog.ToggleReviewed(true);
        reviewListDialog.NextUnreviewedItem();
    }
}
