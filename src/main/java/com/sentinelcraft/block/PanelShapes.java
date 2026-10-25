package com.sentinelcraft.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Hitboxen fuer Wand-Geraete. "facing" ist die Seite, zu der die Vorderseite zeigt. */
public final class PanelShapes {
	private PanelShapes() {}

	private static final VoxelShape P_NORTH = Block.box(3, 3, 13, 13, 13, 16);
	private static final VoxelShape P_SOUTH = Block.box(3, 3, 0, 13, 13, 3);
	private static final VoxelShape P_EAST = Block.box(0, 3, 3, 3, 13, 13);
	private static final VoxelShape P_WEST = Block.box(13, 3, 3, 16, 13, 13);

	private static final VoxelShape C_NORTH = Block.box(5, 5, 8, 11, 11, 16);
	private static final VoxelShape C_SOUTH = Block.box(5, 5, 0, 11, 11, 8);
	private static final VoxelShape C_EAST = Block.box(0, 5, 5, 8, 11, 11);
	private static final VoxelShape C_WEST = Block.box(8, 5, 5, 16, 11, 11);

	public static VoxelShape panel(Direction facing) {
		return switch (facing) {
			case SOUTH -> P_SOUTH;
			case EAST -> P_EAST;
			case WEST -> P_WEST;
			default -> P_NORTH;
		};
	}

	public static VoxelShape camera(Direction facing) {
		return switch (facing) {
			case SOUTH -> C_SOUTH;
			case EAST -> C_EAST;
			case WEST -> C_WEST;
			default -> C_NORTH;
		};
	}
}
