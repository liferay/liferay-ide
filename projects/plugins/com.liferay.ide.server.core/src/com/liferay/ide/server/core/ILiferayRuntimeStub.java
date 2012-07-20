
package com.liferay.ide.server.core;

import org.eclipse.core.runtime.IStatus;

public interface ILiferayRuntimeStub
{

    String EXTENSION_ID = "com.liferay.ide.server.core.runtimeStubs";

    String RUNTIME_TYPE_ID = "runtimeTypeId";

    String DEFAULT = "default";

    String NAME = "name";

    String getRuntimeStubTypeId();

    String getName();

    IStatus validate();

}
