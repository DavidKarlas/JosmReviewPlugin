package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.Actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin.ReviewListDialog;
import org.openstreetmap.josm.tools.Shortcut;
import org.openstreetmap.josm.tools.ImageProvider;

public class NextItemAction extends JosmAction {

    public NextItemAction() {
        super(null, new ImageProvider("dialogs/reviewPlugin/down-c"),
                "Moves to next item in Review plugin list.",
                Shortcut.registerShortcut("Moves to next item in Review plugin",
                        "Moves to next item in Review plugin list.",
                        KeyEvent.VK_DOWN, Shortcut.ALT_CTRL),
                false, "reviewChanges", true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ReviewListDialog reviewListDialog = MainApplication.getMap().getToggleDialog(ReviewListDialog.class);
        reviewListDialog.NextItem();
    }
}
