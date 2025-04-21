package com.lancas.vs_wap.content.explosion;

/*
public class DirectedExplosion extends  {
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

    public DirectedExplosion(Level p_46051_, @Nullable Entity p_46052_, @Nullable DamageSource p_46053_, @Nullable ExplosionDamageCalculator p_46054_, double p_46055_, double p_46056_, double p_46057_, float p_46058_, boolean p_46059_, BlockInteraction p_46060_) {
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
    public DirectedExplosion(Level p_46024_, @Nullable Entity p_46025_, double p_46026_, double p_46027_, double p_46028_, float p_46029_, List<BlockPos> p_46030_) {
        this(p_46024_, p_46025_, p_46026_, p_46027_, p_46028_, p_46029_, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY, p_46030_);
    }

    public DirectedExplosion(Level p_46041_, @Nullable Entity p_46042_, double p_46043_, double p_46044_, double p_46045_, float p_46046_, boolean p_46047_, BlockInteraction p_46048_, List<BlockPos> p_46049_) {
        this(p_46041_, p_46042_, p_46043_, p_46044_, p_46045_, p_46046_, p_46047_, p_46048_);
        thisToBlow.addAll(p_46049_);
    }

    public DirectedExplosion(Level p_46032_, @Nullable Entity p_46033_, double p_46034_, double p_46035_, double p_46036_, float p_46037_, boolean p_46038_, BlockInteraction p_46039_) {
        this(p_46032_, p_46033_, (DamageSource)null, (ExplosionDamageCalculator)null, p_46034_, p_46035_, p_46036_, p_46037_, p_46038_, p_46039_);
    }


    @Override
    public void explode() {
        thisLevel.gameEvent(thisSrc, GameEvent.EXPLODE, new Vec3(thisPos.x, thisPos.y, thisPos.z));
        Set<BlockPos> set = Sets.newHashSet();
        int i = 16;

        for(int j = 0; j < 16; ++j) {
            for(int k = 0; k < 16; ++k) {
                for(int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                        double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                        double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = thisRadius * (0.7F + thisLevel.random.nextFloat() * 0.6F);
                        double d4 = thisPos.x;
                        double d6 = thisPos.y;
                        double d8 = thisPos.z;

                        for(float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = BlockPos.containing(d4, d6, d8);
                            BlockState blockstate = thisLevel.getBlockState(blockpos);
                            FluidState fluidstate = thisLevel.getFluidState(blockpos);
                            if (!thisLevel.isInWorldBounds(blockpos)) {
                                break;
                            }

                            Optional<Float> optional = this.thisDamageCalculator.getBlockExplosionResistance(this, thisLevel, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= ((Float)optional.get() + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && this.thisDamageCalculator.shouldBlockExplode(this, thisLevel, blockpos, blockstate, f)) {
                                set.add(blockpos);
                            }

                            d4 += d0 * (double)0.3F;
                            d6 += d1 * (double)0.3F;
                            d8 += d2 * (double)0.3F;
                        }
                    }
                }
            }
        }

        this.toBlow.addAll(set);
        float f2 = thisRadius * 2.0F;
        int k1 = Mth.floor(thisPos.x - (double)f2 - (double)1.0F);
        int l1 = Mth.floor(thisPos.x + (double)f2 + (double)1.0F);
        int i2 = Mth.floor(thisPos.y - (double)f2 - (double)1.0F);
        int i1 = Mth.floor(thisPos.y + (double)f2 + (double)1.0F);
        int j2 = Mth.floor(thisPos.z - (double)f2 - (double)1.0F);
        int j1 = Mth.floor(thisPos.z + (double)f2 + (double)1.0F);
        List<Entity> list = thisLevel.getEntities(thisSrc, new AABB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
        ForgeEventFactory.onExplosionDetonate(thisLevel, this, list, (double)f2);
        Vec3 vec3 = new Vec3(thisPos.x, thisPos.y, thisPos.z);

        for(int k2 = 0; k2 < list.size(); ++k2) {
            Entity entity = (Entity)list.get(k2);
            if (!entity.ignoreExplosion()) {
                double d12 = Math.sqrt(entity.distanceToSqr(vec3)) / (double)f2;
                if (d12 <= (double)1.0F) {
                    double d5 = entity.getX() - thisPos.x;
                    double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - thisPos.y;
                    double d9 = entity.getZ() - thisPos.z;
                    double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                    if (d13 != (double)0.0F) {
                        d5 /= d13;
                        d7 /= d13;
                        d9 /= d13;
                        double d14 = (double)getSeenPercent(vec3, entity);
                        double d10 = ((double)1.0F - d12) * d14;
                        entity.hurt(this.getDamageSource(), (float)((int)((d10 * d10 + d10) / (double)2.0F * (double)7.0F * (double)f2 + (double)1.0F)));
                        double d11;
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingentity = (LivingEntity)entity;
                            d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingentity, d10);
                        } else {
                            d11 = d10;
                        }

                        d5 *= d11;
                        d7 *= d11;
                        d9 *= d11;
                        Vec3 vec31 = new Vec3(d5, d7, d9);
                        entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
                        if (entity instanceof Player) {
                            Player player = (Player)entity;
                            if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                                this.hitPlayers.put(player, vec31);
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public @NotNull Map<Player, Vec3> getHitPlayers() {
        return this.hitPlayers;
    }
}*/
