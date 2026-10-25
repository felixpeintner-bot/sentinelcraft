package com.sentinelcraft.item;

import net.minecraft.world.item.Item;

/** Keycard der Stufe 1-3. Ein Leser akzeptiert Karten ab seiner eingestellten Stufe. */
public class KeycardItem extends Item {
	private final int cardLevel;

	public KeycardItem(Item.Properties properties, int cardLevel) {
		super(properties);
		this.cardLevel = cardLevel;
	}

	public int getCardLevel() {
		return cardLevel;
	}
}
