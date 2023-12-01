package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.Actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.ReviewListDialog;
import org.openstreetmap.josm.tools.Shortcut;
import org.openstreetmap.josm.tools.ImageProvider;

public class ToggleAction extends JosmAction {

    public ToggleAction() {
        super(null, new ImageProvider("dialogs/reviewPlugin/checklist"),
                "Toggles review state of selected item.",
                Shortcut.registerShortcut("Toggle item in Review plugin",
                        "Toggles review state of selected item.",
                        KeyEvent.VK_SPACE, Shortcut.ALT_CTRL),
                false, "reviewChanges", true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ReviewListDialog reviewListDialog = MainApplication.getMap().getToggleDialog(ReviewListDialog.class);
        reviewListDialog.ToggleReviewed(false);
    }
}
