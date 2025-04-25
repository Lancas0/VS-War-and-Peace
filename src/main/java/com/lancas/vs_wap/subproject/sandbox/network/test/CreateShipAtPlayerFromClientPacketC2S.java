package com.lancas.vs_wap.subproject.sandbox.network.test;

import com.lancas.vs_wap.content.WapBlocks;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxTween;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxBlockClusterData;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxRigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import com.lancas.vs_wap.subproject.sandbox.component.data.TweenData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
                Vector3ic p = new Vector3i();
                for (int i = 0; i < blockCnt; ++i) {
                    blockData.setBlock(p, WapBlocks.Cartridge.PRIMER.getDefaultState());

                    while (blockData.contains(p)) {
                        switch (r.nextInt(0, 6)) {
                            case 0 -> p = p.add(1, 0, 0, new Vector3i());
                            case 1 -> p = p.add(-1, 0, 0, new Vector3i());
                            case 2 -> p = p.add(0, 1, 0, new Vector3i());
                            case 3 -> p = p.add(0, -1, 0, new Vector3i());
                            case 4 -> p = p.add(0, 0, 1, new Vector3i());
                            case 5 -> p = p.add(0, 0, -1, new Vector3i());
                        }
                    }
                }


                ship = new SandBoxServerShip(
                    uuid,
                    new SandBoxTransformData(JomlUtil.d(player.position()), new Quaterniond(), new Vector3d(1, 1, 1)),
                    blockData,
                    new SandBoxRigidbodyData()
                );
                EzDebug.log("ship mass:" + ship.getRigidbody().getExposedData().getMass());
                ship.getRigidbody().applyTorque(new Vector3d(0, 40000000, 0));

                //SandBoxRigidbody rigidbody = new SandBoxRigidbody();
                //ship.addBehaviour(rigidbody, new SandBoxRigidbodyData());
                //rigidbody.addTorque(new Vector3d(0, 40000000, 0));

                ship.addBehaviour(
                    new SandBoxTween(),
                    new TweenData((prev, et) -> {
                        SandBoxTransformData next = SandBoxTransformData.copy(prev);
                        next.scale.set(Math.sin(et));
                        //EzDebug.log("server transform:" + next);
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
