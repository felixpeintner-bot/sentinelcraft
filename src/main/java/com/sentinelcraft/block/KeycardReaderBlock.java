package com.sentinelcraft.block;

import com.mojang.serialization.MapCodec;
import com.sentinelcraft.Msg;
import com.sentinelcraft.blockentity.WireNodeBlockEntity;
import com.sentinelcraft.item.KeycardItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

/**
 * Keycard-Leser. Keycard in die Hand nehmen und Rechtsklick: bei ausreichender Stufe gibt er
 * 3 Sekunden Redstone-Signal und leitet im Kabelnetz Strom durch.
 * Shift + Rechtsklick (nur Besitzer): benoetigte Stufe wechseln (1 -> 2 -> 3 -> 1).
 */
public class KeycardReaderBlock extends WireNodeBlock {
	public static final MapCodec<KeycardReaderBlock> CODEC = simpleCodec(KeycardReaderBlock::new);
	private static final int UNLOCK_TICKS = 60;

	public KeycardReaderBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any()
				.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
				.setValue(BlockStateProperties.POWERED, false));
	}

	@Override
	protected MapCodec<KeycardReaderBlock> codec() {
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
	public void tickNode(ServerLevel level, BlockPos pos, BlockState state, WireNodeBlockEntity be) {
		if (be.timer > 0) {
			be.timer--;
			if (be.timer == 0 && state.getValue(BlockStateProperties.POWERED)) {
				level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, false), Block.UPDATE_ALL);
			}
		}
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hitResult) {
		if (!(stack.getItem() instanceof KeycardItem card)) {
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		}
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof WireNodeBlockEntity be) {
			if (card.getCardLevel() >= be.setting) {
				be.timer = UNLOCK_TICKS;
				be.setChanged();
				level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, true), Block.UPDATE_ALL);
				level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, 1.4f);
				Msg.bar(player, "reader_granted");
			} else {
				level.playSound(null, pos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0f, 1.0f);
				Msg.bar(player, "reader_denied", be.setting);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof WireNodeBlockEntity be) {
			if (player.isShiftKeyDown()) {
				if (be.isOwner(player) || player.isCreative()) {
					be.setting = be.setting % 3 + 1;
					be.setChanged();
					Msg.bar(player, "reader_level", be.setting);
				} else {
					Msg.bar(player, "not_owner");
				}
			} else {
				Msg.bar(player, "reader_hint", be.setting);
			}
		}
		return InteractionResult.SUCCESS;
	}
}
