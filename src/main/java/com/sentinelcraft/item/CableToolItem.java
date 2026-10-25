package com.sentinelcraft.item;

import com.sentinelcraft.Msg;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Das Kabel-Werkzeug. Die eigentliche Logik (zwei Geraete anklicken) steckt in {@link CableToolHandler}.
 * Shift + Rechtsklick in die Luft verwirft die aktuelle Auswahl.
 */
public class CableToolItem extends Item {
	public CableToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.isShiftKeyDown()) {
			if (!level.isClientSide() && CableToolHandler.clear(player)) {
				Msg.bar(player, "cable_cancel");
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}
