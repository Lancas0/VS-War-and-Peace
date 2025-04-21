package com.lancas.vs_wap.obsolete.be;

/*
import com.lancas.einherjar.content.saved.ConstraintsMgr;
import com.lancas.einherjar.event.EventMgr;
import com.lancas.einherjar.foundation.LazyTicks;
import com.lancas.einherjar.foundation.api.Dest;
import com.lancas.einherjar.ship.attachment.HoldableAttachment;
import com.lancas.einherjar.ship.helper.LazyShip;
import com.lancas.einherjar.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.einherjar.content.blocks.artillery.IBreech;
import com.lancas.einherjar.content.blocks.cartridge.IPrimer;
import com.lancas.einherjar.debug.EzDebug;
import com.lancas.einherjar.subproject.blockplusapi.util.QuadConsumer;
import com.lancas.einherjar.util.JomlUtil;
import com.lancas.einherjar.util.ShipUtil;
import com.lancas.einherjar.util.StrUtil;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.primitives.AABBd;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class PrimerBE extends BlockEntity implements IHaveGoggleInformation {

    private static final String SHIP_ID_TAG = "ship_id";

    //do not save any field
    //public long breechId = -1;
    //public long shipId = -1L;
    public String attConstraintKey = "";
    public String oriConstraintKey= "";

    //not save
    private final LazyTicks lazy = new LazyTicks(12);
    //not save
    private final LazyShip lazyShip;

    private TriConsumer<ServerLevel, ServerPlayer, Long> unholdListener = null;

    //client
    //private boolean inBreech = false;

    public PrimerBE(BlockEntityType<?> p_155228_, BlockPos bp, BlockState p_155230_) {
        super(p_155228_, bp, p_155230_);
        lazyShip = LazyShip.ofBlockPos(bp);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        attConstraintKey = tag.getString("att_key");
        oriConstraintKey = tag.getString("ori_key");
    }
    // 写入NBT数据
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putString("att_key", attConstraintKey);
        tag.putString("ori_key", oriConstraintKey);
        super.saveAdditional(tag);
    }

    public void serverTick() {

    }
    public void tick() {
        //todo need client later
        if (!(level instanceof ServerLevel sLevel)) return;
        if (!lazy.shouldWork()) return;
        ServerShip inShip = (ServerShip)lazyShip.shutDownIfGetNull(sLevel);
        if (inShip == null) return;

        BlockState state = getBlockState();

        if (!(state.getBlock() instanceof IPrimer primer)) {
            EzDebug.fatal("primer should be IPrimer");
            return;
        }

        boolean isTriggered = primer.isTriggered(state);
        if (isTriggered) {
            removeConstraint();
            return;
        }

        if (StrUtil.isNotEmpty(attConstraintKey) && StrUtil.isNotEmpty(oriConstraintKey))
            return;

        //ServerShip inShip = ShipUtil.getServerShipAt(sLevel, worldPosition);
        //if (inShip == null) return;  //todo cache

        var primerHoldable = inShip.getAttachment(HoldableAttachment.class);
        if (primerHoldable == null) return;

        Direction primerDir = getBlockState().getValue(DirectionAdder.FACING);// dirPrimer.getDirection(state);

        Dest<ServerShip> breechShip = new Dest<>();
        Dest<BlockPos> breechBp = new Dest<>();
        findBreech(inShip, primerDir, inShip, breechShip, breechBp);
        if (!breechBp.hasValue()) return;

        BlockState breechState = level.getBlockState(breechBp.get());
        Direction breechDir = DirectionAdder.getDirection(breechState);  //todo use IBreech to get breech dir?
        if (breechDir == null) {
            EzDebug.fatal("can not get direction of breech");
            return;
        }

        createConstraints(breechShip.get(), inShip, breechBp.get(), /.*primerDir, primer.getPixelLength(),*./ breechDir, primerHoldable);
    }



    /*@Override
    @OnlyIn(Dist.CLIENT)
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (level == null) return false;

        //locked
        if (inBreech) {
            Direction primerDir = getBlockState().getValue(DirectionBlockAdder.FACING);
            double energy = BallisticsCalculation.calculateEnergyFromPrimer(level, worldPosition, primerDir);
            tooltip.add(Component.literal("energy:" + (energy / 1000.0) + "KJ"));

            Ship primerShip = ShipUtil.getShipByID(level, shipId);
            if (shipId < 0 || primerShip == null) {
                EzDebug.warn("shipId < 0, skip google message");
            } else {
                Vector3d worldPrimerDir = primerShip.getTransform().getShipToWorldRotation().transform(JomlUtil.dNormal(primerDir));
                tooltip.add(Component.literal("direction: " + StrUtil.F2(worldPrimerDir)));
            }

            var breechData = findBreech(primerShip, primerDir);
            if (breechData == null) {
                EzDebug.warn("can not find breech, skip calcualte Hitpos");
            } else {
                Direction breechDir = level.getBlockState(breechData.getSecond()).getValue(DirectionBlockAdder.FACING);
                HashSet<BlockPos> barrelBps = BallisticsCalculation.calculateBarrelBps(level, breechData.getSecond(), breechDir);
                BlockPos headStartBp = BallisticsCalculation.calculateBallisticHead(level, worldPosition, primerDir);
                HashSet<BlockPos> headBps = BallisticsCalculation.getHeadPart(level, headStartBp, primerDir);
                Vector3d hitPos = BallisticsCalculation.calculateHitPos(level, worldPosition, energy, barrelBps, headBps);
                tooltip.add(Component.literal("Predict Hitpos:" + StrUtil.F2(hitPos)));
            }

            BlockPos headStartBp = BallisticsCalculation.calculateBallisticHead(level, worldPosition, primerDir);
            HashSet<String> descSet = new HashSet<>();
            HashSet<BlockPos> headParts = BallisticsCalculation.getHeadPart(level, headStartBp, primerDir);
            /.*BallisticsCalculation.getHeadPart(level, headStartBp, primerDir, (bp, state) -> {
                if (state.getBlock() instanceof ITerminalEffector effector)
                    effector.appendDescription(descSet);
            });*./
            headParts.forEach(headBp -> {
                BlockState headState = level.getBlockState(headBp);
                if (headState.getBlock() instanceof ITerminalEffector effector)
                    effector.appendDescription(descSet);
            });
            if (!descSet.isEmpty()) {
                tooltip.add(Component.literal("Terminal Effects:"));
                tooltip.addAll(descSet.stream().map(Component::literal).toList());
            }

            return true;
        }

        tooltip.add(Component.literal("Not in breech"));
        return true;
    }*./
}
*/