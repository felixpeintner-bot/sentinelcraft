package com.sentinelcraft.block;

import com.mojang.serialization.MapCodec;
import com.sentinelcraft.Msg;
import com.sentinelcraft.blockentity.WireNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

/** Der Generator: mit Brennstoff fuettern, dann liefert er Strom ins Kabelnetz. */
public class GeneratorBlock extends WireNodeBlock {
	public static final MapCodec<GeneratorBlock> CODEC = simpleCodec(GeneratorBlock::new);
	/** Maximal 10 Minuten Vorrat. */
	private static final int MAX_BURN_TIME = 20 * 60 * 10;

	public GeneratorBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.LIT, false));
	}

	@Override
	protected MapCodec<GeneratorBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.LIT);
	}

	@Override
	public boolean isSource(BlockState state) {
		return state.getValue(BlockStateProperties.LIT);
	}

	@Override
	public void tickNode(ServerLevel level, BlockPos pos, BlockState state, WireNodeBlockEntity be) {
		if (be.burnTime > 0) {
			be.burnTime--;
			if (be.burnTime % 20 == 0) {
				be.setChanged();
			}
		}
		boolean lit = be.burnTime > 0;
		if (lit != state.getValue(BlockStateProperties.LIT)) {
			level.setBlock(pos, state.setValue(BlockStateProperties.LIT, lit), Block.UPDATE_ALL);
		}
	}

	/** Brennzeit in Ticks, die ein Item dem Generator bringt (0 = kein Brennstoff). */
	private static int fuelTicks(ItemStack stack) {
		if (stack.is(Items.COAL) || stack.is(Items.CHARCOAL)) {
			return 20 * 80;
		}
		if (stack.is(Items.COAL_BLOCK)) {
			return 20 * 800;
		}
		if (stack.is(Items.BLAZE_ROD)) {
			return 20 * 120;
		}
		return 0;
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hitResult) {
		int add = fuelTicks(stack);
		if (add <= 0) {
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		}
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof WireNodeBlockEntity be) {
			if (be.burnTime + add > MAX_BURN_TIME) {
				Msg.bar(player, "generator_full");
			} else {
				be.burnTime += add;
				be.setChanged();
				if (!player.isCreative()) {
					stack.shrink(1);
				}
				Msg.bar(player, "generator_status", be.burnTime / 20);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof WireNodeBlockEntity be) {
			Msg.bar(player, "generator_status", be.burnTime / 20);
		}
		return InteractionResult.SUCCESS;
	}
}
