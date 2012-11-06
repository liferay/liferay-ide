/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.core;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jst.common.project.facet.core.IClasspathProvider;
import org.eclipse.jst.server.core.internal.RuntimeClasspathProvider;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( { "restriction", "rawtypes" } )
public class LiferayRuntimeStubClasspathFactory implements IAdapterFactory
{

    private static final Class[] ADAPTER_TYPES = { IClasspathProvider.class };

    public Object getAdapter( Object adaptable, Class adapterType )
    {
        IRuntimeComponent rc = (IRuntimeComponent) adaptable;

        return new RuntimeClasspathProvider( rc );
    }

    public Class[] getAdapterList()
    {
        return ADAPTER_TYPES;
    }

}
