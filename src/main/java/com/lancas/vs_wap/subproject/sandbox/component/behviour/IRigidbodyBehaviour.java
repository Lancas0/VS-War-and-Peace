package com.lancas.vs_wap.subproject.sandbox.component.behviour;

import com.lancas.vs_wap.subproject.sandbox.api.component.*;
import com.lancas.vs_wap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vs_wap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;

public interface IRigidbodyBehaviour extends
    IComponentBehaviour<RigidbodyData>, IServerBehaviour<RigidbodyData>, IClientBehaviour<RigidbodyData>,
    IDataReadableBehaviour<RigidbodyData>, IDataWritableBehaviour<RigidbodyData> {

    @Override
    public IRigidbodyDataReader getDataReader();
    @Override
    public IRigidbodyDataWriter getDataWriter();

}
