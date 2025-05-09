package com.lancas.vs_wap.content;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;

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
