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

package com.liferay.ide.server.ui;

import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider;
import org.eclipse.wst.server.ui.FacetRuntimeComponentLabelProvider;

/**
 * @author Greg Amerson
 */
public class LiferayRuntimeStubFacetComponentLabelProvider extends FacetRuntimeComponentLabelProvider
{

    public final class RuntimeLabelProvider implements IRuntimeComponentLabelProvider
    {
        private final IRuntimeComponent rc;

        public RuntimeLabelProvider( IRuntimeComponent rc )
        {
            this.rc = rc;
        }

        public String getLabel()
        {
            return rc.getProperty( "type" ); //$NON-NLS-1$
        }
    }

    @SuppressWarnings( "rawtypes" )
    public Object getAdapter( Object adaptable, Class adapterType )
    {
        IRuntimeComponent rc = (IRuntimeComponent) adaptable;

        return new RuntimeLabelProvider( rc );
    }

}
