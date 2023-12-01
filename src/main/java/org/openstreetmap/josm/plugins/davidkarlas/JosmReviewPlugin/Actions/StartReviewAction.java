package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.Actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.ReviewListDialog;
import org.openstreetmap.josm.tools.Shortcut;
import org.openstreetmap.josm.tools.ImageProvider;

public class StartReviewAction extends JosmAction {

    public StartReviewAction() {
        super(null, new ImageProvider("dialogs/reviewPlugin/icon"),
                "Updates list with latest changes to be reviewed.",
                Shortcut.registerShortcut("Start Review plugin",
                        "Shows 'Review Changes' pad and updates content with latest changes to be reviewed.",
                        KeyEvent.VK_R, Shortcut.ALT_CTRL),
                false, "reviewChanges", true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ReviewListDialog reviewListDialog = MainApplication.getMap().getToggleDialog(ReviewListDialog.class);
        reviewListDialog.StartReview();
    }
}
