package com.lancas.vs_wap.renderer.docker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DockerModelCompiler {
    // 存储所有方块的顶点数据
    private final List<BakedQuad> quads = new ArrayList<>();
    private final Map<ResourceLocation, TextureAtlasSprite> textureMap = new HashMap<>();

    /*private static final VertexFormat VERTEX_FORMAT = new VertexFormat(
        List.of(
            Attribute.POSITION,
            Attribute.COLOR,
            Attribute.UV0,
            Attribute.UV2 // 光照数据
        )
    );*/

    public void addBlock(BlockPos pos, BlockState state) {
        // 获取方块的Baked模型
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);

        // 获取随机种子（用于不同面渲染）
        RandomSource random = RandomSource.create(42L);

        // 遍历所有可能的面向
        for (Direction direction : Direction.values()) {
            // 获取该面向的quads数据
            quads.addAll(model.getQuads(state, direction, random));
        }
        // 添加通用quads（如方块内部）
        quads.addAll(model.getQuads(state, null, random));

        // 记录纹理
        for (BakedQuad quad : quads) {
            textureMap.put(quad.getSprite().atlasLocation(), quad.getSprite());
        }
    }

    public static class ShipVBO {
        private int vboId;
        private int vertexCount;

        public void compile(List<BakedQuad> quads) {
            // 计算总顶点数
            /*vertexCount = quads.size() * 4;

            // 创建临时缓冲区
            ByteBuffer buffer = ByteBuffer.allocateDirect(vertexCount * VERTEX_FORMAT.getVertexSize());
            buffer.order(ByteOrder.nativeOrder());

            // 填充数据
            for (BakedQuad quad : quads) {
                int[] vertexData = quad.getVertices();
                for (int i = 0; i < 4; i++) {
                    // 解包顶点数据（基于BakedQuad的存储格式）
                    int offset = i * 8;
                    float x = Float.intBitsToFloat(vertexData[offset]);
                    float y = Float.intBitsToFloat(vertexData[offset + 1]);
                    float z = Float.intBitsToFloat(vertexData[offset + 2]);
                    // ... 其他属性同理

                    // 写入缓冲区
                    buffer.putFloat(x);
                    buffer.putFloat(y);
                    buffer.putFloat(z);
                    // ... 其他属性
                }
            }
            buffer.flip();*/

            // 创建VBO
            /*vboId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);*/
        }
    }
}
