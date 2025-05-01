package com.lancas.vs_wap.obsolete.blocks;

import com.lancas.vs_wap.content.WapBlocks;
import com.lancas.vs_wap.content.block.blocks.abstrac.DirectionalBlockImpl;
import com.lancas.vs_wap.debug.EzDebug;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.lang.reflect.Field;

public class AllDirectionalBlockStateProvider extends BlockStateProvider {

    public AllDirectionalBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        System.out.println("registerStatesAndModels");

        try {
            // 通过反射获取ModBlocks的所有字段
            Field[] fields = WapBlocks.class.getDeclaredFields();
            for (Field field : fields) {
                System.out.println("[generator] test");
                //LOGGER.info("[generator] test");

                if (BlockEntry.class.isAssignableFrom(field.getType())) {
                    EzDebug.log("block entry field:" + field.getName());
                }
                /*if (Block.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    Block block = (Block) field.get(null);

                    // 检测是否为AllDirectionalBlock子类
                    if (block instanceof AllDirectionalBlock allDirBlock) {
                        registerDirectionalBlock(allDirBlock);
                    }
                }*/
            }
        } catch (Exception e) {
            /*catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to auto-register directional blocks", e);
        }*/
        }
    }

    private void registerDirectionalBlock(DirectionalBlockImpl block) {
        String blockName = block.getName().getString();
        EzDebug.log("[register] new block:" + blockName);

        // 自动生成模型配置
        /*ModelFile modelFile = models().cubeAll(blockName,
            modLoc("block/" + blockName)); // 假设纹理路径为block/[name]

        // 自动生成六个方向的blockstates
        directionalBlock(block, modelFile);*/
    }
}
