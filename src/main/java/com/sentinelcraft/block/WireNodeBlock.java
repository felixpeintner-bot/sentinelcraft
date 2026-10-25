package com.sentinelcraft.block;

import com.sentinelcraft.ModBlockEntities;
import com.sentinelcraft.blockentity.WireNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** Basisklasse aller Geraete, die ans unsichtbare Kabelnetz angeschlossen werden koennen. */
public abstract class WireNodeBlock extends Block implements EntityBlock {
	protected WireNodeBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	/** Stromquelle (nur der Generator, solange er brennt). */
	public boolean isSource(BlockState state) {
		return false;
	}

	/** Leitet der Block Strom weiter? Schalter nur wenn an, Leser nur wenn freigeschaltet usw. */
	public boolean conducts(BlockState state) {
		return true;
	}

	/** Wird jeden Tick auf dem Server aufgerufen. */
	public void tickNode(ServerLevel level, BlockPos pos, BlockState state, WireNodeBlockEntity be) {}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new WireNodeBlockEntity(pos, state);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide() || type != ModBlockEntities.WIRE_NODE) {
			return null;
		}
		BlockEntityTicker<WireNodeBlockEntity> ticker = WireNodeBlockEntity::serverTick;
		return (BlockEntityTicker<T>) ticker;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!level.isClientSide() && placer instanceof Player player
				&& level.getBlockEntity(pos) instanceof WireNodeBlockEntity be) {
			be.owner = player.getUUID();
			be.setChanged();
		}
	}
}
