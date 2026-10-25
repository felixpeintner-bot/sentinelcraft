package com.sentinelcraft.block;

import com.mojang.serialization.MapCodec;
import com.sentinelcraft.blockentity.WireNodeBlockEntity;
import com.sentinelcraft.network.WireNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/** Lampe: leuchtet bei Strom aus dem Kabelnetz ODER bei normalem Redstone-Signal. */
public class WireLampBlock extends WireNodeBlock {
	public static final MapCodec<WireLampBlock> CODEC = simpleCodec(WireLampBlock::new);

	public WireLampBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.LIT, false));
	}

	@Override
	protected MapCodec<WireLampBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.LIT);
	}

	@Override
	public void tickNode(ServerLevel level, BlockPos pos, BlockState state, WireNodeBlockEntity be) {
		if (level.getGameTime() % 5 != 0) {
			return;
		}
		boolean on = WireNetwork.isPowered(level, pos) || level.hasNeighborSignal(pos);
		if (on != state.getValue(BlockStateProperties.LIT)) {
			level.setBlock(pos, state.setValue(BlockStateProperties.LIT, on), Block.UPDATE_ALL);
		}
	}
}
