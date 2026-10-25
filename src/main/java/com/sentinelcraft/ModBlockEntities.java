package com.sentinelcraft;

import com.sentinelcraft.blockentity.WireNodeBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
	private ModBlockEntities() {}

	/** Ein gemeinsamer BlockEntity-Typ fuer alle Geraete, die am Kabelnetz haengen. */
	public static final BlockEntityType<WireNodeBlockEntity> WIRE_NODE = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE,
			SentinelCraft.id("wire_node"),
			FabricBlockEntityTypeBuilder.<WireNodeBlockEntity>create(
					WireNodeBlockEntity::new,
					ModBlocks.GENERATOR,
					ModBlocks.WIRE_LAMP,
					ModBlocks.LIGHT_SWITCH,
					ModBlocks.KEYCARD_READER,
					ModBlocks.CAMERA
			).build());

	public static void init() {}
}
