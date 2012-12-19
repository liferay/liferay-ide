
package com.liferay.ide.server.core;

import org.eclipse.core.runtime.IStatus;

public interface ILiferayRuntimeStub
{

    String EXTENSION_ID = "com.liferay.ide.server.core.runtimeStubs"; //$NON-NLS-1$

    String RUNTIME_TYPE_ID = "runtimeTypeId"; //$NON-NLS-1$

    String DEFAULT = "default"; //$NON-NLS-1$

    String NAME = "name"; //$NON-NLS-1$

    String getRuntimeStubTypeId();

    String getName();

    IStatus validate();

}
