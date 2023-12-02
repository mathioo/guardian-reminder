package com.guardianreminder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

public class GuardianReminderOverlay extends OverlayPanel {

    private final GuardianReminderConfig config;

    private final GuardianReminderPlugin plugin;



    @Inject
    private GuardianReminderOverlay(GuardianReminderConfig config, GuardianReminderPlugin plugin) {
        this.config = config;
        this.plugin = plugin;

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        if (!plugin.hasHorn()) {
            panelComponent.getChildren().add((LineComponent.builder())
                    .left("Horn is not in inventory")
                    .build());
        }
        else if(config.combatNotifier() && plugin.guardianOut) {
            panelComponent.getChildren().add((LineComponent.builder())
                    .left("Not in combat")
                    .build());
        }
        else {
            panelComponent.getChildren().add((LineComponent.builder())
                    .left("Click your horn")
                    .build());
        }


        panelComponent.setBackgroundColor(new Color(255, 0, 0, 150));
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        return panelComponent.render(graphics);
    }
}
