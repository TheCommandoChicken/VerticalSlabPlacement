package com.verticalslabplacement.mixin;

import com.verticalslabplacement.util.MathUtils;
import com.verticalslabplacement.util.ModAxis;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlabBlock.class)
public class SlabBlockMixin extends net.minecraft.block.Block implements net.minecraft.block.Waterloggable {
	@Final
	@Shadow
	public static EnumProperty<SlabType> TYPE;
	@Shadow @Final public static BooleanProperty WATERLOGGED;
	@Shadow @Final protected static VoxelShape TOP_SHAPE;
	@Shadow @Final protected static VoxelShape BOTTOM_SHAPE;
	@Mutable
	@Unique
	private static final EnumProperty<ModAxis> AXIS;

	public SlabBlockMixin(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState().with(TYPE, SlabType.BOTTOM).with(AXIS, ModAxis.Y)).with(WATERLOGGED, false));
	}

	@ModifyArg(method = "appendProperties", at = @At(value = "INVOKE", target = "Lnet/minecraft/state/StateManager$Builder;add([Lnet/minecraft/state/property/Property;)Lnet/minecraft/state/StateManager$Builder;"))
	private Property[] addAxis(Property<?>[] properties) {
		return new Property[]{TYPE, WATERLOGGED, AXIS};
	}

	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		BlockState blockState = ctx.getWorld().getBlockState(blockPos);
		if (blockState.isOf(this)) {
			return blockState.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false);
		} else {
			FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
			BlockState blockState2 = this.stateManager.getDefaultState().with(TYPE, SlabType.BOTTOM).with(AXIS, ModAxis.Y).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
			Direction direction = ctx.getSide();
			double lowCentDist = 0.3;
			double highCentDist = 0.7;
			double xPos = ctx.getHitPos().getX() - (double)blockPos.getX();
			double yPos = ctx.getHitPos().getY() - (double)blockPos.getY();
			double zPos = ctx.getHitPos().getZ() - (double)blockPos.getZ();

			switch (direction) {
                case DOWN -> {
					if (MathUtils.isBetweenValues(xPos, lowCentDist, highCentDist) && MathUtils.isBetweenValues(zPos, lowCentDist, highCentDist)) {
						return blockState2.with(TYPE, SlabType.TOP).with(AXIS, ModAxis.Y);
					}
					return (Math.abs(xPos - 0.5) >= Math.abs(zPos - 0.5)) ? blockState2.with(TYPE, (xPos < lowCentDist) ? SlabType.BOTTOM : SlabType.TOP).with(AXIS, ModAxis.X) : blockState2.with(TYPE, (zPos < lowCentDist) ? SlabType.BOTTOM : SlabType.TOP).with(AXIS, ModAxis.Z);
                }
                case UP -> {
					if (MathUtils.isBetweenValues(xPos, lowCentDist, highCentDist) && MathUtils.isBetweenValues(zPos, lowCentDist, highCentDist)) {
						return blockState2.with(TYPE, SlabType.BOTTOM).with(AXIS, ModAxis.Y);
					}
					return (Math.abs(xPos - 0.5) >= Math.abs(zPos - 0.5)) ? blockState2.with(TYPE, (xPos < lowCentDist) ? SlabType.BOTTOM : SlabType.TOP).with(AXIS, ModAxis.X) : blockState2.with(TYPE, (zPos < lowCentDist) ? SlabType.BOTTOM : SlabType.TOP).with(AXIS, ModAxis.Z);
				}
                case NORTH -> {
					if (MathUtils.isBetweenValues(xPos, lowCentDist, highCentDist) && MathUtils.isBetweenValues(yPos, lowCentDist, highCentDist)) {
						return blockState2.with(TYPE, SlabType.TOP).with(AXIS, ModAxis.Z);
					}
					return (Math.abs(xPos - 0.5) >= Math.abs(yPos - 0.5)) ? blockState2.with(TYPE, (xPos < lowCentDist) ? SlabType.BOTTOM : SlabType.TOP).with(AXIS, ModAxis.X) : blockState2.with(TYPE, (yPos < lowCentDist) ? SlabType.BOTTOM : SlabType.TOP).with(AXIS, ModAxis.Y);
				}
                case SOUTH -> {
					if (MathUtils.isBetweenValues(xPos, lowCentDist, highCentDist) && MathUtils.isBetweenValues(yPos, lowCentDist, highCentDist)) {
						return blockState2.with(TYPE, SlabType.BOTTOM).with(AXIS, ModAxis.Z);
					}
					return (Math.abs(xPos - 0.5) >= Math.abs(yPos - 0.5)) ? blockState2.with(TYPE, (xPos < lowCentDist) ? SlabType.BOTTOM : SlabType.TOP).with(AXIS, ModAxis.X) : blockState2.with(TYPE, (yPos < lowCentDist) ? SlabType.BOTTOM : SlabType.TOP).with(AXIS, ModAxis.Y);
				}
                case WEST -> {
					if (MathUtils.isBetweenValues(yPos, lowCentDist, highCentDist) && MathUtils.isBetweenValues(zPos, lowCentDist, highCentDist)) {
						return blockState2.with(TYPE, SlabType.TOP).with(AXIS, ModAxis.X);
					}
					return (Math.abs(yPos - 0.5) >= Math.abs(zPos - 0.5)) ? blockState2.with(TYPE, (yPos < lowCentDist) ? SlabType.BOTTOM : SlabType.TOP).with(AXIS, ModAxis.Y) : blockState2.with(TYPE, (zPos < lowCentDist) ? SlabType.BOTTOM : SlabType.TOP).with(AXIS, ModAxis.Z);
				}
                case EAST -> {
					if (MathUtils.isBetweenValues(yPos, lowCentDist, highCentDist) && MathUtils.isBetweenValues(zPos, lowCentDist, highCentDist)) {
						return blockState2.with(TYPE, SlabType.BOTTOM).with(AXIS, ModAxis.X);
					}
					return (Math.abs(yPos - 0.5) >= Math.abs(zPos - 0.5)) ? blockState2.with(TYPE, (yPos < lowCentDist) ? SlabType.BOTTOM : SlabType.TOP).with(AXIS, ModAxis.Y) : blockState2.with(TYPE, (zPos < lowCentDist) ? SlabType.BOTTOM : SlabType.TOP).with(AXIS, ModAxis.Z);
				}
                default -> {
                    return null;
                }
            }
		}
	}

	@Inject(method = "canReplace", at = @At("HEAD"), cancellable = true)
	protected void canReplaceVertical(BlockState state, ItemPlacementContext context, CallbackInfoReturnable<Boolean> cir) {
		ItemStack itemStack = context.getStack();
		SlabType slabType = state.get(TYPE);
		ModAxis axis = state.get(AXIS);
		if (slabType != SlabType.DOUBLE && itemStack.isOf(this.asItem())) {
			if (context.canReplaceExisting()) {
				if (axis == ModAxis.Y) {
					boolean bl = context.getHitPos().y - (double) context.getBlockPos().getY() > 0.5;
					Direction direction = context.getSide();
					if (slabType == SlabType.BOTTOM) {
						cir.setReturnValue(direction == Direction.UP || bl && direction.getAxis().isHorizontal());
					} else {
						cir.setReturnValue(direction == Direction.DOWN || !bl && direction.getAxis().isHorizontal());
					}
				} else if (axis == ModAxis.X) {
					boolean bl = context.getHitPos().x - (double) context.getBlockPos().getX() > 0.5;
					Direction direction = context.getSide();
					if (slabType == SlabType.BOTTOM) {
						cir.setReturnValue(direction == Direction.EAST || bl && direction.getAxis().isVertical());
					} else {
						cir.setReturnValue(direction == Direction.WEST || !bl && direction.getAxis().isVertical());
					}
				} else {
					boolean bl = context.getHitPos().z - (double) context.getBlockPos().getZ() > 0.5;
					Direction direction = context.getSide();
					if (slabType == SlabType.BOTTOM) {
						cir.setReturnValue(direction == Direction.SOUTH || bl && direction.getAxis().isVertical());
					} else {
						cir.setReturnValue(direction == Direction.NORTH || !bl && direction.getAxis().isVertical());
					}
				}
			} else {
				cir.setReturnValue(true);
			}
		} else {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
	protected void getExtendedOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
		ModAxis axis = state.get(AXIS);
		SlabType type = state.get(TYPE);
		switch (type) {
            case BOTTOM -> {
                switch (axis) {
                    case Z -> cir.setReturnValue(VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f));
					case Y -> cir.setReturnValue(BOTTOM_SHAPE);
                    case X -> cir.setReturnValue(VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f));
				}
            }
            case TOP -> {
				switch (axis) {
					case Z -> cir.setReturnValue(VoxelShapes.cuboid(0.0f, 0.0f, 0.5f, 1.0f, 1.0f, 1.0f));
					case Y -> cir.setReturnValue(TOP_SHAPE);
					case X -> cir.setReturnValue(VoxelShapes.cuboid(0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f));
				}
			}
		}
	}

	static {
		AXIS = EnumProperty.of("axis", ModAxis.class);
	}
}