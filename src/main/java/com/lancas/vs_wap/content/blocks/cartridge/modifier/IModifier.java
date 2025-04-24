package com.lancas.vs_wap.content.blocks.cartridge.modifier;

import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.ship.type.ProjectileWrapper;
import com.lancas.vs_wap.ship.ballistics.api.IPhysBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface IModifier {
    /*public class ModifierData {
        public ServerLevel level;
        public ServerShip projectileShip;
        public Vector3ic headDirInShip;
        public boolean isOutArtillery;
        public BlockPos modifierBp;
        public Vector3dc launchDir;

        public ModifierData(ServerLevel inLevel, ServerShip inProjectileShip, BlockPos inModifierBp, boolean inIsOutArtillery, Vector3ic inHeadDirInShip, Vector3dc inLaunchDir) {
            level = inLevel;
            projectileShip = inProjectileShip;
            isOutArtillery = inIsOutArtillery;
            headDirInShip = inHeadDirInShip;
            modifierBp = inModifierBp;
            launchDir = inLaunchDir.get(new Vector3d());

            if (inHeadDirInShip.lengthSquared() != 1) {
                EzDebug.fatal("the headDir In Ship is not a normalized dir");
                headDirInShip = new Vector3i(0, 0, 1);
            }

        }

        public BlockState getBlockState() {
            return level.getBlockState(modifierBp);
        }
        public BlockEntity getBlockEntity() {
            return level.getBlockEntity(modifierBp);
        }
        public Vector3d getWorldPos() {
            return projectileShip.getShipToWorld().transformPosition(JomlUtil.dCenter(modifierBp));
        }
        public BlockPos getWorldBp() {
            return JomlUtil.bpContaining(getWorldPos());
        }
        public Vector3dc getLaunchDir() {
            return launchDir;
        }
        //可能会变，所以需要动态获取
        public boolean isHead() {
            AABBic shipAABB = projectileShip.getShipAABB();
            if (shipAABB == null) {
                EzDebug.error("ModifierData is?Head: ship AABB is null");
                return false;
            }
            BlockPos headBp = new BlockPos(
                headDirInShip.x() == 1 ? shipAABB.maxX() - 1 : shipAABB.minX(),
                headDirInShip.y() == 1 ? shipAABB.maxY() - 1 : shipAABB.minY(),
                headDirInShip.z() == 1 ? shipAABB.maxZ() - 1 : shipAABB.minZ()
            );
            return headBp.equals(modifierBp);
        }
        //可能会变，所以需要动态获取
        public boolean isTail() {
            AABBic shipAABB = projectileShip.getShipAABB();
            if (shipAABB == null) {
                EzDebug.error("ModifierData is?Tail: ship AABB is null");
                return false;
            }
            BlockPos tailBp = new BlockPos(
                headDirInShip.x() == -1 ? shipAABB.maxX() - 1 : shipAABB.minX(),
                headDirInShip.y() == -1 ? shipAABB.maxY() - 1 : shipAABB.minY(),
                headDirInShip.z() == -1 ? shipAABB.maxZ() - 1 : shipAABB.minZ()
            );
            return tailBp.equals(modifierBp);
        }
    }*/

    /*public default Vector3dc calculateForceInServerTick(ModifierData data) { return new Vector3d(); }
    public default Vector3dc calculateTorqueInServerTick(ModifierData data) { return new Vector3d(); }
    public default double getDragFactorModification(ModifierData data) { return 0; }*/

    public default double getAirDragMultiplier(ProjectileWrapper projectile, BlockPos pos, BlockState state) { return 1; }
    public default void modifyTempPhysBehaviour(ProjectileWrapper projectile, BlockPos pos, BlockState state, Map<String, BiTuple<BlockPos, IPhysBehaviour>> tempPhysBehaviours) {}


}
