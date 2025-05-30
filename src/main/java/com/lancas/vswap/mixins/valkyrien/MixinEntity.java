package com.lancas.vswap.mixins.valkyrien;


/*
// 定义接口强制 Entity 实例包含 Mixin 的字段


// 强制 Entity 实现该接口
@Mixin(Entity.class)
public abstract class MixinEntity implements VSEntityAccessor {
    // 实现接口方法（需与原 Mixin 字段匹配）
    @Override public boolean vs_getIsModifyingSetPos() { return this.isMo; }
    @Override public void vs_setIsModifyingSetPos(boolean value) { this.isModifyingSetPos = value; }
    @Override public boolean vs_getIsModifyingTeleport() { return this.isModifyingTeleport; }
    @Override public void vs_setIsModifyingTeleport(boolean value) { this.isModifyingTeleport = value; }
}*/


/*
@Mixin(Entity.class)
public abstract class MixinEntity implements IVSEntityAccessor {
    @Unique
    private boolean isModifyingSetPos = false;
    @Unique
    private boolean isModifyingTeleport = false;


    @Override public boolean vs_getIsModifyingSetPos() { return this.isModifyingSetPos; }
    @Override public void vs_setIsModifyingSetPos(boolean value) { this.isModifyingSetPos = value; }
    @Override public boolean vs_getIsModifyingTeleport() { return this.isModifyingTeleport; }
    @Override public void vs_setIsModifyingTeleport(boolean value) { this.isModifyingTeleport = value; }
}*/