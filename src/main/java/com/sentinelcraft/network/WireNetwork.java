package com.sentinelcraft.network;

import com.sentinelcraft.block.WireNodeBlock;
import com.sentinelcraft.blockentity.WireNodeBlockEntity;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Berechnet, ob ein Geraet ueber das Kabelnetz Strom von einem laufenden Generator bekommt. */
public final class WireNetwork {
	private static final int MAX_NODES = 512;

	private WireNetwork() {}

	/**
	 * Breitensuche ueber die Kabel. Schalter, Leser und Kameras leiten nur, wenn sie gerade
	 * "an" sind (siehe {@link WireNodeBlock#conducts}).
	 */
	public static boolean isPowered(Level level, BlockPos start) {
		Set<Long> visited = new HashSet<>();
		Deque<BlockPos> queue = new ArrayDeque<>();
		visited.add(start.asLong());
		queue.add(start);

		while (!queue.isEmpty() && visited.size() < MAX_NODES) {
			BlockPos current = queue.poll();
			if (!(level.getBlockEntity(current) instanceof WireNodeBlockEntity be)) {
				continue;
			}
			for (long l : be.getLinks()) {
				if (!visited.add(l)) {
					continue;
				}
				BlockPos next = BlockPos.of(l);
				if (!level.hasChunkAt(next)) {
					continue;
				}
				BlockState nextState = level.getBlockState(next);
				if (!(nextState.getBlock() instanceof WireNodeBlock node)) {
					continue;
				}
				if (node.isSource(nextState)) {
					return true;
				}
				if (node.conducts(nextState)) {
					queue.add(next);
				}
			}
		}
		return false;
	}
}
