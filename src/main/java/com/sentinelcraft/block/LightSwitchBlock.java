package com.sentinelcraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Lichtschalter: schaltet im Kabelnetz den Strom durch und gibt zusaetzlich ein Redstone-Signal. */
public class LightSwitchBlock extends WireNodeBlock {
	public static final MapCodec<LightSwitchBlock> CODEC = simpleCodec(LightSwitchBlock::new);

	public LightSwitchBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any()
				.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
				.setValue(BlockStateProperties.POWERED, false));
	}

	@Override
	protected MapCodec<LightSwitchBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction clicked = context.getClickedFace();
		Direction facing = clicked.getAxis().isHorizontal() ? clicked : context.getHorizontalDirection().getOpposite();
		return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return PanelShapes.panel(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
	}

	@Override
	public boolean conducts(BlockState state) {
		return state.getValue(BlockStateProperties.POWERED);
	}

	@Override
	protected boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide()) {
			level.setBlock(pos, state.cycle(BlockStateProperties.POWERED), Block.UPDATE_ALL);
			level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.6f,
					state.getValue(BlockStateProperties.POWERED) ? 0.5f : 0.6f);
		}
		return InteractionResult.SUCCESS;
	}
}
