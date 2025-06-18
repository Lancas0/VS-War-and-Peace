package com.lancas.vswap.content.ui.ponder;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.WapConfig;
import com.lancas.vswap.content.WapBlocks;
import com.lancas.vswap.content.WapItems;
import com.lancas.vswap.content.block.blocks.industry.projector.ProjectorLenBe;
import com.lancas.vswap.content.block.blocks.cartridge.fuze.ImpactFuze;
import com.lancas.vswap.content.block.blocks.cartridge.primer.PrimerBlock;
import com.lancas.vswap.content.block.blocks.cartridge.propellant.ShelledPropellant;
import com.lancas.vswap.content.block.blocks.cartridge.warhead.HeWarhead;
import com.lancas.vswap.content.block.blocks.industry.dock.DockGreenPrintHolder;
import com.lancas.vswap.content.block.blocks.industry.projector.ProjectionCenter;
import com.lancas.vswap.content.block.blocks.industry.projector.VSProjector;
import com.lancas.vswap.content.block.blocks.industry.shredder.Shredder;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.subproject.pondervs.PonderVsSceneBuilder;
import com.lancas.vswap.subproject.mstandardized.MaterialStandardizedItem;
import com.lancas.vswap.subproject.pondervs.element.NoPointLineTextElement;
import com.lancas.vswap.subproject.pondervs.element.SimpleRenderBlocks;
import com.lancas.vswap.subproject.pondervs.sandbox.PonderMunitionBehaviour;
import com.lancas.vswap.subproject.pondervs.sandbox.PonderMunitionData;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.component.data.TweenData;
import com.lancas.vswap.subproject.sandbox.constraint.OrientationConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.SliderConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.SliderOrientationConstraint;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.RandUtil;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.chassis.StickerBlock;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.ponder.instruction.FadeIntoSceneInstruction;
import com.simibubi.create.foundation.utility.Pointing;
import com.simibubi.create.infrastructure.ponder.PonderIndex;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class WapPonderScenes {
    public static BlockClusterData makeStandardHeMunition(Direction dir) {
        BlockClusterData blockData = new BlockClusterData();
        Vector3i origin = new Vector3i();
        blockData
            .chainedSetBlock(origin, PrimerBlock.faceTo(dir))
            .chainedSetBlock(origin.add(dir.getStepX(), dir.getStepY(), dir.getStepZ()), ShelledPropellant.getState(false, dir))
            .chainedSetBlock(origin.add(dir.getStepX(), dir.getStepY(), dir.getStepZ()), HeWarhead.faceTo(dir))
            .chainedSetBlock(origin.add(dir.getStepX(), dir.getStepY(), dir.getStepZ()), ImpactFuze.getState(false, dir));
        return blockData;
    }
    public static BlockClusterData makeStandardHeProjectile(Direction dir) {
        BlockClusterData blockData = new BlockClusterData();
        Vector3i origin = new Vector3i();
        blockData
            .chainedSetBlock(origin.add(dir.getStepX(), dir.getStepY(), dir.getStepZ()), HeWarhead.faceTo(dir))
            .chainedSetBlock(origin.add(dir.getStepX(), dir.getStepY(), dir.getStepZ()), ImpactFuze.getState(false, dir));
        return blockData;
    }
    public static BlockClusterData makeStandardHeRemainShell(Direction dir, boolean emptyPropellant) {
        BlockClusterData blockData = new BlockClusterData();
        Vector3i origin = new Vector3i();
        blockData
            .chainedSetBlock(origin, PrimerBlock.faceTo(dir))  //todo triggered primer
            .chainedSetBlock(origin.add(dir.getStepX(), dir.getStepY(), dir.getStepZ()), ShelledPropellant.getState(emptyPropellant, dir));
        return blockData;
    }

    public static final String CARBON_FIBER_SAIL_ID = "industry/carbon_fiber_sail";
    public static void carbonFiberSail(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title(CARBON_FIBER_SAIL_ID, "Carbon Fiber Sail");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(0.9F);
        scene.showBasePlate();

        //scene.world.showSection(util.select.layer(0), Direction.UP);
        //scene.idle(5);
        BlockPos bearingPos = util.grid.at(2, 1, 2);

        //show bearing
        scene.world.showSection(util.select.position(bearingPos), Direction.DOWN);
        scene.idle(5);

        //show sails
        Selection sailsSection = util.select.fromTo(1, 2, 2, 3, 4, 2);
        ElementLink<WorldSectionElement> oldSailsSectionLink = scene.world.showIndependentSection(
            sailsSection,
            Direction.DOWN
        );
        //scene.world.showSection(sailsSection, Direction.DOWN);
        scene.idle(10);

        scene.world.rotateBearing(bearingPos, 180f, 70);
        scene.world.configureCenterOfRotation(oldSailsSectionLink, util.vector.centerOf(bearingPos));
        scene.world.rotateSection(oldSailsSectionLink, 0f, 180f, 0, 70);
        scene.overlay.showText(60)
            .text("It takes 8 traditional sail blocks to start a Windmill Bearing.")
            .pointAt(util.vector.blockSurface(util.grid.at(1, 3, 2), Direction.WEST))
            .placeNearTarget()
            .attachKeyFrame();

        scene.idle(70);

        scene.world.hideIndependentSection(oldSailsSectionLink, Direction.UP, 15);
        scene.idle(15);

        sailsSection.forEach(bp -> {
            if (bp.equals(new BlockPos(2, 2, 2)) || bp.equals(new BlockPos(2, 3, 2)))
                scene.world.modifyBlock(bp, s -> WapBlocks.Industrial.Create.CARBON_FIBER_SAIL.getDefaultState(), false);
            else
                scene.world.modifyBlock(bp, s -> Blocks.AIR.defaultBlockState(), false);
        });
        ElementLink<WorldSectionElement> newSailSection = scene.world.showIndependentSection(sailsSection, Direction.DOWN);
        scene.world.configureCenterOfRotation(newSailSection, util.vector.centerOf(bearingPos));
        scene.idle(20);


        scene.world.rotateBearing(bearingPos, 360f, 140);
        scene.world.rotateSection(newSailSection, 0f, 360f, 0, 140);
        scene.overlay.showText(60)
            .text("One Carbon Fiber Sail is equals to 4 sail blocks.")
            .pointAt(util.vector.blockSurface(util.grid.at(1, 3, 2), Direction.WEST))
            .placeNearTarget()
            .attachKeyFrame();

        scene.idle(60);
    }

    public static class Projector {
        public static final String GP_PROJECT_BASIC_ID = "industry/gp_projector_basic";
        public static void gpProjectorBasic(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(GP_PROJECT_BASIC_ID, "GreenPrint & VS Projector Basic");
            scene.configureBasePlate(0, 0, 5);
            scene.scaleSceneView(0.9F);
            scene.showBasePlate();

            PonderVsSceneBuilder vspScene = new PonderVsSceneBuilder(scene);

            BlockPos projectorPos = util.grid.at(2, 1, 2);
            BlockPos lenPos = util.grid.at(2, 2, 2);
            BlockPos shipBp = util.grid.at(2, 4, 2);


            //show projector
            scene.world.showSection(util.select.position(projectorPos), Direction.DOWN);
            scene.idle(10);

            //use empty greenPrint on the projector
            InputWindowElement greenPrint1 = new InputWindowElement(
                util.vector.blockSurface(projectorPos, Direction.EAST),
                Pointing.RIGHT
            ).withItem(WapItems.GREEN_PRINT.asStack());
            scene.overlay.showControls(greenPrint1, 30);
            scene.world.modifyBlock(projectorPos, s -> s.setValue(VSProjector.HAS_GREEN_PRINT, true), false);

            scene.idle(10);

            //------Make Ship-------
            Vector3d shipInitialPos = new Vector3d(2, 4, 2);
            RigidbodyData rd = new RigidbodyData().setPositionImmediately(shipInitialPos);
            BlockClusterData blockData = new BlockClusterData();
            blockData.setBlock(new Vector3i(0, 0, 0), ProjectionCenter.defaultState(ProjectionCenter.class));
            UUID shipUuid = vspScene.vs.makeShipWithScaleTween(rd, blockData,
                new Vector3d(0, 0, 0), new Vector3d(1, 1, 1),
                20,
                t -> t.curve(TweenData.Curve.OutElastic)
            );

            scene.overlay.showText(60)
                .text("Use empty GreenPrint on a ship, a ship with Projection Center arise.")
                .pointAt(util.vector.centerOf(shipBp))
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(40);

            //------Build Ship---------
            int times = 0;
            scene.idle(5);
            for (int x = 2; x <= 4; ++x)
                for (int y = 3; y <= 5; ++y)
                    for (int z = 2; z <= 4; ++z) {
                        BlockState curState;
                        if (x == 2 && y == 3 && z == 2)
                            curState = Blocks.WHITE_WOOL.defaultBlockState();
                        else if (y == 3 && z == 2)
                            curState = Blocks.RED_WOOL.defaultBlockState();
                        else if (x == 2 && z == 2)
                            curState = Blocks.LIME_WOOL.defaultBlockState();
                        else if (x == 2 && y == 3)
                            curState = Blocks.BLUE_WOOL.defaultBlockState();
                        else
                            curState = Blocks.AIR.defaultBlockState();

                        if (curState.isAir())
                            continue;

                        times++;
                        if (times == 4)
                            scene.overlay.showText(60)
                                .pointAt(util.vector.centerOf(shipBp))
                                .placeNearTarget()
                                .attachKeyFrame()
                                .text("You can start building your ship from here.");

                        Vector3i shipLocalPos = new Vector3i(x - 2, y - 3, z - 2);
                        vspScene.vs.setShipBlock(shipUuid, shipLocalPos, curState);

                        scene.idle(8);
                    }

            //show len
            scene.world.showSection(util.select.position(lenPos), Direction.EAST);
            scene.idle(20);

            Vector3dc increaseMulScale = new Vector3d(1 + ProjectorLenBe.SCALE_STEP);
            Vector3dc decreaseMulScale = new Vector3d(1 - ProjectorLenBe.SCALE_STEP);



            InputWindowElement right = new InputWindowElement(
                util.vector.blockSurface(projectorPos, Direction.EAST),
                Pointing.RIGHT
            ).rightClick();
            scene.overlay.showControls(right, 30);
            for (int i = 0; i < 4; ++i) {
                scene.world.modifyBlockEntity(lenPos, ProjectorLenBe.class, be -> {
                    be.stepScale(true);
                });
                vspScene.vs.modifyShip(shipUuid, s -> s.getRigidbody().getDataWriter().mulScale(increaseMulScale));

                if (i == 2)
                    scene.overlay.showText(60)
                        .text("You can scale the ship by interacting with ProjectorLen")
                        .pointAt(util.vector.centerOf(shipBp))
                        .placeNearTarget()
                        .attachKeyFrame();

                scene.idle(8);
            }

            scene.idle(10);
            InputWindowElement shift = new InputWindowElement(
                util.vector.blockSurface(projectorPos, Direction.EAST),
                Pointing.RIGHT
            ).rightClick().whileSneaking();
            scene.overlay.showControls(shift, 20);
            for (int i = 0; i < 8; ++i) {
                scene.world.modifyBlockEntity(lenPos, ProjectorLenBe.class, be -> {
                    be.stepScale(false);
                });
                vspScene.vs.modifyShip(shipUuid, s -> s.getRigidbody().getDataWriter().mulScale(decreaseMulScale));

                scene.idle(4);
            }

            scene.idle(15);
            InputWindowElement holdRight = new InputWindowElement(
                util.vector.blockSurface(projectorPos, Direction.EAST),
                Pointing.RIGHT
            ).rightClick().scroll();
            scene.overlay.showControls(holdRight, 60);
            int raiseOrDropTicks = 25;
            double raise = 2;
            double drop = -0.5;

            //------Tween Ship Up and Down
            vspScene.vs.tweenShipPosition(
                shipUuid,
                shipInitialPos,
                new Vector3d(shipInitialPos).add(0, raise, 0),
                raiseOrDropTicks
            ).curve(TweenData.Curve.InOutCubic); //todo actually use what curve?
            scene.idle(5);
            scene.overlay.showText(100)
                .text("You can also raise or lower the Projection Ship by right clicking while scrolling")
                .pointAt(util.vector.centerOf(shipBp))
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(raiseOrDropTicks);

            vspScene.vs.tweenShipPosition(
                shipUuid,
                new Vector3d(shipInitialPos).add(0, raise, 0),
                new Vector3d(shipInitialPos).add(0, drop, 0),
                raiseOrDropTicks
            ).curve(TweenData.Curve.InOutCubic);
            scene.idle(raiseOrDropTicks);
        }

        public static final String EXTRACT_AND_INSERT_GP_ID = "industry/extract_and_insert_gp";
        public static void extractAndInsertGp(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(EXTRACT_AND_INSERT_GP_ID, "Extract And Insert GreenPrint");
            scene.configureBasePlate(0, 0, 5);
            scene.scaleSceneView(0.9F);
            scene.showBasePlate();

            BlockPos fromProjectorBp = util.grid.at(1, 1, 2);
            BlockPos toProjectorBp = util.grid.at(3, 1, 1);
            BlockPos gpHolderBp = util.grid.at(3, 1, 3);


            PonderVsSceneBuilder vspScene = new PonderVsSceneBuilder(scene);

            //show blocks and ship
            scene.world.showSection(util.select.position(fromProjectorBp), Direction.DOWN);
            scene.idle(2);
            scene.world.showSection(util.select.position(toProjectorBp), Direction.DOWN);
            scene.idle(2);
            scene.world.showSection(util.select.position(gpHolderBp), Direction.DOWN);
            scene.idle(2);


            //-------------make initial ship---------
            RigidbodyData rd = new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(fromProjectorBp.above(2)));
            BlockClusterData blockData = makeStandardHeMunition(Direction.UP);
            UUID shipUuid = vspScene.vs.makeShipWithScaleTween(
                rd, blockData,
                new Vector3d(0, 0, 0), new Vector3d(1, 1, 1),
                20,
                t -> t.curve(TweenData.Curve.OutElastic)
            );


            scene.idle(35);

            //extract from from
            InputWindowElement shiftRightExtract = new InputWindowElement(
                util.vector.blockSurface(fromProjectorBp, Direction.WEST),
                Pointing.LEFT
            ).whileSneaking().rightClick().withItem(WapItems.GREEN_PRINT.asStack());
            scene.overlay.showControls(shiftRightExtract, 25);
            scene.world.modifyBlock(fromProjectorBp, state -> state.setValue(VSProjector.HAS_GREEN_PRINT, false), false);
            vspScene.vs.hideShip(shipUuid);

            scene.idle(30);

            //insert to to
            InputWindowElement rightInsert = new InputWindowElement(
                util.vector.blockSurface(toProjectorBp, Direction.EAST),
                Pointing.RIGHT
            ).rightClick().withItem(WapItems.GREEN_PRINT.asStack());
            scene.overlay.showControls(rightInsert, 20);
            scene.world.modifyBlock(toProjectorBp, state -> state.setValue(VSProjector.HAS_GREEN_PRINT, true), false);
            vspScene.vs.showShip(shipUuid);
            vspScene.vs.setShipPos(shipUuid, JomlUtil.dCenter(toProjectorBp.above(2)));

            scene.idle(30);

            //extract from to
            InputWindowElement shiftRightExtract2 = new InputWindowElement(
                util.vector.blockSurface(toProjectorBp, Direction.EAST),
                Pointing.RIGHT
            ).whileSneaking().rightClick().withItem(WapItems.GREEN_PRINT.asStack());
            scene.overlay.showControls(shiftRightExtract2, 15);
            scene.world.modifyBlock(toProjectorBp, state -> state.setValue(VSProjector.HAS_GREEN_PRINT, false), false);
            vspScene.vs.hideShip(shipUuid);

            scene.idle(20);

            //insert to gpHolder
            InputWindowElement insertToGpHolder = new InputWindowElement(
                util.vector.blockSurface(gpHolderBp, Direction.EAST),
                Pointing.RIGHT
            ).rightClick().withItem(WapItems.GREEN_PRINT.asStack());
            scene.overlay.showControls(insertToGpHolder, 30);
            scene.world.modifyBlock(gpHolderBp, s -> s.setValue(DockGreenPrintHolder.HAS, true), false);
            scene.idle(30);
        }

        //todo empty greenPrint
        public static final String GP_USE_ON_SHIP_ID = "industry/gp_use_on_ship";
        public static void gpUseOnShip(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(GP_USE_ON_SHIP_ID, "Use GreenPrint on Ship");
            scene.configureBasePlate(0, 0, 5);
            scene.scaleSceneView(0.9F);
            scene.showBasePlate();

            int gravityTweenTicks = 15;
            double dropDist = (gravityTweenTicks * 0.05) * (gravityTweenTicks * 0.05) * 4.9;
            Vector3d shipInitialPos = new Vector3d(1.5, 1 + dropDist, 3.5);

            RigidbodyData rd = new RigidbodyData().setPositionImmediately(shipInitialPos);
            BlockClusterData blockData = makeStandardHeMunition(Direction.UP);
            //SandBoxPonderShip ship = new SandBoxPonderShip(UUID.randomUUID(), rd, blockData);

            PonderVsSceneBuilder vspScene = new PonderVsSceneBuilder(scene);

            BlockPos projectorPos = util.grid.at(2, 1, 2);
            BlockPos lenPos = util.grid.at(2, 2, 2);

            //show projector and ship
            scene.world.showSection(util.select.position(projectorPos), Direction.DOWN);

            //-------Make Ship
            UUID shipUuid = vspScene.vs.makeShipWithPositionTween(
                rd, blockData,
                shipInitialPos,
                new Vector3d(shipInitialPos).add(0, -dropDist, 0),
                gravityTweenTicks,
                t -> t.curve(TweenData.Curve.OutBounce)
            );

            scene.idle(20);

            //use greenPrint on the ship
            InputWindowElement greenPrint1 = new InputWindowElement(
                util.vector.blockSurface(new BlockPos(1, 2, 3), Direction.WEST),
                Pointing.LEFT
            ).withItem(WapItems.GREEN_PRINT.asStack());
            scene.overlay.showControls(greenPrint1, 30);

            scene.idle(15);

            vspScene.vs.hideShip(shipUuid);
            scene.overlay.showText(60)
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(new Vec3(shipInitialPos.x, shipInitialPos.y, shipInitialPos.z))
                .text("Use GreenPrint on a ship, the ship can be saved.");

            scene.idle(50);

            InputWindowElement greenPrint2 = new InputWindowElement(
                util.vector.blockSurface(projectorPos, Direction.EAST),
                Pointing.RIGHT
            ).withItem(WapItems.GREEN_PRINT.asStack());
            scene.overlay.showControls(greenPrint2, 30);
            scene.world.modifyBlock(projectorPos, s -> s.setValue(VSProjector.HAS_GREEN_PRINT, true), false);

            scene.idle(15);

            vspScene.vs.showShip(shipUuid);
            vspScene.vs.modifyShip(shipUuid, s -> {
                s.getRigidbody().getDataWriter()
                    .setPosition(new Vector3d(2.5, 3.5, 2.5));
            });

            scene.overlay.showText(100)
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.blockSurface(projectorPos, Direction.WEST))
                .text("Use GreenPrint on VS Projector, the projector will show the ship saved by GreenPrint.");


            scene.idle(50);
        }

        public static final String PROJECTOR_ROTATION_ID = "industry/projector_rotation";
        public static void projectorRotation(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(PROJECTOR_ROTATION_ID, "VS Projector Rotation");
            scene.configureBasePlate(0, 0, 5);
            scene.scaleSceneView(0.9F);
            scene.showBasePlate();

            BlockPos motorPos = util.grid.at(1, 1, 2);
            BlockPos rightProjectorBp = util.grid.at(1, 2, 2);
            Selection gears = util.select.fromTo(2, 2, 2, 2, 2, 3);
            BlockPos leftProjectorBp = util.grid.at(3, 2, 3);

            PonderVsSceneBuilder vspScene = new PonderVsSceneBuilder(scene);

            scene.world.showSection(util.select.position(motorPos), Direction.DOWN);
            //scene.idle(2);
            scene.world.showSection(util.select.position(rightProjectorBp), Direction.DOWN);
            //scene.idle(2);
            scene.world.showSection(gears, Direction.DOWN);
            //scene.idle(2);
            scene.world.showSection(util.select.position(leftProjectorBp), Direction.DOWN);
            //scene.idle(2);


            RigidbodyData rd = new RigidbodyData();
            rd.setPositionImmediately(JomlUtil.dCenter(rightProjectorBp.above(1)))
                .setOmega(new Vector3d(0, 1, 0));
            BlockClusterData blockData = makeStandardHeMunition(Direction.UP);

            vspScene.vs.makeShip(rd, blockData, false);

            //scene.idle(2);
            RigidbodyData rd2 = new RigidbodyData().copyData(rd);
            rd2.setPositionImmediately(JomlUtil.dCenter(leftProjectorBp.above(1)))
                .setOmega(new Vector3d(0, -1, 0));
            vspScene.vs.makeShip(rd2, blockData, false);
        }
    }

    public static class ShredderAndMs {
        public static final String SHREDDER_USAGE_ID = "industry/shredder_usage";
        public static void shredderUsage(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(SHREDDER_USAGE_ID, "Shredder Usage");
            scene.configureBasePlate(0, 0, 5);
            scene.scaleSceneView(0.9F);
            scene.showBasePlate();

            BlockEntry<Shredder> shredderBlock = WapBlocks.Industrial.Machine.SHREDDER;

            BlockPos shredderPos = util.grid.at(2, 2, 2);
            Selection straightPowerSection = util.select.fromTo(2, 2, 3, 2, 2, 4);
            BlockPos beltStart = util.grid.at(0, 2, 2);
            Selection beltSection = util.select.fromTo(0, 2, 2, 1, 2, 2);
            BlockPos beltPowerGearPos = util.grid.at(1, 2, 3);
            BlockPos barrelPos = shredderPos.below();

            //show shredder and power
            scene.world.showSection(util.select.position(shredderPos), Direction.DOWN);
            scene.world.showSection(straightPowerSection, Direction.NORTH);

            scene.idle(15);

            //put item in
            var inLink = scene.world.createItemEntity(
                shredderPos.above(2).getCenter(),
                new Vec3(0, 0, 0),
                shredderBlock.asStack()
            );
            scene.overlay.showText(50)
                .text("Shredder can shred the item fall on it.")
                .pointAt(shredderPos.getCenter())
                .attachKeyFrame()
                .placeNearTarget();

            scene.idle(7);
            //remove itemStack
            scene.world.modifyEntity(inLink, Entity::discard);


            scene.idle(45);

            var outputLink = scene.world.createItemEntity(
                shredderPos.below().getCenter(),
                new Vec3(0, 0, 0),
                MaterialStandardizedItem.fromBlock(shredderBlock.get(), 1)
            );
            scene.overlay.showText(60)
                .text("After finishing shredding, the output will drop from it's bottom side.")
                .pointAt(shredderPos.below().getCenter())
                .attachKeyFrame()
                .placeNearTarget();

            scene.idle(50);

            scene.world.modifyEntity(outputLink, Entity::discard);

            //show belt and barrel
            scene.world.showSection(util.select.position(barrelPos), Direction.UP);
            scene.idle(3);
            scene.world.showSection(util.select.position(beltPowerGearPos), Direction.NORTH);
            scene.idle(3);
            scene.world.showSection(beltSection, Direction.SOUTH);
            scene.idle(20);

            scene.world.createItemOnBelt(beltStart, Direction.DOWN, shredderBlock.asStack());
            scene.overlay.showText(40)
                .text("Shredder can accept item from belt")
                .pointAt(beltStart.getCenter())
                .attachKeyFrame()
                .placeNearTarget();

            scene.idle(20);
            //remove the item on belt
            scene.world.removeItemsFromBelt(beltStart.east());

            scene.idle(30);

            scene.overlay.showSelectionWithText(util.select.position(barrelPos), 150)
                .text("Shredder can also output to the container below it.")
                .colored(PonderPalette.GREEN)
                .pointAt(util.vector.blockSurface(barrelPos, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget();

            scene.idle(40);
        }

        private static BlockClusterData buildTreeBlockCluster() {
            BlockState upwardLog = Blocks.OAK_LOG.defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
            BlockState leaf = Blocks.OAK_LEAVES.defaultBlockState();

            BlockClusterData bd = new BlockClusterData();
            bd
                .chainedSetBlock(new Vector3i(0, 0, 0), upwardLog)
                .chainedSetBlock(new Vector3i(0, 1, 0), upwardLog)
                .chainedSetBlock(new Vector3i(0, 2, 0), upwardLog)
                .chainedSetBlock(new Vector3i(0, 3, 0), upwardLog)

                .chainedSetBlock(new Vector3i(-1, 2, 0), leaf)
                .chainedSetBlock(new Vector3i(-1, 3, 0), leaf)
                .chainedSetBlock(new Vector3i(1, 2, 0), leaf)
                .chainedSetBlock(new Vector3i(1, 3, 0), leaf)
                .chainedSetBlock(new Vector3i(0, 2, -1), leaf)
                .chainedSetBlock(new Vector3i(0, 3, -1), leaf)
                .chainedSetBlock(new Vector3i(0, 2, 1), leaf)
                .chainedSetBlock(new Vector3i(0, 3, 1), leaf)
            ;

            return bd;
        }

        public static final String SHREDDER_BREAKING_ID = "industry/shredder_breaking";
        public static void shredderBreaking(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(SHREDDER_BREAKING_ID, "Shredder Breaking Effect");
            scene.configureBasePlate(0, 0, 5);
            scene.scaleSceneView(0.9F);
            scene.showBasePlate();

            BlockPos centerShredderPos = util.grid.at(2, 2, 2);
            BlockPos shaftShredderPos = util.grid.at(2, 2, 3);
            BlockPos straightGearPos = util.grid.at(2, 3, 4);
            BlockPos destroyPlankPos = util.grid.at(2, 3, 2);

            Selection barrels = util.select.layer(1);
            Selection otherGears = util.select.position(1, 3, 4).add(util.select.position(3, 3, 4));
            Selection otherShredders = util.select.fromTo(1, 2, 1, 3, 2, 3)
                .substract(util.select.position(centerShredderPos))
                .substract(util.select.position(shaftShredderPos));

            scene.world.modifyBlock(shaftShredderPos,
                s -> AllBlocks.SHAFT.getDefaultState().setValue(BlockStateProperties.AXIS, Direction.Axis.Z),
                false
            );
            scene.world.setKineticSpeed(util.select.layer(2), 32);

            scene.world.showSection(util.select.position(centerShredderPos), Direction.UP);
            scene.world.showSection(util.select.position(shaftShredderPos), Direction.NORTH);
            scene.world.showSection(util.select.position(straightGearPos), Direction.NORTH);

            scene.idle(15);

            scene.world.showSection(util.select.position(destroyPlankPos), Direction.DOWN);


            for (int progress = 0; progress < 10; ++progress) {
                scene.world.incrementBlockBreakingProgress(destroyPlankPos);

                if (progress == 2)
                    scene.overlay.showSelectionWithText(util.select.position(destroyPlankPos), 60)
                        .text("Shredder can break block in front it.")
                        .colored(PonderPalette.GREEN)
                        .attachKeyFrame()
                        .pointAt(util.vector.blockSurface(destroyPlankPos, Direction.WEST));

                scene.idle(4);
            }
            scene.idle(5);

            var outputLink = scene.world.createItemEntity(
                centerShredderPos.below().getCenter(),
                new Vec3(0, 0, 0),
                MaterialStandardizedItem.fromBlock(Blocks.OAK_PLANKS, 1)
            );
            scene.overlay.showText(55)
                .text("After breaking block, the output will drop from it's bottom side.")
                .pointAt(centerShredderPos.below().getCenter())
                .attachKeyFrame()
                .placeNearTarget();

            scene.idle(50);

            //--------full section----------

            scene.world.modifyBlock(shaftShredderPos, s -> Shredder.faceTo(Direction.UP), true);
            scene.world.setKineticSpeed(util.select.layer(2), 32);
            scene.idle(4);
            scene.world.showSection(otherGears, Direction.NORTH);
            scene.idle(4);
            scene.world.showSection(otherShredders, Direction.DOWN);
            scene.idle(4);
            scene.world.showSection(barrels, Direction.UP);
            scene.idle(5);


            //---------break ship---------
            PonderVsSceneBuilder pvs = new PonderVsSceneBuilder(scene);
            Vector3dc gravity = new Vector3d(0, -9.8, 0);
            int height = 14;
            RigidbodyData rd = new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(centerShredderPos.above(height + 1)));
            BlockClusterData bd = buildTreeBlockCluster();

            UUID shipUuid = pvs.vs.makeShip(rd, bd, false);

            //int ticks = pvs.vs.gravitySimulateForHeight(shipUuid, gravity, height, true);
            //scene.idle(ticks + 2);  //wait for drop

            //util whole tree is broken
            for (int i = 0; i < 4; ++i) {
                int breakingLayer = i;

                int dropTicks = pvs.vs.gravitySimulateForHeight(shipUuid, gravity, height, true);
                scene.idle(dropTicks);  //wait for drop

                /*for (int progress = 0; progress < 10; ++progress) {
                    scene.idle(4);

                    pvs.vs.increaseDestroyBlocksProgress(
                        shipUuid,
                        s -> s.selectExistBlocks(-1, breakingLayer, -1, 1, breakingLayer, 1),
                        true,
                        null
                    );
                }*/
                int breakTicks = pvs.vs.destroyBlocksUtilBreak(
                    shipUuid,
                    s -> s.selectExistBlocks(-1, breakingLayer, -1, 1, breakingLayer, 1),
                    2,
                    null
                );
                scene.idle(breakTicks);

                if (i == 0)
                    scene.overlay.showText(80)
                        .text("Shredder can also break blocks in ship")
                        .colored(PonderPalette.GREEN)
                        .attachKeyFrame()
                        .pointAt(util.vector.blockSurface(destroyPlankPos, Direction.WEST))
                        .placeNearTarget();

                height = 1;  //after initial drop, the height will be 1;
            }

            scene.idle(10);
        }

        public static String IntegrationId = "industry/shredder_ms_integration";
        public static void integration(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(IntegrationId, "Material Integration");
            scene.configureBasePlate(0, 0, 5);
            scene.scaleSceneView(0.9F);
            scene.showBasePlate();
            PonderVsSceneBuilder ponderVs = new PonderVsSceneBuilder(scene);
            PonderWorld ponderWorld = ponderVs.getPonderScene().getWorld();

            BlockPos leftShredder = util.grid.at(3, 2, 2);
            BlockPos rightShredder = util.grid.at(1, 2, 2);
            BlockPos center = util.grid.at(2, 1, 2);

            scene.idle(15);
            scene.world.showSection(util.select.layer(2), Direction.UP);


            scene.idle(5);


            ElementLink<EntityElement> input1Link = scene.world.createItemEntity(
                leftShredder.east(3).below().getCenter(),
                new Vec3(0, 0, 0),
                Blocks.DIRT.asItem().getDefaultInstance()
            );
            ponderVs.world.throwEntityTo(input1Link, JomlUtil.dFaceCenter(leftShredder, Direction.EAST).add(0, 1, 0), (s, e) -> e.discard());
            scene.idle(20);

            ElementLink<EntityElement> input2Link = scene.world.createItemEntity(
                rightShredder.west(3).below().getCenter(),
                new Vec3(0, 0, 0),
                Blocks.GRASS_BLOCK.asItem().getDefaultInstance()
            );
            ponderVs.world.throwEntityTo(input2Link, JomlUtil.dFaceCenter(rightShredder, Direction.WEST).add(0, 1, 0), (s, e) -> e.discard());
            scene.idle(20);


            scene.world.modifyBlock(center, s -> Blocks.AIR.defaultBlockState(), false);  //temproy remove the block


            ElementLink<EntityElement> output1Link = scene.world.createItemEntity(
                util.vector.blockSurface(leftShredder, Direction.WEST),
                new Vec3(0, 0, 0),
                MaterialStandardizedItem.fromBlock(Blocks.DIRT, 1)
            );
            ponderVs.world.throwEntityTo(output1Link, JomlUtil.dCenter(center).add(0, 0, 0), null);
            scene.idle(20);

            ElementLink<EntityElement> output2Link = scene.world.createItemEntity(
                util.vector.blockSurface(rightShredder, Direction.EAST),
                new Vec3(0, 0, 0),
                MaterialStandardizedItem.fromBlock(Blocks.GRASS_BLOCK, 1)
            );
            ponderVs.world.throwEntityTo(output2Link, JomlUtil.dCenter(center).add(0, 0, 0), (s, e) -> e.discard());
            scene.idle(10);


            scene.overlay.showSelectionWithText(util.select.position(center), 60)
                .text("Blocks in same category will be shredded into same item")
                .pointAt(center.getCenter())
                .attachKeyFrame()
                .colored(PonderPalette.GREEN)
                .placeNearTarget();
            scene.idle(40);
            scene.overlay.showText(60)
                .text("Don't worry, you can place any block in same category with Material")
                .pointAt(center.getCenter())
                .attachKeyFrame();

            scene.idle(40);
            scene.world.modifyEntity(output1Link, Entity::discard);

            scene.world.hideSection(util.select.layer(2), Direction.UP);

            scene.idle(20);

            scene.world.modifyBlock(center, s -> Blocks.OAK_PRESSURE_PLATE.defaultBlockState(), false);  //as if remove the block: pressure plate won't collision

            BlockPos dirtPos = util.grid.at(1, 1, 1);
            ItemStack dirtMsStack = MaterialStandardizedItem.fromBlock(Blocks.DIRT, 1);

            InputWindowElement placeDirt = new InputWindowElement(
                util.vector.blockSurface(dirtPos, Direction.WEST),
                Pointing.LEFT
            ).withItem(dirtMsStack);
            scene.overlay.showControls(placeDirt, 40);
            scene.idle(5);

            scene.world.modifyBlock(center, s -> Blocks.DIRT_PATH.defaultBlockState(), false);
            scene.world.showSection(util.select.position(dirtPos), Direction.DOWN);

            scene.idle(5);

            /*scene.overlay.showText(60)
                .text("You can place block with Material")
                .pointAt(util.vector.blockSurface(dirtPos, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget();*/

            scene.idle(30);
            scene.overlay.showText(100)
                .text("With " + Component.translatable("key.vswap.switch_key").getString() + " you can switch the block to place.")
                .pointAt(util.vector.blockSurface(dirtPos, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget();

            int times = 0;
            for (int x = 1; x <= 3; ++x)
                for (int y = 1; y <= 3; ++y) {
                    if (x == 1 && y == 1)
                        continue;

                    times++;

                    BlockPos cur = new BlockPos(x, 1, y);
                    scene.world.showSection(util.select.position(cur), Direction.DOWN);
                    scene.idle(4);
                }
        }
    }

    public static class Artillery {
        public static String AssemblyId = "artillery/artillery_assembly";
        public static void assembly(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(AssemblyId, "Artillery Assembly");
            scene.removeShadow();
            scene.configureBasePlate(0, 1, 7);
            scene.scaleSceneView(0.9F);
            scene.showBasePlate();  //显示底面
            //scene.rotateCameraY((float)WapCommonConfig.hotXYZ.y);
            scene.rotateCameraY(-20);

            PonderVsSceneBuilder vsp = new PonderVsSceneBuilder(scene);

            //通过scene.world显示方块

            BlockPos breechPos = util.grid.at(2, 3, 6);
            BlockPos barrelTip = util.grid.at(2, 3, 1);
            Selection artillerySection = util.select.fromTo(barrelTip, breechPos);
            //scene.world.showSection(artillerySection, Direction.NORTH);;
            //--------make ship-------
            //RigidbodyData
            RigidbodyData rd = new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(breechPos));
            BlockClusterData bd = vsp.util.ship.copyWorldSectionAsShipBlocks(artillerySection, JomlUtil.i(breechPos));
            UUID shipUuid = vsp.vs.makeShip(rd, bd, false);
            UUID oriUuid = vsp.vs.makeWithGroundConstraint((uuid, groundShip) ->
                new OrientationConstraint(
                    uuid,
                    groundShip.getUuid(), shipUuid,
                    new Quaterniond(), new Quaterniond()
                )
            );

            scene.idle(15);

            int glueTicks = 15;
            int keepTicks = 30;
            vsp.overlay.showAndTweenOutline("SuperGlueOutline",
                vsp.util.block.blockFace(barrelTip, Direction.NORTH),
                vsp.util.block.boundBetweenIncluding(barrelTip, breechPos),
                PonderPalette.GREEN,
                glueTicks, keepTicks
            ).curve(TweenData.Curve.InQuint);

            scene.idle(10);
            InputWindowElement glueUse = new InputWindowElement(vsp.util.block.midBetween(barrelTip, breechPos), Pointing.LEFT)
                .withItem(AllItems.SUPER_GLUE.asStack());
            scene.overlay.showControls(glueUse, glueTicks);

            scene.idle(glueTicks + keepTicks);

            scene.overlay.showText(50)
                .text("You can use any Physical Bearing Block to control the artillery");

            scene.idle(30);

            if (ModList.get().isLoaded("vscontrolcraft")) {
                Selection vsControlCraftSection = util.select.fromTo(2, 1, 6, 2, 2, 6);
                scene.world.showSection(vsControlCraftSection, Direction.UP);

                /*scene.overlay.showSelectionWithText(vsControlCraftSection, 60)
                    .text("You can use ")
                    .pointAt(vsControlCraftSection.getCenter())
                    .placeNearTarget()
                    .attachKeyFrame()
                    .colored(PonderPalette.OUTPUT);*/

                scene.idle(20);

                int rotTicks = 20;
                int intervalTicks = 5;
                Quaterniond fromRot = new Quaterniond();
                Quaterniond toRot1 = vsp.util.transform.rotOfAngleAxisDeg(20, 1, 0, 0);// new Quaterniond(new AxisAngle4d(3, 1, 0, 0));//vsp.util.transform.rotOfAngleAxisDeg(45, -1, 0, 0);
                Quaterniond toRot2 = vsp.util.transform.rotOfAngleAxisDeg(-30, 1, 0, 0);

                vsp.vs.<OrientationConstraint>tweenConstraint(oriUuid, (ori, t01) -> {
                    ori.setTargetLocalRot(fromRot.slerp(toRot1, t01, new Quaterniond()));
                }, rotTicks).curve(TweenData.Curve.InOutCubic);
                scene.idle(rotTicks + intervalTicks);

                vsp.vs.<OrientationConstraint>tweenConstraint(oriUuid, (ori, t01) -> {
                    ori.setTargetLocalRot(toRot1.slerp(toRot2, t01, new Quaterniond()));
                }, rotTicks).curve(TweenData.Curve.InOutCubic);
                scene.idle(rotTicks + intervalTicks);

                vsp.vs.<OrientationConstraint>tweenConstraint(oriUuid, (ori, t01) -> {
                    ori.setTargetLocalRot(toRot2.slerp(fromRot, t01, new Quaterniond()));
                }, rotTicks).curve(TweenData.Curve.InOutCubic);
                scene.idle(rotTicks + intervalTicks);

                scene.world.hideSection(vsControlCraftSection, Direction.DOWN);
                scene.idle(20);
            }

            if (ModList.get().isLoaded("vs_clockwork")) {
                scene.rotateCameraY(-200);

                scene.idle(15);

                Selection physBearingSection = util.select.fromTo(3, 3, 6, 4, 3, 7);
                scene.world.setKineticSpeed(physBearingSection, 0);
                scene.world.showSection(physBearingSection, Direction.WEST);

                scene.idle(20);

                scene.world.setKineticSpeed(physBearingSection, 32);
                scene.world.setKineticSpeed(util.select.position(4, 3, 7), -32);  //set first gear speed negative

                int rotTicks = 40;
                int intervalTicks = 2;
                Quaterniond fromRot = new Quaterniond();
                Quaterniond toRot1 = vsp.util.transform.rotOfAngleAxisDeg(-170, 1, 0, 0);// new Quaterniond(new AxisAngle4d(3, 1, 0, 0));//vsp.util.transform.rotOfAngleAxisDeg(45, -1, 0, 0);
                //Quaterniond toRot2 = vsp.util.transform.rotOfAngleAxisDeg(-90, 1, 0, 0);

                vsp.vs.<OrientationConstraint>tweenConstraint(oriUuid, (ori, t01) -> {
                    ori.setTargetLocalRot(fromRot.slerp(toRot1, t01, new Quaterniond()));
                }, rotTicks).curve(TweenData.Curve.Linear);
                scene.idle(rotTicks + intervalTicks);

                scene.world.setKineticSpeed(physBearingSection, -32);
                scene.world.setKineticSpeed(util.select.position(4, 3, 7), 32);  //set first gear speed negative

                vsp.vs.<OrientationConstraint>tweenConstraint(oriUuid, (ori, t01) -> {
                    ori.setTargetLocalRot(toRot1.slerp(fromRot, t01, new Quaterniond()));
                }, rotTicks).curve(TweenData.Curve.Linear);
                scene.idle(rotTicks + intervalTicks);

                scene.world.setKineticSpeed(physBearingSection, 0);

            }


        }

        public static String DockerLoadingId = "artillery/artillery_docker_loading";
        public static void dockerLoading(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(DockerLoadingId, "Artillery Docker Loading");
            scene.configureBasePlate(0, 0, 5);
            scene.scaleSceneView(0.9F);
            //scene.showBasePlate();
            scene.world.showSection(util.select.layer(0), Direction.UP);
            //scene.rotateCameraY((float)WapCommonConfig.hotXYZ.y);
            scene.rotateCameraY(-60);

            PonderVsSceneBuilder ponderVs = new PonderVsSceneBuilder(scene);

            BlockPos breechPos = util.grid.at(2, 2, 4);
            BlockPos barrelTipPos = util.grid.at(2, 2, 0);
            Selection artillerySection = util.select.fromTo(2, 2, 0, 2, 2, 4).add(util.select.position(1, 2, 4));

            RigidbodyData artilleryRd = new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(2, 2, 4));
            BlockClusterData artilleryBd = ponderVs.util.ship.copyWorldSectionAsShipBlocks(artillerySection, new Vector3i(2, 2, 4));
            UUID artillery = ponderVs.vs.makeShip(artilleryRd, artilleryBd, false);
            BlockPos levelerPos = util.grid.at(1, 2, 4);
            //scene.world.showSection(artillerySection, Direction.WEST);
            //scene.idle(5);
            //scene.world.showSection(util.select.position(levelerPos), Direction.EAST);

            //ponderVs.scene.switchToLocalSpaceOf(artillery, 5, false);


            scene.overlay.showText(60)
                .text("There are multiple ways to load munition...");

            scene.idle(35);

            BlockClusterData munitionBd = makeStandardHeMunition(Direction.NORTH);
            BlockClusterData remainShellBd = makeStandardHeRemainShell(Direction.NORTH, true);
            BlockClusterData projectileBd = makeStandardHeProjectile(Direction.NORTH);

            ItemStack munitionDocker = Docker.stackOfSaBlockData(0, munitionBd);
            //ItemStack remainShellDocker = Docker.stackOfSaBlockData(0, );

            var dockerItemLink = scene.world.createItemEntity(util.vector.centerOf(0, 10, 2), new Vec3(0, 0, 0), munitionDocker);
            scene.idle(30);

            scene.overlay.showText(75)
                .text("You can use munition ship or itemfied ship(Docker) to load")
                .colored(PonderPalette.GREEN)
                .pointAt(util.vector.centerOf(0, 1, 3))
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(45);

            scene.overlay.showSelectionWithText(util.select.position(breechPos), 65)
                .text("Interact with breech to load")
                .colored(PonderPalette.OUTPUT)
                .pointAt(util.vector.blockSurface(breechPos, Direction.SOUTH))
                .attachKeyFrame()
                .placeNearTarget();

            scene.idle(35);

            InputWindowElement dockerUse = new InputWindowElement(util.vector.blockSurface(breechPos, Direction.UP), Pointing.DOWN)
                .withItem(munitionDocker)
                .rightClick();

            scene.overlay.showControls(dockerUse, 30);
            scene.idle(35);

            //-------load munition---------
            scene.world.modifyEntity(dockerItemLink, Entity::discard);

            RigidbodyData loadInitialRd = new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(2, 2, 8));
            //BlockClusterData loadMunitionBd = makeStandardHeMunition(Direction.NORTH);
            /*UUID loadShipUuid = ponderVs.vs.makeShipWithPositionTween(
                loadInitialRd, munitionBd,
                JomlUtil.dCenter(2, 2, 8), JomlUtil.dCenter(2, 2, 4),
                20, t -> t.curve(TweenData.Curve.InOutCubic)
            );*/
            UUID loadShipUuid = ponderVs.vs.makeShip(loadInitialRd, munitionBd, false);
            UUID loadConstraintUuid = ponderVs.vs.makeConstraint(u -> new SliderOrientationConstraint(
                u, artillery, loadShipUuid,
                new Vector3d(0, 0, 0), new Vector3d(0, 0, 0),
                new Quaterniond(), new Quaterniond(),
                new Vector3d(0, 0, -1)
            ).withFixedDistance(-2.0));

            ponderVs.vs.<SliderOrientationConstraint>tweenConstraint(loadConstraintUuid, (c, t01) -> {
                c.setFixedDistance(2 * t01 - 2);
            }, 20).curve(TweenData.Curve.InOutCubic);

            scene.idle(35);

            //scene.world.modifyBlock(levelerPos, s -> s.setValue(BlockStateProperties.POWERED, true), false);
            ponderVs.vs.modifyShipBlock(artillery, new Vector3i(-1, 0, 0), s -> s.setValue(BlockStateProperties.POWERED, true));
            scene.effects.indicateRedstone(levelerPos);

            scene.idle(5);

            // ------make projectile and remain shell(remain shell is docker in real world, but I want to control timeRate, so I use Ship for RemainShell)-----
            ponderVs.vs.hideShip(loadShipUuid);  //hide and reset, wait for another load animation
            ponderVs.vs.modifyShip(loadShipUuid, s -> s.getRigidbody().getDataWriter().set(loadInitialRd));

            RigidbodyData projectileRd = new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(2, 2, 2));

            UUID projectileUuid = ponderVs.vs.makeShip(projectileRd, projectileBd, false);  //make and shot projectile
            ponderVs.vs.modifyShip(projectileUuid, s -> {
                var rigidReader = s.getRigidbody().getDataReader();
                Vector3d projectileVel = JomlUtil.dNormal(Direction.NORTH).mul(Math.sqrt(2 * WapConfig.standardPropellantEnergy / rigidReader.getMass()));
                s.getRigidbody().getDataWriter().setVelocity(projectileVel).setEarthGravity();
            });

            RigidbodyData remainShellRd = new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(2, 2, 4));
            UUID remainShellUuid = ponderVs.vs.makeShip(remainShellRd, remainShellBd, false);
            ponderVs.vs.modifyShip(remainShellUuid, s -> s.getRigidbody()
                .getDataWriter()
                .setVelocity(0, 0, 5)
            );

            //THE WORLD!
            ponderVs.vs.setPhysTimeScale(0.02);

            scene.idle(5);
            ponderVs.scene.setCameraRotateX(-10);
            ponderVs.scene.setCameraRotateY(160);
            scene.idle(15);

            scene.overlay.showText(60)
                .pointAt(barrelTipPos.getCenter())
                .text("The projectile part will be shot")
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(55);

            ponderVs.scene.setCameraRotateY(20);
            scene.idle(20);

            scene.overlay.showText(65)
                .pointAt(breechPos.getCenter())
                .text("The remain part will be ejected")
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(55);

            ponderVs.scene.resetCameraRotation();
            scene.idle(20);

            //Time start flow...
            ponderVs.vs.modifyShip(remainShellUuid, s -> s.getRigidbody().getDataWriter().setEarthGravity());
            ponderVs.vs.setPhysTimeScale(1);


            scene.idle(30);
            Quaterniond rot = ponderVs.util.transform.rotOfAngleAxisDeg(30, 1, 0, 0);
            ponderVs.vs.tweenShipRotation(artillery, new Quaterniond(), rot, 20);
            //relocate hiding load munition, projectile, shell
            Vector3d newLoadFrom = ponderVs.util.transform.rotateAround(JomlUtil.dCenter(2, 2, 8), JomlUtil.dCenter(breechPos), rot, new Vector3d());
            Vector3d newLaunchDir = rot.transform(new Vector3d(0, 0, -1));

            ponderVs.vs.<SliderOrientationConstraint>modifyConstraint(loadConstraintUuid, c -> {
                c.setFixedDistance(-2.0);
            });

            scene.idle(5);
            ponderVs.overlay.showShipBlockAndText(artillery, new Vector3i(0, 0, 0), PonderPalette.OUTPUT, 75);

            //ponderVs.vs.modifyShip(loadShipUuid, s -> s.getRigidbody().getDataWriter().setPosition(newLoadFrom).setRotation(rot));
            ponderVs.vs.hideShip(projectileUuid);
            ponderVs.vs.modifyShip(projectileUuid, s -> s.getRigidbody().getDataWriter().setNoMovement().setPosition(JomlUtil.dCenter(breechPos)).setRotation(rot).setNoGravity());
            ponderVs.vs.hideShip(remainShellUuid);
            ponderVs.vs.modifyShip(remainShellUuid, s -> s.getRigidbody().getDataWriter().setNoMovement().setPosition(JomlUtil.dCenter(breechPos)).setRotation(rot).setNoGravity());
            //ponderVs.vs.modifyShip(loadShipUuid, s -> s.);


            RigidbodyData munitionHeadRd = new RigidbodyData();
            BlockClusterData munitionHeadBd = makeStandardHeProjectile(Direction.NORTH);
            UUID munitionHead = ponderVs.vs.makeShip(munitionHeadRd, munitionHeadBd, true);
            RigidbodyData munitionTailRd = new RigidbodyData();
            BlockClusterData munitionTailBd = makeStandardHeRemainShell(Direction.NORTH, false);
            UUID munitionTail = ponderVs.vs.makeShip(munitionTailRd, munitionTailBd, true);

            UUID munitionHeadConstraint = ponderVs.vs.makeConstraint(u ->
                new SliderOrientationConstraint(u, artillery, munitionHead, new Vector3d(), new Vector3d(), new Quaterniond(), new Quaterniond(), new Vector3d(0, 0, -1))
                    .withFixedDistance(-2.0)
            );
            UUID munitionTailConstraint = ponderVs.vs.makeConstraint(u ->
                new SliderOrientationConstraint(u, artillery, munitionTail, new Vector3d(), new Vector3d(), new Quaterniond(), new Quaterniond(), new Vector3d(0, 0, -1))
                    .withFixedDistance(-2.0)
            );


            scene.idle(25);


                /*.text("You can also multi load in this way")
                .pointAt(breechPos.getCenter())
                .attachKeyFrame()
                .placeNearTarget();*/

            //ponderVs.vs.showShip(loadShipUuid);
            //ponderVs.vs.tweenShipPosition(loadShipUuid, newLoadFrom, JomlUtil.dCenter(breechPos), 20)
            //    .curve(TweenData.Curve.InOutCubic);
            /*ponderVs.vs.<SliderOrientationConstraint>tweenConstraint(loadConstraintUuid, (c, t01) -> {
                c.setFixedDistance(2 * t01 - 2);
            }, 20).curve(TweenData.Curve.InOutCubic);*/

            //scene.idle(20);

            scene.rotateCameraY(-25);
            scene.rotateCameraY(-100);

            scene.idle(10);

            ponderVs.vs.showShip(munitionHead);
            ponderVs.vs.<SliderOrientationConstraint>tweenConstraint(munitionHeadConstraint, (c, t01) -> {
                c.setFixedDistance(2 * t01 - 2);
            }, 20).curve(TweenData.Curve.InOutCubic);

            scene.idle(25);

            ponderVs.vs.showShip(munitionHead);
            ponderVs.vs.<SliderOrientationConstraint>tweenConstraint(munitionHeadConstraint, (c, t01) -> {
                c.setFixedDistance(2 * t01);
            }, 20).curve(TweenData.Curve.InOutCubic);

            ponderVs.vs.showShip(munitionTail);
            ponderVs.vs.<SliderOrientationConstraint>tweenConstraint(munitionTailConstraint, (c, t01) -> {
                c.setFixedDistance(2 * t01 - 2);
            }, 20).curve(TweenData.Curve.InOutCubic);


            scene.idle(25);
            ponderVs.vs.modifyShipBlock(artillery, new Vector3i(-1, 0, 0), s -> s.setValue(BlockStateProperties.POWERED, false));
            scene.idle(3);
            ponderVs.vs.modifyShipBlock(artillery, new Vector3i(-1, 0, 0), s -> s.setValue(BlockStateProperties.POWERED, true));
            scene.effects.indicateRedstone(levelerPos);

            ponderVs.vs.hideShip(munitionHead);
            ponderVs.vs.hideShip(munitionTail);

            /*ponderVs.vs.showShip(projectileUuid);
            ponderVs.vs.showShip(remainShellUuid);
            ponderVs.vs.modifyShip(projectileUuid, s -> {
                var rigidReader = s.getRigidbody().getDataReader();
                Vector3d projectileVel = new Vector3d(newLaunchDir).mul(Math.sqrt(2 * WapCommonConfig.standardPropellantEnergy / rigidReader.getMass()));
                s.getRigidbody().getDataWriter().setVelocity(projectileVel).setEarthGravity();
            });
            ponderVs.vs.modifyShip(remainShellUuid, s -> {
                s.getRigidbody().getDataWriter()
                    .setVelocity(newLaunchDir.mul(-5, new Vector3d()))
                    .setEarthGravity();
            });*/
            Dest<UUID> remain = new Dest<>();
            fireAndEject(ponderVs, JomlUtil.dCenter(breechPos), Direction.NORTH, JomlUtil.dWorldNormal(rot, Direction.NORTH), null, remain);

            //ponderVs.scene.focusOnShip(remain.get(), new Vector3d(0, 0, 0), 60, true);
            //scene.idle(70);
        }

        public static String ArmLoadingId = "artillery/artillery_arm_loading";
        public static void armLoading(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(ArmLoadingId, "Artillery Docker Loading");
            scene.configureBasePlate(0, 0, 5);
            scene.scaleSceneView(0.9F);
            scene.showBasePlate();
            scene.rotateCameraY(-30);

            PonderVsSceneBuilder ponderVs = new PonderVsSceneBuilder(scene);

            BlockPos armPos = util.grid.at(1, 1, 3);
            BlockPos depotPos = util.grid.at(0, 1, 3);
            BlockPos breechPos = util.grid.at(2, 2, 4);
            BlockPos levelerPos = util.grid.at(2, 3, 4);

            //Selection artillerySection = util.select.fromTo(2, 2, 0, 2, 2, 4);
            scene.world.showSection(util.select.layer(1), Direction.DOWN);
            scene.idle(4);
            scene.world.showSection(util.select.layer(2), Direction.DOWN);
            scene.idle(4);
            scene.world.showSection(util.select.layer(3), Direction.DOWN);

            BlockClusterData loadBd = makeStandardHeMunition(Direction.NORTH);
            ItemStack docker = Docker.stackOfSaBlockData(0, loadBd);

            scene.overlay.showText(60)
                .text("Breech can also be load by Mechanical Arm");
            scene.idle(30);

            scene.world.createItemOnBeltLike(depotPos, Direction.EAST, docker);
            scene.idle(10);

            scene.world.instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);

            scene.idle(10);
            scene.world.removeItemsFromBelt(depotPos);

            scene.world.instructArm(armPos, ArmBlockEntity.Phase.SEARCH_OUTPUTS, docker, -1);
            scene.idle(10);
            scene.world.instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, docker, 0);
            scene.idle(10);
            scene.world.instructArm(armPos, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);

            RigidbodyData rd = new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(breechPos));
            UUID shipUuid = ponderVs.vs.makeShip(rd, loadBd, false);

            scene.rotateCameraY(-110);
            ponderVs.scene.setCameraRotateX(15);

            scene.idle(55);

            ponderVs.scene.resetCameraRotation();
            scene.rotateCameraY(-30);

            scene.idle(10);

            scene.world.toggleRedstonePower(util.select.position(levelerPos));
            scene.effects.indicateRedstone(levelerPos);

            ponderVs.vs.hideShip(shipUuid);
            fireAndEject(ponderVs, JomlUtil.dCenter(breechPos), Direction.NORTH, JomlUtil.dNormal(Direction.NORTH), null, null);
        }

        public static String PhysLoadingId = "artillery/artillery_phys_loading";
        public static void physLoad(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(PhysLoadingId, "Artillery Docker Loading");
            scene.configureBasePlate(0, 0, 5);
            scene.scaleSceneView(0.9F);
            scene.showBasePlate();
            scene.rotateCameraY(-90);
            PonderVsSceneBuilder ponderVs = new PonderVsSceneBuilder(scene);

            BlockPos stickerPos = util.grid.at(0, 1, 0);
            BlockPos barrelTipPos = util.grid.at(2, 2, 0);
            BlockPos breechPos = util.grid.at(2, 2, 4);
            BlockPos levelerPos = util.grid.at(2, 3, 4);
            Selection artillerySection = util.select.fromTo(barrelTipPos, breechPos);

            UUID munitionShip = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(2, 1, 4)),
                makeStandardHeMunition(Direction.NORTH),
                false
            );

            scene.world.showSection(artillerySection, Direction.DOWN);
            scene.idle(4);
            scene.world.showSection(util.select.position(levelerPos), Direction.DOWN);

            scene.overlay.showText(60)
                .text("You can also load by any Phys Piston or Phys Sticker");

            scene.idle(35);

            BlockClusterData stickerBd = ponderVs.util.ship.copyWorldSectionAsShipBlocks(
                util.select.position(stickerPos).add(util.select.position(stickerPos.above())),
                JomlUtil.i(stickerPos)
            );
            UUID stickerShip = ponderVs.vs.makeShipWithPositionTween(
                new RigidbodyData(), stickerBd,
                JomlUtil.dCenter(2, -12, 5), JomlUtil.dCenter(2, 1, 5),
                20,
                t -> t.curve(TweenData.Curve.InOutQuint)
            );

            //ponderVs.overlay.outlineShipBlock(stickerShip, new Vector3i(0, 0, 0), 100);

            scene.idle(25);

            ponderVs.vs.tweenShipPosition(stickerShip,
                JomlUtil.dCenter(2, 1, 5),
                JomlUtil.dCenter(2, 1, 5).add(0, 0, -0.5),
                10
            ).curve(TweenData.Curve.InOutCubic);

            scene.idle(12);
            ponderVs.vs.toggleShipRedstonePower(stickerShip, new Vector3i(0, 1, 0));
            ponderVs.vs.modifyShipBlock(stickerShip, new Vector3i(0, 0, 0), s ->
                s.setValue(StickerBlock.EXTENDED, true)
            );
            ponderVs.effect.indicateShipRedstone(stickerShip, new Vector3i(0, 1, 0));

            UUID stickConstraint = ponderVs.vs.makeConstraint(u ->
                new SliderConstraint(
                    u, stickerShip, munitionShip,
                    new Vector3d(0, 0, 0), new Vector3d(0, 0, 0),
                    JomlUtil.dNormal(Direction.NORTH)
                ).withFixedDistance(0.5)
            );

            scene.idle(6);

            ponderVs.vs.tweenShipPosition(
                stickerShip,
                JomlUtil.dCenter(2, 1, 5).add(0, 0, -0.5), JomlUtil.dCenter(2, 1, 9),
                20
            ).curve(TweenData.Curve.InOutCubic);
            scene.idle(23);
            ponderVs.vs.tweenShipPosition(
                stickerShip,
                JomlUtil.dCenter(2, 1, 9), JomlUtil.dCenter(2, 2, 9),
                12
            ).curve(TweenData.Curve.InOutCubic);
            scene.idle(14);
            ponderVs.vs.tweenShipPosition(
                stickerShip,
                JomlUtil.dCenter(2, 2, 9), JomlUtil.dCenter(2, 2, 5),
                20
            ).curve(TweenData.Curve.InOutCubic);
            scene.idle(23);

            ponderVs.vs.toggleShipRedstonePower(stickerShip, new Vector3i(0, 1, 0));
            ponderVs.vs.modifyShipBlock(stickerShip, new Vector3i(0, 0, 0), s ->
                s.setValue(StickerBlock.EXTENDED, true)
            );
            ponderVs.effect.indicateShipRedstone(stickerShip, new Vector3i(0, 1, 0));

            scene.idle(20);
            ponderVs.vs.deleteConstraint(stickConstraint);
            //ponderVs.vs.modifyShip(munitionShip, s -> s.getRigidbody().getDataWriter().setPosition(JomlUtil.dCenter(breechPos)));

            ponderVs.vs.tweenShipPosition(stickerShip,
                JomlUtil.dCenter(2, 2, 5), JomlUtil.dCenter(2, 2, 25),
                40
            ).curve(TweenData.Curve.InQuad);

            scene.idle(30);

            scene.world.toggleRedstonePower(util.select.position(levelerPos));
            scene.effects.indicateRedstone(levelerPos);
            ponderVs.vs.hideShip(munitionShip);

            fireAndEject(ponderVs, JomlUtil.dCenter(breechPos), Direction.NORTH, JomlUtil.dNormal(Direction.NORTH), null, null);
        }


        private static void fireAndEject(PonderVsSceneBuilder ponderVs, Vector3d breechCenter, Direction localDir, Vector3d launchDir, @Nullable Dest<UUID> projectileUuid, @Nullable Dest<UUID> shellUuid) {
            BlockClusterData projectileBd = makeStandardHeProjectile(localDir);
            BlockClusterData remainShellBd = makeStandardHeRemainShell(localDir, true);

            UUID projectile = ponderVs.vs.makeShip(new RigidbodyData().setPositionImmediately(breechCenter), projectileBd, false);  //make and shot projectile
            ponderVs.vs.modifyShip(projectile, s -> {
                var rigidReader = s.getRigidbody().getDataReader();
                Vector3d projectileVel = new Vector3d(launchDir).mul(Math.sqrt(2 * WapConfig.standardPropellantEnergy / rigidReader.getMass()));
                s.getRigidbody().getDataWriter().setVelocity(projectileVel).setEarthGravity();
            });

            RigidbodyData remainShellRd = new RigidbodyData().setPositionImmediately(breechCenter);
            UUID remainShellUuid = ponderVs.vs.makeShip(remainShellRd, remainShellBd, false);
            ponderVs.vs.modifyShip(remainShellUuid, s -> s.getRigidbody()
                .getDataWriter()
                .setVelocity(new Vector3d(launchDir).mul(-6))
                //.setGravity(new Vector3d(0, -4.9, 0))
            );
            ponderVs.vs.delayModifyShip(8, remainShellUuid, s -> s.getRigidbody().getDataWriter().setEarthGravity());


            Dest.setIfExistDest(projectileUuid, projectile);
            Dest.setIfExistDest(shellUuid, remainShellUuid);
        }


        public static String TEST_ID = "test";
        public static void test(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(TEST_ID, "Artillery Docker Loading");
            scene.configureBasePlate(0, 0, 3);
            scene.scaleSceneView(0.9F);
            scene.showBasePlate();
            //scene.rotateCameraY(-30);
            //PonderVsSceneBuilder ponderVs = new PonderVsSceneBuilder(scene);

            scene.world.showSection(util.select.layer(1), Direction.UP);
            scene.world.showSection(util.select.layer(2), Direction.UP);
            scene.world.showSection(util.select.layer(3), Direction.UP);
        }
    }

    public static class Munition {
        public static final String PrimerGeneralId = "munition/primer_general";
        public static void primerGeneral(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(PrimerGeneralId, "Primer General");
            scene.configureBasePlate(0, 0, 5);
            //scene.scaleSceneView(0.9F);
            scene.showBasePlate();
            scene.removeShadow();
            scene.rotateCameraY(-10);
            //scene.rotateCameraY(-90);
            PonderVsSceneBuilder ponderVs = new PonderVsSceneBuilder(scene);


            BlockPos simplePrimer = new BlockPos(0, 1, 4);
            BlockPos torpedoPrimer = new BlockPos(0, 1, 2);
            BlockPos rocketPrimer = new BlockPos(0, 1, 0);
            Selection primersSection = util.select.position(simplePrimer).add(util.select.position(torpedoPrimer)).add(util.select.position(rocketPrimer));

            BlockPos barrelTip = new BlockPos(2, 2, 0);
            BlockPos breechPos = new BlockPos(3, 1, 5);
            Selection artillerySection = util.select.fromTo(barrelTip, new BlockPos(3, 2, 4));

            Selection munition1Section = util.select.fromTo(1, 1, 0, 1, 1, 3);
            Selection munition2Section = util.select.fromTo(1, 2, 0, 1, 2, 3);
            Selection munition3Section = util.select.fromTo(1, 3, 0, 1, 3, 3);

            Selection munition1Head = util.select.position(1, 1, 0);
            Selection munition1Tail = util.select.fromTo(1, 1, 1, 1, 1, 3);
            BlockClusterData head1Bd = ponderVs.util.ship.copyWorldSectionAsShipBlocks(munition1Head, new Vector3i(1, 1, 0));
            BlockClusterData tailBd = ponderVs.util.ship.copyWorldSectionAsShipBlocks(munition1Tail, new Vector3i(1, 1, 3));

            Quaterniond rot = ponderVs.util.transform.rotOfAngleAxisDeg(45, 1, 0, 0);
            Supplier<Vector3d> launchDir = () -> rot.transform(new Vector3d(0, 0, -1));
            UUID artillery = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(breechPos)).setRotImmediately(rot),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(artillerySection, new Vector3i(3, 2, 4)),
                false
            );

            ponderVs.scene.focusOnShip(artillery, new Vector3d(0, 0, 0), 4, false);

            scene.idle(10);
            scene.world.showSection(util.select.position(simplePrimer), Direction.DOWN);
            scene.idle(4);
            scene.world.showSection(util.select.position(torpedoPrimer), Direction.DOWN);
            scene.idle(4);
            scene.world.showSection(util.select.position(rocketPrimer), Direction.DOWN);
            scene.idle(15);

            /*scene.overlay.showSelectionWithText(primersSection, 60)
                .text("Primers is the basic part of a munition")
                .attachKeyFrame();*/
            //scene.overlay.showOutline(PonderPalette.OUTPUT, "TorpedoPrimer", util.select.position(simplePrimer), 30);
            //scene.overlay.showOutline(PonderPalette.OUTPUT, "RocketPrimer", util.select.position(simplePrimer), 30);
            scene.overlay.showText(60)
                .text("Different Primer has different functionality.")
                .colored(PonderPalette.OUTPUT);


            Runnable toggleToFire1 = () -> {
                ponderVs.vs.toggleShipRedstonePower(artillery, new Vector3i(-1, 0, 0));
                ponderVs.effect.indicateShipRedstone(artillery, new Vector3i(-1, 0, 0));
            };
            Runnable toggleToFire2 = () -> {
                ponderVs.vs.toggleShipRedstonePower(artillery, new Vector3i(-1, 0, 0));
                scene.idle(3);
                ponderVs.vs.toggleShipRedstonePower(artillery, new Vector3i(-1, 0, 0));
                ponderVs.effect.indicateShipRedstone(artillery, new Vector3i(-1, 0, 0));
            };

            UUID munition1 = ponderVs.vs.makeShip(
                new RigidbodyData(),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(munition1Section, new Vector3i(1, 1, 3)),
                true
            );
            UUID c1 = ponderVs.vs.makeConstraint(u ->
                new SliderOrientationConstraint(
                    u, artillery, munition1,
                    new Vector3d(), new Vector3d(),
                    new Quaterniond(), new Quaterniond(),
                    new Vector3d(0, 0, -1)
                ).withFixedDistance(-4.0)
            );

            UUID munition2 = ponderVs.vs.makeShip(
                new RigidbodyData(),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(munition2Section, new Vector3i(1, 2, 3)),
                true
            );
            UUID c2 = ponderVs.vs.makeConstraint(u ->
                new SliderOrientationConstraint(
                    u, artillery, munition2,
                    new Vector3d(), new Vector3d(),
                    new Quaterniond(), new Quaterniond(),
                    new Vector3d(0, 0, -1)
                ).withFixedDistance(-4.0)
            );

            UUID munition3 = ponderVs.vs.makeShip(
                new RigidbodyData(),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(munition3Section, new Vector3i(1, 3, 3)),
                true
            );
            UUID c3 = ponderVs.vs.makeConstraint(u ->
                new SliderOrientationConstraint(
                    u, artillery, munition3,
                    new Vector3d(), new Vector3d(),
                    new Quaterniond(), new Quaterniond(),
                    new Vector3d(0, 0, -1)
                ).withFixedDistance(-4.0)
            );

            scene.idle(35);
            ponderVs.overlay.tweenDirectionalForBlock("BasicPrimerAABB", simplePrimer, Direction.UP, PonderPalette.OUTPUT, 10, 20)
                .curve(TweenData.Curve.InQuint);

            NoPointLineTextElement basicPrimerText = new NoPointLineTextElement();
            basicPrimerText.new Builder(ponderVs.getPonderScene())
                .text("Basic Primer propellant by gunpowder gas")
                .colored(PonderPalette.OUTPUT)
                .pointAt(util.vector.blockSurface(simplePrimer, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget();
            ponderVs.overlay.addAnimatedElement(basicPrimerText, 70);

            /*scene.overlay.showSelectionWithText(util.select.position(simplePrimer), 60)
                .text("Basic Primer propellant by gunpowder gas")
                .colored(PonderPalette.OUTPUT)
                .pointAt(util.vector.blockSurface(simplePrimer, Direction.WEST))
                .independent()
                .attachKeyFrame()
                .placeNearTarget();*/

            //scene.overlay.showOutline(PonderPalette.OUTPUT, "SimplePrimer", util.select.position(simplePrimer), 40);
            ponderVs.vs.showShip(munition1);
            scene.idle(10);
            ponderVs.vs.<SliderOrientationConstraint>tweenConstraint(c1, (c, t01) -> c.setFixedDistance(4 * t01 - 4), 20)
                .curve(TweenData.Curve.InCubic);
            scene.idle(25);
            toggleToFire1.run();

            ponderVs.vs.hideShip(munition1);
            UUID head1 = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(breechPos))
                    .setRotImmediately(rot)
                    .setVelocityImmediately(launchDir.get().mul(30))
                    .setEarthGravityImmediately(),
                head1Bd,
                false
            );
            ponderVs.vs.modifyShip(head1, s -> s.addBehaviour(
                new PonderMunitionBehaviour(),
                new PonderMunitionData(ponderVs.getPonderScene(), new Vector3i(0, 0, -1), new Vector3i())
            ));
            UUID tail1 = ponderVs.vs.makeShip(
                new RigidbodyData()
                    .setPositionImmediately(JomlUtil.dCenter(breechPos))
                    .setRotImmediately(rot).setVelocityImmediately(launchDir.get().mul(-5))
                    .setEarthGravityImmediately(),
                tailBd,
                false
            );

            scene.idle(5);

            ponderVs.vs.setPhysTimeScale(0.2);
            ponderVs.scene.focusOnShip(head1, new Vector3d(0, 0, 0), 55, false);
                //.curve(TweenData.Curve.InOutSine.div(2));
            scene.idle(20);
            ponderVs.vs.setPhysTimeScale(1);
            scene.idle(50);
            ponderVs.scene.focusOnShip(artillery, new Vector3d(0, 0, 0), 20, false);
                //.curve(TweenData.Curve.OutExpo);
            scene.idle(25);

            ponderVs.scene.tweenSceneView(1f, 0.75f, 20)
                .curve(TweenData.Curve.InOutCubic);

            scene.idle(25);

            ponderVs.overlay.tweenDirectionalForBlock("TorpedoPrimer", torpedoPrimer, Direction.UP, PonderPalette.OUTPUT, 10, 20)
                .curve(TweenData.Curve.InQuint);
            NoPointLineTextElement torpedoText = new NoPointLineTextElement();
            torpedoText.new Builder(ponderVs.getPonderScene())
                .attachKeyFrame()
                .text("Torpedo Primer ignore propellant and shot in a small velocity")
                .pointAt(torpedoPrimer.getCenter())
                .placeNearTarget();
            ponderVs.overlay.addAnimatedElement(torpedoText, 60);

            HashMap<BlockPos, BlockState> water = new HashMap<>();
            for (int x = 0; x < 5; ++x)
                water.put(new BlockPos(x, 0, 0), Blocks.WATER.defaultBlockState());
            for (int i = 1; i <= 10; ++i) {
                scene.addInstruction(new FadeIntoSceneInstruction<>(20, Direction.UP, new SimpleRenderBlocks(water)
                    .withTransformer(new Matrix4f().translation(0, -2.5f, -i).scale(1, 5, 1))) {
                    @Override
                    protected Class<SimpleRenderBlocks> getElementClass() {
                        return SimpleRenderBlocks.class;
                    }
                });
                scene.idle(2);
            }

            scene.idle(15);
            ponderVs.vs.showShip(munition2);
            ponderVs.vs.<SliderOrientationConstraint>tweenConstraint(c2, (c, t01) -> {
                c.setFixedDistance(-4 + 4 * t01);
            }, 20).curve(TweenData.Curve.InCubic);

            scene.idle(23);

            toggleToFire2.run();

            ponderVs.vs.deleteConstraint(c2);
            ponderVs.vs.modifyShip(munition2, s -> {
                s.addBehaviour(
                    new PonderMunitionBehaviour(),
                    new PonderMunitionData(ponderVs.getPonderScene(), new Vector3i(0, 0, -1), new Vector3i())
                );
                s.getRigidbody().getDataWriter().setVelocity(launchDir.get().mul(5));
            });

            scene.idle(10);

            ponderVs.scene.focusOnShip(munition2, new Vector3d(0, 0, 0), 80, true);

            scene.idle(5);

            ponderVs.vs.modifyShip(munition2, s -> {
                s.getRigidbody().getDataWriter().setEarthGravity();
            });

            scene.idle(20);

            ponderVs.vs.tweenShip(munition2, (s, alpha) -> {
                s.getRigidbody().getDataWriter().setGravity(
                    new Vector3d(0, 11.27, 0).lerp(new Vector3d(0, -5, 0), alpha)
                );
            }, 40);

            scene.idle(75);

            ponderVs.overlay.tweenDirectionalForBlock("RocketBooster", rocketPrimer, Direction.UP, PonderPalette.OUTPUT, 10, 20)
                .curve(TweenData.Curve.InQuint);
            NoPointLineTextElement rocketText = new NoPointLineTextElement();
            rocketText.new Builder(ponderVs.getPonderScene())
                .attachKeyFrame()
                .text("Rocket Booster as primer")
                .pointAt(torpedoPrimer.getCenter())
                .placeNearTarget();
            ponderVs.overlay.addAnimatedElement(rocketText, 110);

            scene.idle(25);

            //---------loading munition3-------------

            ponderVs.vs.showShip(munition3);
            ponderVs.vs.<SliderOrientationConstraint>tweenConstraint(c3, (c, t01) -> {
                c.setFixedDistance(4 * t01 - 4);
            }, 20).curve(TweenData.Curve.InCubic);

            scene.idle(30);

            toggleToFire2.run();

            ponderVs.vs.deleteConstraint(c3);
            ponderVs.vs.modifyShip(munition3, s -> {
                s.getRigidbody().getDataWriter().setVelocity(launchDir.get().mul(20));
                s.addBehaviour(
                    new PonderMunitionBehaviour(),
                    new PonderMunitionData(ponderVs.getPonderScene(), new Vector3i(0, 0, -1), new Vector3i())
                );
            });

            scene.idle(5);
            ponderVs.vs.modifyShip(munition3, s -> s.getRigidbody().getDataWriter().setEarthGravity());
            //ponderVs.vs.setPhysTimeScale(0.2);
            Vector3d focusLocal = new Vector3d(0, 0, 0);
            ponderVs.scene.focusOnShip(munition3, focusLocal, 95, true);

            scene.idle(30);

            ponderVs.effect.emitShipDustParticle(
                munition3,
                JomlUtil.dCenterExtended(new Vector3d(0, 0,0), 0.5),
                PonderPalette.WHITE.getColor(),
                t01 -> new Vector3d(0, 0, 4),
                400,
                30
            );

            ponderVs.vs.modifyShip(munition3, s -> {
                s.getRigidbody().getDataWriter().setGravity(new Vector3d(0, -2, 0));
            });
            ponderVs.scene.tween((p, t01) -> focusLocal.set(0, 0, 5 * t01), 20)
                .curve(TweenData.Curve.OutCubic);
            scene.idle(24);
            ponderVs.scene.tween((p, t01) -> focusLocal.set(0, 0, 5 * (1 - t01)), 20)
                .curve(TweenData.Curve.OutCubic);

            scene.idle(20);
        }

        public static final String MunitionStructureId = "munition/munition_structure";
        public static void munitionStructure(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(MunitionStructureId, "Primer General");
            scene.configureBasePlate(0, 0, 5);
            scene.showBasePlate();
            scene.removeShadow();
            PonderVsSceneBuilder ponderVs = new PonderVsSceneBuilder(scene);

            Selection m1Section = util.select.fromTo(1, 1, 0, 1, 1, 3);
            Selection m2Section = util.select.fromTo(2, 1, 0, 2, 1, 4);
            Selection m3Section = util.select.fromTo(3, 1, 0, 3, 1, 3);

            double munitionY = 1.6875;

            UUID m1 = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(1.5, munitionY, 0.5),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(m1Section, new Vector3i(1, 1, 0)),
                false
            );
            UUID m2 = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(2.5, munitionY, 0.5),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(m2Section, new Vector3i(2, 1, 0)),
                false
            );
            UUID m3 = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(3.5, munitionY, 0.5),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(m3Section, new Vector3i(3, 1, 0)),
                false
            );

            scene.idle(20);

            //ponderVs.overlay.outlineShipBlock(m1, new Vector3i(0, 0, 3), PonderPalette.OUTPUT.getColor(), 40);
            scene.idle(5);
            //ponderVs.overlay.outlineShipBlock(m2, new Vector3i(0, 0, 4), PonderPalette.OUTPUT.getColor(), 40);
            scene.overlay.showText(180)
                .pointAt(JomlUtil.center(2, 1, 4))
                .placeNearTarget()
                .text("There must be a primer in the bottom of a munition")
                .colored(PonderPalette.OUTPUT)
                .attachKeyFrame();
            scene.idle(5);
            //ponderVs.overlay.outlineShipBlock(m3, new Vector3i(0, 0, 3), PonderPalette.OUTPUT.getColor(), 40);

            scene.idle(45);

            //ponderVs.overlay.outlineShipBlocks(m1, List.of(new Vector3i(0, 0, 1), new Vector3i(0, 0, 2)), PonderPalette.OUTPUT.getColor(), 40);
            scene.idle(5);
            /*ponderVs.overlay.outlineShipBlocks(m2,
                List.of(new Vector3i(0, 0, 1), new Vector3i(0, 0, 2), new Vector3i(0, 0, 3)),
                PonderPalette.OUTPUT.getColor(),
                40
            );*/
            scene.overlay.showText(135)
                .pointAt(JomlUtil.center(2, 1, 2))
                .placeNearTarget()
                .text("You can customize your munition")
                .attachKeyFrame();
            scene.idle(5);
            /*ponderVs.overlay.outlineShipBlocks(m3,
                List.of(new Vector3i(0, 0, 1), new Vector3i(0, 0, 2)),
                PonderPalette.OUTPUT.getColor(),
                40
            );*/
            scene.idle(45);

            //ponderVs.overlay.outlineShipBlock(m1, new Vector3i(0, 0, 0), PonderPalette.OUTPUT.getColor(), 30);
            scene.idle(5);
            //ponderVs.overlay.outlineShipBlock(m2, new Vector3i(0, 0, 0), PonderPalette.OUTPUT.getColor(), 30);
            scene.overlay.showText(90)
                .pointAt(JomlUtil.center(2, 1, 0))
                .placeNearTarget()
                .text("The fuze or AP warhead must be the front of munition")
                .colored(PonderPalette.OUTPUT)
                .attachKeyFrame();
            scene.idle(5);
            //ponderVs.overlay.outlineShipBlock(m3, new Vector3i(0, 0, 0), PonderPalette.OUTPUT.getColor(), 30);
        }

        public static final String MunitionApId = "munition/munition_ap";
        public static void munitionAp(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(MunitionApId, "Ap effects");
            scene.configureBasePlate(0, 0, 5);
            scene.showBasePlate();
            scene.removeShadow();
            PonderVsSceneBuilder ponderVs = new PonderVsSceneBuilder(scene);
            PonderScene ponderScene = ponderVs.getPonderScene();
            PonderWorld ponderWorld = ponderVs.getPonderScene().getWorld();

            HashMap<BlockPos, BlockState> copied = new HashMap<>();
            util.select.fromTo(1, 0, 1, 3, 12, 3)
                .forEach(x -> copied.computeIfAbsent(x.immutable(), ponderWorld::getBlockState));

            Selection fakeBase = util.select.fromTo(0, 1, 0, 4, 12 ,4);
            scene.world.showSection(fakeBase, Direction.DOWN);

            UUID apfsds = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(2, 28, 2)).setVelocityImmediately(new Vector3d(0, -20, 0)),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(util.select.fromTo(0, 13, 0, 0, 14, 0), new Vector3i(0, 13, 0)),
                false
            );


            ponderVs.scene.focusOnShip(apfsds, new Vector3d(0, 0, 0), 120, false);
            ponderVs.vs.modifyShip(apfsds, s -> {
                s.addBehaviour(
                    new PonderMunitionBehaviour(),
                    new PonderMunitionData(
                        ponderScene,
                        new Vector3i(0, -1, 0),
                        new Vector3i(0, 0, 0)
                    )
                        .addBreakableSection(ponderScene.getBaseWorldSection())
                        .withRemainDestroyCnt(999999)
                        .withDestroyRadius(ss -> ss.getRigidbody().getDataReader().getVelocity().lengthSquared() > 1E-8 ? 0.35 : 0)
                        .withBreakCallback(state -> {
                            double vy = s.getRigidbody().getDataReader().getVelocity().y();
                            s.getRigidbody().getDataWriter().setVelocity(0, Math.min(vy + 1, 0), 0);
                        })
                );
            });

            scene.idle(10);

            ponderVs.vs.setPhysTimeScale(0.05);

            //----------info displaying---------
            ponderVs.overlay.textPointToFocusRelative(Vec3.ZERO, 65)
                .attachKeyFrame()
                .placeNearTarget()
                .text("The ap terminal effect is decided by ap warhead and kinetic energy");

            scene.idle(68);

            ponderVs.overlay.textPointToFocusRelative(Vec3.ZERO.add(0, 0, 0), 60)
                .attachKeyFrame()
                .placeNearTarget()
                .text("APFSDS有高穿透倍率和低毁伤倍率");

            scene.idle(40);

            //scene.idle(25);

            //

            ponderVs.scene.setCameraRotateX(-87);
            scene.idle(4);
            ponderVs.vs.setPhysTimeScale(1);
            //ponderVs.scene.focusOn(0, 16, 0, 4, false);

            scene.idle(30);

            //scene.
            BlockPos breakMidBp = new BlockPos(2, 12, 2);
            Vec3 breakMid = breakMidBp.getCenter();
            ponderVs.overlay.showAndTweenOutline("APFSDS",
                new AABB(breakMid, breakMid),
                new AABB(breakMidBp),
                PonderPalette.OUTPUT,
                20, 20
            ).curve(TweenData.Curve.OutElastic);
            ponderVs.overlay.addNoLineText(70)
                .text("所以APFSDS能够穿透远距离，但是造成的毁伤面积较小")
                .pointAt(breakMid)
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(50);

            Runnable resetBlocks = () -> {
                ponderVs.world.unlimitedModifyBlock(
                    copied,
                    s -> false
                );
            };
            resetBlocks.run();
            ponderVs.vs.hideShip(apfsds);


            //-------APCR------
            UUID apcr = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(2, 28, 2)).setVelocityImmediately(new Vector3d(0, -20, 0)),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(util.select.fromTo(1, 13, 0, 1, 14, 0), new Vector3i(1, 13, 0)),
                false
            );
            ponderVs.vs.modifyShip(apcr, s -> {
                s.addBehaviour(
                    new PonderMunitionBehaviour(),
                    new PonderMunitionData(
                        ponderScene,
                        new Vector3i(0, -1, 0),
                        new Vector3i(0, 0, 0)
                    )
                        .addBreakableSection(ponderScene.getBaseWorldSection())
                        .withRemainDestroyCnt(999999)
                        .withDestroyRadius(ss -> ss.getRigidbody().getDataReader().getVelocity().lengthSquared() > 1E-8 ? 0.6 : 0)
                        .withBreakCallback(state -> {
                            double vy = s.getRigidbody().getDataReader().getVelocity().y();
                            s.getRigidbody().getDataWriter().setVelocity(0, Math.min(vy + 4, 0), 0);
                        })
                );
            });

            ponderVs.scene.focusOnShip(apcr, new Vector3d(), 70, false);
            ponderVs.scene.setCameraRotateX(-45);

            scene.idle(10);
            ponderVs.vs.setPhysTimeScale(0.05);

            //----------info displaying---------

            ponderVs.overlay.textPointToFocusRelative(Vec3.ZERO, 60)
                .attachKeyFrame()
                .placeNearTarget()
                .text("APCR有中等穿透倍率和中等毁伤倍率");

            scene.idle(35);

            //--------APCR Effect--------

            ponderVs.scene.setCameraRotateX(-87);
            scene.idle(4);
            ponderVs.vs.setPhysTimeScale(1);
            //ponderVs.scene.focusOn(0, 16, 0, 4, false);

            scene.idle(30);

            //scene.

            ponderVs.overlay.showAndTweenOutline(
                "APCR Area",
                new AABB(breakMidBp.getCenter(), breakMidBp.getCenter()),
                JomlUtil.centerExtended(breakMidBp, 1.5, 0.5, 1.5),
                PonderPalette.OUTPUT,
                20,
                20
            ).curve(TweenData.Curve.OutElastic);

            ponderVs.overlay.addNoLineText(70)
                .text("所以APCR在造成一定的毁伤面积的同时穿透一定距离")
                .pointAt(breakMid)
                .placeNearTarget()
                .attachKeyFrame()
                .colored(PonderPalette.OUTPUT);

            scene.idle(50);

            resetBlocks.run();
            ponderVs.vs.hideShip(apcr);


            //-------Block Effect-----

            UUID block = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(2, 28, 2)).setVelocityImmediately(new Vector3d(0, -20, 0)),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(util.select.fromTo(2, 13, 0, 2, 14, 0), new Vector3i(2, 13, 0)),
                false
            );
            ponderVs.vs.modifyShip(block, s -> {
                s.addBehaviour(
                    new PonderMunitionBehaviour(),
                    new PonderMunitionData(
                        ponderScene,
                        new Vector3i(0, -1, 0),
                        new Vector3i(0, 0, 0)
                    )
                        .addBreakableSection(ponderScene.getBaseWorldSection())
                        .withRemainDestroyCnt(999999)
                        .withDestroyRadius(ss -> ss.getRigidbody().getDataReader().getVelocity().lengthSquared() > 1E-8 ? 1.8 : 0)
                        .withBreakCallback(state -> {
                            double vy = s.getRigidbody().getDataReader().getVelocity().y();
                            s.getRigidbody().getDataWriter().setVelocity(0, Math.min(vy + 20, 0), 0);
                        })
                );
            });

            ponderVs.scene.focusOnShip(block, new Vector3d(), 120, false);
            ponderVs.scene.setCameraRotateX(-45);

            scene.idle(10);
            ponderVs.vs.setPhysTimeScale(0.05);

            //----------info displaying---------

            ponderVs.overlay.textPointToFocusRelative(Vec3.ZERO, 70)
                .attachKeyFrame()
                .placeNearTarget()
                .text("普通方块也能作为穿甲弹头，其毁伤倍率较高但是穿透倍率很低");

            scene.idle(35);

            //--------Effect--------

            ponderVs.scene.setCameraRotateX(-87);
            scene.idle(5);
            ponderVs.vs.setPhysTimeScale(1);
            //ponderVs.scene.focusOn(0, 16, 0, 4, false);

            scene.idle(25);

            //scene.

            ponderVs.overlay.showAndTweenOutline(
                "BLOCK Area",
                new AABB(breakMidBp.getCenter(), breakMidBp.getCenter()),
                JomlUtil.centerExtended(breakMidBp.below(2), 2.5, 0.5, 2.5),
                PonderPalette.OUTPUT,
                20,
                20
            ).curve(TweenData.Curve.OutElastic);

            scene.idle(10);


            ponderVs.overlay.addNoLineText(70)
                .text("Animation is for reference only, please refer to the actual effect")
                .pointAt(breakMid)
                .colored(PonderPalette.BLUE)
                .placeNearTarget()
                .attachKeyFrame();
        }

        public static final String MunitionGliderApId = "munition/munition_glider";
        public static void munitionGlider(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(MunitionGliderApId, "Munition Glider");
            scene.configureBasePlate(0, 0, 5);
            //scene.showBasePlate();
            scene.removeShadow();
            PonderVsSceneBuilder ponderVs = new PonderVsSceneBuilder(scene);
            PonderScene ponderScene = ponderVs.getPonderScene();
            PonderWorld ponderWorld = ponderVs.getPonderScene().getWorld();

            Quaterniond rot = ponderVs.util.transform.rotOfAngleAxisDeg(45, 1, 0, 0);


            double absVel = 30;

            double time = absVel / 1.414 / 9.8;
            double zTravelWhenReachMax = absVel / 1.414 * time;
            double yTravelWhenReachMax = absVel / 1.414 * time - 4.9 * time * time;
            Vector3d focusCenter = new Vector3d(0, yTravelWhenReachMax, -zTravelWhenReachMax);
            ponderVs.scene.focusOn(0, (float)focusCenter.y, (float)focusCenter.z, 4, false);

            UUID m1 = ponderVs.vs.makeShip(
                new RigidbodyData()
                    .setVelocityImmediately(rot.transform(new Vector3d(0, 0, -absVel)))
                    .setEarthGravityImmediately(),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(util.select.fromTo(0, 1, 0, 0, 1, 1), new Vector3i(0, 1, 0)),
                true
            );
            ponderVs.vs.modifyShip(m1, s ->
                s.addBehaviour(
                    new PonderMunitionBehaviour(),
                    new PonderMunitionData(
                        ponderScene,
                        new Vector3i(0, 0, -1),
                        new Vector3i(0, 0, 0)
                    )
                )
            );
            scene.idle(4);
            ponderVs.vs.showShip(m1);

            ponderVs.vs.setPhysTimeScale(2);
            scene.idle(10);  //speed up the out-screen flight
            ponderVs.vs.setPhysTimeScale(1);

            scene.idle(40 - 20);
            ponderVs.scene.focusOnShip(m1, new Vector3d(0, 0, 0), 70, false);

            scene.idle(15);

            ponderVs.overlay.textPointToFocusRelativeNoLine(Vec3.ZERO, 60)
                .text("The projectile will be effected by gravity")
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(40);

            //Now it's not focused
            scene.idle(30);
            ponderVs.scene.focusOn(0, (float)yTravelWhenReachMax, -(float)zTravelWhenReachMax, 4, false);
            scene.idle(5);
            //I suppose it's back now

            UUID m2 = ponderVs.vs.makeShip(
                new RigidbodyData().setVelocityImmediately(rot.transform(new Vector3d(0, 0, -30))).setEarthGravityImmediately(),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(util.select.fromTo(1, 1, 0, 1, 1, 2), new Vector3i(1, 1, 0)),
                true
            );
            ponderVs.vs.modifyShip(m2, s ->
                s.addBehaviour(
                    new PonderMunitionBehaviour(),
                    new PonderMunitionData(
                        ponderScene,
                        new Vector3i(0, 0, -1),
                        new Vector3i(0, 0, 0)
                    )
                )
            );
            scene.idle(4);
            ponderVs.vs.showShip(m2);

            ponderVs.vs.setPhysTimeScale(2);
            scene.idle(10);  //speed up the out-screen flight
            ponderVs.vs.setPhysTimeScale(1);

            scene.idle(40 - 20);
            ponderVs.scene.focusOnShip(m2, new Vector3d(0, 0, 0), 70, false);

            UUID wing1 = ponderVs.vs.makeShip(
                new RigidbodyData(),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(util.select.position(4, 1, 0), new Vector3i(4, 1, 0)),
                true
            );
            UUID wing2 = ponderVs.vs.makeShip(
                new RigidbodyData(),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(util.select.position(4, 1, 0), new Vector3i(4, 1, 0)),
                true
            );
            UUID c1 = ponderVs.vs.makeConstraint(u ->
                new SliderOrientationConstraint(
                    u, m2, wing1,
                    new Vector3d(0, 0, 0), new Vector3d(0, 0, 0),
                    new Quaterniond(), new Quaterniond(),
                    new Vector3d(1, 0, 0)
                )
            );
            UUID c2 = ponderVs.vs.makeConstraint(u ->
                new SliderOrientationConstraint(
                    u, m2, wing2,
                    new Vector3d(0, 0, 0), new Vector3d(0, 0, 0),
                    new Quaterniond(), new Quaterniond(),
                    new Vector3d(-1, 0, 0)
                )
            );
            scene.idle(4);  //wait for wing to constrainted
            ponderVs.vs.showShip(wing1);
            ponderVs.vs.showShip(wing2);
            ponderVs.vs.<SliderOrientationConstraint>tweenConstraint(c1, (c, t01) -> {
                c.setFixedDistance(0.5 * t01);
            }, 15).curve(TweenData.Curve.InOutSine);
            ponderVs.vs.<SliderOrientationConstraint>tweenConstraint(c2, (c, t01) -> {
                c.setFixedDistance(0.5 * t01);
            }, 15).curve(TweenData.Curve.InOutSine);
            //scene.idle(5);

            ponderVs.vs.tweenShip(m2, (s, t01) -> {
                s.getRigidbody().getDataWriter().setGravity(new Vector3d(0, Mth.lerp(t01, -9.8, -0.2), 0));
            }, 30);

            ponderVs.overlay.textPointToFocusRelativeNoLine(Vec3.ZERO, 90)
                .text("The glider wings will expand when the projectile reaches the highest point")
                .placeNearTarget()
                .attachKeyFrame();
        }

        public static final String MunitionTailFindId = "munition/munition_tf";
        public static void munitionTailFin(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(MunitionTailFindId, "Munition Tail Fin");
            scene.configureBasePlate(0, 0, 5);
            //scene.showBasePlate();
            scene.removeShadow();
            PonderVsSceneBuilder ponderVs = new PonderVsSceneBuilder(scene);
            PonderScene ponderScene = ponderVs.getPonderScene();
            PonderWorld ponderWorld = ponderVs.getPonderScene().getWorld();

            Quaterniond rot = ponderVs.util.transform.rotOfAngleAxisDeg(45, 1, 0, 0);


            double absVel = 30;

            double time = absVel / 1.414 / 9.8;
            double zTravelWhenReachMax = absVel / 1.414 * time;
            double yTravelWhenReachMax = absVel / 1.414 * time - 4.9 * time * time;
            Vector3d focusCenter = new Vector3d(0, yTravelWhenReachMax - 0.5, -zTravelWhenReachMax - 2);
            ponderVs.scene.focusOn(0, (float)focusCenter.y, (float)focusCenter.z, 4, false);

            UUID m1 = ponderVs.vs.makeShip(
                new RigidbodyData()
                    .setVelocityImmediately(rot.transform(new Vector3d(0, 0, -absVel)))
                    .setEarthGravityImmediately(),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(util.select.fromTo(0, 1, 0, 0, 1, 1), new Vector3i(0, 1, 0)),
                true
            );
            ponderVs.vs.modifyShip(m1, s ->
                s.addBehaviour(
                    new PonderMunitionBehaviour(),
                    new PonderMunitionData(
                        ponderScene,
                        new Vector3i(0, 0, -1),
                        new Vector3i(0, 0, 0)
                    )
                )
            );
            scene.idle(4);
            ponderVs.vs.showShip(m1);

            ponderVs.vs.setPhysTimeScale(2);
            scene.idle(10);  //speed up the out-screen flight
            ponderVs.vs.setPhysTimeScale(1);

            scene.idle(40 - 20 - 1);
            //ponderVs.scene.focusOnShip(m1, new Vector3d(0, 0, 0), 70, false);

            ponderVs.vs.setPhysTimeScale(0.02);
            //ponderVs.scene.focusOnShip(m1, new Vector3d(0, 0, 0), 70, false);

            scene.idle(5);

            double windExtend1 = 2.5;

            ponderVs.overlay.showAndTweenOutline("WindOutline",
                JomlUtil.centerExtended(focusCenter, windExtend1, windExtend1, 0),
                JomlUtil.centerExtended(focusCenter, windExtend1, windExtend1, 5),
                PonderPalette.WHITE,
                20,
                160
            );
            scene.idle(25);
            ponderVs.overlay.textPointToFocusRelativeNoLine(Vec3.ZERO, 60)
                .text("Projectile effect by wind during flight")
                .placeNearTarget()
                .colored(PonderPalette.OUTPUT)
                .attachKeyFrame();


            for (int i = 0; i < 4; ++i) {
                ponderVs.overlay.showAndTweenOutline("Wind1-1" + i,
                    JomlUtil.centerExtended(focusCenter.add(0, 0, -5, new Vector3d()), windExtend1, windExtend1, 0),
                    JomlUtil.centerExtended(focusCenter.add(0, 0, 5, new Vector3d()), windExtend1, windExtend1, 0),
                    PonderPalette.WHITE,
                    20,
                    0
                ).curve(TweenData.Curve.InOutCubic);
                scene.idle(15);
            }

            ponderVs.scene.setCameraRotateX(-2);
            ponderVs.scene.setCameraRotateY(90);

            ponderVs.overlay.textPointToFocusRelativeNoLine(Vec3.ZERO, 75)
                .text("Which may cause displacement")
                .placeNearTarget()
                .colored(PonderPalette.OUTPUT)
                .attachKeyFrame();

            //Quaterniond displacement = RandUtil.nextQuaterniond(0.3, 0.31);
            final Vector3d[] initialVel = new Vector3d[] { null };
            final Vector3d[] displacedVel = new Vector3d[] { null };
            //int displaceTicks = 0;
            int totalDisTicks = 100;
            int displaceTicks = 80;
            double td = (double)displaceTicks / totalDisTicks;
            double tLerpTimes = 1.0 / (1.0 - td);
            ponderVs.vs.tweenShip(m1, (s, t01) -> {
                Vector3dc vel = s.getRigidbody().getDataReader().getVelocity();
                var rigidWriter = s.getRigidbody().getDataWriter();
                if (t01 < td) {
                    if (initialVel[0] == null) {
                        initialVel[0] = new Vector3d(vel);
                    }
                    Quaterniond displacement = RandUtil.nextQuaterniond(0.005, 0.01);
                    rigidWriter.updateVelocity(v -> v.rotate(displacement, new Vector3d()));
                } else {
                    if (displacedVel[0] == null) {
                        displacedVel[0] = new Vector3d(vel);
                    }
                    Vector3d curLerp = displacedVel[0].lerp(initialVel[0], (t01 - td) * tLerpTimes, new Vector3d());
                    rigidWriter.setVelocity(curLerp);
                }

                //Vector3d curV = initialVel[0].lerp(displacedVel[0], t01, new Vector3d());
                //s.getRigidbody().getDataWriter().setVelocity(displacement.transform(curV, new Vector3d()));
            }, totalDisTicks);

            for (int i = 0; i < 5; ++i) {
                ponderVs.overlay.showAndTweenOutline("Wind1-2" + i,
                    JomlUtil.centerExtended(focusCenter.add(0, 0, -5, new Vector3d()), windExtend1, windExtend1, 0),
                    JomlUtil.centerExtended(focusCenter.add(0, 0, 5, new Vector3d()), windExtend1, windExtend1, 0),
                    PonderPalette.WHITE,
                    20,
                    0
                ).curve(TweenData.Curve.InOutCubic);
                scene.idle(15);
            }

            ponderVs.scene.setCameraRotateX(-45);
            ponderVs.scene.setCameraRotateY(135);
            scene.idle(15);
            ponderVs.vs.setPhysTimeScale(1);

            //------with tf------
            focusCenter.add(0.5, 0, 0);  //I don't know why, but add this can make WindOutline in place
            ponderVs.scene.focusOn((float)focusCenter.x, (float)focusCenter.y, (float)focusCenter.z, 4, false);


            UUID m2 = ponderVs.vs.makeShip(
                new RigidbodyData()
                    .setVelocityImmediately(rot.transform(new Vector3d(0, 0, -absVel)))
                    .setEarthGravityImmediately(),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(util.select.fromTo(1, 1, 0, 1, 1, 2), new Vector3i(0, 1, 0)),
                true
            );
            ponderVs.vs.modifyShip(m2, s ->
                s.addBehaviour(
                    new PonderMunitionBehaviour(),
                    new PonderMunitionData(
                        ponderScene,
                        new Vector3i(0, 0, -1),
                        new Vector3i(0, 0, 0)
                    )
                )
            );
            scene.idle(4);
            ponderVs.vs.showShip(m2);

            ponderVs.vs.setPhysTimeScale(2);
            scene.idle(10);  //speed up the out-screen flight
            ponderVs.vs.setPhysTimeScale(1);

            scene.idle(40 - 20 - 1);
            //ponderVs.scene.focusOnShip(m1, new Vector3d(0, 0, 0), 70, false);

            ponderVs.vs.setPhysTimeScale(0.02);
            //ponderVs.scene.focusOnShip(m1, new Vector3d(0, 0, 0), 70, false);

            scene.idle(5);

            double wind2Extend = 1.25;
            ponderVs.overlay.showAndTweenOutline("WindOutline2",
                JomlUtil.centerExtended(focusCenter, wind2Extend, wind2Extend, 0),
                JomlUtil.centerExtended(focusCenter, wind2Extend, wind2Extend, 5),
                PonderPalette.WHITE,
                20,
                100
            );
            scene.idle(15);
            ponderVs.overlay.textPointToFocusRelativeNoLine(Vec3.ZERO, 80)
                .text("With Fin Tail, wind drag is greatly decreased, which make projectile stable")
                .placeNearTarget()
                .colored(PonderPalette.GREEN)
                .attachKeyFrame();

            ponderVs.scene.setCameraRotateX(-2);
            ponderVs.scene.setCameraRotateY(90);


            for (int i = 0; i < 4; ++i) {
                ponderVs.overlay.showAndTweenOutline("Wind2" + i,  //avoid same name
                    JomlUtil.centerExtended(focusCenter.add(0, 0, -5, new Vector3d()), wind2Extend, wind2Extend, 0),
                    JomlUtil.centerExtended(focusCenter.add(0, 0, 5, new Vector3d()), wind2Extend, wind2Extend, 0),
                    PonderPalette.WHITE,
                    20,
                    0
                ).curve(TweenData.Curve.InOutCubic);
                scene.idle(20);
            }

            ponderVs.scene.setCameraRotateX(-45);
            ponderVs.scene.setCameraRotateY(135);
            scene.idle(15);
            ponderVs.vs.setPhysTimeScale(1);


        }
    }

    public static class Dock {
        public static final String DockUsageId = "dock/dock_usage";
        public static void dockUsage(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(DockUsageId, "Dock Usage");
            scene.configureBasePlate(0, 0, 7);
            scene.showBasePlate();
            //scene.removeShadow();
            PonderVsSceneBuilder ponderVs = new PonderVsSceneBuilder(scene);
            PonderScene ponderScene = ponderVs.getPonderScene();
            PonderWorld ponderWorld = ponderVs.getPonderScene().getWorld();

            BlockPos centerDockPos = new BlockPos(3, 1, 3);
            Selection dockSection = util.select.fromTo(2, 1, 2, 4, 1, 4);
            Selection beltSection = util.select.fromTo(6, 1, 0, 6, 1, 6);
            Selection gearsSection = util.select.fromTo(1, 1, 4, 1, 1, 6).add(util.select.fromTo(5, 1, 4, 5, 1, 6));
            BlockPos toDockArm = new BlockPos(1, 1, 3);
            BlockPos depot = new BlockPos(0, 1, 3);
            BlockPos toBeltArm = new BlockPos(5, 1, 3);

            Selection verticalMunSection = util.select.fromTo(0, 1, 0, 0, 3, 0);
            Selection horizonMunSection = util.select.fromTo(1, 1, 0, 3, 1, 0);
            BlockClusterData vertData = ponderVs.util.ship.copyWorldSectionAsShipBlocks(verticalMunSection, new Vector3i(0, 1, 0));
            BlockClusterData horData = ponderVs.util.ship.copyWorldSectionAsShipBlocks(horizonMunSection, new Vector3i(1, 1, 0));

            scene.idle(3);
            scene.world.showSection(util.select.position(centerDockPos), Direction.DOWN);


            scene.idle(15);
            InputWindowElement dockerUseElement = new InputWindowElement(util.vector.blockSurface(centerDockPos, Direction.WEST), Pointing.LEFT)
                .withItem(Docker.defaultStack())
                .rightClick();

            /*scene.overlay.showSelectionWithText(util.select.position(centerDockPos), 60)
                .text("You can place ship on Dock by interactive with Docker or holding Ship")
                .placeNearTarget()
                .attachKeyFrame();*/
            ponderVs.overlay.addNoLineText(60)
                .text("You can place ship on Dock by interactive with Docker or holding Ship")
                .pointAt(centerDockPos.above(2).getCenter())
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(20);

            scene.overlay.showControls(dockerUseElement, 25);

            scene.idle(20);

            UUID verPlacedShip = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(centerDockPos.above()).sub(0, 0.5, 0)),
                vertData,
                false
            );

            scene.idle(30);

            ponderVs.overlay.addNoLineText(60)
                .pointAt(centerDockPos.above(2).getCenter())
                .text("Then right click with empty hands on Dock to fetch the ship.")
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(20);

            InputWindowElement fetchDockerEle = new InputWindowElement(util.vector.blockSurface(centerDockPos, Direction.WEST), Pointing.LEFT)
                .rightClick();
            scene.overlay.showControls(fetchDockerEle, 25);

            scene.idle(20);

            ponderVs.vs.hideShip(verPlacedShip);

            scene.idle(30);


            scene.world.showSection(util.select.position(toDockArm), Direction.DOWN);
            scene.world.showSection(util.select.position(toBeltArm), Direction.DOWN);
            scene.idle(5);
            scene.world.showSection(gearsSection, Direction.NORTH);
            scene.idle(5);
            scene.world.showSection(util.select.position(depot), Direction.EAST);
            scene.world.showSection(beltSection, Direction.WEST);

            scene.idle(15);

            ItemStack vertDocker = Docker.stackOfSaBlockData(0, vertData);
            ItemStack horDocker = Docker.stackOfSaBlockData(0, horData);

            var verDockerLink = scene.world.createItemEntity(depot.above(10).getCenter(), Vec3.ZERO, vertDocker);
            ponderVs.world.throwEntityTo(verDockerLink, JomlUtil.dCenter(depot), (s, e) -> {
                e.discard();
                ponderVs.util.beltPlaceLikePlaceImmediately(s, depot, vertDocker, Direction.WEST);
            });

            //scene.idle(26);
            //scene.world.createItemOnBeltLike(depot, Direction.DOWN, vertDocker);

            scene.idle(35);

            scene.world.instructArm(toDockArm, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
            scene.idle(10);

            scene.overlay.showText(60)
                .pointAt(toDockArm.getCenter())
                .placeNearTarget()
                .attachKeyFrame()
                .text("Arm can transport Docker to Dock");

            scene.idle(10);
            scene.world.instructArm(toDockArm, ArmBlockEntity.Phase.SEARCH_OUTPUTS, vertDocker, 0);
            scene.world.removeItemsFromBelt(depot);
            scene.idle(20);
            scene.world.instructArm(toDockArm, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, vertDocker, 0);
            scene.idle(20);
            scene.world.instructArm(toDockArm, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);

            ponderVs.vs.showShip(verPlacedShip);

            scene.idle(15);

            scene.world.instructArm(toBeltArm, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
            scene.idle(20);
            scene.overlay.showText(60)
                .pointAt(toBeltArm.getCenter())
                .placeNearTarget()
                .attachKeyFrame()
                .text("And can also fetch ship from Dock");
            scene.world.instructArm(toBeltArm, ArmBlockEntity.Phase.SEARCH_OUTPUTS, vertDocker, 0);
            ponderVs.vs.hideShip(verPlacedShip);
            scene.idle(20);
            scene.world.instructArm(toBeltArm, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, vertDocker, 0);
            scene.idle(20);
            scene.world.instructArm(toBeltArm, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
            scene.world.createItemOnBelt(new BlockPos(6, 1, 3), Direction.EAST, vertDocker);

            scene.idle(40);

            var horDockerLink = scene.world.createItemEntity(depot.above(10).getCenter(), Vec3.ZERO, horDocker);
            ponderVs.world.throwEntityTo(horDockerLink, JomlUtil.dCenter(depot), (s, e) -> {
                e.discard();
                ponderVs.util.beltPlaceLikePlaceImmediately(s, depot, horDocker, Direction.WEST);
            });
            scene.idle(35);

            scene.overlay.showText(50)
                .pointAt(util.vector.blockSurface(depot, Direction.UP))
                .attachKeyFrame()
                .placeNearTarget()
                .text("When the bottom size is larger than docker's size...");

            scene.idle(40);

            scene.world.showSection(dockSection, Direction.UP);
            scene.idle(15);

            ponderVs.scene.setCameraRotateX(-75);

            scene.idle(20);

            ponderVs.overlay.addNoLineText(60)
                .pointAt(toDockArm.getCenter().add(2, 4, 2))
                .attachKeyFrame()
                .placeNearTarget()
                .text("You can expand the dock.");

            scene.idle(15);

            ponderVs.overlay.showAndTweenOutline("Dock",
                JomlUtil.centerExtended(centerDockPos, 0),
                JomlUtil.centerExtended(centerDockPos, 1.5, 0.5, 1.5),
                PonderPalette.GREEN,
                20, 40
            );
            scene.idle(5);
            scene.overlay.showText(60)
                .text("The connected rect sized docks are considered as a combined")
                .pointAt(centerDockPos.getCenter())
                .colored(PonderPalette.GREEN)
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(45);

            ponderVs.scene.setCameraRotateX(-45);

            scene.idle(20);

            //search input?
            scene.world.instructArm(toDockArm, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
            scene.idle(20);
            scene.world.instructArm(toDockArm, ArmBlockEntity.Phase.SEARCH_OUTPUTS, horDocker, 0);
            scene.world.removeItemsFromBelt(depot);
            scene.idle(20);
            scene.world.instructArm(toDockArm, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, horDocker, 0);
            scene.idle(20);
            scene.world.instructArm(toDockArm, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
            UUID horPlacedShip = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(2,2, 3).sub(0, 0.1875, 0)),
                horData,
                false
            );
            scene.idle(20);


            scene.world.instructArm(toBeltArm, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
            scene.idle(20);
            scene.world.instructArm(toBeltArm, ArmBlockEntity.Phase.SEARCH_OUTPUTS, horDocker, 0);
            ponderVs.vs.hideShip(horPlacedShip);
            scene.idle(20);
            scene.world.instructArm(toBeltArm, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, horDocker, 0);
            scene.idle(20);
            scene.world.instructArm(toBeltArm, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
            scene.world.createItemOnBelt(new BlockPos(6, 1, 3), Direction.EAST, horDocker);
        }

        public static final String DockConstraintId = "dock/dock_constraint";
        public static void dockConstraint(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(DockConstraintId, "Dock Usage");
            scene.configureBasePlate(0, 0, 5);
           // scene.showBasePlate();
            scene.removeShadow();
            PonderVsSceneBuilder ponderVs = new PonderVsSceneBuilder(scene);
            PonderScene ponderScene = ponderVs.getPonderScene();
            PonderWorld ponderWorld = ponderVs.getPonderScene().getWorld();

            Selection munitionSection = util.select.fromTo(0, 1, 0, 0, 5, 0);
            Selection dockSection = util.select.fromTo(1, 1, 1, 3, 1, 3);


            UUID dock = ponderVs.vs.makeShip(
                new RigidbodyData(),//d.setOmegaImmediately(omega),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(dockSection, new Vector3i(2, 1, 2)),
                false
            );
            UUID munition = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(0, 0.5, 0),//.setEarthGravityImmediately(),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(munitionSection, new Vector3i(0, 1, 0)),
                false
            );
            UUID constraint = ponderVs.vs.makeConstraint(u ->
                new SliderOrientationConstraint(u, dock, munition,
                    new Vector3d(0, 0.5, 0), new Vector3d(0, 0, 0),
                    new Quaterniond(), new Quaterniond(),
                    new Vector3d(0, 1, 0)
                )
            );
            ponderVs.scene.focusOnShip(dock, new Vector3d(0, 0, 0), 200, false);


            ponderVs.overlay.textPointToFocusRelative(Vec3.ZERO, 100)
                .placeNearTarget()
                .attachKeyFrame()
                .text("The ship will be constrainted by dock, which make it usable on ship.");

            scene.idle(20);

            Vector3d targetOmega1 = new Vector3d(0, 6, 0);
            ponderVs.vs.tweenShip(dock, (s, t01) -> {
                s.getRigidbody().getDataWriter().setOmega(new Vector3d(0, 0, 0).lerp(targetOmega1, t01));
            }, 50).curve(TweenData.Curve.OutCubic.andThen(TweenData.Curve.InCubic.upsideDown()));

            scene.idle(50);

            Vector3d targetOmega2 = new Vector3d(6, 0, 0);
            ponderVs.vs.tweenShip(dock, (s, t01) -> {
                s.getRigidbody().getDataWriter().setOmega(new Vector3d(0, 0, 0).lerp(targetOmega2, t01));
            }, 50).curve(TweenData.Curve.OutCubic.andThen(TweenData.Curve.InCubic.upsideDown()));

            scene.idle(50);

            Vector3d targetOmega3 = new Vector3d(0, 0, 6);
            ponderVs.vs.tweenShip(dock, (s, t01) -> {
                s.getRigidbody().getDataWriter().setOmega(new Vector3d(0, 0, 0).lerp(targetOmega3, t01));
            }, 50).curve(TweenData.Curve.OutCubic.andThen(TweenData.Curve.InCubic.upsideDown()));

            scene.idle(50);
            ponderVs.overlay.textPointToFocusRelativeNoLine(Vec3.ZERO, 200)
                .placeNearTarget()
                .attachKeyFrame()
                .text("However, the ship will drop if the dock detect the possible multiblock change(Place or destory dock near the strucutre)");

            scene.idle(20);

            ponderVs.vs.modifyShip(dock, s -> {
                s.getBlockCluster().getDataWriter().setBlock(new Vector3i(1, 1, 1), WapBlocks.Industrial.DOCK.getDefaultState());
            });
            ponderVs.vs.modifyShip(munition, s -> s.getRigidbody().getDataWriter().setEarthGravity());
            ponderVs.vs.deleteConstraint(constraint);





        }

        public static final String DockConstructId = "dock/dock_construct";
        public static void dockConstruction(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(DockConstructId, "Dock Construct");
            scene.configureBasePlate(0, 0, 16);
            scene.showBasePlate();
            scene.scaleSceneView(0.65f);
            scene.rotateCameraY(90);
            scene.removeShadow();
            PonderVsSceneBuilder ponderVs = new PonderVsSceneBuilder(scene);
            PonderScene ponderScene = ponderVs.getPonderScene();
            PonderWorld ponderWorld = ponderVs.getPonderScene().getWorld();

            BlockPos gpHolder = new BlockPos(20, 1, 17);
            BlockPos detector = new BlockPos(19, 1, 17);
            BlockPos unlocker = new BlockPos(18, 1, 17);

            BlockPos wingDepot = new BlockPos(3, 1, 2);
            BlockPos wingArm = new BlockPos(4, 1, 2);

            scene.world.modifyBlock(gpHolder, s -> s.setValue(DockGreenPrintHolder.HAS, true), false);

            Selection planeBodySection = util.select.fromTo(5, 2, 4, 16, 8, 16).add(util.select.position(17, 7, 10));
            Selection propSection = util.select.fromTo(17, 2, 8, 17, 6, 12);

            BlockPos planeOrigin = new BlockPos(16, 4, 10);
            BlockPos propOrigin = new BlockPos(17, 4, 10);

            //BlockPos armsFocusPos = new BlockPos(3, 2, 4);

            UUID planeBody = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(planeOrigin)),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(planeBodySection, JomlUtil.i(planeOrigin)),
                false
            );
            UUID prop = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(propOrigin)),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(propSection, JomlUtil.i(propOrigin)),
                false
            );
            UUID constraint = ponderVs.vs.makeConstraint(u ->
                new SliderOrientationConstraint(
                    u, planeBody, prop,
                    new Vector3d(), new Vector3d(),
                    new Quaterniond(), new Quaterniond(),
                    new Vector3d(1, 0, 0)
                ).withFixedDistance(1.25)
            );

            List<Vector3i> wingBlockPoses = List.of(
                new Vector3i(0, 1, 0),
                new Vector3i(0, 2, 0),
                //new Vector3i(0, -1, 0),
                new Vector3i(0, -2, 0)
                /*new Vector3i(0, 0, 1),
                new Vector3i(0, 0, 2),
                new Vector3i(0, 0, -1),
                new Vector3i(0, 0, -2)*/
            );
            for (Vector3i wingPos : wingBlockPoses) {
                ponderVs.vs.modifyShipBlock(prop, wingPos, s -> Blocks.AIR.defaultBlockState());
            }

            scene.world.showSection(util.select.everywhere().substract(planeBodySection).substract(propSection), Direction.DOWN);

            scene.idle(20);

            ponderVs.overlay.textPointToFocusRelativeNoLine(Vec3.ZERO, 100)
                .text("You can use dock to assemble ships")
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(40);

            scene.rotateCameraY(90);
            //ponderVs.scene.tweenSceneView(0.5f, 0.75f, 20).curve(TweenData.Curve.InOutCubic);

            scene.idle(10);
            /*UUID gpFocusPoint = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(gpHolder)),
                new BlockClusterData(),
                true
            );*/
            //ponderVs.scene.focusOnShip(gpFocusPoint, new Vector3d(0, 4, 0), 60, false);
            InputWindowElement pointToGp = new InputWindowElement(util.vector.blockSurface(gpHolder, Direction.UP), Pointing.DOWN)
                .withItem(WapBlocks.Industrial.DOCK_GP_HOLDER.asStack());
            scene.overlay.showControls(pointToGp, 20);

            scene.idle(15);

            scene.overlay.showText(75)
                .pointAt(gpHolder.getCenter())
                .text("To assemble ship on a dock, there must be a gp holder in it")
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(60);

            /*InputWindowElement hasGp = new InputWindowElement(util.vector.blockSurface(gpHolder, Direction.UP), Pointing.DOWN)
                .withItem(WapItems.GREEN_PRINT.asStack());
            scene.overlay.showControls(hasGp, 20);

            scene.idle(25);*/

            ponderVs.overlay.addNoLineText(60)
                .pointAt(gpHolder.above().west(4).getCenter())
                .text("And it must contain the gp which contain the ship you want to assemble")
                .placeNearTarget()
                .attachKeyFrame();


            scene.idle(65);

            scene.rotateCameraY(-90);
            //ponderVs.scene.tweenSceneView(0.75f, 0.5f, 20).curve(TweenData.Curve.InOutCubic);

            scene.idle(20);

            scene.overlay.showText(60)
                .pointAt(wingDepot.getCenter())
                .text("And then arm will start to work")
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(5);


            int stepTicks = 9;
            ItemStack sailMs = MaterialStandardizedItem.fromBlock(AllBlocks.SAIL.get(), 1);
            int wingIx = 0;
            for (Vector3i wingPos : wingBlockPoses) {
                scene.world.createItemOnBeltLike(wingDepot, Direction.WEST, sailMs);
                scene.idle(2);
                scene.world.instructArm(wingArm, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, 0);
                scene.idle(stepTicks);
                scene.world.instructArm(wingArm, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
                scene.idle(stepTicks);
                scene.world.removeItemsFromBelt(wingDepot);
                scene.world.instructArm(wingArm, ArmBlockEntity.Phase.SEARCH_OUTPUTS, sailMs, 0);
                scene.idle(stepTicks);
                scene.world.instructArm(wingArm, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, sailMs, 0);
                scene.idle(stepTicks);
                scene.world.instructArm(wingArm, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);

                ponderVs.vs.modifyShipBlock(prop, wingPos,
                    s -> AllBlocks.SAIL.getDefaultState().setValue(BlockStateProperties.FACING, Direction.EAST)
                );

                if (wingIx == 0)
                    scene.overlay.showText(60)
                        .pointAt(propOrigin.getCenter())
                        .attachKeyFrame()
                        .text("The block in ship can be placed")
                        .placeNearTarget();

                wingIx++;
            }
            scene.idle(5);
            scene.rotateCameraY(90);

            scene.idle(10);

            InputWindowElement pointToDetector = new InputWindowElement(detector.getCenter(), Pointing.DOWN)
                .withItem(WapBlocks.Industrial.DOCK_CONSTRUCTION_DETECTOR.asStack());
            scene.overlay.showControls(pointToDetector, 20);

            scene.idle(15);

            scene.overlay.showText(65)
                .pointAt(detector.east(4).getCenter())
                .text("When the construction is completed, the detector will emit redstone signal")
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(20);

            scene.world.modifyBlock(detector, s -> s.setValue(BlockStateProperties.POWERED, true), false);

            scene.idle(40);

            InputWindowElement pointToUnlocker = new InputWindowElement(unlocker.getCenter(), Pointing.DOWN)
                .withItem(WapBlocks.Industrial.DOCK_UNLOCKER.asStack());
            scene.overlay.showControls(pointToUnlocker, 20);

            scene.idle(15);

            scene.overlay.showText(65)
                .pointAt(unlocker.east(2).getCenter())
                .text("When DockUnlocker receive redstone signal, the ship will be unlocked")
                .placeNearTarget()
                .attachKeyFrame();

            scene.idle(25);

            ponderVs.vs.playMotionForShip(planeBody, VsWap.MODID, "airplane_drop", 1.5, 40);

            ponderVs.vs.playMotionForShip(planeBody, VsWap.MODID, "airplane_motion", 2, 100);

            Quaterniond rot = new Quaterniond();
            Quaterniond tickRot = new Quaterniond(new AxisAngle4d(Math.PI / 5, 1, 0, 0));

            scene.idle(20);

            ponderVs.vs.<SliderOrientationConstraint>tweenConstraint(constraint, (c, t01) -> {
                //rot.premul(new Quaterniond(tickRot.scale(t01))).normalize();
                tickRot.scale(t01, new Quaterniond()).mul(rot, rot);
                rot.normalize();
                c.setTargetLocalRot(rot);
            }, 220).curve(TweenData.Curve.InOutQuint);

            scene.idle(140);

            AABB docksAABB = new AABB(5, 1, 2, 21, 2, 18);
            ponderVs.overlay.showAndTweenOutline("AirPlane",
                new AABB(13, 2, 10, 13, 4, 10),
                docksAABB.setMinY(2).setMaxY(4),
                PonderPalette.GREEN,
                //0.1f,
                20, 60
            ).curve(TweenData.Curve.InOutCubic);

            scene.idle(15);

            ponderVs.overlay.textPointToFocusRelativeNoLine(Vec3.ZERO, 100)
                .text("Only after the above ship are moved away can dock construct next ship")
                .placeNearTarget()
                .attachKeyFrame();

        }
    }

    public static class DockerScene {
        public static final String DockUsageId = "docker/docker_usage";
        public static void dockerUsage(SceneBuilder scene, SceneBuildingUtil util) {
            scene.title(DockUsageId, "Docker Usage");
            scene.configureBasePlate(0, 0, 5);
            //scene.showBasePlate();
            scene.removeShadow();
            PonderVsSceneBuilder ponderVs = new PonderVsSceneBuilder(scene);
            PonderScene ponderScene = ponderVs.getPonderScene();
            PonderWorld ponderWorld = ponderVs.getPonderScene().getWorld();

            UUID base = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(2, 0, 2)),
                ponderVs.util.ship.copyWorldSectionAsShipBlocks(util.select.layer(0), new Vector3i(2, 0, 2)),
                false
            );

            ponderVs.overlay.textPointToFocusRelativeNoLine(Vec3.ZERO, 70)
                .text("Docker can spawn a ship when use on block")
                .attachKeyFrame()
                .placeNearTarget();

            scene.idle(20);

            InputWindowElement dockShipPlace = new InputWindowElement(
                util.vector.blockSurface(new BlockPos(2, 1, 1), Direction.NORTH),
                Pointing.RIGHT
            ).withItem(WapItems.DOCKER.asStack()).rightClick();
            scene.overlay.showControls(dockShipPlace, 20);

            scene.idle(15);


            UUID dockerPlaced = ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(2, 1, 1).sub(0, 0.1875, 0)),
                makeStandardHeMunition(Direction.SOUTH),
                false
            );

            scene.idle(20);

            ponderVs.vs.playMotionForShip(base, VsWap.MODID, "base_rotate", 4, 80);
            ponderVs.vs.playMotionForShip(dockerPlaced, VsWap.MODID, "base_rotate_push_away_obj", 0.25, 80);

            scene.idle(25);

            AtomicReference<Quaterniond> firstRot = new AtomicReference<>();
            Quaterniond targetRot = new Quaterniond();
            ponderVs.vs.tweenShip(base, (s, t01) -> {
                if (firstRot.get() == null) {
                    firstRot.set(new Quaterniond(s.getRigidbody().getDataReader().getRotation()));
                }
                Quaterniond cur = firstRot.get().get(new Quaterniond()).slerp(targetRot, t01);
                s.getRigidbody().getDataWriter().setRotation(cur);
            }, 15).curve(TweenData.Curve.InOutCubic);

            scene.idle(35);

            ponderVs.overlay.textPointToFocusRelativeNoLine(Vec3.ZERO, 60)
                .text("Use Alt to switch to Block Place Mode(Only when scale is 1)")
                .attachKeyFrame()
                .placeNearTarget();

            scene.idle(10);

            InputWindowElement dockBlockPlace = new InputWindowElement(
                util.vector.blockSurface(new BlockPos(4, 1, 4), Direction.EAST),
                Pointing.RIGHT
            ).withItem(WapItems.DOCKER.asStack()).rightClick();
            scene.overlay.showControls(dockBlockPlace, 20);

            scene.idle(15);

            ponderVs.vs.modifyShip(base, s -> s.getBlockCluster().getDataWriter()
                .chainedSetBlock(new Vector3i(2, 1, 2), PrimerBlock.faceTo(Direction.WEST))
                .chainedSetBlock(new Vector3i(1, 1, 2), ShelledPropellant.getState(false, Direction.WEST))
                .chainedSetBlock(new Vector3i(0, 1, 2), HeWarhead.faceTo(Direction.WEST))
                .chainedSetBlock(new Vector3i(-1, 1, 2), ImpactFuze.getState(false, Direction.WEST))
            );

            scene.idle(20);

            ponderVs.vs.playMotionForShip(base, VsWap.MODID, "base_rotate", 4, 80);
            scene.idle(25);

            firstRot.set(null);
            ponderVs.vs.tweenShip(base, (s, t01) -> {
                if (firstRot.get() == null) {
                    firstRot.set(new Quaterniond(s.getRigidbody().getDataReader().getRotation()));
                }
                Quaterniond cur = firstRot.get().get(new Quaterniond()).slerp(targetRot, t01);
                s.getRigidbody().getDataWriter().setRotation(cur);
            }, 15).curve(TweenData.Curve.InOutCubic);

            scene.idle(25);

            /*ponderVs.vs.tweenShipPosition(
                dockerPlaced,
                JomlUtil.dCenter(2, 1, 1).sub(0, 0.1875, 0),
                JomlUtil.dCenter(2, 0, 1),
                15
            ).curve(TweenData.Curve.OutQuint);
            scene.idle(20);*/

            Selection gears1 = util.select.fromTo(0, 1, 4, 4, 4, 4);
            Selection gears2 = util.select.fromTo(0, 1, 3, 4, 2, 3);
            Selection machines = util.select.fromTo(0, 1, 2, 4, 1, 2);
            Selection docks = util.select.fromTo(3, 1, 0, 4, 1, 1);
            Selection artillery = util.select.fromTo(1, 3, 1, 4, 3, 1);

            BlockPos breech = new BlockPos(1, 3, 3);

            BlockPos depot = new BlockPos(4, 1, 2);
            BlockPos arm = new BlockPos(2, 1, 2);
            BlockPos deployer = new BlockPos(0, 1, 2);

            scene.world.showSection(gears1, Direction.EAST);
            scene.idle(5);
            scene.world.showSection(gears2, Direction.EAST);
            scene.idle(5);
            scene.world.showSection(machines, Direction.EAST);

            scene.idle(20);

            BlockClusterData deployerPlaceBlocks = makeStandardHeMunition(Direction.UP);
            ItemStack deployerDocker = Docker.stackOfSaBlockData(0, deployerPlaceBlocks);

            scene.world.createItemOnBeltLike(depot, Direction.WEST, deployerDocker);
            scene.idle(10);
            scene.world.instructArm(arm, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
            scene.idle(20);
            scene.world.removeItemsFromBelt(depot);
            scene.world.instructArm(arm, ArmBlockEntity.Phase.SEARCH_OUTPUTS, deployerDocker, 0);
            scene.idle(20);
            scene.world.instructArm(arm, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, deployerDocker, 0);
            scene.idle(20);
            scene.world.instructArm(arm, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, 1);
            scene.world.modifyBlockEntityNBT(
                util.select.position(deployer),
                DeployerBlockEntity.class,
                t -> t.put("HeldItem", deployerDocker.serializeNBT())
            );
            scene.world.moveDeployer(deployer, 1f, 20);

            scene.idle(4);

            scene.overlay.showText(60)
                .pointAt(deployer.getCenter())
                .placeNearTarget()
                .attachKeyFrame()
                .text("Deployer can also use Docker to place ship");


            scene.idle(20);

            scene.world.modifyBlockEntityNBT(
                util.select.position(deployer),
                DeployerBlockEntity.class,
                t -> t.put("HeldItem", ItemStack.EMPTY.serializeNBT())
            );
            ponderVs.vs.makeShip(
                new RigidbodyData().setPositionImmediately(JomlUtil.dCenter(-2, 1, 2)).setEarthGravityImmediately(),
                deployerPlaceBlocks,
                false
            );

            scene.idle(40);

            scene.world.showSection(docks, Direction.DOWN);
            scene.idle(10);
            scene.world.showSection(/*artillery*/util.select.layer(3), Direction.DOWN);

            scene.idle(20);

            ponderVs.overlay.showAndTweenOutline(
                "Docks",
                JomlUtil.centerExtended(JomlUtil.d(docks.getCenter()), 0, 0.5, 0),
                JomlUtil.centerExtended(JomlUtil.d(docks.getCenter()), 1, 0.5, 1),
                PonderPalette.GREEN,
                20, 60
            ).curve(TweenData.Curve.OutElastic);
            ponderVs.overlay.showAndTweenOutline(
                "Breech",
                JomlUtil.centerExtended(breech, 0, 0.5, 0),
                JomlUtil.centerExtended(breech, 0.5, 0.5, 0.5),
                PonderPalette.GREEN,
                20, 60
            ).curve(TweenData.Curve.OutElastic);

            scene.idle(10);

            ponderVs.overlay.textPointToFocusRelativeNoLine(Vec3.ZERO, 200)
                .text("Most blocks in this mod that can interact with ships can also interact docker")
                .colored(PonderPalette.GREEN)
                .attachKeyFrame()
                .placeNearTarget();
        }
    }

}
