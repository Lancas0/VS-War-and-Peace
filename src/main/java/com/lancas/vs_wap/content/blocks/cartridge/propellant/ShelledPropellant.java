package com.lancas.vs_wap.content.blocks.cartridge.propellant;


import com.lancas.vs_wap.content.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.content.info.block.WapBlockInfos;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.PropertyAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.List;

import static com.lancas.vs_wap.content.WapBlocks.Cartridge.Propellant.Empty.EMPTY_PROPELLANT;

public class ShelledPropellant extends BlockPlus implements IPropellant {
    public static final BooleanProperty EMPTY = BooleanProperty.create("empty");
    public static final double ENERGY = 8E4;

    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            ShelledPropellant.class,
            () -> {
                /*var entry = EinherjarBlocks.Cartridge.Propellant.SHELLED_PROPELLANT;

                LootPool drop = LootPool.lootPool()
                    .name("cased_propellant_triggered_pool")
                    .setRolls(ConstantValue.exactly(1)) // 固定掉落1次
                    .add(
                        // 当Empty为true时
                        //todo first create in there rather than in registry
                        LootItem.lootTableItem(BlockItemPlus.getOrCreateFrom(this, null))
                            .when(
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(this)
                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(EMPTY, true))
                            )
                            .apply(SetNbtFunction.setTag(
                                // 设置NBT：{"triggered": 1b}
                                new CompoundTag() {{ putBoolean("empty", true); }}
                            ))
                    )
                    .add(
                        // 当Empty为false时
                        LootItem.lootTableItem(BlockItemPlus.getOrCreateFrom(this, null))
                            .when(
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(this)
                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(EMPTY, false))
                            )
                            .apply(SetNbtFunction.setTag(
                                // 设置NBT：{"triggered": 1b}
                                new CompoundTag() {{ putBoolean("empty", false); }}
                            ))
                    )
                    .build();*/

                return List.of(
                    new DefaultCartridgeAdder(),
                    new PropertyAdder<>(EMPTY, false)
                    //,
                    //new BlockDropAdder(entry, 0, true, false, List.of(drop))
                );
            }
        );
    }

    public ShelledPropellant(Properties p_49795_) {
        super(p_49795_);
    }

    /*
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) return;
        ServerShip onShip = ShipUtil.getShipAt((ServerLevel)level, pos);
        if (onShip == null) return;

        boolean hasSignal = level.hasNeighborSignal(pos);
        Direction dir = state.getValue(FACING);
        if (hasSignal) {
            ShipBuilder splitShipBuilder = DirectionalSplitHandler.trySplit((ServerLevel)level, pos, dir);
            if (splitShipBuilder != null) {
                Vector3d velocity = onShip.getTransform().getShipToWorldRotation().transform(JomlUtil.dNormal(dir)).mul(20);
                splitShipBuilder.setWorldVelocity(velocity);
                //EzDebug.Log("vel:" + splitShipBuilder.get().getVelocity());
            }
        }
    }*/

    @Override
    public boolean isEmpty(BlockState state) { return state.getValue(EMPTY); }
    @Override
    public void setAsEmpty(ServerLevel level, BlockPos pos, BlockState state) {
        Direction dir = state.getValue(DirectionAdder.FACING);
        level.setBlock(pos, EMPTY_PROPELLANT.getDefaultState().setValue(DirectionAdder.FACING, dir), Block.UPDATE_ALL);
        //level.setBlockAndUpdate(pos, state.setValue(EMPTY, true));
    }
    @Override
    public double getPower(BlockState state) { return WapBlockInfos.propellant_power.valueOrDefaultOf(state); }
}
