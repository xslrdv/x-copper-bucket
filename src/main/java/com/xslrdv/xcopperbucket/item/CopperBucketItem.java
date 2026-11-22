
package com.xslrdv.xcopperbucket.item;

import com.xslrdv.xcopperbucket.registry.ModItems;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class CopperBucketItem extends BucketItem {

    public CopperBucketItem(Fluid content, Properties properties) {
        super(content, properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Get hit result, where player to look at, if bucket empty - get fluid, if not - get block
        BlockHitResult hitResult = getPlayerPOVHitResult(
            level,
            player,
            this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE
        );

        if (hitResult.getType() == HitResult.Type.MISS || hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }

        BlockPos blockpos = hitResult.getBlockPos();
        Direction direction = hitResult.getDirection();
        BlockPos adjacentPos = blockpos.relative(direction);

        // Check usable water with world
        if (!level.mayInteract(player, blockpos) || !player.mayUseItemAt(adjacentPos, direction, itemstack)) {
            return InteractionResult.FAIL;
        }

        // Variant 1, if bucket empty - try to get fluid or powder snow
        if (this.content == Fluids.EMPTY) {
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.getBlock() instanceof BucketPickup bucketPickup) {
                // Vanilla, remove block and return bucket (WATER_BUCKET, LAVA_BUCKET или POWDER_SNOW_BUCKET)
                ItemStack vanillaFilled = bucketPickup.pickupBlock(player, level, blockpos, blockstate);

                if (!vanillaFilled.isEmpty()) {
                    player.awardStat(Stats.ITEM_USED.get(this));
                    bucketPickup.getPickupSound(blockstate).ifPresent(sound -> player.playSound(sound, 1.0F, 1.0F));
                    level.gameEvent(player, GameEvent.FLUID_PICKUP, blockpos);

                    // Swap vanilla full bucket to copper bucket
                    ItemStack copperFilled = ModItems.convertVanillaToCopperBucket(vanillaFilled);
                    ItemStack resultStack = ItemUtils.createFilledResult(itemstack, player, copperFilled);

                    if (!level.isClientSide()) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, copperFilled);
                    }

                    return InteractionResult.SUCCESS.heldItemTransformedTo(resultStack);
                }
            }
            return InteractionResult.FAIL;
        }

        // Variant 2, if bucket full - try to put fluid to ground
        BlockState blockstate = level.getBlockState(blockpos);
        // Check if can put fluid to in block
        BlockPos targetPos = canBlockContainFluid(player, level, blockpos, blockstate) ? blockpos : adjacentPos;

        if (this.emptyContents(player, level, targetPos, hitResult, itemstack)) {
            this.checkExtraContent(player, level, itemstack, targetPos);
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, targetPos, itemstack);
            }
            player.awardStat(Stats.ITEM_USED.get(this));

            // Return empty copper bucket
            ItemStack emptyResult = getEmptyCopperSuccessItem(itemstack, player);
            return InteractionResult.SUCCESS.heldItemTransformedTo(emptyResult);
        }

        return InteractionResult.FAIL;
    }

    /// Return empty copper bucket in survival, or full bucket in creative.
    public static ItemStack getEmptyCopperSuccessItem(ItemStack bucketStack, Player player) {
        return !player.hasInfiniteMaterials() ? new ItemStack(ModItems.COPPER_BUCKET.get()) : bucketStack;
    }
}
