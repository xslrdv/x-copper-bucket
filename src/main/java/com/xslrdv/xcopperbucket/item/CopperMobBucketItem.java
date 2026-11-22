package com.xslrdv.xcopperbucket.item;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class CopperMobBucketItem extends MobBucketItem {

    private final Fluid bucketFluid;
    private final SoundEvent bucketEmptySound;

    public CopperMobBucketItem(EntityType<? extends Mob> entityType, Fluid fluid, SoundEvent emptySound, Properties properties) {
        super(entityType, fluid, emptySound, properties);
        this.bucketFluid = fluid;
        this.bucketEmptySound = emptySound;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (hitResult.getType() == HitResult.Type.MISS || hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }

        BlockPos blockpos = hitResult.getBlockPos();
        Direction direction = hitResult.getDirection();
        BlockPos adjacentPos = blockpos.relative(direction);

        if (!level.mayInteract(player, blockpos) || !player.mayUseItemAt(adjacentPos, direction, itemstack)) {
            return InteractionResult.FAIL;
        }

        boolean isDryBucket = this.bucketFluid == Fluids.EMPTY;
        BlockState blockstate = level.getBlockState(blockpos);
        BlockPos targetPos = (!isDryBucket && this.canBlockContainFluid(player, level, blockpos, blockstate))
                ? blockpos
                : adjacentPos;

        boolean emptied;
        if (isDryBucket) {
            // Dry bucketб play sound and trigger game event without placing fluid
            level.playSound(player, targetPos, this.bucketEmptySound, SoundSource.NEUTRAL, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.FLUID_PLACE, targetPos);
            emptied = true;
        } else {
            // Pour out water
            emptied = this.emptyContents(player, level, targetPos, hitResult, itemstack);
        }

        if (emptied) {
            // Spawn mob from copper bucket with his NBT / Data Components
            this.checkExtraContent(player, level, itemstack, targetPos);

            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, targetPos, itemstack);
            }
            player.awardStat(Stats.ITEM_USED.get(this));

            // Return empty copper bucket in survival, or full bucket in creative
            ItemStack emptyResult = CopperBucketItem.getEmptyCopperSuccessItem(itemstack, player);
            return InteractionResult.SUCCESS.heldItemTransformedTo(emptyResult);
        }

        return InteractionResult.FAIL;
    }
}