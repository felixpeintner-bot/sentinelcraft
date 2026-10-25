package com.sentinelcraft;

import com.sentinelcraft.item.CableToolItem;
import com.sentinelcraft.item.KeycardItem;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class ModItems {
	private ModItems() {}

	public static final Item CABLE_TOOL = register("cable_tool", CableToolItem::new, new Item.Properties().stacksTo(1));
	public static final Item KEYCARD_1 = register("keycard_1", p -> new KeycardItem(p, 1), new Item.Properties().stacksTo(16));
	public static final Item KEYCARD_2 = register("keycard_2", p -> new KeycardItem(p, 2), new Item.Properties().stacksTo(16));
	public static final Item KEYCARD_3 = register("keycard_3", p -> new KeycardItem(p, 3), new Item.Properties().stacksTo(16));

	private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, SentinelCraft.id(name));
		Item item = factory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	public static void init() {}
}
