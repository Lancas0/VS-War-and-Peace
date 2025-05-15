package com.lancas.vs_wap.content.block.blocks.industry.projector;

import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.InteractableBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.ShapeByStateAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

//todo ignored by all Scheme
public class ProjectCenter extends BlockPlus implements IProjectBlock {
    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(ProjectCenter.class, () -> List.of(
            new ShapeByStateAdder(state -> Shapes.block()),
            new InteractableBlockAdder() {
                @Override
                public InteractionResult onInteracted(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    ItemStack holdingStack = player.getItemInHand(hand);

                    if (!(holdingStack.getItem() instanceof BlockItem blockItem))
                        return InteractionResult.PASS;

                    BlockState toPlaceState = blockItem.getBlock().getStateForPlacement(new BlockPlaceContext(player, hand, holdingStack, hit));
                    if (toPlaceState == null)
                        return InteractionResult.PASS;

                    level.setBlockAndUpdate(pos, toPlaceState);
                    return InteractionResult.CONSUME;   //todo shrink hand item 1
                }
            }
        ));
    }

    //public Properties AIR = BlockStateProperties.
    public ProjectCenter(Properties p) {
        super(p);
    }

    /*@Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(ProjectCenter.class, () -> List.of(
            new ShapeByStateAdder(state -> state.isAir() Shapes.block())
        ));
    }
    @Override
    public RenderShape getRenderShape(BlockState p_48758_) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState p_48760_, BlockGetter p_48761_, BlockPos p_48762_, CollisionContext p_48763_) {
        return Shapes.block();
    }*/

    /*@Override
    public void onRemove(BlockState p_60515_, Level p_60516_, BlockPos p_60517_, BlockState p_60518_, boolean p_60519_) {
        super.onRemove(p_60515_, p_60516_, p_60517_, p_60518_, p_60519_);
        EzDebug.log("on remove");
    }

    @Override
    public void onPlace(BlockState p_60566_, Level p_60567_, BlockPos p_60568_, BlockState p_60569_, boolean p_60570_) {
        super.onPlace(p_60566_, p_60567_, p_60568_, p_60569_, p_60570_);
        EzDebug.log("on place");
    }

    @Override
    public void playerDestroy(Level p_49827_, Player p_49828_, BlockPos p_49829_, BlockState p_49830_, @Nullable BlockEntity p_49831_, ItemStack p_49832_) {
        super.playerDestroy(p_49827_, p_49828_, p_49829_, p_49830_, p_49831_, p_49832_);
        EzDebug.log("set destroyed by:" + p_49828_);
    }

    @Override
    public void setPlacedBy(Level p_49847_, BlockPos p_49848_, BlockState p_49849_, @Nullable LivingEntity p_49850_, ItemStack p_49851_) {
        super.setPlacedBy(p_49847_, p_49848_, p_49849_, p_49850_, p_49851_);
        EzDebug.log("set placed by:" + p_49850_);
    }*/

    @Override
    public BlockState representBlock() { return Blocks.AIR.defaultBlockState(); }
}
