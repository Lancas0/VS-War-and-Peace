package com.lancas.vswap.obsolete.be;

/*
public class EngineIgniterBlockEntity extends BlockEntity {

    public EngineIgniterBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public boolean activated = false;
    private Direction direction; // 从方块状态获取
    private BlockPos currentBurnPos;
    private int currentBurtTicks;
    private INozzleBlock iNozzle;
    private ServerShip onShip;

    public void activate(Direction dir) {
        if (level == null || level.isClientSide) return;
        if (activated) return;

        activated = initIfStructureValid((ServerLevel)level, dir);
    }
    public void reset() {
        activated = false;
        direction = null;
        currentBurnPos = null;
        currentBurtTicks = 0;
        iNozzle = null;

        if (onShip != null)
            PresistantForceInducer.apply(onShip, new Vector3d());
        onShip = null;
    }

    private boolean initIfStructureValid(ServerLevel sLevel, Direction dir) {
        BlockPos curCheckPos = worldPosition.offset(dir.getNormal());
        while (true) {
            BlockState state = sLevel.getBlockState(curCheckPos);
            Block block = state.getBlock();

            //EzDebug.Log("checking: dir is " + dir + ", pos is " + curCheckPos + ", blk is " + block.getName().getString());

            if (block instanceof INozzleBlock nozzleBlock) {
                direction = dir;
                currentBurnPos = worldPosition.offset(dir.getNormal());
                currentBurtTicks = 0;
                iNozzle = nozzleBlock;
                //EzDebug.Log("valid structure");

                getValidFuelBlockAt(sLevel, currentBurnPos).setAsLited(sLevel, currentBurnPos);
                onShip = ShipUtil.getShipAt(sLevel, worldPosition);

                return true;
            }
            if (getValidFuelBlock(block) == null) return false;

            curCheckPos = curCheckPos.relative(dir);
        }
    }

    public void tick() {
        if (!activated) return;
        if (level == null || level.isClientSide) return;

        ServerLevel sLevel = (ServerLevel)level;

        ISolidFuelBlock curFuleBlock = getValidFuelBlockAt(sLevel, currentBurnPos);
        // 检查当前燃烧位置是否有效
        if (curFuleBlock == null) {
            reset();
            return;
        }

        //invoke event
        //EzDebug.Log("power is:" + iNozzle.getPower(direction));
        if (onShip != null) {
            Vector3d force = onShip.getTransform().getShipToWorldRotation().transform(iNozzle.getPower(direction));
            PresistantForceInducer.apply(onShip, force);
        }


        // 燃烧进度处理
        if (++currentBurtTicks >= curFuleBlock.getMaxBurnTicks()) {
            setFuelEmpty(sLevel, currentBurnPos);
            currentBurnPos = findNextFuel(sLevel, currentBurnPos);
            currentBurtTicks = 0;

            //EzDebug.Log("find next fuel:" + currentBurnPos);

            if (currentBurnPos == null) {  //fail to find next fuel
                reset();
            }
        }

        setChanged();
    }

    private BlockPos findNextFuel(ServerLevel sLevel, BlockPos pos) {
        BlockPos nextPos = pos.relative(direction);
        ISolidFuelBlock nextFuel = getValidFuelBlockAt(sLevel, nextPos);

        if (nextFuel != null) {
            nextFuel.setAsLited(sLevel, nextPos);
            return nextPos;
        } else {
            return null;
        }
    }
    private void setFuelEmpty(ServerLevel sLevel, BlockPos pos) {
        //sLevel.removeBlock(pos, false);
        //todo do not change facing
        sLevel.setBlock(pos, EinherjarBlocks.EMPTY_SOLID_FUEL.getDefaultState(), Block.UPDATE_ALL);
    }
    private ISolidFuelBlock getValidFuelBlock(Block block) {
        if (block instanceof ISolidFuelBlock fuelBlock)
            return fuelBlock;
        return null;
    }
    private ISolidFuelBlock getValidFuelBlockAt(ServerLevel sLevel, BlockPos pos) {
        return getValidFuelBlock(sLevel.getBlockState(pos).getBlock());
    }

}
*/