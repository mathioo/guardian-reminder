package com.guardianreminder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("guardianreminder")
public interface GuardianReminderConfig extends Config
{
	@ConfigItem(
		keyName = "combatNotifier",
		name = "Combat notifier",
		description = "Notify if the guardian is not in combat"
	)
	default boolean combatNotifier()
	{
		return false;
	}
}
