package com.lancas.vswap.content;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.util.ShipUtil;
import com.lancas.vswap.util.StrUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.valkyrienskies.core.api.ships.ServerShip;

//@Mod.EventBusSubscriber
public class WapCommands {
    //public static class RemoveAllCommands
    //@SubscribeEvent
    /*public static void register(RegisterCommandsEvent event) {
        new SandBoxCommand().register(event.getDispatcher());
    }*/

    private static LiteralArgumentBuilder<Object> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }


    public static void registerServerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        //CommandDispatcher<VSCommandSource>
        dispatcher.register(Commands.literal("sandbox")
            .then(Commands.literal("remove")
                .then(Commands.literal("all-client")
                    .executes(ctx -> {
                        SandBoxClientWorld.INSTANCE.markAllDeleted();
                        //todo say remove x ships;
                        return 1;
                    })


                )
                .then(Commands.literal("all-level")
                    .executes(ctx -> {
                        SandBoxClientWorld.INSTANCE.markAllDeleted();
                        SandBoxServerWorld.getOrCreate(ctx.getSource().getLevel()).markAllDeleted();
                        return 1;
                    })
                )
                .then(Commands.literal("all-level-server")
                    .executes(ctx -> {
                        SandBoxServerWorld.getOrCreate(ctx.getSource().getLevel()).markAllDeleted();
                        return 1;
                    })
                )
                .then(Commands.literal("all-server")
                    .executes(ctx -> {
                        ctx.getSource().getServer().getAllLevels().forEach(
                            l -> SandBoxServerWorld.getOrCreate(l).markAllDeleted()
                        );
                        return 1;
                    })
                )
            )
        );



        dispatcher.register(Commands.literal("vs_debug")
            .then(Commands.literal("get_pos")
                .then(Commands.argument("id", LongArgumentType.longArg())
                    .executes(ctx -> {
                        ServerShip ship = ShipUtil.getServerShipByID(ctx.getSource().getLevel(), LongArgumentType.getLong(ctx, "id"));
                        if (ship == null)
                            EzDebug.warn("ship is null!");
                        else {
                            EzDebug.log("ship world pos is " + StrUtil.toNormalString(ship.getTransform().getPositionInWorld()));
                            EzDebug.log("ship  ship pos is " + StrUtil.toNormalString(ship.getTransform().getPositionInShip()));
                        }
                        return 1;
                    })
                )
            )
            /*.then(Commands.literal("get_aabb")
                .then(Commands.argument("id", LongArgumentType.longArg())
                    .executes(ctx -> {
                        ServerShip ship = ShipUtil.getServerShipByID(ctx.getSource().getLevel(), LongArgumentType.getLong(ctx, "id"));
                        if (ship == null)
                            EzDebug.warn("ship is null!");
                        else {
                            EzDebug.log("ship world pos is " + StrUtil.toNormalString(ship.getTransform().getPositionInWorld()));
                            EzDebug.log("ship  ship pos is " + StrUtil.toNormalString(ship.getTransform().getPositionInShip()));
                        }
                        return 1;
                    })
                )
            )*/
        );
    }

    /*private static class SandBoxCommand {
        public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
            Commands.literal("sa")

                );

            /.*dispatcher.register(Commands.literal("sa")

                //.requires(source -> source.hasPermission(2)) // 需要OP权限
                .then(Commands.literal("remove")
                    .then(Commands.literal("all").executes())
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("shipID", StringArgumentType.string())
                            .executes(ctx -> giveDockerItem(
                                ctx.getSource(),
                                EntityArgument.getPlayer(ctx, "player"),
                                StringArgumentType.getString(ctx, "shipID")
                            ))
                        ))
                )
                .then(Commands.literal("setrotation")
                    .then(Commands.argument("entity", EntityArgument.entity())
                        .then(Commands.argument("degrees", FloatArgumentType.floatArg(-360, 360))
                            .executes(ctx -> setRotation(
                                ctx.getSource(),
                                EntityArgument.getEntity(ctx, "entity"),
                                FloatArgumentType.getFloat(ctx, "degrees")
                            ))
                        )
                    )
                ));*./
        }
    }*/
}
