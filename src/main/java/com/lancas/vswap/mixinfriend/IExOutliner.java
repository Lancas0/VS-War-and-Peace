package com.lancas.vswap.mixinfriend;

import com.lancas.vswap.mixins.create.ui.OutlineEntryAccessor;
import com.lancas.vswap.subproject.pondervs.outline.IOutlineChaser;
import com.simibubi.create.foundation.outliner.Outline;
import com.simibubi.create.foundation.outliner.Outliner;

import java.util.Optional;

public interface IExOutliner {
    public Outline.OutlineParams addWithoutChase(Object slot, Outline outline);
    public <T extends Outline & IOutlineChaser> Outline.OutlineParams chaseOrAdd(Object slot, T outline);
    public <T extends IOutlineChaser> Optional<Outline.OutlineParams> tryChase(Object slot, T chaser);
}
