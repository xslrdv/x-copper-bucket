package com.xslrdv.xcopperbucket.registry;

import com.xslrdv.xcopperbucket.XCopperBucket;
import com.xslrdv.xcopperbucket.item.CopperBucketItem;
import com.xslrdv.xcopperbucket.item.CopperMobBucketItem;
import com.xslrdv.xcopperbucket.item.CopperSolidBucketItem;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(XCopperBucket.MODID);

    public static final DeferredItem<Item> COPPER_BUCKET = ITEMS.registerItem("copper_bucket",
        props -> new CopperBucketItem(Fluids.EMPTY, props.stacksTo(16)));

    public static final DeferredItem<Item> COPPER_WATER_BUCKET = ITEMS.registerItem("copper_water_bucket",
        props -> new CopperBucketItem(Fluids.WATER, props.stacksTo(1).craftRemainder(COPPER_BUCKET.get())));

    public static final DeferredItem<Item> COPPER_MILK_BUCKET = ITEMS.registerItem("copper_milk_bucket",
        props -> new Item(props
            .stacksTo(1)
            .craftRemainder(COPPER_BUCKET.get())
            .component(DataComponents.CONSUMABLE, Consumables.MILK_BUCKET)
            .usingConvertsTo(COPPER_BUCKET.get())
        ));

    public static final DeferredItem<Item> COPPER_POWDER_SNOW_BUCKET = ITEMS.registerItem("copper_powder_snow_bucket",
        props -> new CopperSolidBucketItem(
            Blocks.POWDER_SNOW,
            SoundEvents.BUCKET_EMPTY_POWDER_SNOW,
            props.stacksTo(1).craftRemainder(COPPER_BUCKET.get())
        ));

    //#region Mobs
    public static final DeferredItem<Item> COPPER_COD_BUCKET = ITEMS.registerItem("copper_cod_bucket",
        props -> new CopperMobBucketItem(EntityTypes.COD, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH,
            props.stacksTo(1).craftRemainder(COPPER_BUCKET.get())));

    public static final DeferredItem<Item> COPPER_SALMON_BUCKET = ITEMS.registerItem("copper_salmon_bucket",
        props -> new CopperMobBucketItem(EntityTypes.SALMON, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH,
            props.stacksTo(1).craftRemainder(COPPER_BUCKET.get())));

    public static final DeferredItem<Item> COPPER_TROPICAL_FISH_BUCKET = ITEMS.registerItem("copper_tropical_fish_bucket",
        props -> new CopperMobBucketItem(EntityTypes.TROPICAL_FISH, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH,
            props.stacksTo(1).craftRemainder(COPPER_BUCKET.get())));

    public static final DeferredItem<Item> COPPER_PUFFERFISH_BUCKET = ITEMS.registerItem("copper_pufferfish_bucket",
        props -> new CopperMobBucketItem(EntityTypes.PUFFERFISH, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH,
            props.stacksTo(1).craftRemainder(COPPER_BUCKET.get())));

    public static final DeferredItem<Item> COPPER_AXOLOTL_BUCKET = ITEMS.registerItem("copper_axolotl_bucket",
        props -> new CopperMobBucketItem(EntityTypes.AXOLOTL, Fluids.WATER, SoundEvents.BUCKET_EMPTY_AXOLOTL,
            props.stacksTo(1).craftRemainder(COPPER_BUCKET.get())));

    public static final DeferredItem<Item> COPPER_TADPOLE_BUCKET = ITEMS.registerItem("copper_tadpole_bucket",
        props -> new CopperMobBucketItem(EntityTypes.TADPOLE, Fluids.WATER, SoundEvents.BUCKET_EMPTY_TADPOLE,
            props.stacksTo(1).craftRemainder(COPPER_BUCKET.get())));

    public static final DeferredItem<Item> COPPER_SULFUR_CUBE_BUCKET = ITEMS.registerItem("copper_sulfur_cube_bucket",
        props -> new CopperMobBucketItem(EntityTypes.SULFUR_CUBE, Fluids.EMPTY, SoundEvents.BUCKET_EMPTY_SULFUR_CUBE,
            props.stacksTo(1).craftRemainder(COPPER_BUCKET.get())));
    //#endregion

    public static ItemStack convertVanillaToCopperBucket(ItemStack vanillaStack) {
        if (vanillaStack.is(Items.WATER_BUCKET)) {
            return COPPER_WATER_BUCKET.get().getDefaultInstance();
        }
        if (vanillaStack.is(Items.POWDER_SNOW_BUCKET)) {
            return COPPER_POWDER_SNOW_BUCKET.get().getDefaultInstance();
        }
        return vanillaStack;
    }

    public static ItemStack convertVanillaMobBucketToCopper(ItemStack vanillaMobStack) {
        Item targetItem = null;
        if (vanillaMobStack.is(Items.COD_BUCKET)) targetItem = COPPER_COD_BUCKET.get();
        else if (vanillaMobStack.is(Items.SALMON_BUCKET)) targetItem = COPPER_SALMON_BUCKET.get();
        else if (vanillaMobStack.is(Items.TROPICAL_FISH_BUCKET)) targetItem = COPPER_TROPICAL_FISH_BUCKET.get();
        else if (vanillaMobStack.is(Items.PUFFERFISH_BUCKET)) targetItem = COPPER_PUFFERFISH_BUCKET.get();
        else if (vanillaMobStack.is(Items.AXOLOTL_BUCKET)) targetItem = COPPER_AXOLOTL_BUCKET.get();
        else if (vanillaMobStack.is(Items.TADPOLE_BUCKET)) targetItem = COPPER_TADPOLE_BUCKET.get();
        else if (vanillaMobStack.is(Items.SULFUR_CUBE_BUCKET)) targetItem = COPPER_SULFUR_CUBE_BUCKET.get();

        if (targetItem != null) {
            ItemStack copperStack = new ItemStack(targetItem);
            copperStack.applyComponents(vanillaMobStack.getComponents());
            return copperStack;
        }
        return vanillaMobStack;
    }
	
	public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
