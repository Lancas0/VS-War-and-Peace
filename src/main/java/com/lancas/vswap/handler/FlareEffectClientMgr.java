package com.lancas.vswap.handler;

import com.lancas.vswap.ModMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ModMain.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FlareEffectClientMgr {
    public static double playerPreferredGamma;

    public static class FlareData {
        public int range = 0;
        public int ticksRemain = 0;
        public float gamma = 0;
        //todo color
        public FlareData(int inRange, int inTicks, float inGamma) {
            range = inRange;
            ticksRemain = inTicks;
            gamma = inGamma;
        }
    }

    private final static Map<Vec3, FlareData> flareDataMap = new LinkedHashMap<>();


    public static void addFlareData(int range, Vec3 flarePos, int ticks, float gamma) {
        if (flarePos == null) return;
        flareDataMap.put(flarePos, new FlareData(range, ticks, gamma));
    }


    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        //if (flareDataMap.isEmpty()) return;    check flares even empty so playerPreferredGamma can be set

        Minecraft mc = Minecraft.getInstance();
        Options options = mc.options;
        Player player = mc.player;
        if (player == null) return;

        float maxGamma = -1f;
        boolean anyEffectingFlare = false;
        Iterator<Map.Entry<Vec3, FlareData>> it = flareDataMap.entrySet().iterator();
        while (it.hasNext()) {
            var current = it.next();
            Vec3 flarePos = current.getKey();
            FlareData flareData = current.getValue();

            double dist = flarePos.distanceTo(player.position());

            if (dist <= flareData.range) {
                anyEffectingFlare = true;
                maxGamma = Math.max(maxGamma, flareData.gamma);
            }

            if (--flareData.ticksRemain <= 0)
                it.remove();
        }


        if (anyEffectingFlare)
            options.gamma().set((double)maxGamma);
        else
            options.gamma().set(playerPreferredGamma);
    }

    /*private static final Map<BlockPos, FlareEffect> activeFlares = new ConcurrentHashMap<>();
    private static final int PARTICLE_DENSITY = 50;

    public static void addFlare(BlockPos pos, int duration) {
        activeFlares.put(pos, new FlareEffect(pos, duration));
    }*/

    /*@SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityType.BLOCK_ENTITY,
            ctx -> new OptimizedOreRenderer());
    }

    private void changeGamma() {

    }*/

    /*@SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            activeFlares.values().removeIf(FlareEffect::tick);
        }
    }

    @SubscribeEvent
    public static void onWorldRenderLast(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        EzDebug.Log("test re render the level");
        event.getLevelRenderer().renderLevel(
            event.getPoseStack(),
            event.getPartialTick(),
            event.getRenderTick(),
            true,
            event.getCamera(),
            Minecraft.getInstance().gameRenderer,
            new LightTexture(Minecraft.getInstance().gameRenderer, Minecraft.getInstance()),
            event.getProjectionMatrix()
        );
    }

    private static class FlareEffect {
        private final BlockPos pos;
        private int age;
        private final int maxAge;
        //private final ParticleSystem particleSystem;

        public FlareEffect(BlockPos pos, int duration) {
            this.pos = pos;
            this.maxAge = duration;
            //this.particleSystem = new ParticleSystem(PARTICLE_DENSITY);
        }

        public boolean tick() {
            if (++age >= maxAge) return true;
            //particleSystem.tick();
            return false;
        }

        /.*public void render(RenderLevelStageEvent event, PoseStack poseStack) {
            // 应用着色器
            ShaderInstance shader = Minecraft.getInstance().getResourceManager()
                .getShader(ModShaders.FLARE_SHADER);

            shader.setSampler("DepthTexture", Minecraft.getInstance().getMainRenderTarget().getDepthTextureId());
            shader.safeGetUniform("Time").set(age / 20f);
            shader.safeGetUniform("LightPosition").set(
                (float)pos.getX() + 0.5f,
                (float)pos.getY() + 0.5f,
                (float)pos.getZ() + 0.5f
            );

            // 渲染全屏效果
            shader.apply();
            renderFullscreenQuad(event.getPoseStack());
            shader.clear();

            // 渲染粒子
            particleSystem.render(poseStack, event.getPartialTick());
        }*./
    }*/
}

