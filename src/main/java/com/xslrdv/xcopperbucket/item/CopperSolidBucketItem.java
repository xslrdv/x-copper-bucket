package com.xslrdv.xcopperbucket.item;

import com.xslrdv.xcopperbucket.registry.ModItems;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

public class CopperSolidBucketItem extends SolidBucketItem {

    public CopperSolidBucketItem(Block block, SoundEvent placeSound, Properties properties) {
        super(block, placeSound, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult result = super.useOn(context);
        Player player = context.getPlayer();

        if (result.consumesAction() && player != null && !player.hasInfiniteMaterials()) {
            InteractionHand hand = context.getHand();
            player.setItemInHand(hand, ModItems.COPPER_BUCKET.get().getDefaultInstance());
        }

        return result;
    }
}
