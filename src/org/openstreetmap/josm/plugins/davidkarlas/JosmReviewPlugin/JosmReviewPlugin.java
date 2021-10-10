package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class JosmReviewPlugin extends Plugin {
    public JosmReviewPlugin(PluginInformation info) {
        super(info);
    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        ReviewListDialog existingDialog=MainApplication.getMap().getToggleDialog(ReviewListDialog.class);
        if (existingDialog != null) {
            oldFrame.removeToggleDialog(existingDialog);
        }
        newFrame.addToggleDialog(new ReviewListDialog());
    }
}
