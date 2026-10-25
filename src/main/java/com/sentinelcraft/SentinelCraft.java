package com.sentinelcraft;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class SentinelCraft implements ModInitializer {
	public static final String MOD_ID = "sentinelcraft";

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		// Reihenfolge ist wichtig: Bloecke -> Items -> BlockEntity-Typ
		ModBlocks.init();
		ModItems.init();
		ModBlockEntities.init();
		ModCreativeTabs.init();
		com.sentinelcraft.item.CableToolHandler.register();
	}
}
