package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin;

import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class JosmReviewPlugin extends Plugin {
    public JosmReviewPlugin(PluginInformation info) {
        super(info);
    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        if (newFrame != null) {
            newFrame.addToggleDialog(new ReviewListDialog());
        }
    }
}
