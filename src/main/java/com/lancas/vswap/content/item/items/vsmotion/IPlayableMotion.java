package com.lancas.vswap.content.item.items.vsmotion;

import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;

public interface IPlayableMotion {
    public void setCurrentFrame(double frame);
    public boolean play(IRigidbodyDataWriter rigidWriter, double timeScale);
    public boolean isEmpty();
}
