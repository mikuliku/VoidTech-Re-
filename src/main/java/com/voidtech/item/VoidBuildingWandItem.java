package com.voidtech.item;

import com.voidtech.block.VoidMiningMachineBlock;
import com.voidtech.multiblock.VoidMiningStructure;
import com.voidtech.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class VoidBuildingWandItem extends Item {

    public VoidBuildingWandItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        BlockPos controllerPos = context.getClickedPos();

        if (player == null
                || !(level.getBlockState(controllerPos).getBlock() instanceof VoidMiningMachineBlock machine)) {
            return InteractionResult.PASS;
        }

        int tier = machine.getTier();
        int radius = VoidMiningStructure.radiusFor(tier);
        int height = VoidMiningStructure.heightFor(tier);

        boolean reinforced = tier >= 4;
        ItemStack frameStack = new ItemStack(
                reinforced ? ModBlocks.REINFORCED_VOID_FRAME.get() : ModBlocks.VOID_FRAME.get()
        );

        int required = countMissingFrames(level, controllerPos, radius, height);

        if (countItem(player, frameStack) < required) {
            player.displayClientMessage(
                    Component.translatable(
                            "message.voidtech.building_wand.not_enough",
                            required,
                            countItem(player, frameStack)
                    ),
                    true
            );
            return InteractionResult.FAIL;
        }

        int placed = 0;

        for (int y = 0; y < height; y++) {
            int layerRadius = radius - y;
            if (layerRadius < 1) {
                continue;
            }

            for (int x = -layerRadius; x <= layerRadius; x++) {
                for (int z = -layerRadius; z <= layerRadius; z++) {
                    boolean shell = Math.abs(x) == layerRadius || Math.abs(z) == layerRadius;
                    if (!shell) {
                        continue;
                    }

                    BlockPos pos = controllerPos.offset(x, y + 1, z);
                    if (isReplaceable(level, pos)) {
                        level.setBlock(
                                pos,
                                reinforced
                                        ? ModBlocks.REINFORCED_VOID_FRAME.get().defaultBlockState()
                                        : ModBlocks.VOID_FRAME.get().defaultBlockState(),
                                3
                        );
                        placed++;
                    }
                }
            }
        }

        removeItems(player, frameStack, placed);

        player.displayClientMessage(
                Component.translatable(
                        "message.voidtech.building_wand.success",
                        tier,
                        placed
                ),
                true
        );

        return InteractionResult.sidedSuccess(false);
    }

    private static int countMissingFrames(Level level, BlockPos controllerPos, int radius, int height) {
        int required = 0;

        for (int y = 0; y < height; y++) {
            int layerRadius = radius - y;
            if (layerRadius < 1) {
                continue;
            }

            for (int x = -layerRadius; x <= layerRadius; x++) {
                for (int z = -layerRadius; z <= layerRadius; z++) {
                    boolean shell = Math.abs(x) == layerRadius || Math.abs(z) == layerRadius;
                    if (!shell) {
                        continue;
                    }

                    if (isReplaceable(level, controllerPos.offset(x, y + 1, z))) {
                        required++;
                    }
                }
            }
        }

        return required;
    }

    private static boolean isReplaceable(Level level, BlockPos pos) {
        return level.getBlockState(pos).isAir() || level.getBlockState(pos).canBeReplaced();
    }

    private static int countItem(Player player, ItemStack target) {
        int count = 0;

        for (ItemStack stack : player.getInventory().items) {
            if (ItemStack.isSameItem(stack, target)) {
                count += stack.getCount();
            }
        }

        return count;
    }

    private static void removeItems(Player player, ItemStack target, int amount) {
        int remaining = amount;

        for (ItemStack stack : player.getInventory().items) {
            if (!ItemStack.isSameItem(stack, target)) {
                continue;
            }

            int removed = Math.min(remaining, stack.getCount());
            stack.shrink(removed);
            remaining -= removed;

            if (remaining <= 0) {
                return;
            }
        }
    }
}
