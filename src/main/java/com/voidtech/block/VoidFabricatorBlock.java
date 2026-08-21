package com.voidtech.block;

import com.voidtech.block.entity.VoidFabricatorBlockEntity;
import com.voidtech.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public final class VoidFabricatorBlock extends Block implements EntityBlock {
    private final int tier;

    public VoidFabricatorBlock(Properties properties, int tier) {
        super(properties);
        this.tier = Math.max(1, Math.min(6, tier));
    }

    public int getTier() {
        return tier;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VoidFabricatorBlockEntity(
                ModBlockEntities.VOID_FABRICATOR.get(), pos, state, tier);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return type == ModBlockEntities.VOID_FABRICATOR.get()
                ? (lvl, pos, blockState, be) ->
                VoidFabricatorBlockEntity.serverTick(
                        lvl, pos, blockState, (VoidFabricatorBlockEntity) be)
                : null;
    }

    @Override
    public InteractionResult use(
            BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof net.minecraft.world.MenuProvider provider) {
            NetworkHooks.openScreen(serverPlayer, provider, buf -> {
                buf.writeVarInt(tier);
                buf.writeBlockPos(pos);
            });
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
