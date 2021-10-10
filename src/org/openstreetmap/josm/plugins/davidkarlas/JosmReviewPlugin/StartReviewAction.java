package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.tools.Shortcut;
import org.openstreetmap.josm.tools.ImageProvider;

public class StartReviewAction extends JosmAction {

    public StartReviewAction() {
        super("Start Review", new ImageProvider("dialogs/reviewPlugin/icon"),
                "Shows 'Review Changes' pad and updates content with latest changes to be reviewed.",
                Shortcut.registerShortcut("Start Review",
                        "Shows 'Review Changes' pad and updates content with latest changes to be reviewed.",
                        KeyEvent.VK_R, Shortcut.CTRL_SHIFT),
                false, "reviewChanges", true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ReviewListDialog reviewListDialog = MainApplication.getMap().getToggleDialog(ReviewListDialog.class);
        reviewListDialog.StartReview();
        reviewListDialog.buttonShown();
    }
}
