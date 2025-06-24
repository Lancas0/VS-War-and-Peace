package com.lancas.vswap.content.block.blocks.cartridge.propellant;


import com.lancas.vswap.content.WapBlocks;
import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.content.info.block.WapBlockInfos;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.PropertyAdder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.List;


public class ShelledPropellant extends BlockPlus implements IPropellant {
    //public static final BooleanProperty EMPTY = BooleanProperty.create("empty");
    public static final double ENERGY = 8E4;

    public static BlockState getState(boolean empty, Direction faceTo) {
        if (empty)
            return WapBlocks.General.EMPTY_SHELL.getDefaultState().setValue(DirectionAdder.FACING, faceTo);

        return WapBlocks.Cartridge.Propellant.SHELLED_PROPELLANT.getDefaultState()
            .setValue(DirectionAdder.FACING, faceTo);
    }

    @Override
    public List<IBlockAdder> getAdders() {
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
                    new DefaultCartridgeAdder(true)//,
                    //new PropertyAdder<>(EMPTY, false)
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
    public boolean isEmpty(BlockState state) { return false; }
    @Override
    public BlockState getEmptyState(BlockState state) {
        Direction dir = state.getValue(DirectionAdder.FACING);
        return WapBlocks.General.EMPTY_SHELL.getDefaultState().setValue(DirectionAdder.FACING, dir);
        //level.setBlock(pos, EMPTY_PROPELLANT.getDefaultState().setValue(DirectionAdder.FACING, dir), Block.UPDATE_ALL);
        //level.setBlockAndUpdate(pos, state.setValue(EMPTY, true));
    }
    @Override
    public double getSPE(BlockState state) { return WapBlockInfos.StdPropellantEnergy.valueOrDefaultOf(state); }
}
