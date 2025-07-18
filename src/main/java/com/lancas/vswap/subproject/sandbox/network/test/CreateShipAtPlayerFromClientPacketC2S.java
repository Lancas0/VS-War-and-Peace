package com.lancas.vswap.subproject.sandbox.network.test;

import com.lancas.vswap.content.WapBlocks;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.behviour.SandBoxTween;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.component.data.TweenData;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.JomlUtil;
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
                BlockClusterData blockData = new BlockClusterData();
                Vector3ic p = new Vector3i();
                for (int i = 0; i < blockCnt; ++i) {
                    blockData.setBlock(p, WapBlocks.Cartridge.Primer.PRIMER.getDefaultState());

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
                    new RigidbodyData(new TransformPrimitive(JomlUtil.d(player.position()), new Quaterniond(), new Vector3d(1, 1, 1))),
                    blockData
                );
                EzDebug.log("ship mass:" + ship.getRigidbody().getDataReader().getMass());
                ship.getRigidbody().getDataWriter().applyWorldTorque(new Vector3d(0, 40000000, 0));

                //SandBoxRigidbody rigidbody = new SandBoxRigidbody();
                //ship.addBehaviour(rigidbody, new SandBoxRigidbodyData());
                //rigidbody.addTorque(new Vector3d(0, 40000000, 0));

                ship.addBehaviour(
                    new SandBoxTween(),
                    new TweenData(
                        /*(prev, t01, step01) -> {
                        TransformPrimitive next = new TransformPrimitive(prev);
                        next.scale.set(Math.sin(t01));
                        //EzDebug.log("server transform:" + next);
                        return next;
                    }*/TweenData.TweenFunction.Scale, 10, true)
                );
            } catch (Exception e) {
                EzDebug.error("exception when create ship:" + e.toString());
                return;
            }

            EzDebug.log("create ship with uuid:" + uuid);

            SandBoxServerWorld.addShip(level, ship, true);
        });
        ctx.get().setPacketHandled(true);
    }
}
