package com.lancas.vswap.content.block.blocks.industry.dock;

import com.lancas.vswap.content.WapBlockEntites;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.PropertyAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.RedstoneOnOffAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.ShapeByStateAdder;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

import static com.lancas.vswap.content.block.blocks.industry.dock.Dock.*;

public class DockUnlocker extends BlockPlus implements IBE<DockBe> {
    public DockUnlocker(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() { return BlockPlus.addersIfAbsent(DockUnlocker.class, () -> List.of(
        new ShapeByStateAdder(s -> Shapes.block()),
        //new PropertyAdder<>(PINGPONG, false),
        new PropertyAdder<>(CONNECT_N, false),
        new PropertyAdder<>(CONNECT_S, false),
        new PropertyAdder<>(CONNECT_W, false),
        new PropertyAdder<>(CONNECT_E, false),

        DockBlockAdder,

        new RedstoneOnOffAdder(true) {
            @Override
            public void onPoweredOnOff(Level level, BlockPos pos, BlockState state, boolean isOn) {
                if (level.isClientSide || !isOn) return;

                if (!(level.getBlockEntity(pos) instanceof DockBe be)) {
                    EzDebug.warn("Fail to get DockBe at " + pos.toShortString());
                    return;
                }

                be.getControllerBE().unboundHoldingShip(false, false, null);
            }
        }
    )); }

    @Override
    public Class<DockBe> getBlockEntityClass() { return DockBe.class; }
    @Override
    public BlockEntityType<? extends DockBe> getBlockEntityType() { return WapBlockEntites.DOCK_BE.get(); }
}
