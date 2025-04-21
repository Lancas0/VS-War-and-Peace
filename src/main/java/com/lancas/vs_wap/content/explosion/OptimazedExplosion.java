package com.lancas.vs_wap.content.explosion;

/*
public class OptimazedExplosion extends Explosion {
    protected RandomSource thisRandom;
    protected Level thisLevel;
    protected Entity thisSrc;
    private Vec3 thisPos;
    protected float thisRadius;
    protected boolean thisHasFire;
    protected ExplosionDamageCalculator thisDamageCalculator;
    protected Map<Player, Vec3> thisHitPlayers;
    protected ObjectArrayList<BlockPos> thisToBlow;
    protected BlockInteraction thisBlockInteraction;
    protected DamageSource thisDamageSource;

    public OptimazedExplosion(Level p_46051_, @Nullable Entity p_46052_, @Nullable DamageSource p_46053_, @Nullable ExplosionDamageCalculator p_46054_, double p_46055_, double p_46056_, double p_46057_, float p_46058_, boolean p_46059_, BlockInteraction p_46060_) {
        super(p_46051_, p_46052_, p_46053_, p_46054_, p_46055_, p_46056_, p_46057_, p_46058_, p_46059_, p_46060_);

        thisRandom = RandomSource.create();
        thisToBlow = new ObjectArrayList();
        thisHitPlayers = Maps.newHashMap();
        thisLevel = p_46051_;
        thisSrc = p_46052_;
        thisRadius = p_46058_;
        thisPos = new Vec3(p_46055_, p_46056_, p_46057_);
        thisHasFire = p_46059_;
        thisBlockInteraction = p_46060_;
        thisDamageSource = p_46053_ == null ? p_46051_.damageSources().explosion(this) : p_46053_;

        if (p_46054_ != null) {
            thisDamageCalculator = p_46054_;
        } else {
            thisDamageCalculator = (p_46052_ == null ? new ExplosionDamageCalculator() : new EntityBasedExplosionDamageCalculator(p_46052_));
        }
    }
    /.*public DirectedExplosion(Level p_46051_, @Nullable Entity p_46052_, @Nullable DamageSource p_46053_, @Nullable ExplosionDamageCalculator p_46054_, double p_46055_, double p_46056_, double p_46057_, float p_46058_, boolean p_46059_, BlockInteraction p_46060_) {
        super(p_46051_, p_46052_, p_46053_, p_46054_, p_46055_, p_46056_, p_46057_, p_46058_, p_46059_, p_46060_);
        thisLevel = p_46051_;
        thisSrc = p_46052_;
        thisPos = new Vec3(p_46055_, p_46056_, p_46057_);
        thisRadius = p_46058_;

        if (p_46054_ != null) {
            thisDamageCalculator = p_46054_;
        } else {
            thisDamageCalculator = (p_46052_ == null ? new ExplosionDamageCalculator() : new EntityBasedExplosionDamageCalculator(p_46052_));
        }
    }*./
    public OptimazedExplosion(Level p_46024_, @Nullable Entity p_46025_, double p_46026_, double p_46027_, double p_46028_, float p_46029_, List<BlockPos> p_46030_) {
        this(p_46024_, p_46025_, p_46026_, p_46027_, p_46028_, p_46029_, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY, p_46030_);
    }

    public OptimazedExplosion(Level p_46041_, @Nullable Entity p_46042_, double p_46043_, double p_46044_, double p_46045_, float p_46046_, boolean p_46047_, BlockInteraction p_46048_, List<BlockPos> p_46049_) {
        this(p_46041_, p_46042_, p_46043_, p_46044_, p_46045_, p_46046_, p_46047_, p_46048_);
        thisToBlow.addAll(p_46049_);
    }

    public OptimazedExplosion(Level p_46032_, @Nullable Entity p_46033_, double p_46034_, double p_46035_, double p_46036_, float p_46037_, boolean p_46038_, BlockInteraction p_46039_) {
        this(p_46032_, p_46033_, (DamageSource)null, (ExplosionDamageCalculator)null, p_46034_, p_46035_, p_46036_, p_46037_, p_46038_, p_46039_);
    }









    protected static double fastSqrt(double x) {
        return Double.longBitsToDouble(((Double.doubleToLongBits(x) >> 32) + 1072632448) << 31);
    }


}
*/
//todo cause damage to entity
//todo modifiable block interaction
/*
public class OptimazedExplosion extends Explosion {
    protected final BlockPos center;
    protected final Level level;
    protected final int radius;

    @Nullable
    LivingEntity livingBase;

    public OptimazedExplosion(
        @NotNull BlockPos inCenter,
        @NotNull Level inLevel,
        int inRadius//,
        //BlockInteraction inInteraction
    ) {
        super(inLevel, null, inCenter.getX(), inCenter.getY(), inCenter.getZ(), inRadius, false, BlockInteraction.DESTROY);
        this.center = inCenter;
        this.level = inLevel;
        this.radius = inRadius;
    }

    /.*public void setLivingBase(
        @Nullable
        LivingEntity livingBase) {
        this.livingBase = livingBase;
    }

    public
    @Nullable
    LivingEntity getLivingBase() {
        return livingBase;
    }*./

    public void applyExplosion() {
        StopWatch watch = new StopWatch();
        watch.start();
        //in 2/3 radius, the block must be destroyed
        //in the outer 1/3 radius, the block has more chance to survive as the distance increase
        int twoThirdRadius = (int)(radius * 0.666f);

        for (int tx = -radius; tx < radius + 1; tx++) {
            for (int ty = -radius; ty < radius + 1; ty++) {
                for (int tz = -radius; tz < radius + 1; tz++) {
                    //use Manhattan distance so it must be fast
                    int manhattanDist = Math.abs(tx) + Math.abs(ty) + Math.abs(tz);

                    BlockPos pos = center.offset(tx, ty, tz);
                    BlockState state = level.getBlockState(pos);
                    Block block = state.getBlock();

                    if (radius < twoThirdRadius) {

                    }
                    if (manhattanDist <= radius - 2) {
                        BlockPos pos = center.offset(tx, ty, tz);
                        BlockState state = level.getBlockState(pos);
                        Block block = state.getBlock();
                        if (block != Blocks.BEDROCK && !state.isAir()) {
                            block.onBlockExploded(state, level, pos, this);
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        EzDebug.Log("The explosion took " + watch + " to explode");
    }

    @Override
    public void collectBlocksAndDamageEntities() {
        applyExplosion();
    }

    @Override
    public void affectWorld(boolean spawnParticles) {
        applyExplosion();
    }

    @Override
    public
    @Nullable
    LivingEntity getCausingEntity() {
        return livingBase;
    }

    @Override
    public List<BlockPos> getAffectedBlocks() {
        List<BlockPos> poses = new ArrayList<>();
        for (int tx = -radius; tx < radius + 1; tx++) {
            for (int ty = -radius; ty < radius + 1; ty++) {
                for (int tz = -radius; tz < radius + 1; tz++) {
                    if (Math.sqrt(Math.pow(tx, 2) + Math.pow(ty, 2) + Math.pow(tz, 2)) <= radius - 2) {
                        BlockPos pos = center.add(tx, ty, tz);
                        BlockState state = world.getBlockState(pos);
                        Block block = state.getBlock();
                        if (block != Blocks.BEDROCK && !state.isAir()) {
                            poses.add(pos);
                        }
                    }
                }
            }
        }
        return poses;
    }
}*/