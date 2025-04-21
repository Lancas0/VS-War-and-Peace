package com.lancas.vs_wap.content;

import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.foundation.utility.AttachedRegistry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class WAPBehaviours {
    //SimpleRegistry<Block, MovementBehaviour> REGISTRY = SimpleRegistry.create();
    private static final AttachedRegistry<Block, MovementBehaviour> BLOCK_BEHAVIOURS
        = new AttachedRegistry(ForgeRegistries.BLOCKS);


    public static void registerBehaviour(ResourceLocation block, MovementBehaviour behaviour) {
        BLOCK_BEHAVIOURS.register(block, behaviour);
    }

    public static void registerBehaviour(Block block, MovementBehaviour behaviour) {
        BLOCK_BEHAVIOURS.register(block, behaviour);
    }


    public static <B extends Block> NonNullConsumer<? super B> movementBehaviour(MovementBehaviour behaviour) {
        return (b) -> registerBehaviour(b, behaviour);
    }

    public static void register(Block block, MovementBehaviour behaviour) {
        registerBehaviour(block, behaviour);
    }

    /*public static void register() {
        //AllMovementBehaviours.registerBehaviour(AllBlocks.ScopeBlockRO.get(), new ScopeMovementBehaviour());
    }*/
}
