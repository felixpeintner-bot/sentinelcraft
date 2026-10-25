package com.sentinelcraft.blockentity;

import com.mojang.serialization.Codec;
import com.sentinelcraft.ModBlockEntities;
import com.sentinelcraft.block.WireNodeBlock;
import com.sentinelcraft.item.CableToolHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * Gemeinsamer BlockEntity fuer alle Geraete am Kabelnetz.
 * Die "unsichtbaren Kabel" sind nur Eintraege in der links-Liste (beidseitig gespeichert).
 */
public class WireNodeBlockEntity extends BlockEntity {
	public static final int MAX_LINKS = 8;

	private final List<Long> links = new ArrayList<>();

	/** Generator: verbleibende Brennzeit in Ticks. */
	public int burnTime;
	/** Leser: Freischaltzeit. Kamera: Alarmzeit. */
	public int timer;
	/** Kamera: Abklingzeit der Besitzer-Nachricht. */
	public int cooldown;
	/** Leser: benoetigte Keycard-Stufe (1-3). Kamera: 1 = Besitzer ignorieren, 0 = alle erkennen. */
	public int setting = 1;
	/** Besitzer (Spieler, der den Block gesetzt hat). */
	public UUID owner;

	public WireNodeBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.WIRE_NODE, pos, state);
	}

	// ---------- Kabel ----------

	public List<Long> getLinks() {
		return Collections.unmodifiableList(links);
	}

	public boolean hasLink(BlockPos other) {
		return links.contains(other.asLong());
	}

	public int linkCount() {
		return links.size();
	}

	public void addLink(BlockPos other) {
		long l = other.asLong();
		if (!links.contains(l)) {
			links.add(l);
			setChanged();
		}
	}

	public void removeLink(BlockPos other) {
		if (links.remove((Long) other.asLong())) {
			setChanged();
		}
	}

	public boolean isOwner(Player player) {
		return owner == null || owner.equals(player.getUUID());
	}

	// ---------- Speichern ----------

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.store("Links", Codec.LONG.listOf(), new ArrayList<>(links));
		output.putInt("BurnTime", burnTime);
		output.putInt("Timer", timer);
		output.putInt("Cooldown", cooldown);
		output.putInt("Setting", setting);
		if (owner != null) {
			output.putString("Owner", owner.toString());
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		links.clear();
		input.read("Links", Codec.LONG.listOf()).ifPresent(links::addAll);
		burnTime = input.getIntOr("BurnTime", 0);
		timer = input.getIntOr("Timer", 0);
		cooldown = input.getIntOr("Cooldown", 0);
		setting = input.getIntOr("Setting", 1);
		String o = input.getStringOr("Owner", "");
		owner = o.isEmpty() ? null : UUID.fromString(o);
	}

	// ---------- Tick ----------

	public static void serverTick(Level level, BlockPos pos, BlockState state, WireNodeBlockEntity be) {
		if (!(level instanceof ServerLevel sl)) {
			return;
		}
		if (!(state.getBlock() instanceof WireNodeBlock block)) {
			return;
		}
		long time = sl.getGameTime();

		// Verwaiste Kabel entfernen (Partner-Block wurde abgebaut)
		if (time % 20 == 0 && !be.links.isEmpty()) {
			boolean changed = be.links.removeIf(l -> {
				BlockPos p = BlockPos.of(l);
				return sl.hasChunkAt(p) && !(sl.getBlockEntity(p) instanceof WireNodeBlockEntity);
			});
			if (changed) {
				be.setChanged();
			}
		}

		// Kabel sichtbar machen, solange jemand das Kabel-Werkzeug in der Hand haelt
		if (time % 10 == 0 && !be.links.isEmpty()
				&& sl.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 16.0,
						e -> e instanceof Player pl && CableToolHandler.holdsTool(pl)) != null) {
			Vec3 a = Vec3.atCenterOf(pos);
			for (long l : be.links) {
				if (pos.asLong() >= l) {
					continue; // jede Verbindung nur von einer Seite zeichnen
				}
				Vec3 b = Vec3.atCenterOf(BlockPos.of(l));
				int steps = (int) Math.min(40, Math.max(2, a.distanceTo(b) * 2));
				for (int i = 0; i <= steps; i++) {
					Vec3 v = a.lerp(b, i / (double) steps);
					sl.sendParticles(ParticleTypes.END_ROD, v.x, v.y, v.z, 1, 0, 0, 0, 0);
				}
			}
		}

		block.tickNode(sl, pos, state, be);
	}
}
