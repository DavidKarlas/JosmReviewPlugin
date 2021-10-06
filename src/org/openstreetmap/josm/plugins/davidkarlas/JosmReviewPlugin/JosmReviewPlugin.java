package org.openstreetmap.josm.plugins.davidkarlas.JosmReviewPlugin;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class JosmReviewPlugin extends Plugin {
    StartReviewAction startReviewAction;
    public JosmReviewPlugin(PluginInformation info) {
        super(info);
        
        startReviewAction = new StartReviewAction();
        MainMenu.add(MainApplication.getMenu().toolsMenu, startReviewAction);
    }
}
