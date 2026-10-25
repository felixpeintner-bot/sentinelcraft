package com.sentinelcraft;

import com.sentinelcraft.block.CameraBlock;
import com.sentinelcraft.block.GeneratorBlock;
import com.sentinelcraft.block.KeycardReaderBlock;
import com.sentinelcraft.block.LightSwitchBlock;
import com.sentinelcraft.block.WireLampBlock;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class ModBlocks {
	private ModBlocks() {}

	public static final Block GENERATOR = register("generator", GeneratorBlock::new,
			BlockBehaviour.Properties.of()
					.strength(3.5f, 6.0f)
					.sound(SoundType.METAL)
					.lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 7 : 0));

	public static final Block WIRE_LAMP = register("wire_lamp", WireLampBlock::new,
			BlockBehaviour.Properties.of()
					.strength(0.5f)
					.sound(SoundType.GLASS)
					.lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 15 : 0));

	public static final Block LIGHT_SWITCH = register("light_switch", LightSwitchBlock::new,
			BlockBehaviour.Properties.of()
					.strength(0.8f)
					.sound(SoundType.METAL)
					.noOcclusion());

	public static final Block KEYCARD_READER = register("keycard_reader", KeycardReaderBlock::new,
			BlockBehaviour.Properties.of()
					.strength(3.0f, 6.0f)
					.sound(SoundType.METAL)
					.noOcclusion());

	public static final Block CAMERA = register("camera", CameraBlock::new,
			BlockBehaviour.Properties.of()
					.strength(2.0f, 6.0f)
					.sound(SoundType.METAL)
					.noOcclusion());

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, SentinelCraft.id(name));
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, SentinelCraft.id(name));

		Block block = factory.apply(properties.setId(blockKey));
		BlockItem item = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());

		Registry.register(BuiltInRegistries.ITEM, itemKey, item);
		return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
	}

	public static void init() {}
}
