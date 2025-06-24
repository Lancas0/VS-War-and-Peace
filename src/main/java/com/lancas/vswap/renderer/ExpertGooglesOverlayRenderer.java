package com.lancas.vswap.renderer;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.WapLang;
import com.lancas.vswap.content.block.blocks.cartridge.warhead.WarheadAPCR;
import com.lancas.vswap.content.block.blocks.cartridge.warhead.apds.WarheadAPDS;
import com.lancas.vswap.content.info.block.WapBlockInfos;
import com.lancas.vswap.foundation.api.QuadPredicate;
import com.lancas.vswap.foundation.math.WapBallisticMath;
import com.lancas.vswap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vswap.ship.ballistics.helper.BallisticsMath;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.StrUtil;
import com.lancas.vswap.util.WorldUtil;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.CreateClient;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock;
import com.simibubi.create.content.contraptions.piston.PistonExtensionPoleBlock;
import com.simibubi.create.content.equipment.goggles.*;
import com.simibubi.create.content.trains.entity.TrainRelocator;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.gui.RemovedGuiUtils;
import com.simibubi.create.foundation.gui.Theme;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.mixin.accessor.MouseHandlerAccessor;
import com.simibubi.create.foundation.outliner.Outline;
import com.simibubi.create.foundation.outliner.Outliner;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@OnlyIn(Dist.CLIENT)
//@Mod.EventBusSubscriber
@Mod.EventBusSubscriber(modid = VsWap.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ExpertGooglesOverlayRenderer {
    @FunctionalInterface
    public interface TooltipProvider extends QuadPredicate<ClientLevel, LocalPlayer, BlockPos, List<Component>> {
        public default TooltipProvider mustWear(EquipmentSlot slot, Item item) {
            return (TooltipProvider) and((l, p, bp, t) -> {
                return p.getItemBySlot(slot).is(item);
            });
        }
        public default TooltipProvider mustNotWear(EquipmentSlot slot, Item item) {
            return (TooltipProvider) and((l, p, bp, t) -> {
                return !p.getItemBySlot(slot).is(item);
            });
        }
        public default TooltipProvider mustWear(EquipmentSlot slot, Item... anyItem) {
            return (TooltipProvider) and((l, p, bp, t) -> {
                ItemStack wearing = p.getItemBySlot(slot);
                for (Item item : anyItem) {
                    if (wearing.is(item))
                        return true;
                }
                return false;
            });
        }
        public default TooltipProvider mustShifting() {
            return (TooltipProvider) and((l, p, bp, t) -> {
                return p.isShiftKeyDown();
            });
        }
        public default TooltipProvider mustNotShifting() {
            return (TooltipProvider) and((l, p, bp, t) -> {
                return !p.isShiftKeyDown();
            });
        }

        public default TooltipProvider mustHolding(InteractionHand hand, Item item) {
            return (TooltipProvider) and((l, p, bp, t) -> {
                return p.getItemInHand(hand).is(item);
            });
        }
        public default TooltipProvider mustHolding(InteractionHand hand, Item... anyItem) {
            return (TooltipProvider) and((l, p, bp, t) -> {
                ItemStack holding = p.getItemInHand(hand);
                for (Item item : anyItem) {
                    if (holding.is(item))
                        return true;
                }
                return false;
            });
        }
        public default TooltipProvider mustNotHolding(InteractionHand hand, Item item) {
            return (TooltipProvider) and((l, p, bp, t) -> {
                return !p.getItemInHand(hand).is(item);
            });
        }
    }

    //todo icon provider
    /*@FunctionalInterface
    public interface IconProvider extends QuadPredicate<ClientLevel, LocalPlayer, BlockPos, List<Component>> {
        public default TooltipProvider playerMustWear(EquipmentSlot slot, Item item) {
            return (TooltipProvider) and((l, p, bp, t) -> {
                return p.getItemBySlot(slot).is(item);
            });
        }
        public default TooltipProvider playerMustShifting() {
            return (TooltipProvider) and((l, p, bp, t) -> {
                return p.isShiftKeyDown();
            });
        }
        public default TooltipProvider playerMustNotShifting() {
            return (TooltipProvider) and((l, p, bp, t) -> {
                return !p.isShiftKeyDown();
            });
        }
    }*/

    public static ExpertGooglesOverlayRenderer INSTANCE = new ExpertGooglesOverlayRenderer();

    private final List<TooltipProvider> tooltipProviders = new ArrayList<>();
    public void addTooltipProvider(@NotNull TooltipProvider provider) {
        tooltipProviders.add(provider);
    }

    private static final Map<Object, Outliner.OutlineEntry> outlines = CreateClient.OUTLINER.getOutlines();
    public static int hoverTicks = 0;
    //public static BlockPos lastHovered = null;


    public ExpertGooglesOverlayRenderer() {
        //add create googles tooltip provider
        addTooltipProvider((level, player, bp, tooltip) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.options.hideGui || player.isSpectator())
                return false;

            BlockEntity be = level.getBlockEntity(bp);
            boolean wearingGoggles = GogglesItem.isWearingGoggles(player);
            boolean hasGoggleInformation = be instanceof IHaveGoggleInformation;
            boolean hasHoveringInformation = be instanceof IHaveHoveringInformation;
            boolean goggleAddedInformation = false;
            boolean hoverAddedInformation = false;
            //ItemStack item = AllItems.GOGGLES.asStack();
            if (hasGoggleInformation && wearingGoggles) {
                boolean isShifting = player.isShiftKeyDown();
                IHaveGoggleInformation gte = (IHaveGoggleInformation)be;
                goggleAddedInformation = gte.addToGoggleTooltip(tooltip, isShifting);
                //item = gte.getIcon(isShifting);
            }

            if (hasHoveringInformation) {
                if (!tooltip.isEmpty()) {
                    tooltip.add(Components.immutableEmpty());
                }

                IHaveHoveringInformation hte = (IHaveHoveringInformation)be;
                hoverAddedInformation = hte.addToTooltip(tooltip, player.isShiftKeyDown());
                if (goggleAddedInformation && !hoverAddedInformation) {
                    tooltip.remove(tooltip.size() - 1);
                }
            }

            if (be instanceof IDisplayAssemblyExceptions) {
                boolean exceptionAdded = ((IDisplayAssemblyExceptions)be).addExceptionToTooltip(tooltip);
                if (exceptionAdded) {
                    hasHoveringInformation = true;
                    hoverAddedInformation = true;
                }
            }

            if (!hasHoveringInformation && (hasHoveringInformation = hoverAddedInformation = TrainRelocator.addToTooltip(tooltip, mc.player.isShiftKeyDown()))) {
                //hoverTicks = prevHoverTicks + 1;
            }

            if (hasGoggleInformation && !goggleAddedInformation && hasHoveringInformation && !hoverAddedInformation) {
                //hoverTicks = 0;
                return false;
            } else {
                BlockState state = level.getBlockState(bp);
                if (wearingGoggles && AllBlocks.PISTON_EXTENSION_POLE.has(state)) {
                    Direction[] directions = Iterate.directionsInAxis(((Direction)state.getValue(PistonExtensionPoleBlock.FACING)).getAxis());
                    int poles = 1;
                    boolean pistonFound = false;

                    for(Direction dir : directions) {
                        int attachedPoles = PistonExtensionPoleBlock.PlacementHelper.get().attachedPoles(level, bp, dir);
                        poles += attachedPoles;
                        pistonFound |= level.getBlockState(bp.relative(dir, attachedPoles + 1)).getBlock() instanceof MechanicalPistonBlock;
                    }

                    if (!pistonFound) {
                        //hoverTicks = 0;
                        return false;
                    }

                    if (!tooltip.isEmpty()) {
                        tooltip.add(Components.immutableEmpty());
                    }

                    Lang.translate("gui.goggles.pole_length", new Object[0]).text(" " + poles).forGoggles(tooltip);
                }
            }

            return true;
        });
        //add critical degree showcase
        addTooltipProvider(((level, player, bp, tooltip) -> {
            if (player.getMainHandItem().getItem() instanceof BlockItem bi) {
                Block bb = bi.getBlock();
                if (!(bb instanceof WarheadAPDS) && !(bb instanceof WarheadAPCR))
                    return false;


                BlockState warheadState = bi.getBlock().defaultBlockState();
                BlockState armourState = level.getBlockState(bp);
                if (!(Minecraft.getInstance().hitResult instanceof BlockHitResult hitResult))
                    return false;
                if (armourState.isAir())
                    return false;

                Vector3d incidenceDir = JomlUtil.d(hitResult.getLocation().subtract(player.getEyePosition())).normalize();
                Vector3d normal = WorldUtil.getWorldDirection(level, bp, hitResult.getDirection());

                double warheadHardness = WapBlockInfos.ArmourRhae.valueOrDefaultOf(bi.getBlock().defaultBlockState());
                double armourHardness = WapBlockInfos.ArmourRhae.valueOrDefaultOf(armourState);

                double criticalDeg = WapBlockInfos.CriticalDegree.valueOrDefaultOf(warheadState);//BallisticsMath.calculateCriticalDegree(warheadHardness, armourHardness);
                double incidenceDeg = BallisticsMath.calIncidenceDeg(incidenceDir, normal);
                //todo translate
                tooltip.add(Component.translatable("info.vswap.critical_degree")
                    .append(": " + StrUtil.F2(criticalDeg) + "°")
                    .append(", ")
                    .append(Component.translatable("info.vswap.current_degree"))
                    .append(": " + StrUtil.F2(incidenceDeg) + "°")
                );

                double ricochetPob = WapBallisticMath.DEG.calRicochetPob(warheadState, incidenceDeg);
                tooltip.add(Component.translatable("term.vswap.ricochet_probability").append(": ").append(WapLang.percent01Format(ricochetPob)));


                /*CreateClient.OUTLINER.showLine(
                    "ballistic_incidence_vector", player.getEyePosition().subtract(0, 0.1, 0), hitResult.getLocation().subtract(0, 0.1, 0))
                    .lineWidth(0.05f)
                    .colored(WapColors.DARK_ORANGE);

                double resEnergy = BallisticsMath.calResistEnergyByDeg(armourState, incidenceDeg);
                tooltip.add(Component.literal("侵彻阈值:" + StrUtil.F2(resEnergy / 1000) + "KJ"));

                double abEnergy = BallisticsMath.calAbsorbedEnergyByDeg(warheadState, armourState, incidenceDeg);
                tooltip.add(Component.literal("吸收动能:" + StrUtil.F2(abEnergy / 1000) + "KJ"));

                double fatalKE = BallisticsMath.getFatalKEByDeg(armourHardness, incidenceDeg);
                tooltip.add(Component.literal("致命阈值:" + StrUtil.F2(fatalKE / 1000) + "KJ"));*/
                double rhea = WapBallisticMath.DEG.calRhae(armourState, incidenceDeg);
                //todo translate
                tooltip.add(WapBlockInfos.ArmourRhae.getDisplayValue(rhea));

                tooltip.add(WapBlockInfos.ArmourAbsorbRatio.getDisplayValue(armourState));

                double afterNorRhea = WapBallisticMath.DEG.calAfterNormalizationRhae(armourState, warheadState, incidenceDeg);
                tooltip.add(Component.translatable("term.vswap.after_normalization_rhae").append(": ").append(WapBlockInfos.ArmourRhae.formatter.apply(afterNorRhea)));

                double fatalPP = WapBallisticMath.DEG.calFatalPP(armourState, incidenceDeg);
                tooltip.add(Component.translatable("term.vswap.fatal_propellant").append(StrUtil.F2(fatalPP)));

                return true;
            }

            return false;
        }));
    }



    public final IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.gameMode == null || mc.player == null)
            return;
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        HitResult hitResult = mc.hitResult;
        if (!(hitResult instanceof BlockHitResult blockHitResult)) {
            //lastHovered = null;
            hoverTicks = 0;
            return;
        }

        for (Outliner.OutlineEntry entry : outlines.values()) {
            if (entry.isAlive()) {
                Outline outline = entry.getOutline();
                if ((outline instanceof ValueBox) && !((ValueBox) outline).isPassive) {
                    return;
                }
            }
        }

        ClientLevel level = mc.level;
        BlockPos pos = blockHitResult.getBlockPos();
        int prevHoverTicks = hoverTicks;  //what this for?
        hoverTicks++;
        //lastHovered = pos;  //what this for?

        BlockPos targetPos = proxiedOverlayPosition(level, pos);

        //gather tooltips
        boolean shouldShowTooltip = false;
        List<Component> tooltip = new ArrayList<>();

        for (var provider : tooltipProviders) {
            List<Component> curTooltip = new ArrayList<>();
            if (provider.test(level, mc.player, targetPos, curTooltip)) {
                tooltip.addAll(curTooltip);
                shouldShowTooltip = true;
            }
        }

        if (!shouldShowTooltip || tooltip.isEmpty()) {
            hoverTicks = 0;
            return;
        }

        renderTooltip(graphics, partialTicks, width, height, tooltip);
    };

    private void renderTooltip(GuiGraphics graphics, float partialTicks, int width, int height, List<Component> tooltip) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;
        ItemStack curGoogles = mc.player.getItemBySlot(EquipmentSlot.HEAD);

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        int tooltipTextWidth = 0;

        for(FormattedText textLine : tooltip) {
            int textLineWidth = mc.font.width(textLine);
            if (textLineWidth > tooltipTextWidth) {
                tooltipTextWidth = textLineWidth;
            }
        }

        int tooltipHeight = 8;
        if (tooltip.size() > 1) {
            tooltipHeight += 2;
            tooltipHeight += (tooltip.size() - 1) * 10;
        }

        CClient cfg = AllConfigs.client();
        int posX = width / 2 + (Integer)cfg.overlayOffsetX.get();
        int posY = height / 2 + (Integer)cfg.overlayOffsetY.get();
        posX = Math.min(posX, width - tooltipTextWidth - 20);
        posY = Math.min(posY, height - tooltipHeight - 20);
        float fade = Mth.clamp(((float)hoverTicks + partialTicks) / 24.0F, 0.0F, 1.0F);
        Boolean useCustom = (Boolean)cfg.overlayCustomColor.get();
        Color colorBackground = useCustom ? new Color((Integer)cfg.overlayBackgroundColor.get()) : Theme.c(Theme.Key.VANILLA_TOOLTIP_BACKGROUND).scaleAlpha(0.75F);
        Color colorBorderTop = useCustom ? new Color((Integer)cfg.overlayBorderColorTop.get()) : Theme.c(Theme.Key.VANILLA_TOOLTIP_BORDER, true).copy();
        Color colorBorderBot = useCustom ? new Color((Integer)cfg.overlayBorderColorBot.get()) : Theme.c(Theme.Key.VANILLA_TOOLTIP_BORDER, false).copy();
        if (fade < 1.0F) {
            poseStack.translate(Math.pow((double)(1.0F - fade), (double)3.0F) * (double)Math.signum((float)(Integer)cfg.overlayOffsetX.get() + 0.5F) * (double)8.0F, (double)0.0F, (double)0.0F);
            colorBackground.scaleAlpha(fade);
            colorBorderTop.scaleAlpha(fade);
            colorBorderBot.scaleAlpha(fade);
        }

        GuiGameElement.of(curGoogles).at((float)(posX + 10), (float)(posY - 16), 450.0F).render(graphics);
        if (!Mods.MODERNUI.isLoaded()) {
            RemovedGuiUtils.drawHoveringText(graphics, tooltip, posX, posY, width, height, -1, colorBackground.getRGB(), colorBorderTop.getRGB(), colorBorderBot.getRGB(), mc.font);
            poseStack.popPose();
        } else {
            MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;
            Window window = Minecraft.getInstance().getWindow();
            double guiScale = window.getGuiScale();
            double cursorX = mouseHandler.xpos();
            double cursorY = mouseHandler.ypos();
            ((MouseHandlerAccessor)mouseHandler).create$setXPos((double)Math.round(cursorX / guiScale) * guiScale);
            ((MouseHandlerAccessor)mouseHandler).create$setYPos((double)Math.round(cursorY / guiScale) * guiScale);
            RemovedGuiUtils.drawHoveringText(graphics, tooltip, posX, posY, width, height, -1, colorBackground.getRGB(), colorBorderTop.getRGB(), colorBorderBot.getRGB(), mc.font);
            ((MouseHandlerAccessor)mouseHandler).create$setXPos(cursorX);
            ((MouseHandlerAccessor)mouseHandler).create$setYPos(cursorY);
            poseStack.popPose();
        }
    }


    public static BlockPos proxiedOverlayPosition(Level level, BlockPos pos) {
        BlockState targetedState = level.getBlockState(pos);
        Block block = targetedState.getBlock();
        if (block instanceof IProxyHoveringInformation proxy) {
            return proxy.getInformationSource(level, pos, targetedState);
        }
        return pos;
    }



    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        /*event.registerAbove(VanillaGuiOverlay.AIR_LEVEL.id(), "remaining_air", RemainingAirOverlay.INSTANCE);
        event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "train_hud", TrainHUD.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "value_settings", CreateClient.VALUE_SETTINGS_HANDLER);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "track_placement", TrackPlacementOverlay.INSTANCE);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "goggle_info", GoggleOverlayRenderer.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "blueprint", BlueprintOverlayRenderer.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "linked_controller", LinkedControllerClientHandler.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "schematic", CreateClient.SCHEMATIC_HANDLER);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "toolbox", ToolboxHandlerClient.OVERLAY);*/
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "expert_goggle_info", INSTANCE.OVERLAY);
    }

    //public static void register() { }

}
