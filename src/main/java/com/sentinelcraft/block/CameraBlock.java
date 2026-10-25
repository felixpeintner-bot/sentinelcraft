package com.sentinelcraft.block;

import com.mojang.serialization.MapCodec;
import com.sentinelcraft.Msg;
import com.sentinelcraft.blockentity.WireNodeBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Ueberwachungskamera. Erkennt Spieler im Sichtfeld (14 Bloecke, ca. 70 Grad, nur mit freier Sicht).
 * Bei Alarm: Redstone-Signal, leitet im Kabelnetz Strom durch und meldet dem Besitzer eine Nachricht.
 * Shift + Rechtsklick (nur Besitzer): umschalten, ob der Besitzer selbst ignoriert wird.
 */
public class CameraBlock extends WireNodeBlock {
	public static final MapCodec<CameraBlock> CODEC = simpleCodec(CameraBlock::new);
	private static final double RANGE = 14.0;
	private static final double MIN_DOT = 0.35;
	private static final int ALERT_TICKS = 40;

	public CameraBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any()
				.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
				.setValue(BlockStateProperties.POWERED, false));
	}

	@Override
	protected MapCodec<CameraBlock> codec() {
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
		return PanelShapes.camera(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
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
		boolean alert = state.getValue(BlockStateProperties.POWERED);

		if (be.timer > 0) {
			be.timer--;
			if (be.timer == 0) {
				alert = false;
			}
		}
		if (be.cooldown > 0) {
			be.cooldown--;
		}

		if (level.getGameTime() % 10 == 0) {
			Player seen = findIntruder(level, pos, state, be);
			if (seen != null) {
				be.timer = ALERT_TICKS;
				alert = true;
				if (be.cooldown == 0) {
					be.cooldown = 200;
					notifyOwner(level, pos, be, seen);
				}
			}
		}

		if (alert != state.getValue(BlockStateProperties.POWERED)) {
			level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, alert), Block.UPDATE_ALL);
		}
	}

	private static Player findIntruder(ServerLevel level, BlockPos pos, BlockState state, WireNodeBlockEntity be) {
		Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		Vec3 forward = new Vec3(facing.getStepX(), 0, facing.getStepZ());
		Vec3 eye = Vec3.atCenterOf(pos).add(forward.scale(0.6));

		List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(RANGE));
		for (Player p : players) {
			if (p.isSpectator()) {
				continue;
			}
			if (be.setting == 1 && be.owner != null && be.owner.equals(p.getUUID())) {
				continue;
			}
			Vec3 target = p.getEyePosition();
			Vec3 dir = target.subtract(eye);
			if (dir.length() > RANGE || dir.normalize().dot(forward) < MIN_DOT) {
				continue;
			}
			BlockHitResult hit = level.clip(new ClipContext(eye, target, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, p));
			if (hit.getType() == HitResult.Type.MISS) {
				return p;
			}
		}
		return null;
	}

	private static void notifyOwner(ServerLevel level, BlockPos pos, WireNodeBlockEntity be, Player seen) {
		if (be.owner == null) {
			return;
		}
		ServerPlayer owner = level.getServer().getPlayerList().getPlayer(be.owner);
		if (owner != null) {
			Msg.chat(owner, "camera_alert", pos.getX(), pos.getY(), pos.getZ(), seen.getName());
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof WireNodeBlockEntity be) {
			if (player.isShiftKeyDown()) {
				if (be.isOwner(player) || player.isCreative()) {
					be.setting = be.setting == 1 ? 0 : 1;
					be.setChanged();
					Msg.bar(player, be.setting == 1 ? "camera_ignores_owner" : "camera_detects_all");
				} else {
					Msg.bar(player, "not_owner");
				}
			} else {
				Msg.bar(player, be.setting == 1 ? "camera_ignores_owner" : "camera_detects_all");
			}
		}
		return InteractionResult.SUCCESS;
	}
}
