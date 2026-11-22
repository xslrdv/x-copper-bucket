package com.xslrdv.xcopperbucket.event;

import com.xslrdv.xcopperbucket.XCopperBucket;
import com.xslrdv.xcopperbucket.registry.ModItems;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Bucketable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = XCopperBucket.MODID)
public class CopperBucketEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        ItemStack heldStack = event.getItemStack();
        Entity target = event.getTarget();

        if (!(target instanceof LivingEntity livingTarget) || !livingTarget.isAlive()) {
            return;
        }

        // Water creatures - pick up with filled water bucket
        if (heldStack.is(ModItems.COPPER_WATER_BUCKET.get()) && target instanceof Bucketable bucketable) {
            ItemStack filledCopperBucket = getCopperBucketForEntity(target);
            if (!filledCopperBucket.isEmpty()) {
                pickupMob(event, player, heldStack, livingTarget, bucketable, filledCopperBucket);
            }
        }

        // Empty copper bucket interactions
        if (heldStack.is(ModItems.COPPER_BUCKET.get())) {
            // Milk adult cows, mooshrooms, and goats
            if (!livingTarget.isBaby() && isMilkableAnimal(livingTarget)) {
                milkAnimal(event, player, heldStack, livingTarget);
                return;
            }

            // For Sulfur Cube - pick up only with empty bucket
            if (target instanceof Bucketable bucketable) {
                ItemStack sulfurBucket = getDryCopperBucketForEntity(target);
                if (!sulfurBucket.isEmpty()) {
                    pickupMob(event, player, heldStack, livingTarget, bucketable, sulfurBucket);
                    return;
                }
            }
        }
    }

    private static void pickupMob(
            PlayerInteractEvent.EntityInteract event,
            Player player,
            ItemStack heldStack,
            LivingEntity target,
            Bucketable bucketable,
            ItemStack resultBucket
    ) {
        if (!player.level().isClientSide()) {
            target.playSound(bucketable.getPickupSound(), 1.0F, 1.0F);
            bucketable.saveToBucketTag(resultBucket);

            ItemStack handResult = ItemUtils.createFilledResult(heldStack, player, resultBucket, false);
            player.setItemInHand(event.getHand(), handResult);

            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, resultBucket);
            }

            target.discard();
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static ItemStack getCopperBucketForEntity(Entity entity) {
        ItemStack vanillaBucket = entity instanceof Bucketable b ? b.getBucketItemStack() : ItemStack.EMPTY;
        if (vanillaBucket.isEmpty()) {
            return ItemStack.EMPTY;
        }
        // Use convert item
        return ModItems.convertVanillaMobBucketToCopper(vanillaBucket);
    }
    
    private static boolean isMilkableAnimal(LivingEntity entity) {
        var type = entity.getType();
        return type == EntityTypes.COW
            || type == EntityTypes.MOOSHROOM
            || type == EntityTypes.GOAT;
    }

    private static void milkAnimal(
            PlayerInteractEvent.EntityInteract event,
            Player player,
            ItemStack heldStack,
            LivingEntity animal
    ) {
        if (!player.level().isClientSide()) {
            SoundEvent milkSound = SoundEvents.COW_MILK;
            if (animal instanceof Goat goat) {
                milkSound = goat.isScreamingGoat()
                        ? SoundEvents.GOAT_SCREAMING_MILK
                        : SoundEvents.GOAT_MILK;
            }

            player.playSound(milkSound, 1.0F, 1.0F);

            ItemStack milkBucket = new ItemStack(ModItems.COPPER_MILK_BUCKET.get());
            ItemStack handResult = ItemUtils.createFilledResult(heldStack, player, milkBucket);
            player.setItemInHand(event.getHand(), handResult);
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static ItemStack getDryCopperBucketForEntity(Entity entity) {
        ItemStack vanillaBucket = entity instanceof Bucketable b ? b.getBucketItemStack() : ItemStack.EMPTY;
        if (vanillaBucket.is(net.minecraft.world.item.Items.SULFUR_CUBE_BUCKET)) {
            return new ItemStack(ModItems.COPPER_SULFUR_CUBE_BUCKET.get());
        }
        return ItemStack.EMPTY;
    }
}
