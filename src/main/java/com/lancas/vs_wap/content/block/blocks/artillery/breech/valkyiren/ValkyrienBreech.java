package com.lancas.vs_wap.content.block.blocks.artillery.breech.valkyiren;

import com.lancas.vs_wap.content.block.blockentity.ValkyrienBreechBE;
import com.lancas.vs_wap.content.block.blocks.artillery.breech.IBreech;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.InteractableBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.register.BlockInteractEvent;
import com.lancas.vs_wap.util.ShapeBuilder;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

import static com.lancas.vs_wap.content.WapBlockEntites.VALKYRIEN_BREECH_BE;

public class ValkyrienBreech extends BlockPlus implements IBE<ValkyrienBreechBE> /*extends BlockPlus implements IBreech, IBE<>*/ {
    public ValkyrienBreech(Properties p_49795_) { super(p_49795_); }

    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(ValkyrienBreech.class, () -> List.of(
            new DirectionAdder(false, true, ShapeBuilder.ofCubicRing(0, 0, 0, 2, 16).get()),
            new InteractableBlockAdder() {
                @Override
                public InteractionResult onInteracted(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    if (level.isClientSide)
                        return InteractionResult.SUCCESS;
                    withBlockEntityDo(level, pos,
                        be -> NetworkHooks.openScreen((ServerPlayer) player, be, be::sendToMenu));
                    return InteractionResult.SUCCESS;
                }
            }
        ));
    }

    @Override
    public Class<ValkyrienBreechBE> getBlockEntityClass() { return ValkyrienBreechBE.class; }
    @Override
    public BlockEntityType<? extends ValkyrienBreechBE> getBlockEntityType() { return VALKYRIEN_BREECH_BE.get(); }



}
