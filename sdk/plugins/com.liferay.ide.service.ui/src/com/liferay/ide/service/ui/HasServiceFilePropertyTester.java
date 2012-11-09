/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.service.ui;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;

/**
 * @author Gregory Amerson
 */
public class HasServiceFilePropertyTester extends PropertyTester
{

    public boolean test( Object receiver, String property, Object[] args, Object expectedValue )
    {
        if( receiver instanceof IResource )
        {
            IResource resource = (IResource) receiver;

            boolean isLiferayProject = ProjectUtil.isLiferayProject( resource.getProject() );

            if( isLiferayProject )
            {
                try
                {
                    // IDE-110 IDE-648
                    IVirtualFolder webappRoot = CoreUtil.getDocroot( resource.getProject() );

                    if( webappRoot != null )
                    {
                        for( IContainer container : webappRoot.getUnderlyingFolders() )
                        {
                            if( container != null && container.exists() )
                            {
                                Path path = new Path( "WEB-INF/" + ILiferayConstants.LIFERAY_SERVICE_BUILDER_XML_FILE );
                                IFile serviceFile = container.getFile( path );

                                if( serviceFile.exists() )
                                {
                                    return true;
                                }
                            }
                        }
                    }
                }
                catch( Throwable t )
                {
                    // ignore
                }
            }
        }

        return false;
    }

}
