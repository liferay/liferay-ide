/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.ui.handlers;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Terry Jia
 */
public class CompareFileHandler extends AbstractCompareFileHandler
{

    protected File getTemplateFile( IFile currentFile ) throws Exception
    {
        final IProject project = currentFile.getProject();

        final ILiferayProject liferayProject = LiferayCore.create( project );

        final String themeParent = liferayProject.getProperty( "theme.parent", "_styled" );

        final ILiferayPortal portal = liferayProject.adapt( ILiferayPortal.class );

        if( portal != null )
        {
            final IPath themesPath = portal.getAppServerPortalDir().append( "html/themes/" + themeParent );

            String name = currentFile.getName();
            String parent = currentFile.getParent().getName();

            IPath diffs = themesPath.append( "_diffs" );

            IPath tempFilePath = diffs.append( parent ).append( name );

            if( !tempFilePath.toFile().exists() )
            {
                tempFilePath = themesPath.append( parent ).append( name );
            }

            if( tempFilePath.toFile().exists() )
            {
                return tempFilePath.toFile();
            }
        }

        return null;
    }

}
