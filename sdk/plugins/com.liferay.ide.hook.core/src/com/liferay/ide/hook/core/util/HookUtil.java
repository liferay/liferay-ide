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
import com.liferay.ide.hook.core.model.HookVersionType;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;


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

    /**
     * A small utility method used to compute the DTD version
     *
     * @param document
     *            - the document that is loaded by the editor
     * @return - {@link HookVersionType}
     */
    public static HookVersionType getDTDVersion( Document document )
    {
        HookVersionType dtdVersion = null;
        DocumentType docType = document.getDoctype();
        if( docType != null )
        {
            String publicId = docType.getPublicId();
            String systemId = docType.getSystemId();
            if( publicId != null && systemId != null )
            {
                if( publicId.contains( "6.0.0" ) || systemId.contains( "6.0.0" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    dtdVersion = HookVersionType.v6_0_0;
                }
                else if( publicId.contains( "6.1.0" ) || systemId.contains( "6.1.0" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    dtdVersion = HookVersionType.v6_1_0;
                }
            }
    
        }
    
        return dtdVersion;
    }

}
