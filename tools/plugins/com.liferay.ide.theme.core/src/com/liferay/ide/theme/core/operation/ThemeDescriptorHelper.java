/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.theme.core.operation;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.LiferayDescriptorHelper;
import com.liferay.ide.theme.core.ThemeCore;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
public class ThemeDescriptorHelper extends LiferayDescriptorHelper
{

    public static final String DEFUALT_FILE_TEMPLATE =
        "<?xml version=\"1.0\"?>\n<!DOCTYPE look-and-feel PUBLIC \"-//Liferay//DTD Look and Feel {0}//EN\" \"http://www.liferay.com/dtd/liferay-look-and-feel_{1}.dtd\">\n\n<look-and-feel>\n\t<compatibility>\n\t\t<version>__VERSION__</version>\n\t</compatibility>\n\t<theme id=\"__ID__\" name=\"__NAME__\" />\n</look-and-feel>"; //$NON-NLS-1$

    public ThemeDescriptorHelper( IProject project )
    {
        super( project );
    }

    public void createDefaultFile( IContainer container, String version, String id, String name )
    {
        if( container == null || id == null || name == null )
        {
            return;
        }

        try
        {
            final Path path = new Path( ILiferayConstants.LIFERAY_LOOK_AND_FEEL_XML_FILE ); //$NON-NLS-1$
            final IFile lookAndFeelFile = container.getFile( path );
            final String descriptorVersion = getDescriptorVersionFromPortalVersion( version );

            CoreUtil.prepareFolder( (IFolder) lookAndFeelFile.getParent() );

            String contents =
                MessageFormat.format( DEFUALT_FILE_TEMPLATE, descriptorVersion, descriptorVersion.replace( '.', '_' ) );
            contents =
                contents.replaceAll( "__VERSION__", version + "+" ).replaceAll( "__ID__", id ).replaceAll( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    "__NAME__", name ); //$NON-NLS-1$

            lookAndFeelFile.create( new ByteArrayInputStream( contents.getBytes() ), true, null );
        }
        catch( CoreException e )
        {
            ThemeCore.logError( "Error creating default descriptor file", e ); //$NON-NLS-1$
        }
    }

}
