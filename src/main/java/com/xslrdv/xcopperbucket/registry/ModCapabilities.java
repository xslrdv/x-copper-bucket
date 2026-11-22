package com.xslrdv.xcopperbucket.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModCapabilities {

    public static void register(IEventBus bus) {
        bus.addListener(ModCapabilities::commonSetup);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            registerCauldronInteractions();
            registerDispenserBehaviors();
        });
    }

    private static void registerCauldronInteractions() {
        //  Put fluids from copper bucket to empty cauldron
        CauldronInteractions.EMPTY.put(
            ModItems.COPPER_WATER_BUCKET.get(),
            (state, level, pos, player, hand, stack) -> CauldronInteractions.emptyBucket(
                level, pos, player, hand, stack,
                Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3),
                SoundEvents.BUCKET_EMPTY
            )
        );
        CauldronInteractions.EMPTY.put(
            ModItems.COPPER_POWDER_SNOW_BUCKET.get(),
            (state, level, pos, player, hand, stack) -> CauldronInteractions.emptyBucket(
                level, pos, player, hand, stack,
                Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3),
                SoundEvents.BUCKET_EMPTY_POWDER_SNOW
            )
        );

        // Get fluids from cauldron to copper bucket
        CauldronInteractions.WATER.put(
            ModItems.COPPER_BUCKET.get(),
            (state, level, pos, player, hand, stack) -> CauldronInteractions.fillBucket(
                state, level, pos, player, hand, stack,
                ModItems.COPPER_WATER_BUCKET.get().getDefaultInstance(),
                s -> s.getValue(LayeredCauldronBlock.LEVEL) == 3,
                SoundEvents.BUCKET_FILL
            )
        );
        CauldronInteractions.POWDER_SNOW.put(
            ModItems.COPPER_BUCKET.get(),
            (state, level, pos, player, hand, stack) -> CauldronInteractions.fillBucket(
                state, level, pos, player, hand, stack,
                ModItems.COPPER_POWDER_SNOW_BUCKET.get().getDefaultInstance(),
                s -> s.getValue(LayeredCauldronBlock.LEVEL) == 3,
                SoundEvents.BUCKET_FILL_POWDER_SNOW
            )
        );
    }

    private static void registerDispenserBehaviors() {
        DefaultDispenseItemBehavior emptyToCopperBucketBehavior = new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispense = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(BlockSource source, ItemStack stack) {
                DispensibleContainerItem containerItem = (DispensibleContainerItem) stack.getItem();
                BlockPos targetPos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                Level level = source.level();

                if (containerItem.emptyContents(null, level, targetPos, null, stack)) {
                    containerItem.checkExtraContent(null, level, stack, targetPos);
                    return this.consumeWithRemainder(source, stack, new ItemStack(ModItems.COPPER_BUCKET.get()));
                }
                return this.defaultDispense.dispense(source, stack);
            }
        };

        DispenserBlock.registerBehavior(ModItems.COPPER_WATER_BUCKET.get(), emptyToCopperBucketBehavior);
        DispenserBlock.registerBehavior(ModItems.COPPER_POWDER_SNOW_BUCKET.get(), emptyToCopperBucketBehavior);
        DispenserBlock.registerBehavior(ModItems.COPPER_COD_BUCKET.get(), emptyToCopperBucketBehavior);
        DispenserBlock.registerBehavior(ModItems.COPPER_SALMON_BUCKET.get(), emptyToCopperBucketBehavior);
        DispenserBlock.registerBehavior(ModItems.COPPER_TROPICAL_FISH_BUCKET.get(), emptyToCopperBucketBehavior);
        DispenserBlock.registerBehavior(ModItems.COPPER_PUFFERFISH_BUCKET.get(), emptyToCopperBucketBehavior);
        DispenserBlock.registerBehavior(ModItems.COPPER_AXOLOTL_BUCKET.get(), emptyToCopperBucketBehavior);
        DispenserBlock.registerBehavior(ModItems.COPPER_TADPOLE_BUCKET.get(), emptyToCopperBucketBehavior);
        DispenserBlock.registerBehavior(ModItems.COPPER_SULFUR_CUBE_BUCKET.get(), emptyToCopperBucketBehavior);
    }
}
