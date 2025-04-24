package com.lancas.vs_wap.subproject.sandbox.network.test;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxRigidbody;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxTween;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxBlockClusterData;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxRigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import com.lancas.vs_wap.subproject.sandbox.component.data.TweenData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Math;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class CreateShipAtPlayerFromClientPacketC2S {
    //private Vector3f position;
    private final int blockCnt;

    public CreateShipAtPlayerFromClientPacketC2S(int inBlockCnt) {
        blockCnt = inBlockCnt;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(blockCnt);
    }
    public static CreateShipAtPlayerFromClientPacketC2S decode(FriendlyByteBuf buffer) {
        return new CreateShipAtPlayerFromClientPacketC2S(
            buffer.readInt()
        );
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Random r = new Random();
            ServerPlayer player = ctx.get().getSender();
            ServerLevel level = (ServerLevel)player.level();

            UUID uuid = UUID.randomUUID();
            SandBoxServerShip ship;
            try {
                SandBoxBlockClusterData blockData = new SandBoxBlockClusterData();
                BlockPos p = new BlockPos(0, 0, 0);
                for (int i = 0; i < blockCnt; ++i) {
                    blockData.setBlock(p, Blocks.IRON_BLOCK.defaultBlockState());

                    while (blockData.contains(p)) {
                        switch (r.nextInt(0, 6)) {
                            case 0 -> p = p.offset(1, 0, 0);
                            case 1 -> p = p.offset(-1, 0, 0);
                            case 2 -> p = p.offset(0, 1, 0);
                            case 3 -> p = p.offset(0, -1, 0);
                            case 4 -> p = p.offset(0, 0, 1);
                            case 5 -> p = p.offset(0, 0, -1);
                        }
                    }
                }


                ship = new SandBoxServerShip(
                    uuid,
                    new SandBoxTransformData(JomlUtil.d(player.position()), new Quaterniond(), new Vector3d(1, 1, 1)),
                    blockData
                );
                //ship.addBehaviour(new SandBoxRigidbody(), new SandBoxRigidbodyData());
                ship.addBehaviour(
                    new SandBoxTween(),
                    new TweenData((prev, et) -> {
                        SandBoxTransformData next = SandBoxTransformData.copy(prev);
                        next.position.add(0, Math.sin(et / 2.0), 0);
                        return next;
                    })
                );
            } catch (Exception e) {
                EzDebug.error("exception when create ship:" + e.toString());
                return;
            }

            EzDebug.log("create ship with uuid:" + uuid);

            SandBoxServerWorld.addShip(level, ship);
        });
        ctx.get().setPacketHandled(true);
    }
}
