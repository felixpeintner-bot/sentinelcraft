package com.sentinelcraft;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;

public final class ModCreativeTabs {
	private ModCreativeTabs() {}

	public static void init() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(output -> {
			output.accept(ModBlocks.GENERATOR);
			output.accept(ModBlocks.LIGHT_SWITCH);
			output.accept(ModBlocks.WIRE_LAMP);
			output.accept(ModBlocks.KEYCARD_READER);
			output.accept(ModBlocks.CAMERA);
		});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> {
			output.accept(ModItems.CABLE_TOOL);
			output.accept(ModItems.KEYCARD_1);
			output.accept(ModItems.KEYCARD_2);
			output.accept(ModItems.KEYCARD_3);
		});
	}
}
