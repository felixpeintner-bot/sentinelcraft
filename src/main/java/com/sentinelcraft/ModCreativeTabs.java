package com.sentinelcraft;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;

public final class ModCreativeTabs {
	private ModCreativeTabs() {}

	public static void init() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries -> {
			entries.accept(ModBlocks.GENERATOR);
			entries.accept(ModBlocks.LIGHT_SWITCH);
			entries.accept(ModBlocks.WIRE_LAMP);
			entries.accept(ModBlocks.KEYCARD_READER);
			entries.accept(ModBlocks.CAMERA);
		});
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
			entries.accept(ModItems.CABLE_TOOL);
			entries.accept(ModItems.KEYCARD_1);
			entries.accept(ModItems.KEYCARD_2);
			entries.accept(ModItems.KEYCARD_3);
		});
	}
}
