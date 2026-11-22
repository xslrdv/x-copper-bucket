package com.xslrdv.xcopperbucket.registry;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class ModCreativeTabs {
    private static void addToTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.COPPER_BUCKET);
            event.accept(ModItems.COPPER_WATER_BUCKET);
            event.accept(ModItems.COPPER_MILK_BUCKET);
            event.accept(ModItems.COPPER_POWDER_SNOW_BUCKET);
            event.accept(ModItems.COPPER_COD_BUCKET);
            event.accept(ModItems.COPPER_SALMON_BUCKET);
            event.accept(ModItems.COPPER_TROPICAL_FISH_BUCKET);
            event.accept(ModItems.COPPER_PUFFERFISH_BUCKET);
            event.accept(ModItems.COPPER_AXOLOTL_BUCKET);
            event.accept(ModItems.COPPER_TADPOLE_BUCKET);
            event.accept(ModItems.COPPER_SULFUR_CUBE_BUCKET);
        }
    }
	
	public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModCreativeTabs::addToTabs);
    }
}
