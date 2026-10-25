package com.sentinelcraft.item;

import com.sentinelcraft.ModItems;
import com.sentinelcraft.Msg;
import com.sentinelcraft.block.WireNodeBlock;
import com.sentinelcraft.blockentity.WireNodeBlockEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Kabel legen: Kabel-Werkzeug auf Geraet A klicken, dann auf Geraet B. Gleiche zwei nochmal = Kabel entfernen.
 * Das Kabel ist unsichtbar; mit dem Werkzeug in der Hand sieht man es als Partikel-Linie.
 */
public final class CableToolHandler {
	public static final int MAX_DISTANCE = 24;

	private record Selection(ResourceKey<Level> dimension, BlockPos pos) {}

	private static final Map<UUID, Selection> SELECTIONS = new HashMap<>();

	private CableToolHandler() {}

	public static void register() {
		UseBlockCallback.EVENT.register(CableToolHandler::onUseBlock);
	}

	public static boolean holdsTool(Player player) {
		return player.getMainHandItem().is(ModItems.CABLE_TOOL) || player.getOffhandItem().is(ModItems.CABLE_TOOL);
	}

	/** @return true, wenn eine Auswahl existierte. */
	public static boolean clear(Player player) {
		return SELECTIONS.remove(player.getUUID()) != null;
	}

	private static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		if (player.isSpectator() || !player.getItemInHand(hand).is(ModItems.CABLE_TOOL)) {
			return InteractionResult.PASS;
		}
		BlockPos pos = hit.getBlockPos();
		if (!(level.getBlockState(pos).getBlock() instanceof WireNodeBlock)) {
			return InteractionResult.PASS;
		}
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		Selection first = SELECTIONS.get(player.getUUID());

		// Erster Klick (oder Auswahl aus anderer Dimension)
		if (first == null || !first.dimension().equals(level.dimension())) {
			SELECTIONS.put(player.getUUID(), new Selection(level.dimension(), pos.immutable()));
			Msg.bar(player, "cable_first");
			return InteractionResult.SUCCESS;
		}

		// Gleiches Geraet nochmal: Auswahl abbrechen
		if (first.pos().equals(pos)) {
			SELECTIONS.remove(player.getUUID());
			Msg.bar(player, "cable_cancel");
			return InteractionResult.SUCCESS;
		}

		if (first.pos().distSqr(pos) > (double) MAX_DISTANCE * MAX_DISTANCE) {
			Msg.bar(player, "cable_far", MAX_DISTANCE);
			return InteractionResult.SUCCESS;
		}

		if (!(level.getBlockEntity(first.pos()) instanceof WireNodeBlockEntity a)
				|| !(level.getBlockEntity(pos) instanceof WireNodeBlockEntity b)) {
			SELECTIONS.remove(player.getUUID());
			Msg.bar(player, "cable_lost");
			return InteractionResult.SUCCESS;
		}

		if (a.hasLink(pos)) {
			a.removeLink(pos);
			b.removeLink(first.pos());
			Msg.bar(player, "cable_unlinked");
		} else {
			if (a.linkCount() >= WireNodeBlockEntity.MAX_LINKS || b.linkCount() >= WireNodeBlockEntity.MAX_LINKS) {
				Msg.bar(player, "cable_full", WireNodeBlockEntity.MAX_LINKS);
				return InteractionResult.SUCCESS;
			}
			a.addLink(pos);
			b.addLink(first.pos());
			Msg.bar(player, "cable_linked");
		}
		SELECTIONS.remove(player.getUUID());
		return InteractionResult.SUCCESS;
	}
}
