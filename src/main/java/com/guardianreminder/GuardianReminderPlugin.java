package com.guardianreminder;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.events.NpcSpawned;

@Slf4j
@PluginDescriptor(
	name = "Guardian Reminder",
	description = "Adds an overlay reminding you to use your Guardian",
	tags = {"relic", "leagues", "guardian", "horn"}
)
public class GuardianReminderPlugin extends Plugin {

	public boolean hornClicked = false;
	public boolean guardianOut = false;

	public int attackDelay = 0;
	public int guardianIndex = 0;

	@Inject
	private Client client;

	@Inject
	private GuardianReminderOverlay overlay;

	@Inject
	private OverlayManager overlayManager;
	@Inject
	private GuardianReminderConfig config;


	@Override
	protected void startUp() throws Exception {
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception {
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if(!client.getWorldType().contains(WorldType.SEASONAL))
		{
			overlayManager.remove(overlay);
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		if (chatMessage.getMessage().contains("can't summon your guardian here") && chatMessage.getType().equals(ChatMessageType.GAMEMESSAGE)) {
			//log.debug("Can't summon guardian here");
			hornClicked = false;
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
		if(!hasHorn()){
			overlayManager.add(overlay);
		}
		if (menuOptionClicked.getItemId() == 28769) {
			if (menuOptionClicked.getMenuOption().equals("Summon") || menuOptionClicked.getMenuOption().equals("Change-outfit")) {
				hornClicked = true;
			}
			if (menuOptionClicked.getMenuOption().equals("Dismiss")) {
				guardianIndex = 0;
				guardianOut = false;
				overlayManager.add(overlay);
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		if (config.combatNotifier()) {
			if (guardianIndex != 0 && guardianOut) {
				if (guardianCombat()) {
					//log.debug("Guardian is attacking");
					overlayManager.remove(overlay);
				}
				if (!guardianCombat()) {
					//log.debug("Guardian is not attacking");
					overlayManager.add(overlay);
				}
			}
			attackDelay++;
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned) {
		final NPC npc = npcSpawned.getNpc();

		if (!hornClicked && npc.getIndex() == guardianIndex) {
			guardianOut = true;
			//log.debug("I went back to my guardian");
			overlayManager.remove(overlay);
		}

		if (hornClicked) {
			guardianOut = true;
			guardianIndex = npc.getIndex();
			hornClicked = false;
			//log.debug("I spawned my guardian");
			overlayManager.remove(overlay);
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned) {
		final NPC npc = npcDespawned.getNpc();
		if (npc.getIndex() == guardianIndex && npc.getId() == 12589) {
			//log.debug("My guardian despawned");
			guardianOut = false;
			hornClicked = false;
			overlayManager.add(overlay);
		}
	}

	@Provides
	GuardianReminderConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(GuardianReminderConfig.class);
	}

	public boolean hasHorn() {
		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		if (inventory == null) {
			return false;
		}
		return inventory.contains(ItemID.GUARDIAN_HORN);
	}

	public boolean guardianCombat() {
		if (client.getCachedNPCs()[guardianIndex].getAnimation() != -1) {
			attackDelay = 0;
			return true;
		}
        return attackDelay < 4;
    }
}


