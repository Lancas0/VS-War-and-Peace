package com.lancas.vs_wap.content.blocks.cartridge.warhead;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.network.NetworkHandler;
import com.lancas.vs_wap.foundation.network.server2client.ConeParticlePacketS2C;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;


public class FragWarheadBlock extends Block implements IWarheadBlock {
    public static final String ID = "frag_warhead_block";
    //todo use abstract block to avoid load to much blocks beacuse of property variance
    public static final IntegerProperty DAMAGE = IntegerProperty.create("damage", 4, 10);
    public static final IntegerProperty RADIUS = IntegerProperty.create("radius", 6, 20);
    public static final IntegerProperty ANGLE = IntegerProperty.create("angle", 90, 180);

    // 支持6个方向的朝向属性
    //public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public FragWarheadBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(DAMAGE, 10)
            .setValue(RADIUS, 6)
            .setValue(ANGLE, 180)
        );
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DAMAGE, RADIUS, ANGLE);
    }

    private static void applyConeDamage(ServerLevel level, Vec3 warheadPos, Vec3 axis, double damage, int radius, int angle) {
        //todo get more accurate AABB
        AABB area = new AABB(
            warheadPos.x - radius, warheadPos.y - radius, warheadPos.z - radius,
            warheadPos.x + radius, warheadPos.y + radius, warheadPos.z + radius
        );
        List<Entity> entities = level.getEntities(null, area);

        for (Entity entity : entities) {
            //EzDebug.Log("testing:" + entity.getName().getString());

            if (entity instanceof LivingEntity) {
                Vec3 entityPos = entity.position();
                Vec3 directionToEntity = entityPos.subtract(warheadPos).normalize();


                double dot = axis.dot(directionToEntity);
                double angleRad = Math.acos(dot);
                double angleDeg = Math.toDegrees(angleRad);

                EzDebug.log("ent:" + entity.getName().getString() + ", entityPos:" + entityPos + ", dirToEnt:" + directionToEntity + ", axis:" + axis + ", angle:" + angleDeg);

                //ensure that axis and dirToEntity are normalized
                if (isInCone(axis, directionToEntity, angle)) {
                    EzDebug.log("In Cone:" + entity.getName().getString());
                    entity.hurt(level.damageSources().explosion(null), (float)damage);
                }
            }
        }
    }
    // 圆锥检测算法
    private static boolean isInCone(Vec3 axis, Vec3 dirToEntity, double coneAngleDeg) {
        double dot = axis.dot(dirToEntity);
        double angleRad = Math.acos(dot);
        double angleDeg = Math.toDegrees(angleRad);

        //EzDebug.Log("axis:" + axis + ", dirToEnt:" + dirToEntity + ", dot:" + dot + ", angleDeg:" + angleDeg + ", coneAngleDeg/2:" + coneAngleDeg / 2);
        boolean inCone = angleDeg <= coneAngleDeg / 2;

        return angleDeg <= coneAngleDeg / 2;
    }


    @Override
    public void onDestroyByExplosion(ServerLevel level, BlockPos pos, BlockState state, Explosion explosion) {
        if (level == null) return;
        //todo avoid check
        if (!(state.getBlock() instanceof FragWarheadBlock fragWarhead))
            return;

        EzDebug.log("onDestoryByExplosing");

        int damage = state.getValue(FragWarheadBlock.DAMAGE);
        int radius = state.getValue(FragWarheadBlock.RADIUS);
        int angle = state.getValue(FragWarheadBlock.ANGLE);

        Vec3 explosionPos = explosion.getPosition();
        Vec3 warheadCenter = pos.getCenter();
        Vec3 axis = warheadCenter.subtract(explosionPos).normalize();

        applyConeDamage(level, pos.getCenter(), axis, damage, radius, angle);
        NetworkHandler.channel.send(
            PacketDistributor.ALL.with(() -> { return null; }), //todo only send to near players
            new ConeParticlePacketS2C(pos, JomlUtil.f(axis), angle, radius)
        );
    }
}