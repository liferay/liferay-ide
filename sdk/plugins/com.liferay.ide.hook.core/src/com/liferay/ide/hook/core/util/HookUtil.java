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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/
package com.liferay.ide.hook.core.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.core.model.Hook;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;


/**
 * @author Gregory Amerson
 */
public class HookUtil
{

    public static IFolder getCustomJspFolder(Hook hook, IProject project)
    {
        CustomJspDir element = hook.getCustomJspDir().element();

        if( element != null )
        {
            // IDE-110 IDE-648
            IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

            if( element != null && webappRoot != null )
            {
                org.eclipse.sapphire.modeling.Path customJspDir = element.getValue().getContent();

                for( IContainer folder : webappRoot.getUnderlyingFolders() )
                {
                    IFolder customJspFolder = folder.getFolder( new Path( customJspDir.toPortableString() ) );

                    if( customJspFolder != null )
                    {
                        return customJspFolder;
                    }
                }
            }
        }

        return null;
    }

}
