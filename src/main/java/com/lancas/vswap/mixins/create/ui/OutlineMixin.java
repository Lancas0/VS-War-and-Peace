package com.lancas.vswap.mixins.create.ui;

import com.lancas.vswap.mixinfriend.IExOutliner;
import com.lancas.vswap.subproject.pondervs.outline.IOutlineChaser;
import com.simibubi.create.foundation.outliner.Outline;
import com.simibubi.create.foundation.outliner.Outliner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Optional;

@Mixin(Outliner.class)
public abstract class OutlineMixin implements IExOutliner {

    @Shadow(remap = false) @Final
    private Map<Object, Outliner.OutlineEntry> outlines;
    @Shadow(remap = false) @Final
    private Map<Object, Outliner.OutlineEntry> outlinesView;

    @Shadow(remap = false)
    protected abstract void addOutline(Object slot, Outline outline);

    @Override
    public Outline.OutlineParams addWithoutChase(Object slot, Outline outline) {
        if (!this.outlines.containsKey(slot)) {
            this.addOutline(slot, outline);
            return outline.getParams();
        }

        OutlineEntryAccessor entry = (OutlineEntryAccessor)outlines.get(slot);
        if (entry.getOutline() == outline) {
            entry.setTicksTillRemoval(1);
            return entry.getOutline().getParams();
        }

        //replace previous
        Outliner.OutlineEntry newEntry = new Outliner.OutlineEntry(outline);
        outlines.put(slot, newEntry);
        return outline.getParams();
    }

    @Override
    public <T extends Outline & IOutlineChaser> Outline.OutlineParams chaseOrAdd(Object slot, T outline) {
        if (!this.outlines.containsKey(slot)) {
            this.addOutline(slot, outline);
            return outline.getParams();
        }

        OutlineEntryAccessor entry = (OutlineEntryAccessor)outlines.get(slot);
        if (outline.tryChase(entry.getOutline())) {
            entry.setTicksTillRemoval(1);
            return entry.getOutline().getParams();
        }

        //replace previous
        Outliner.OutlineEntry newEntry = new Outliner.OutlineEntry(outline);
        outlines.put(slot, newEntry);
        return outline.getParams();
    }

    @Override
    public <T extends IOutlineChaser> Optional<Outline.OutlineParams> tryChase(Object slot, T chaser) {
        OutlineEntryAccessor entry = (OutlineEntryAccessor)outlines.get(slot);
        if (entry == null) {
            return Optional.empty();
        }

        if (chaser.tryChase(entry.getOutline())) {
            return Optional.of(entry.getOutline().getParams());
        }
        return Optional.empty();
    }
}
