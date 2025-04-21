package com.lancas.vs_wap.content;

import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.content.info.block.WapBlockInfos;
import kotlin.Triple;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.apigame.world.chunks.BlockType;
import org.valkyrienskies.mod.common.BlockStateInfoProvider;
import org.valkyrienskies.mod.common.BlockStateInfo;
import org.valkyrienskies.physics_api.voxel.*;

import java.util.*;


public class WapMass implements BlockStateInfoProvider {
    @NotNull
    public static final WapMass INSTANCE = new WapMass();

    private WapMass() { }

    @NotNull
    public List<Triple<Integer, Integer, Integer>> getBlockStateData() {
        return Collections.emptyList();
    }

    @NotNull
    public List<Lod1LiquidBlockState> getLiquidBlockStates() { return Collections.emptyList(); }

    public int getPriority() {
        return 200;
    }

    @NotNull
    public List<Lod1SolidBlockState> getSolidBlockStates() {
        return Collections.emptyList();
    }

    @Nullable
    public Double getBlockStateMass(@NotNull BlockState state) {
        return WapBlockInfos.mass.valueOrNullOf(state);
    }

    @Nullable
    public BlockType getBlockStateType(@NotNull BlockState blockState) {
        return null;
    }

    public final void register() {
        var registry = BlockStateInfo.INSTANCE.getREGISTRY();
        Registry.register(registry, new ResourceLocation(ModMain.MODID, "registry_mass"), this);
    }
}

/*public class EinherjarBlockInfo implements BlockStateInfoProvider {
    @Override
    public @NotNull List<Triple<Integer, Integer, Integer>> getBlockStateData() {
        return List.of();
    }

    @Override
    public int getPriority() { return 200; }

    @Override
    public @Nullable Double getBlockStateMass(@NotNull BlockState blockState) {
        return 999.0;
    }

    @Override
    public @Nullable BlockType getBlockStateType(@NotNull BlockState blockState) {
        return null;
    }

    @Override
    public @NotNull List<Lod1SolidBlockState> getSolidBlockStates() {
        /.*ShapeBuilder primerShape = PrimerBlock.UP_SHAPE;

        var primerSolidState = new Lod1SolidBlockState(
            new Lod1SolidBeltCollisionShape(
                primerShape.createVSBounds(),
                primerShape.createCollisionPoints(),
                primerShape.createVSBounds(),
                Lod1BeltType.FLAT,
                new Vector3f(0, 0, 0)
            ),
            0f,
            1f,
            1f,
            0
        );

        List<Lod1SolidBlockState> states = new ArrayList<>();
        states.add(primerSolidState);
        return states;*./
        //return new ArrayList<>();
        return null;
    }

    @Override
    public @NotNull List<Lod1LiquidBlockState> getLiquidBlockStates() {
        return List.of();
    }


    public void register() {
        var registry = BlockStateInfo.INSTANCE.getREGISTRY();
        Registry.register(registry, new ResourceLocation(ModMain.MODID, "primer"), this);
    }
}*/
/*public class EinherjarBlockMassProvider implements BlockStateInfoProvider {
    @NotNull
    public static final EinherjarBlockMassProvider INSTANCE = new EinherjarBlockMassProvider();

    private static final double DEFAULT_ELASTICITY = 0.3d;
    private static final double DEFAULT_FRICTION = 0.5d;
    private static final double DEFAULT_HARDNESS = 1.0d;
    /.*private static boolean registeredBlocks;
    static final /.* synthetic *./ KProperty<Object>[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(MassDatapackResolver.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0))};

    @NotNull
    private static final HashMap<ResourceLocation, VSBlockStateInfo> map = new HashMap<>();
    @NotNull
    private static final List<Lod1SolidBlockState> _solidBlockStates = new ArrayList();
    @NotNull
    private static final List<Lod1LiquidBlockState> _liquidBlockStates = new ArrayList();
    @NotNull
    private static final List<Triple<Integer, Integer, Integer>> _blockStateData = new ArrayList();
    @NotNull
    private static final Map<BlockState, BlockType> blockStateToId = new HashMap();
    //@NotNull
    //private static final Logger logger$delegate = LoggingKt.logger().m1294provideDelegatej8jxBIw(INSTANCE, $$delegatedProperties[0]);
    *./

    public static BlockTypeImpl SOLID_TYPE = (BlockTypeImpl)BlockTypeImpl.a.b();
    public static BlockTypeImpl AIR_TYPE = (BlockTypeImpl)BlockTypeImpl.a.a();

    private EinherjarBlockMassProvider() { }

    /.*@NotNull
    public final VSMassDataLoader getLoader() {
        return new VSMassDataLoader();
    }*./

    @Override
    public int getPriority() {
        return 300;
    }

    @Override
    @Nullable
    public Double getBlockStateMass(@NotNull BlockState blockState) {
        if (blockState.getBlock() instanceof TailFin) {
            return 50.0;
        }
        return null;
        /.*VSBlockStateInfo vSBlockStateInfo = map.get(BuiltInRegistries.BLOCK.getKey(blockState.getBlock()));
        if (vSBlockStateInfo != null) {
            return Double.valueOf(vSBlockStateInfo.getMass());
        }
        return null;*./
    }

    @Override
    @Nullable
    public BlockType getBlockStateType(@NotNull BlockState blockState) {
        return VSGameUtilsKt.getVsCore().getBlockTypes().getSolid();
    }

    /.*public final boolean getRegisteredBlocks() {
        return registeredBlocks;
    }*./

    @Override
    @NotNull
    public List<Lod1SolidBlockState> getSolidBlockStates() {
        LodBlockBoundingBox fullLodBoundingBox = LodBlockBoundingBox.Companion.createVSBoundingBox((byte) 0, (byte) 0, (byte) 0, (byte) 15, (byte) 15, (byte) 15);
        List<CollisionPoint> fullBlockCollisionPoints = List.of(
            new CollisionPoint(new Vector3f(0.25f, 0.25f, 0.25f), 0.25f),
            new CollisionPoint(new Vector3f(0.25f, 0.25f, 0.75f), 0.25f),
            new CollisionPoint(new Vector3f(0.25f, 0.75f, 0.25f), 0.25f),
            new CollisionPoint(new Vector3f(0.25f, 0.75f, 0.75f), 0.25f),
            new CollisionPoint(new Vector3f(0.75f, 0.25f, 0.25f), 0.25f),
            new CollisionPoint(new Vector3f(0.75f, 0.25f, 0.75f), 0.25f),
            new CollisionPoint(new Vector3f(0.75f, 0.75f, 0.25f), 0.25f),
            new CollisionPoint(new Vector3f(0.75f, 0.75f, 0.75f), 0.25f)
        );

        Lod1SolidBoxesCollisionShape fullBlockCollisionShape = new Lod1SolidBoxesCollisionShape(
            fullLodBoundingBox,
            fullBlockCollisionPoints,
            List.of(fullLodBoundingBox),
            Collections.emptyList()
        );
        Lod1SolidBlockState solidBlockState = new Lod1SolidBlockState(
            fullBlockCollisionShape,
            0.3f,
            (float) DEFAULT_FRICTION,
            (float) DEFAULT_HARDNESS,
            SOLID_TYPE.getState()
        );

        return List.of(
            solidBlockState
        );
    }

    @Override // org.valkyrienskies.mod.common.BlockStateInfoProvider
    @NotNull
    public List<Lod1LiquidBlockState> getLiquidBlockStates() {
        return Collections.emptyList();
    }

    @Override // org.valkyrienskies.mod.common.BlockStateInfoProvider
    @NotNull
    public List<Triple<Integer, Integer, Integer>> getBlockStateData() {
        return List.of(
            new Triple<>(SOLID_TYPE.getState(), AIR_TYPE.getState(), SOLID_TYPE.getState())
        );
    }

    public void register() {
        var registry = BlockStateInfo.INSTANCE.getREGISTRY();
        Registry.register(registry, new ResourceLocation(ModMain.MODID, TailFin.ID), this);
    }
    /*


    public final void registerAllBlockStates(@NotNull Iterable<? extends BlockState> blockStates) {
        _blockStateData.add(new Triple<>(
            Integer.valueOf(BlockTypeImpl.a.b().toInt()),
            Integer.valueOf(BlockTypeImpl.a.a().toInt()),
            Integer.valueOf(BlockTypeImpl.a.b().toInt())
        ));



        BlockTypeImpl air;
        Lod1SolidBoxesCollisionShape lod1SolidBoxesCollisionShape;
        Lod1SolidBoxesCollisionShape lod1SolidBoxesCollisionShape2;
        //Intrinsics.checkNotNullParameter(blockStates, "blockStates");
        LodBlockBoundingBox fullLodBoundingBox = LodBlockBoundingBox.Companion.createVSBoundingBox((byte) 0, (byte) 0, (byte) 0, (byte) 15, (byte) 15, (byte) 15);
        List fullBlockCollisionPoints = List.of(new CollisionPoint[]{new CollisionPoint(new Vector3f(0.25f, 0.25f, 0.25f), 0.25f), new CollisionPoint(new Vector3f(0.25f, 0.25f, 0.75f), 0.25f), new CollisionPoint(new Vector3f(0.25f, 0.75f, 0.25f), 0.25f), new CollisionPoint(new Vector3f(0.25f, 0.75f, 0.75f), 0.25f), new CollisionPoint(new Vector3f(0.75f, 0.25f, 0.25f), 0.25f), new CollisionPoint(new Vector3f(0.75f, 0.25f, 0.75f), 0.25f), new CollisionPoint(new Vector3f(0.75f, 0.75f, 0.25f), 0.25f), new CollisionPoint(new Vector3f(0.75f, 0.75f, 0.75f), 0.25f)});
        Lod1SolidBoxesCollisionShape fullBlockCollisionShape = new Lod1SolidBoxesCollisionShape(fullLodBoundingBox, fullBlockCollisionPoints, List.of(fullLodBoundingBox), Collections.emptyList());
        Lod1SolidBlockState solidBlockState = new Lod1SolidBlockState(fullBlockCollisionShape, (float) 0.3d, (float) DEFAULT_FRICTION, (float) DEFAULT_HARDNESS, BlockTypeImpl.a.b().toInt());
        _solidBlockStates.add(solidBlockState);
        _blockStateData.add(new Triple<>(Integer.valueOf(BlockTypeImpl.a.b().toInt()), Integer.valueOf(BlockTypeImpl.a.a().toInt()), Integer.valueOf(BlockTypeImpl.a.b().toInt())));
        Lod1LiquidBlockState waterBlockState = new Lod1LiquidBlockState(fullLodBoundingBox, 1000.0f, 0.3f, new Vector3f(), BlockTypeImpl.a.c().toInt());
        Lod1LiquidBlockState lavaBlockState = new Lod1LiquidBlockState(fullLodBoundingBox, 10000.0f, 1.0f, new Vector3f(), BlockTypeImpl.a.d().toInt());
        _liquidBlockStates.add(waterBlockState);
        _liquidBlockStates.add(lavaBlockState);
        _blockStateData.add(new Triple<>(Integer.valueOf(BlockTypeImpl.a.a().toInt()), Integer.valueOf(BlockTypeImpl.a.c().toInt()), Integer.valueOf(BlockTypeImpl.a.c().toInt())));
        _blockStateData.add(new Triple<>(Integer.valueOf(BlockTypeImpl.a.a().toInt()), Integer.valueOf(BlockTypeImpl.a.d().toInt()), Integer.valueOf(BlockTypeImpl.a.d().toInt())));
        BlockGetter blockGetter = new BlockGetter() { // from class: org.valkyrienskies.mod.common.config.MassDatapackResolver$registerAllBlockStates$dummyBlockGetter$1
            public int getHeight() {
                return CBORConstants.INT_BREAK;
            }

            public int getMinBuildHeight() {
                return 0;
            }

            @Nullable
            public BlockEntity getBlockEntity(@NotNull BlockPos blockPos) {
                Intrinsics.checkNotNullParameter(blockPos, "blockPos");
                return null;
            }

            @NotNull
            public BlockState getBlockState(@NotNull BlockPos blockPos) {
                Intrinsics.checkNotNullParameter(blockPos, "blockPos");
                BlockState defaultBlockState = Blocks.VOID_AIR.defaultBlockState();
                Intrinsics.checkNotNullExpressionValue(defaultBlockState, "defaultBlockState(...)");
                return defaultBlockState;
            }

            @NotNull
            public FluidState getFluidState(@NotNull BlockPos blockPos) {
                Intrinsics.checkNotNullParameter(blockPos, "blockPos");
                FluidState defaultFluidState = Fluids.EMPTY.defaultFluidState();
                Intrinsics.checkNotNullExpressionValue(defaultFluidState, "defaultFluidState(...)");
                return defaultFluidState;
            }
        };
        VoxelShape[] topShapes = StairBlockAccessor.getTopShapes();
        //Intrinsics.checkNotNullExpressionValue(topShapes, "getTopShapes(...)");
        VoxelShape[] bottomShapes = StairBlockAccessor.getBottomShapes();
        //Intrinsics.checkNotNullExpressionValue(bottomShapes, "getBottomShapes(...)");
        Map voxelShapeToCollisionShapeMap = generateStairCollisionShapes((VoxelShape[]) ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(topShapes, bottomShapes), SlabBlockAccessor.getBottomAABB()), SlabBlockAccessor.getTopAABB()));
        int nextSolidId = 2;
        Ref.IntRef nextFluidId = new Ref.IntRef();
        nextFluidId.element = 4;
        Ref.IntRef nextVoxelStateId = new Ref.IntRef();
        nextVoxelStateId.element = 4;
        HashMap generatedCollisionShapesMap = new HashMap();
        Map liquidMaterialToDensityMap = MapsKt.mapOf(new Pair[]{TuplesKt.to(Fluids.WATER, new Pair(Float.valueOf(1000.0f), Float.valueOf(0.3f))), TuplesKt.to(Fluids.LAVA, new Pair(Float.valueOf(10000.0f), Float.valueOf(1.0f))), TuplesKt.to(Fluids.FLOWING_WATER, new Pair(Float.valueOf(1000.0f), Float.valueOf(0.3f))), TuplesKt.to(Fluids.FLOWING_LAVA, new Pair(Float.valueOf(10000.0f), Float.valueOf(1.0f)))});
        HashMap fluidStateToBlockTypeMap = new HashMap();
        for (Object element$iv : blockStates) {
            BlockState blockState = (BlockState) element$iv;
            if (blockState.isAir()) {
                air = (BlockTypeImpl)VSGameUtilsKt.getVsCore().getBlockTypes().getAir();
            } else if (blockState.liquid()) {
                FluidState fluidState = blockState.getFluidState();
                //Intrinsics.checkNotNullExpressionValue(fluidState, "getFluidState(...)");
                air = (BlockType) registerAllBlockStates$getFluidState(fluidStateToBlockTypeMap, liquidMaterialToDensityMap, nextFluidId, nextVoxelStateId, fluidState).getSecond();
            } else if (blockState.isSolid()) {
                VoxelShape voxelShape = blockState.getShape(blockGetter, BlockPos.ZERO);
                if (voxelShapeToCollisionShapeMap.containsKey(voxelShape)) {
                    Lod1SolidCollisionShape lod1SolidCollisionShape = voxelShapeToCollisionShapeMap.get(voxelShape);
                    Intrinsics.checkNotNull(lod1SolidCollisionShape);
                    lod1SolidBoxesCollisionShape = lod1SolidCollisionShape;
                } else if (generatedCollisionShapesMap.containsKey(voxelShape)) {
                    if (generatedCollisionShapesMap.get(voxelShape) != null) {
                        Object obj = generatedCollisionShapesMap.get(voxelShape);
                        Intrinsics.checkNotNull(obj);
                        lod1SolidBoxesCollisionShape2 = (Lod1SolidCollisionShape) obj;
                    } else {
                        lod1SolidBoxesCollisionShape2 = fullBlockCollisionShape;
                    }
                    Lod1SolidCollisionShape lod1SolidCollisionShape2 = lod1SolidBoxesCollisionShape2;
                    Intrinsics.checkNotNull(lod1SolidCollisionShape2);
                    lod1SolidBoxesCollisionShape = lod1SolidCollisionShape2;
                } else {
                    MassDatapackResolver massDatapackResolver = INSTANCE;
                    Intrinsics.checkNotNull(voxelShape);
                    Lod1SolidBoxesCollisionShape generated = massDatapackResolver.generateShapeFromVoxel(voxelShape);
                    generatedCollisionShapesMap.put(voxelShape, generated);
                    Lod1SolidBoxesCollisionShape lod1SolidBoxesCollisionShape3 = generated;
                    if (lod1SolidBoxesCollisionShape3 == null) {
                        lod1SolidBoxesCollisionShape3 = fullBlockCollisionShape;
                    }
                    lod1SolidBoxesCollisionShape = lod1SolidBoxesCollisionShape3;
                }
                Lod1SolidCollisionShape collisionShape = lod1SolidBoxesCollisionShape;
                VSBlockStateInfo vsBlockStateInfo = map.get(BuiltInRegistries.BLOCK.getKey(blockState.getBlock()));
                int solidStateId = nextSolidId;
                nextSolidId = solidStateId + 1;
                Lod1SolidBlockState newSolidBlockState = new Lod1SolidBlockState(collisionShape, vsBlockStateInfo != null ? (float) vsBlockStateInfo.getElasticity() : 0.3f, vsBlockStateInfo != null ? (float) vsBlockStateInfo.getFriction() : 0.5f, 1.0f, solidStateId);
                _solidBlockStates.add(newSolidBlockState);
                int blockStateId = nextVoxelStateId.element;
                nextVoxelStateId.element = blockStateId + 1;
                int fluidId = BlockTypeImpl.a.a().toInt();
                if (!blockState.getFluidState().isEmpty()) {
                    FluidState fluidState2 = blockState.getFluidState();
                    Intrinsics.checkNotNullExpressionValue(fluidState2, "getFluidState(...)");
                    fluidId = ((Number) registerAllBlockStates$getFluidState(fluidStateToBlockTypeMap, liquidMaterialToDensityMap, nextFluidId, nextVoxelStateId, fluidState2).getFirst()).intValue();
                }
                _blockStateData.add(new Triple<>(Integer.valueOf(solidStateId), Integer.valueOf(fluidId), Integer.valueOf(blockStateId)));
                air = new BlockTypeImpl(blockStateId);
            } else {
                air = VSGameUtilsKt.getVsCore().getBlockTypes().getSolid().getAir();
            }
            BlockType blockType = air;
            blockStateToId.put(blockState, blockType);
        }
        registeredBlocks = true;
    }

    private static final Pair<Integer, BlockType> registerAllBlockStates$getFluidState(HashMap<FluidState, Pair<Integer, BlockType>> fluidStateToBlockTypeMap, Map<FlowingFluid, Pair<Float, Float>> liquidMaterialToDensityMap, Ref.IntRef nextFluidId, Ref.IntRef nextVoxelStateId, FluidState fluidState) {
        Pair cached = fluidStateToBlockTypeMap.get(fluidState);
        if (cached != null) {
            return cached;
        }
        byte maxY = (byte) RangesKt.coerceIn(MathKt.roundToInt(fluidState.getOwnHeight() * 16.0d) - 1, 0, 15);
        LodBlockBoundingBox fluidBox = LodBlockBoundingBox.Companion.createVSBoundingBox((byte) 0, (byte) 0, (byte) 0, (byte) 15, maxY, (byte) 15);
        if (liquidMaterialToDensityMap.containsKey(fluidState.getType())) {
            Pair<Float, Float> pair = liquidMaterialToDensityMap.get(fluidState.getType());
            Intrinsics.checkNotNull(pair);
            Pair<Float, Float> pair2 = pair;
            float density = ((Number) pair2.component1()).floatValue();
            float dragCoefficient = ((Number) pair2.component2()).floatValue();
            Vector3f vector3f = new Vector3f();
            int i = nextFluidId.element;
            nextFluidId.element = i + 1;
            Lod1LiquidBlockState newFluidBlockState = new Lod1LiquidBlockState(fluidBox, density, dragCoefficient, vector3f, i);
            int stateId = nextVoxelStateId.element;
            nextVoxelStateId.element = stateId + 1;
            _liquidBlockStates.add(newFluidBlockState);
            _blockStateData.add(new Triple<>(Integer.valueOf(BlockTypeImpl.a.a().toInt()), Integer.valueOf(newFluidBlockState.getLod1LiquidBlockStateId()), Integer.valueOf(stateId)));
            BlockTypeImpl blockTypeNew = new BlockTypeImpl(stateId);
            fluidStateToBlockTypeMap.put(fluidState, TuplesKt.to(Integer.valueOf(newFluidBlockState.getLod1LiquidBlockStateId()), blockTypeNew));
            return TuplesKt.to(Integer.valueOf(newFluidBlockState.getLod1LiquidBlockStateId()), blockTypeNew);
        }
        return TuplesKt.to(Integer.valueOf(BlockTypeImpl.a.c().toInt()), BlockTypeImpl.a.c());
    }*/