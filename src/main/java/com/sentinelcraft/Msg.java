package com.sentinelcraft;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** Kleine Hilfsklasse fuer Spielernachrichten. Nur hier anpassen, falls sich die API aendert. */
public final class Msg {
	private Msg() {}

	/** Nachricht in der Action-Bar (ueber der Hotbar). */
	public static void bar(Player player, String key, Object... args) {
		player.displayClientMessage(Component.translatable("message." + SentinelCraft.MOD_ID + "." + key, args), true);
	}

	/** Nachricht im Chat. */
	public static void chat(Player player, String key, Object... args) {
		player.displayClientMessage(Component.translatable("message." + SentinelCraft.MOD_ID + "." + key, args), false);
	}
}
